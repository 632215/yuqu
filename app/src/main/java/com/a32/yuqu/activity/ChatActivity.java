package com.a32.yuqu.activity;

import android.app.Service;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.a32.yuqu.R;
import com.a32.yuqu.adapter.MsgAdapter;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.base.BaseActivity;
import com.a32.yuqu.utils.CommonUtils;
import com.a32.yuqu.utils.CommonlyUtils;
import com.a32.yuqu.utils.Constant;
import com.a32.yuqu.utils.KeyBoardUtils;
import com.a32.yuqu.view.FillListView;
import com.a32.yuqu.view.TopTitleBar;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ChatActivity extends BaseActivity implements TopTitleBar.OnTopTitleBarCallback, TopTitleBar.OnSaveCallBack {
    //    @Bind(R.id.listView)
//    ListView listView;
    @Bind(R.id.listView)
    FillListView listView;
    @Bind(R.id.btn_send)
    Button btn_send;
    @Bind(R.id.titleBar)
    TopTitleBar titleBar;
    @Bind(R.id.et_content)
    EditText et_content;
    private String toChatUsername = "";//实际上是指用户电话号码
    private int chatType = 1;
    private List<EMMessage> msgList = new ArrayList<>();
    private EMConversation conversation;
    protected int pagesize = 20;
//    private MessageAdapter messageAdapter;
    private MsgAdapter messageAdapter;
    private Vibrator myVibrator;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        toChatUsername = CommonlyUtils.getObjectUser().getUserPhone();
        getAllMessage();//得到会话对象
        titleBar.setTitle("与" + CommonlyUtils.getObjectUser().getUserName() + "的会话");
        titleBar.setOnTopTitleBarCallback(ChatActivity.this);
        titleBar.setSaveText("清空消息");
        titleBar.setOnSaveCallBack(this);
        msgList = conversation.getAllMessages();
        messageAdapter = new MsgAdapter(msgList, ChatActivity.this, toChatUsername);
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
                KeyBoardUtils.closeKeybord(et_content, ChatActivity.this);
            }

        });
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
        if (msgs.size() > 0) {
            titleBar.setSaveVisibility();
        } else {
            titleBar.setSaveUnVisibility();
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
        messageAdapter.setData(msgList);
//        messageAdapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
            titleBar.setSaveVisibility();
        } else {
            titleBar.setSaveUnVisibility();
        }
        et_content.setText("");
        et_content.clearFocus();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            if (myVibrator.hasVibrator()) {
                myVibrator.vibrate(1000);
            }
            Log.i(MyApplicaption.Tag, "收到消息了");
            for (EMMessage message : messages) {
                Log.i(MyApplicaption.Tag, "处理消息中");
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
                    msgList.add(message);
                    Log.i(MyApplicaption.Tag, "数据该刷新了");
                }
            }
            Log.i(MyApplicaption.Tag, "准备进入updateUI");
            updateUI();
            Log.i(MyApplicaption.Tag, "进入updateUI");

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

    private void updateUI() {
        new Thread() {
            public void run() {
                //这儿是耗时操作，完成之后更新UI；
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        //更新UI
                        if (msgList.size() > 0) {
                            et_content.setSelection(listView.getCount() - 1);
                            titleBar.setSaveVisibility();
                        } else {
                            titleBar.setSaveUnVisibility();
                        }
                        Log.i(MyApplicaption.Tag,"进入updateUI");
                        msgList=conversation.getAllMessages();
                        Log.i(MyApplicaption.Tag,"size："+ String.valueOf(msgList.size()));
                        messageAdapter.setData(msgList);
                    }

                });
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    //清楚聊天记录
    @Override
    public void onSaveClick() {
        EMClient.getInstance().chatManager().deleteConversation(CommonlyUtils.getObjectUser().getUserPhone(), true);
        getAllMessage();
        msgList.clear();
        messageAdapter.setData(msgList);
//        messageAdapter.notifyDataSetChanged();
        titleBar.setSaveUnVisibility();
    }
}
