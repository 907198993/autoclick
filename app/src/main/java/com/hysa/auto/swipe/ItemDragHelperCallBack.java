package com.hysa.auto.swipe;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ItemDragHelperCallBack extends ItemTouchHelper.Callback {

    private OnItemDragListener onItemDragListener;

    public ItemDragHelperCallBack(OnItemDragListener onItemDragListener) {
        this.onItemDragListener = onItemDragListener;
    }
    /**
     * 返回可以滑动的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int dragFlags;
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            //网格布局管理器允许上下左右拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            //其他布局管理器允许上下拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    /**
     * 拖拽到新位置时候的回调方法
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //不同Type之间不允许移动
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        if (onItemDragListener != null) {
            onItemDragListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    /**
     * 当用户左右滑动的时候执行的方法
     *
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    public interface OnItemDragListener {

        void onItemMove(int startPos, int endPos);

    }
}
