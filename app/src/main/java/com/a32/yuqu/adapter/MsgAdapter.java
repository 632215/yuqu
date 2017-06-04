package com.a32.yuqu.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.bean.UserInfo;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static com.a32.yuqu.utils.Utils.context;

/**
 * Created by root on 6/01/17.
 */

public class MsgAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context mContext;
    private LayoutInflater inflater;
    private String obiectUserName="";
    
    public MsgAdapter(List<EMMessage> msgs, Context context_,String obiectUserName) {
        this.msgs = msgs;
        this.mContext = context_;
        this.obiectUserName = obiectUserName;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(List<EMMessage> objects) {
        this.msgs = objects;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        EMMessage message = (EMMessage) getItem(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EMMessage message = (EMMessage) getItem(position);
        int viewType = getItemViewType(position);
        if (convertView == null) {
            if (viewType == 0) {
                convertView = inflater.inflate(R.layout.item_message_received, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
            }
        }
       holder = (MsgAdapter.ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            holder.head = (CircleImageView) convertView.findViewById(R.id.iv_userhead);
            convertView.setTag(holder);
        }
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());
        if (viewType==0){
            setHeadImg(CommonlyUtils.getObjectUser(),holder);//设置聊天对象的头像
        }else {
            setHeadImg(CommonlyUtils.getUserInfo(mContext),holder);//设置自己的头像
        }
        return convertView;
    }
    

    private void setHeadImg(UserInfo objectUser, MsgAdapter.ViewHolder holder) {
        if (FileUtil.fileIsExists(objectUser.getUserHead())){
            Picasso.with(context).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/"+objectUser.getUserHead()))
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

    public static ViewHolder holder;

    class ViewHolder {
        TextView tv;
        CircleImageView head;
    }
    
}
