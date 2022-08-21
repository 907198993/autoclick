package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.model.ChatWantProductEntity;

import org.json.JSONObject;

/**
 * 产品
 */
public class EaseChatRowWantProduct extends EaseChatRowText {


    private ImageView ivGoodImage;
    private TextView tvGoodDesc;
    private ImageView ivVideo;

    public EaseChatRowWantProduct(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_want_product : R.layout.ease_row_sent_want_product, this);
    }

    @Override
    protected void onFindViewById() {
        ivGoodImage = findViewById(R.id.ivGoodImage);
        tvGoodDesc = findViewById(R.id.tvGoodDesc);
        ivVideo = findViewById(R.id.ivVideo);
    }


    @Override
    public void onSetUpView() {
        String goodImage = message.getStringAttribute(EaseConstant.MESSAGE_PRODUCT_IMAGE, "");
        String goodDesc = message.getStringAttribute(EaseConstant.MESSAGE_PRODUCT_DESC, "");
        if (goodImage.isEmpty()) {
            try {
                JSONObject jsonObject = message.getJSONObjectAttribute("want");
                String jsonObjectimg = jsonObject.getString("url");
                String[] strs = jsonObjectimg.split("\"");
                goodImage =strs[1];
                goodDesc = jsonObject.getString("title");
            } catch (Exception e) {
                e.printStackTrace();
            }

        /*    Gson gson = new Gson();
            String wantText = message.getStringAttribute("want", "");
            ChatWantProductEntity entity = gson.fromJson(wantText, ChatWantProductEntity.class);
            goodImage = entity.getUrl().get(0);
            goodDesc = entity.getTitle();*/
        }

        ivVideo.setVisibility(message.getStringAttribute(EaseConstant.MESSAGE_PRODUCT_MEDIATYPE, "0").equals("1") ? View.VISIBLE : View.GONE);
        if (!TextUtils.isEmpty(goodImage)) {
            RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.img_defaut_chat);
            Glide.with(context).load(goodImage).apply(options).into(ivGoodImage);
        } else {
            ivGoodImage.setImageResource(R.drawable.img_defaut_chat);
        }
        tvGoodDesc.setText(goodDesc);

    }
}
