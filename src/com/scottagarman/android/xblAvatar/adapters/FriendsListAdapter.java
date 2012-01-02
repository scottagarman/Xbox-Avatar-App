package com.scottagarman.android.xblAvatar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.managers.AvatarsManager;

public class FriendsListAdapter extends BaseAdapter {
    private Context mCtx;
    private LayoutInflater mInflater;
    private AvatarsManager mAvatarsManager;

    public FriendsListAdapter(Context ctx, AvatarsManager manager) {
        mCtx = ctx;
        mInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAvatarsManager = manager;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null || convertView.getTag() == null){
            convertView = mInflater.inflate(R.layout.list_item_friend, parent, false);
            holder = new ViewHolder();
            
            holder.text = (TextView)convertView.findViewById(R.id.list_item_friend_text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(mAvatarsManager.getAvatarUrlAtIndex(i));

        return convertView;
    }

    static class ViewHolder {
        TextView text;
    }

    @Override
    public int getCount() {
        return mAvatarsManager.getSize();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}