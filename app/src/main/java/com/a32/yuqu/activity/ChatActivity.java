package com.a32.yuqu.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.MessageAdapter;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.CommonUtils;
import com.a32.yuqu.utils.Constant;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;

import java.util.List;

import butterknife.Bind;

public class ChatActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback {
    @Bind(R.id.listView)
    ListView listView;

    @Bind(R.id.btn_send)
    Button btn_send;

    @Bind(R.id.titleBar)
    TopTitleBar titleBar;

    private String toChatUsername;

    @Bind(R.id.et_content)
    EditText et_content;

    private int chatType = 1;

    private List<EMMessage> msgList;
    private EMConversation conversation;
    protected int pagesize = 20;
    private MessageAdapter messageAdapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        toChatUsername = this.getIntent().getStringExtra("username");
        titleBar.setTitle(toChatUsername);
        titleBar.setOnTopTitleBarCallback(this);
        getAllMessage();
        msgList = conversation.getAllMessages();
        messageAdapter = new MessageAdapter(msgList, ChatActivity.this);
        listView.setAdapter(messageAdapter);
        listView.setSelection(listView.getCount() - 1);
        btn_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {

                    return;
                }
                setMesaage(content);
            }

        });
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }

    protected void getAllMessage() {
        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                CommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    private void setMesaage(String content) {

        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(ChatType.GroupChat);
        // 发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        msgList.add(message);

        messageAdapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
        et_content.setText("");
        et_content.clearFocus();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == ChatType.GroupChat || message.getChatType() == ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);
                    messageAdapter.notifyDataSetChanged();
                    if (msgList.size() > 0) {
                        et_content.setSelection(listView.getCount() - 1);

                    }

                }
            }

            // 收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> list) {
            // 收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> list) {
            // 收到已送达回执
        }


        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
