package com.a32.yuqu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.bean.UserBean;
import com.a32.yuqu.db.InviteMessage;
import com.a32.yuqu.db.InviteMessgeDao;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.http.HttpResult;
import com.a32.yuqu.http.progress.ProgressSubscriber;
import com.a32.yuqu.http.progress.SubscriberOnNextListener;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本demo只提供好友的邀请通知，群相关的通知，已经拒绝申请的处理请参考官方demo
 */
public class ApplyListAdapter extends ArrayAdapter<InviteMessage> {

    private Context context;
    private InviteMessgeDao messgeDao;
    private InviteMessgeDao inviteMessgeDao;
    private String name="";
    public ApplyListAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        messgeDao = new InviteMessgeDao(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_invite_msg, null);
            holder.tv_reason = (TextView) convertView.findViewById(R.id.tv_reason);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.btnAgree = (TextView) convertView.findViewById(R.id.btnAgree);
            holder.btnDisAgree = (TextView) convertView.findViewById(R.id.btnDisAgree);
            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str1 = "已同意你的好友请求";
        String str2 = "同意";

        String str3 = "请求加你为好友";
        inviteMessgeDao = new InviteMessgeDao(context);

        final InviteMessage msg = getItem(position);
        Log.i(MyApplicaption.Tag, "msg.getFrom()" + msg.getFrom());
        getheadPath(msg.getFrom(), holder);
        if (msg != null) {
            holder.btnAgree.setVisibility(View.INVISIBLE);
            holder.btnDisAgree.setVisibility(View.INVISIBLE);
//            holder.tv_reason.setText(msg.getReason());
            if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAGREED) {
                holder.tv_reason.setText(str1);
            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED || msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED ||
                    msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                holder.btnAgree.setVisibility(View.VISIBLE);
//                holder.btnAgree.setEnabled(true);
//                holder.btnAgree.setBackgroundResource(android.R.drawable.btn_default);
                holder.btnDisAgree.setVisibility(View.VISIBLE);
                if (msg.getReason() == null||msg.getReason() == "") {
                    // 如果没写理由
                    holder.tv_reason.setText(str3);
                }else {
                    holder.tv_reason.setText(msg.getReason());
                }
                // 设置点击事件
                holder.btnAgree.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 同意别人发的好友请求
                        acceptInvitation(holder.btnAgree, holder.btnDisAgree, msg);
                    }
                });
                holder.btnDisAgree.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 拒绝别人发的好友请求
                        try {
                            holder.btnDisAgree.setVisibility(View.INVISIBLE);
                            holder.btnAgree.setVisibility(View.INVISIBLE);
                            EMClient.getInstance().contactManager().declineInvitation(msg.getFrom());
                            inviteMessgeDao.deleteMessage(msg.getFrom());
                            Toast.makeText(context,"已拒绝"+name+"用户的申请", Toast.LENGTH_SHORT).show();
                            holder.tv_reason.setText("已拒绝");
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.AGREED) {

            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.REFUSED) {

            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION_ACCEPTED) {

            } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION_DECLINED) {

            }

        } else {
            holder.tv_reason.setText("已处理");
        }
        return convertView;
    }

    private void getheadPath(String phone, final ViewHolder holder) {
        SubscriberOnNextListener onNextListener = new SubscriberOnNextListener<UserBean>() {

            @Override
            public void onNext(UserBean info) {
                if (info != null) {
                    if (FileUtil.fileIsExists(info.getHead())) {
                        Picasso.with(context).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.head);
                    } else {
                        Picasso.with(context).load((HttpMethods.BASE_URL + "upload/" + info.getHead()))
                                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                                .placeholder(R.mipmap.head)//加载中
                                .error(R.mipmap.head)//加载失败
                                .into(holder.head);
                    }
                    name=info.getName();
                    holder.tv_name.setText(info.getName());
                }
            }

            @Override
            public void onError(String Msg) {
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        HttpMethods.getInstance().getheadPath(new ProgressSubscriber<HttpResult<UserBean>>(onNextListener, context, false), map);
    }

    /**
     * 同意好友请求或者群申请
     */
    private void acceptInvitation(final TextView buttonAgree, final TextView btnDisAgree, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = "正在同意...";
        final String str2 = "已同意";
        final String str3 = "同意失败:";
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEINVITEED) {//同意好友请求
                        EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.BEAPPLYED) { //同意加群申请
                        EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                    } else if (msg.getStatus() == InviteMessage.InviteMesageStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                    }
                    msg.setStatus(InviteMessage.InviteMesageStatus.AGREED);
                    // 更新db
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonAgree.setText(str2);
                            buttonAgree.setBackgroundDrawable(null);
                            buttonAgree.setEnabled(false);

                            buttonAgree.setVisibility(View.INVISIBLE);
                            btnDisAgree.setVisibility(View.INVISIBLE);
                            inviteMessgeDao.deleteMessage(msg.getFrom());
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressLint("ShowToast")
                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    private static class ViewHolder {
        TextView tv_name;
        TextView tv_reason;
        TextView btnAgree;
        TextView btnDisAgree;

        CircleImageView head;
    }

}
