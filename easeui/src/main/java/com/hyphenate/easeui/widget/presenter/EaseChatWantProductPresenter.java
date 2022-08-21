package com.hyphenate.easeui.widget.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowWantProduct;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatWantProductPresenter extends EaseChatTextPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowWantProduct(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
    }
}
