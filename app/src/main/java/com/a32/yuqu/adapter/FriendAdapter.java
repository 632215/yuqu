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
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.db.EaseUser;
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

public class FriendAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<EaseUser> usersList;

    public FriendAdapter(Context mContext, List<EaseUser> objects) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.usersList = objects;
    }

    public void setData(List<EaseUser> objects) {
        this.usersList = objects;
        notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
        }

        if (parent instanceof FillListView) {
            if (((FillListView) parent).isMeasure) {
                return convertView;
            }
        }

        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        }
        EaseUser easeUser = usersList.get(position);
        //加载头像
        getheadPath(easeUser.getUsername(), holder);
        return convertView;
    }


    private void getheadPath(String phone, final ViewHolder holder) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null) {
//                        Picasso.with(mcontext).load(HttpMethods.BASE_URL + "upload/" + info.getHead())
                    if (FileUtil.fileIsExists(info.getHead())) {
                        Picasso.with(mContext).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.avatar);
                    } else {
                        Picasso.with(mContext).load((HttpMethods.BASE_URL + "upload/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.avatar);
                    }
                    holder.tv_name.setText(info.getName());
                }
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        HttpMethods.getInstance().getheadPath(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, mContext, false), map);
    }

    public ViewHolder holder;

    class ViewHolder {
        CircleImageView avatar;//头像
        TextView tv_name;//用户名
    }
}
