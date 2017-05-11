package com.a32.yuqu.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.bean.DXWbean;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.a32.yuqu.view.FillListView;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 6/01/17.
 */

public class DXWAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<DXWbean.ListBean> xinwenList= new ArrayList<>();

    public DXWAdapter(Context mContext, List<DXWbean.ListBean> objects) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.xinwenList = objects;
    }

    public void setData(List<DXWbean.ListBean> objects) {
        this.xinwenList = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return xinwenList.size();
    }

    @Override
    public Object getItem(int position) {
        return xinwenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_dxw, parent, false);
        }
        if(parent instanceof FillListView){
            if(((FillListView) parent).isMeasure){
                return convertView;
            }
        }
        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        }
        DXWbean.ListBean bean =xinwenList.get(position);
        holder.title.setText(bean.getTitle());
        return convertView;
    }

    public ViewHolder holder;

    class ViewHolder {
        TextView title;
    }
}
