package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.domain.EaseEmojicon;

/**
 * 名片
 */
public class EaseChatRowBusinessCard extends EaseChatRowText {

    private ImageView ivStoreHead;
    private TextView tvStoreName;
    private TextView tvStoreDesc;


    public EaseChatRowBusinessCard(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_business_card : R.layout.ease_row_sent_business_card, this);
    }

    @Override
    protected void onFindViewById() {
        ivStoreHead = (ImageView) findViewById(R.id.ivStoreHead);
        tvStoreName = (TextView) findViewById(R.id.tvStoreName);
        tvStoreDesc = (TextView) findViewById(R.id.tvStoreDesc);
    }


    @Override
    public void onSetUpView() {
        tvStoreName.setText(message.getStringAttribute(EaseConstant.MESSAGE_BUSINESS_CARD_NAME, ""));
        tvStoreDesc.setText(message.getStringAttribute(EaseConstant.MESSAGE_BUSINESS_CARD_DESC,""));
        String headUrl = message.getStringAttribute(EaseConstant.MESSAGE_BUSINESS_CARD_AVATAR, "");
        if (!TextUtils.isEmpty(headUrl)) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ease_default_expression);
            Glide.with(context).load(headUrl).apply(options).into(ivStoreHead);
        } else {
            ivStoreHead.setImageResource(R.drawable.ease_default_expression);
        }
    }
}
