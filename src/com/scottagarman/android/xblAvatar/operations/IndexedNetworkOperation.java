package com.scottagarman.android.xblAvatar.operations;




public class IndexedNetworkOperation extends NetworkOperation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6681072180967110802L;
	
	public int index;
	public int section;
	
	public IndexedNetworkOperation(String url, NetworkOperationCompleteListener del) {
		super(url, del);
	}

	public IndexedNetworkOperation(String url, NetworkOperationCompleteListener del, int index, int section) {
		super(url, del);
		this.index = index;
		this.section = section;
	}
	
	
}
