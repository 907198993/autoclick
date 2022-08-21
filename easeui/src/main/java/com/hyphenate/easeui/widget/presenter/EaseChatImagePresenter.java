package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.BaseAdapter;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseShowBigImageActivity;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowImage;
import com.hyphenate.easeui.widget.event.EaseImageEvent;

import java.io.File;
import java.util.ArrayList;

import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatImagePresenter extends EaseChatFilePresenter {

    public EMMessage[] messages;

    public EaseChatImagePresenter(EMMessage[] messages) {
        this.messages = messages;
    }


    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowImage(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(final EMMessage message) {
        super.handleReceiveMessage(message);

        getChatRow().updateView(message);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                getChatRow().updateView(message);
            }

            @Override
            public void onError(int code, String error) {
                getChatRow().updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                getChatRow().updateView(message);
            }
        });
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        //super.onBubbleClick(message);
        KLog.d("bj====EaseImageEvent onBubbleClick");
        EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
        if (EMClient.getInstance().getOptions().getAutodownloadThumbnail()) {
            ArrayList<String> imageList = new ArrayList<String>();
            for (EMMessage msg : messages) {
                if (msg.getType() == EMMessage.Type.IMAGE) {
                    EMImageMessageBody mImageMessageBody = ((EMImageMessageBody) msg.getBody());
                    imageList.add(mImageMessageBody.getRemoteUrl());
                }
            }
            int position = 0;
            for (int i = 0; i < imageList.size(); i++) {
                if (imgBody.getRemoteUrl().equals(imageList.get(i))) {
                    position = i;
                }
            }
            RxBus.getDefault().post(new EaseImageEvent(position, imageList));
        }

    }
}
