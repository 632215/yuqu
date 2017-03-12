package com.a32.yuqu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.db.EaseUser;

import java.util.List;

/**
 * Created by 32 on 2017/2/25.
 */

public class ContactAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<EaseUser> usersList;

    public ContactAdapter(Context mContext, List<EaseUser> list) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.usersList = list;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public EaseUser getItem(int position) {
//        return position;
        return usersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, null);
            holder = new viewHolder();
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_avatar= (ImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        EaseUser easeUser=usersList.get(position);
        holder.tv_name.setText(easeUser.getUsername());
        return convertView;
    }

    viewHolder holder;

    class viewHolder {
        ImageView iv_avatar;//头像
        TextView tv_name;//用户名

    }
}
