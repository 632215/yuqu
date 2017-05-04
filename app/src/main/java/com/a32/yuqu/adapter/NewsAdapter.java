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

public class NewsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<EMConversation> copyConversationList= new ArrayList<EMConversation>();

    public NewsAdapter(Context mContext, List<EMConversation> objects) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.copyConversationList = objects;
    }

    public void setData(List<EMConversation> objects) {
        this.copyConversationList = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return copyConversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return copyConversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_conversation, parent, false);
        }

        if(parent instanceof FillListView){
            if(((FillListView) parent).isMeasure){
                return convertView;
            }
        }

        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            convertView.setTag(holder);
        }
        // 获取与此用户/群组的会话
        EMConversation conversation = (EMConversation) getItem(position);
        // 获取用户username或者群组groupid
        String username = conversation.getLastMessage().getUserName();
        //加载头像
        Log.i(MyApplicaption.Tag,"----username"+username);
        getheadPath(username,holder);
        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }
        if (conversation.getAllMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(lastMessage.getBody().toString().substring(5, lastMessage.getBody().toString().length() - 1));
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void getheadPath(String phone, final ViewHolder holder) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null){
                    if (FileUtil.fileIsExists(info.getHead())){
                        Picasso.with(mContext).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/myHead/"+info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.avatar);
                    }else{
                        Picasso.with(mContext).load((HttpMethods.BASE_URL + "upload/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.avatar);
                    }
                    holder.name.setText(info.getName());
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

    public ViewHolder holder;

    class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView name;
        /**
         * 消息未读数
         */
        TextView unreadLabel;
        /**
         * 最后一条消息的内容
         */
        TextView message;
        /**
         * 最后一条消息的时间
         */
        TextView time;
        /**
         * 最后一条消息的发送状态
         */
        View msgState;
        /**
         * 整个list中每一行总布局
         */
        CircleImageView avatar;/**头像*/
    }


}
