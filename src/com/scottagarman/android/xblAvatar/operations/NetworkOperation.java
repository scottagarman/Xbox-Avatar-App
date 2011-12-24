package com.scottagarman.android.xblAvatar.operations;

import android.os.Handler;
import android.os.Message;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkOperation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3389953904685901495L;
	
	// Delegate for listening on operation completion
	public NetworkOperationCompleteListener delegate;
	
	// Enum for request types
	public static enum RequestMethod{
		POST, 	
		GET,	
		PUT,		
		DELETE
	}
	
	// Constants for thread messages
	private final int NETWORK_OPERATION_SUCCESS = 100;
	private final int NETWORK_OPERATION_FAILURE = 101;
	
	// Static thread pool for execution
	private static ExecutorService operationPool;
	
	// ivar: handle on thread process
	private Future<NetworkOperationRunnable> future;
	
	// ivars for request data
	public String operationUrl;
	public RequestMethod requestMethod = RequestMethod.GET;
	public String requestBody;
	
	// ivars for response data
	public byte[] responseData;
	public Exception responseError;
	
	public NetworkOperation(String url, NetworkOperationCompleteListener del){
		operationUrl = url;
		delegate = del;
	}
	
	/**
	 * Gets static operation pool.
	 */
	public static ExecutorService getOperationPool(){
		if(operationPool == null)
			operationPool = Executors.newFixedThreadPool(10);
		return operationPool;
	}
	
	/**
	 * Adds operation runnable to thread pool and begins thread tasks.
	 */
	@SuppressWarnings("unchecked")
	public void beginOperation(){
		future = (Future<NetworkOperationRunnable>)NetworkOperation.getOperationPool().submit(new NetworkOperationRunnable());
	}
	
	/**
	 * Cancels operation.
	 */
	public void cancel(){
		if(future != null){
			delegate = null;
			future.cancel(true);
		}
	}
	
	/**
	 * This internal class is used to encapsulate the run functionality
	 * of the network operation class. We wouldn't want outside entities
	 * to be able to call run on network com.sincerely.android.ink.operations. They're very private
	 * people.
	 * 
	 * @author harrisonlee
	 *
	 */
	private class NetworkOperationRunnable implements Runnable {
		
		public void run(){
			try {
				
				// Create uri and request stub
				URI uri = new URI(operationUrl);
				HttpUriRequest request = null;

				// Determine request method
				switch(requestMethod){

				case GET:
					request = new HttpGet(uri);
					break;

				case POST:

					// Create entity body from request body
					StringEntity postEntity = new StringEntity(requestBody, HTTP.UTF_8);
					postEntity.setContentType(URLEncodedUtils.CONTENT_TYPE);

					// Set post entity
					HttpPost post = new HttpPost(uri);
					post.setEntity(postEntity);
					request = post;
					break;

				case PUT:
					request = new HttpPut(uri);
					break;

				case DELETE:
					request = new HttpDelete(uri);
					break;

				}

				// Set timeout for connection and socket response
				request.getParams().setParameter("http.connection.timeout", new Integer(60 * 1000));
				request.getParams().setParameter("http.socket.timeout", new Integer(60 * 1000));

				// Create client and setup connection for https if necessary
				HttpClient client = null;

				if(uri.getScheme().equals("https")){
                    client = new DefaultHttpClient(getClientConnManager(request), request.getParams());
				} else {
					client = new DefaultHttpClient();
				}

				request.getParams().setParameter("http.socket.timeout", new Integer(60 * 1000));

				// Get response from client and set response data
				HttpResponse response 	= client.execute(request);
				responseData 			= IOUtils.toByteArray(response.getEntity().getContent());
				
				// All done
    			mHandler.sendEmptyMessage(NETWORK_OPERATION_SUCCESS);
				
			} catch (Exception e) {
				responseError = e;
				mHandler.sendEmptyMessage(NETWORK_OPERATION_FAILURE);
			}
				
				
				
		}
		
		private ThreadSafeClientConnManager getClientConnManager(HttpUriRequest request) throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, IOException {
			
			// Create a dummy keystore
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);
			
	        /*
	         * Create socket factory from the trust all implementation defined
	         * in this class. Also set factory to trust all hostnames.
	         */
			SSLSocketFactory sslf = new TrustAllSSLSocketFactory(trustStore);
			sslf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			
			// Create https scheme with socket factory
			Scheme https = new Scheme("https", sslf, 443);
			
			// Register scheme with registry and return connection manager
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(https);
			
			return new ThreadSafeClientConnManager(request.getParams(), registry);
		}

	}
	
	/**
	 * Thread handler. This object is used to notify the main UI thread
	 * that activity has completed on the network operation thread.
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			
			// Successful operation
			case NETWORK_OPERATION_SUCCESS:
				if(delegate != null)
					delegate.onNetworkOperationComplete(NetworkOperation.this);
				break;
			
			// Failed operation
			case NETWORK_OPERATION_FAILURE:
				if(delegate != null)
					delegate.onNetworkOperationCompleteWithError(NetworkOperation.this);
				break;
			
			}
			
			super.handleMessage(msg);
		}
	};
	
	/**
	 * Interface used to classify callbacks for thread actions used by
	 * network com.sincerely.android.ink.operations.
	 * 
	 * @author harrisonlee
	 *
	 */
	public interface NetworkOperationCompleteListener {
		public void onNetworkOperationComplete(NetworkOperation operation);
		public void onNetworkOperationCompleteWithError(NetworkOperation operation);
	}
	
	
	/**
	 * The following class is used to prevent our network operation
	 * from throwing security exceptions when accessing HTTPS URLs.
	 * 
	 * Often, Java trust managers and hostname verifiers prevent 
	 * applications from accessing secure websites without user permission. 
	 * Since we know which web resources the application will use, and 
	 * because we know we can be trusted, we're not worried about
	 * verifying hosts or certificates. The user will just have to take
	 * our word for it.
	 * 
	 */	
	public class TrustAllSSLSocketFactory extends SSLSocketFactory {
	    SSLContext sslContext = SSLContext.getInstance("TLS");

	    public TrustAllSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
	        super(truststore);

	        TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
	            public X509Certificate[] getAcceptedIssuers() {return null;}
	        };

	        sslContext.init(null, new TrustManager[] { tm }, null);
	    }

	    @Override
	    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
	        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
	    }

	    @Override
	    public Socket createSocket() throws IOException {
	        return sslContext.getSocketFactory().createSocket();
	    }
	}
	
}
