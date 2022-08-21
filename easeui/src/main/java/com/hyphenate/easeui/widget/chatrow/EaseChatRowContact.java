package com.hyphenate.easeui.widget.chatrow;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.widget.EaseImageView;
import com.luck.picture.lib.tools.SPUtils;

import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 名片
 */
public class EaseChatRowContact extends EaseChatRowText {

    private TextView tvPhone;
    private TextView tvWeChat;
    private TextView tvQQ;
    private TextView tvPhoneChange;
    private TextView tvWeChatChange;
    private TextView tvQQChange;
    //新加
    private EaseImageView iv_Userhead;

    private LinearLayout llWeChat;
    private LinearLayout llPhone;
    private LinearLayout llQQ;

    public EaseChatRowContact(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_contact : R.layout.ease_row_received_contact, this);

    }

    @Override
    protected void onFindViewById() {
        tvPhone = findViewById(R.id.tvPhone);
        tvWeChat = findViewById(R.id.tvWeChat);
        tvQQ = findViewById(R.id.tvQQ);
        tvPhoneChange = findViewById(R.id.tvPhoneChange);
        tvWeChatChange = findViewById(R.id.tvWeChatChange);
        tvQQChange = findViewById(R.id.tvQQChange);
        llPhone = findViewById(R.id.llPhone);
        llWeChat = findViewById(R.id.llWeChat);
        llQQ = findViewById(R.id.llQQ);
        iv_Userhead =findViewById(R.id.iv_userhead);
        tvPhoneChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cliCopy(tvPhone.getText().toString());
                showCommonDialog("已复制手机号,是否拨打", "可在拨号键盘直接粘贴手机号");
            }
        });
        tvWeChatChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cliCopy(tvWeChat.getText().toString());
                showCommonDialog("已复制微信号,是否打开微信", "粘贴微信号搜索添加对方为好友");
            }
        });
        tvQQChange.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cliCopy(tvQQChange.getText().toString());
                showCommonDialog("已复制QQ号,是否打开QQ", "粘贴QQ号搜索添加对方为好友");
            }
        });
    }


    @Override
    public void onSetUpView() {
        String phone = message.getStringAttribute(EaseConstant.MESSAGE_CONTACT_PHONE, "");
        String weChat = message.getStringAttribute(EaseConstant.MESSAGE_CONTACT_WECHAT, "");
        String qq = message.getStringAttribute(EaseConstant.MESSAGE_CONTACT_QQ, "");
        String icon= message.getStringAttribute(EaseConstant.MESSAGE_CONTACT_ICO, "");

        tvPhone.setText(phone);
        tvWeChat.setText(weChat);
        tvQQ.setText(qq);
        if(!TextUtils.isEmpty(icon)){
            Glide.with(context).load(icon).into(iv_Userhead);
        }


        llWeChat.setVisibility(TextUtils.isEmpty(weChat) ? View.GONE : VISIBLE);
        llPhone.setVisibility(TextUtils.isEmpty(phone) ? View.GONE : VISIBLE);
        llQQ.setVisibility(TextUtils.isEmpty(qq) ? View.GONE : VISIBLE);
    }

    private MaterialDialog contactDialog;

    public void showCommonDialog(final String title, final String content) {
        if (contactDialog == null) {
            contactDialog = new MaterialDialog.Builder(context)
                    .customView(R.layout.dialog_contact, false)
                    .cancelable(true).build();
        }
        TextView tvTitle = (TextView) contactDialog.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) contactDialog.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) contactDialog.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) contactDialog.findViewById(R.id.tvConfirm);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                contactDialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.contains("手机号")) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPhone.getText().toString()));
                    context.startActivity(dialIntent);
                } else if (title.contains("微信号")) {
                    Intent intent = new Intent();
                    ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(componentName);
                    if (context.getPackageManager().resolveActivity(intent, 0) != null) {
                        context.startActivity(intent);
                    } else {
                        ToastUtils.showShortSafe("未检测到微信，请安装微信后使用");
                    }
                } else {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
                    context.startActivity(intent);
                }
                contactDialog.dismiss();
            }
        });
        contactDialog.show();
    }


    private void cliCopy(String content) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String strText = content;
        //创建一个新的文本clip对象
        ClipData mClipData = ClipData.newPlainText(content, strText);
        //把clip对象放在剪贴板中
        cm.setPrimaryClip(mClipData);
    }
}
