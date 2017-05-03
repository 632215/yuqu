package com.a32.yuqu.adapter;

import android.annotation.SuppressLint;
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
import com.a32.yuqu.bean.UserInfo;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 32 on 2017/3/12.
 */

public class MessageAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context context;
    private LayoutInflater inflater;
    private String obiectUserName="";

    public MessageAdapter(List<EMMessage> msgs, Context context_,String obiectUserName) {
        this.msgs = msgs;
        this.context = context_;
        this.obiectUserName = obiectUserName;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == 0) {
                convertView = inflater.inflate(R.layout.item_message_received, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
            }
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            holder.head = (CircleImageView) convertView.findViewById(R.id.iv_userhead);
            convertView.setTag(holder);
        }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());
        if (viewType==0){
//            getheadPath(obiectUserName,holder);
            setHeadImg(CommonlyUtils.getObjectUser(),holder);//设置自己的头像
        }else {
            setHeadImg(CommonlyUtils.getUserInfo(context),holder);//设置自己的头像
        }
        return convertView;
    }

    class ViewHolder {

        TextView tv;
        CircleImageView head;
    }

    private void setHeadImg(UserInfo objectUser,ViewHolder holder) {
        if (FileUtil.fileIsExists(objectUser.getUserHead())){
            Picasso.with(context).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/myHead/"+objectUser.getUserHead()))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                    .placeholder(R.mipmap.head)//加载中
                    .error(R.mipmap.head)//加载失败
                    .into(holder.head);
        }else{
            Picasso.with(context).load((HttpMethods.BASE_URL + "upload/" + objectUser.getUserHead()))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                    .placeholder(R.mipmap.head)//加载中
                    .error(R.mipmap.head)//加载失败
                    .into(holder.head);
        }
    }

//    private void getheadPath(String name, final ViewHolder holder) {
//        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {
//
//            @Override
//            public void onNext(UserBean info) {
//                Log.i(MyApplicaption.Tag,"info--"+info.getName()+"----"+info.getHead());
//                if (info != null){
//                    if (FileUtil.fileIsExists(info.getHead())){
//                        Picasso.with(context).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/myHead/"+info.getHead()))
//                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
//                                .placeholder(R.mipmap.head)//加载中
//                                .error(R.mipmap.head)//加载失败
//                                .into(holder.head);
//                    }else{
//                        Picasso.with(context).load((HttpMethods.BASE_URL + "upload/" + info.getHead()))
//                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
//                                .placeholder(R.mipmap.head)//加载中
//                                .error(R.mipmap.head)//加载失败
//                                .into(holder.head);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(String Code, String Msg) {
//            }
//        };
//        Map<String, String> map = new HashMap<>();
//        map.put("name",name);
//        HttpMethods.getInstance().getUserByName(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, context, false), map);
//    }
}
