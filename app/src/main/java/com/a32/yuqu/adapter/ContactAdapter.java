package com.a32.yuqu.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.db.EaseUser;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            holder.iv_avatar= (CircleImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        EaseUser easeUser=usersList.get(position);
        //加载头像
        getheadPath(easeUser.getUsername(),holder);
        return convertView;
    }

    viewHolder holder;

    class viewHolder {
        CircleImageView iv_avatar;//头像
        TextView tv_name;//用户名
    }

    private void getheadPath(String phone, final viewHolder holder) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null){
//                        Picasso.with(mcontext).load(HttpMethods.BASE_URL + "upload/" + info.getHead())
                    if (FileUtil.fileIsExists(info.getHead())){
                        Picasso.with(mContext).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/"+info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.iv_avatar);
                    }else{
                        Picasso.with(mContext).load((HttpMethods.BASE_URL + "upload/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.iv_avatar);
                    }
                    holder.tv_name.setText(info.getName());
                }
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone",phone);
        HttpMethods.getInstance().getheadPath(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, mContext, false), map);
    }
}
