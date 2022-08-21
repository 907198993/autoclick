package com.hyphenate.easeui.widget.event;

import java.util.ArrayList;

public class EaseImageEvent {

    public int position;

    public ArrayList<String> images;

    public EaseImageEvent(int position, ArrayList<String> images) {
        this.position = position;
        this.images = images;
    }
}
