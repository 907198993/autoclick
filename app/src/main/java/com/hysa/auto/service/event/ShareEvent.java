package com.hysa.auto.service.event;

/**
 * 分享事件
 */
public class ShareEvent {

    public int type;  //-1分享结束，1分享开始

    public ShareEvent(int type) {
        this.type = type;
    }
}
