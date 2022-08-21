/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_TYPE_RECALL = "message_recall";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";

    public static final String MESSAGE_ATTR_AT_MSG = "em_at_list";
    public static final String MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL";


    public static final String BROWSE_UI_Mode = "browse_ui_mode";
    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    public static final int CHATTYPE_CHATGROUP = 4;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";

    //接收者用户名
    public static final String EXTRA_USER_NAME = "ToUserNick";
    //接收者头像
    public static final String EXTRA_USER_ICON = "ToUserIconUrl";
    //发送者的ID
    public static final String EXTRA_USER_SEND_ID = "EXTRA_USER_SEND_ID";
    //发送者用户名
    public static final String EXTRA_FROM_USER_NAME = "FromUserNick";
    //发送者头像
    public static final String EXTRA_FROM_USER_ICON = "FromUserIconUrl";

    //自定义消息类型Key
    public static String MESSAGE_CUSTOM_TYPE = "MESSAGE_CUSTOM_TYPE";
    //发送图片给云相册好友
    public static String MESSAGE_BUSINESS_SHARE = "MESSAGE_BUSINESS_SHARE";
    //自定义消息value  名片
    public static String MESSAGE_BUSINESS_CARD = "MESSAGE_BUSINESS_CARD";
    //名片商家名字
    public static String MESSAGE_BUSINESS_CARD_NAME = "MESSAGE_BUSINESS_CARD_NAME";
    //名片商家介绍详情
    public static String MESSAGE_BUSINESS_CARD_DESC = "MESSAGE_BUSINESS_CARD_DESC";
    //名片商家头像
    public static String MESSAGE_BUSINESS_CARD_AVATAR = "MESSAGE_BUSINESS_CARD_AVATAR";
    //名片商家ID
    public static String MESSAGE_BUSINESS_CARD_ID = "MESSAGE_BUSINESS_CARD_ID";
    //自定义消息value  产品
    public static String MESSAGE_PRODUCT = "MESSAGE_PRODUCT";
    //产品ID
    public static String MESSAGE_PRODUCT_ID = "MESSAGE_PRODUCT_ID";
    //产品图片
    public static String MESSAGE_PRODUCT_IMAGE = "MESSAGE_PRODUCT_IMAGE";
    public static String MESSAGE_PRODUCT_MEDIATYPE = "MESSAGE_PRODUCT_MEDIATYPE";
    //产品描述
    public static String MESSAGE_PRODUCT_DESC = "MESSAGE_PRODUCT_DESC";
    //自定义消息value  联系方式
    public static String MESSAGE_BUSINESS_CONTACT = "MESSAGE_BUSINESS_CONTACT";
    public static String MESSAGE_CONTACT_PHONE = "MESSAGE_CONTACT_PHONE";
    public static String MESSAGE_CONTACT_WECHAT = "MESSAGE_CONTACT_WECHAT";
    public static String MESSAGE_CONTACT_QQ = "MESSAGE_CONTACT_QQ";
    public static String MESSAGE_CONTACT_ICO = "MESSAGE_CONTACT_ICO";

    //求购商品
    public static String MESSAGE_WANT_PROCUCT = "wantProductNoticeCustom";
}
