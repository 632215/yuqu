package com.a32.yuqu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * Created by 32 on 2017/3/12.
 */

public class MessageAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context context;
    private LayoutInflater inflater;

    public MessageAdapter(List<EMMessage> msgs, Context context_) {
        this.msgs = msgs;
        this.context = context_;
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
            convertView.setTag(holder);
        }

        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());
        return convertView;
    }
    class ViewHolder {

        TextView tv;
    }
}
