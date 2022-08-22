package com.hysa.auto.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hysa.auto.R;
import com.hysa.auto.widget.CustomDialog;

public class DialogUtil {

    public static MaterialDialog showCustomDialog(Context context, int layoutId) {
        return showCustomDialog(context, layoutId, true);
    }

    public static MaterialDialog showCustomDialog(Context context, int layoutId, boolean isFoot) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(true).build();
        if (isFoot) {
            Window window = builder.getWindow();
            window.setWindowAnimations(R.style.TransDialogAnim);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        return builder;
    }

    public static MaterialDialog showCustomDialog(Context context, int layoutId, boolean isFoot, boolean cancelable) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(cancelable).build();
        if (isFoot) {
            Window window = builder.getWindow();
            window.setWindowAnimations(R.style.TransDialogAnim);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        return builder;
    }

    public static CustomDialog showBottomDialog(Context context, int layoutId) {
        return showBottomDialog(context, layoutId, true);
    }

    public static CustomDialog showBottomDialog(Context context, int layoutId, boolean isFoot) {
        CustomDialog builder = new CustomDialog(new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(true));
        if (isFoot) {
            Window window = builder.getWindow();
            window.setWindowAnimations(R.style.TransDialogAnim);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        return builder;
    }

    public static MaterialDialog showCustomSimpleDialog(Context context, int layoutId) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(true).build();
        return builder;
    }

    public static MaterialDialog showCustomSimpleDialog(Context context, int layoutId, boolean cancelable) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(cancelable).build();
        return builder;
    }

    public static MaterialDialog showCommonDialog(Context context, String title, String content, View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
        final MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_common, false)
                .cancelable(true).build();
        TextView tvTitle = (TextView) builder.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) builder.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) builder.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) builder.findViewById(R.id.tvConfirm);
        tvTitle.setText(title);
        tvContent.setText(content);
        if (cancelListener == null) {
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            });
        } else {
            tvCancel.setOnClickListener(cancelListener);
        }
        tvConfirm.setOnClickListener(confirmListener);
        return builder;
    }

    public static MaterialDialog showCommonIconDialog(Context context, int image, String title, String content, String cancel, String confirm, View.OnClickListener cancelListener, View.OnClickListener confirmListener) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_common, false)
                .cancelable(true).build();
        ImageView ivIcon = (ImageView) builder.findViewById(R.id.ivIcon);
        TextView tvTitle = (TextView) builder.findViewById(R.id.tvTitle);
        TextView tvContent = (TextView) builder.findViewById(R.id.tvContent);
        TextView tvCancel = (TextView) builder.findViewById(R.id.tvCancel);
        TextView tvConfirm = (TextView) builder.findViewById(R.id.tvConfirm);
        tvCancel.setText(cancel);
        tvConfirm.setText(confirm);
        if (image > 0) {
            ivIcon.setImageResource(image);
            ivIcon.setVisibility(View.VISIBLE);
        }
        tvTitle.setText(title);
        tvContent.setText(content);
        tvCancel.setOnClickListener(cancelListener);
        tvConfirm.setOnClickListener(confirmListener);
        return builder;
    }

    public static void showMessageDialog(Context context, String title, String content) {
        final MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_message, false)
                .cancelable(true).build();
        ImageView iv_close = (ImageView) builder.findViewById(R.id.ivClose);
        TextView tv_title = (TextView) builder.findViewById(R.id.tvTitle);
        TextView tv_content = (TextView) builder.findViewById(R.id.tvContent);
        TextView tv_confirm = (TextView) builder.findViewById(R.id.tvBtn);
        iv_close.setVisibility(View.GONE);
        tv_title.setText(title);
        tv_content.setText(content);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    public static MaterialDialog showAccessible(final Context context, boolean open, boolean openTwo, View.OnClickListener manualClick, View.OnClickListener automaticClick) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_accessible_suspend, false)
                .cancelable(true).build();

        TextView tvOpen = (TextView) builder.findViewById(R.id.tvOpen);
        TextView tvOpenTwo = (TextView) builder.findViewById(R.id.tvOpenTwo);
        if (!open) {
            tvOpen.setText("开启");
            tvOpen.setTextColor(context.getResources().getColor(R.color.white));
            tvOpen.setBackgroundResource(R.drawable.shape_bg_black);
        } else {
            tvOpen.setText("已开启");
            tvOpen.setTextColor(context.getResources().getColor(R.color.text99));
            tvOpen.setBackgroundResource(R.drawable.shape_black_border);
        }
        if (!openTwo) {
            tvOpenTwo.setText("开启");
            tvOpenTwo.setTextColor(context.getResources().getColor(R.color.white));
            tvOpenTwo.setBackgroundResource(R.drawable.shape_bg_black);
        } else {
            tvOpenTwo.setText("已开启");
            tvOpenTwo.setTextColor(context.getResources().getColor(R.color.text99));
            tvOpenTwo.setBackgroundResource(R.drawable.shape_black_border);
        }
        tvOpen.setOnClickListener(manualClick);
        tvOpenTwo.setOnClickListener(automaticClick);
        builder.show();
        return builder;
    }

    public static MaterialDialog showAccessible(final Context context, View.OnClickListener onClickListener) {
        MaterialDialog builder = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_accessibility, false)
                .cancelable(true).build();

        TextView tvOpen = (TextView) builder.findViewById(R.id.tvOpen);
        tvOpen.setOnClickListener(onClickListener);
        builder.show();
        return builder;
    }

    /**
     * 设置输入法跟随对话框弹出
     *
     * @param dialog
     * @param editText
     */
    public static void setSoftInputMode(Dialog dialog, EditText editText) {
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * 同步微销通好友dialog
     */
//    public static MaterialDialog showSyncSuccessDialog(final Context context, String content, View.OnClickListener onClickListener) {
//        MaterialDialog builder = new MaterialDialog.Builder(context)
//                .customView(R.layout.dialog_wxt_friends_sync, false)
//                .cancelable(false).build();
//
//        TextView tvContent = (TextView) builder.findViewById(R.id.tv_content);
//        builder.findViewById(R.id.iv_close).setOnClickListener(v -> builder.dismiss());
//        builder.findViewById(R.id.tv_check_friends).setOnClickListener(v -> {
//            builder.dismiss();
//            onClickListener.onClick(v);
//        });
//        tvContent.setText("本次同步" + content + "位好友");
//        builder.show();
//        return builder;
//    }

    /**
     * 微销通账号绑定信息提示
     */
//    public static MaterialDialog showWXTInfoDialog(final Context context, String title, String content, String confirmText, View.OnClickListener onClickListener, View.OnClickListener cancelOnClick) {
//        MaterialDialog builder = new MaterialDialog.Builder(context)
//                .customView(R.layout.dialog_bind_wxt_info, false)
//                .cancelable(false).build();
//
//        TextView tvTitle = (TextView) builder.findViewById(R.id.tvTitle);
//        TextView tvContent = (TextView) builder.findViewById(R.id.tvContent);
//        TextView tvConfirm = (TextView) builder.findViewById(R.id.tvConfirm);
//        if (cancelOnClick != null) {
//            builder.findViewById(R.id.tvCancel).setOnClickListener(v -> {
//                builder.dismiss();
//                cancelOnClick.onClick(v);
//            });
//        } else {
//            builder.findViewById(R.id.tvCancel).setOnClickListener(v -> builder.dismiss());
//        }
//        tvConfirm.setOnClickListener(v -> {
//            builder.dismiss();
//            onClickListener.onClick(v);
//        });
//        tvTitle.setText(title);
//        tvContent.setText(content);
//        tvConfirm.setText(confirmText);
//        builder.show();
//        return builder;
//    }



    /**
     * 显示设置描述内容的对话框
     */
//    public static MaterialDialog showSetProductDescritionDialog(Context context, String currentDes, ProductDescritionComfirmListener comfirmListener) {
//        MaterialDialog dialog = showCustomSimpleDialog(context, R.layout.dialog_set_product_description);
//        EditText etInputDescription = (EditText) dialog.findViewById(R.id.et_input_description);
//        View tvSure = dialog.findViewById(R.id.tv_sure);
//        if (StringUtils.isEmpty(currentDes)) {
//            tvSure.setClickable(false);
//            tvSure.setBackgroundResource(R.color.textCC);
//        } else {
//            etInputDescription.setText(currentDes);
//            tvSure.setClickable(true);
//            tvSure.setBackgroundResource(R.color.textMain);
//        }
//        etInputDescription.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s.toString())) {
//                    tvSure.setClickable(true);
//                    tvSure.setBackgroundResource(R.color.textMain);
//                } else {
//                    tvSure.setClickable(false);
//                    tvSure.setBackgroundResource(R.color.textCC);
//                }
//            }
//        });
//        View.OnClickListener clickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()) {
//                    case R.id.tv_sure://点击确认
//                        String inputString = etInputDescription.getText().toString().trim();
//                        comfirmListener.comfirm(inputString);
//                        dialog.dismiss();
//                        break;
//                    case R.id.iv_close_product_description://点击关闭
//                        dialog.dismiss();
//                        break;
//                }
//            }
//        };
//        dialog.findViewById(R.id.iv_close_product_description).setOnClickListener(clickListener);
//        tvSure.setOnClickListener(clickListener);
//        setSoftInputMode(dialog, etInputDescription);
//        return dialog;
//    }

    /**
     * 设置商品描述对话框的接口
     */
    public interface ProductDescritionComfirmListener {

        /**
         * 确认修改的回调
         *
         * @param content
         */
        void comfirm(String content);
    }

    private static int mSelectedItemIndex = 0;  //当前选中的item


    /**
     * 显示规格对话框
     *
     * @param context
     * @param entity
     * @param onSpecListen
     */
//    public static void showSpecDialog(Context context, MaterialEntity entity, OnSpecListen onSpecListen) {
//        MaterialDialog dialog = new MaterialDialog.Builder(context)
//                .customView(R.layout.dialog_spec_layout, false)
//                .cancelable(true)
//                .build();
//        Window window = dialog.getWindow();
//        window.setWindowAnimations(R.style.TransDialogAnim);
//        window.setGravity(Gravity.BOTTOM);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//        ImageView iv_add = (ImageView) dialog.findViewById(R.id.iv_add_spec_dialog);
//        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rv_spec_dialog);
//        TextView tv_confirm = (TextView) dialog.findViewById(R.id.tv_confirm_spec_dialog);
//        View view_empty = dialog.findViewById(R.id.view_empty_spec_dialog);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context,
//                LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(layoutManager);
//        GoodSpecAdapter adapter = new GoodSpecAdapter(entity.spec);
//        recyclerView.setAdapter(adapter);
//
//        OnSpecCreateOrUpdateListen listen = new OnSpecCreateOrUpdateListen() {
//            @Override
//            public void onCreate(int position, String spec) {
//                entity.spec.add(spec);
//                adapter.notifyItemInserted(entity.spec.size() - 1);
//                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
//            }
//
//            @Override
//            public void onUpdate(int position, String spec) {
//                entity.spec.set(position, spec);
//                adapter.notifyItemChanged(position);
//            }
//        };
//
//        view_empty.setOnClickListener(v -> dialog.dismiss());
//        iv_add.setOnClickListener(view -> showAddSpecDialog(context, 0, "", listen));
//        tv_confirm.setOnClickListener(view -> {
//            if (onSpecListen != null) {
//                onSpecListen.onChange(entity);
//            }
//            dialog.dismiss();
//        });
//
//        adapter.setOnItemChildClickListen(new GoodSpecAdapter.OnItemChildClickListen() {
//            @Override
//            public void onItemClick(View view) {
//                int pos = recyclerView.getChildAdapterPosition(view);
//                showAddSpecDialog(context, pos, entity.spec.get(pos), listen);
//            }
//
//            @Override
//            public void onItemDeleteClick(int position) {
//                if (position >= 0 && position < entity.spec.size()) {
//                    entity.spec.remove(position);
//                    adapter.notifyItemRemoved(position);
//                }
//            }
//        });
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//            @Override
//            public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
//                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//                int swipeFlags = 0;
//                return makeMovementFlags(dragFlags, swipeFlags);
//            }
//
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
//                                  @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
//                                  @NonNull @NotNull RecyclerView.ViewHolder target) {
//                //得到当拖拽的viewHolder的Position
//                int fromPosition = viewHolder.getAdapterPosition();
//                //拿到当前拖拽到的item的viewHolder
//                int toPosition = target.getAdapterPosition();
//                if (fromPosition < toPosition) {
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(entity.spec, i, i + 1);
//                    }
//                } else {
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(entity.spec, i, i - 1);
//                    }
//                }
//                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
//
//            }
//
//            @Override
//            public boolean isLongPressDragEnabled() {
//                return entity.spec.size() > 1;
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(recyclerView);
//
//        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                if (onSpecListen != null) {
//                    onSpecListen.onChange(entity);
//                }
//            }
//        });
//        dialog.show();
//    }

    /**
     * 显示添加规格对话框
     */
//    private static void showAddSpecDialog(Context context, int position, String specText,
//                                          OnSpecCreateOrUpdateListen listen) {
//        MaterialDialog dialog = showCustomDialog(context, R.layout.dialog_edit, false);
//        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close_edit_dialog);
//        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title_edit_dialog);
//        final EditText editText = (EditText) dialog.findViewById(R.id.et_edit_dialog);
//        final TextView tv_sure = (TextView) dialog.findViewById(R.id.tv_sure_edit_dialog);
//        ImageView iv_clear = (ImageView) dialog.findViewById(R.id.iv_clear_edit_dialog);
//
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (TextUtils.isEmpty(s.toString())) {
//                    tv_sure.setEnabled(false);
//                    tv_sure.setClickable(false);
//                    iv_clear.setVisibility(View.GONE);
//                } else {
//                    tv_sure.setEnabled(true);
//                    tv_sure.setClickable(true);
//                    iv_clear.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        editText.setHint("输入规格");
//        if (TextUtils.isEmpty(specText)) {
//            tv_title.setText("添加规格");
//            editText.setText("");
//        } else {
//            tv_title.setText("修改规格");
//            editText.setText(specText);
//            editText.setSelection(specText.length());
//        }
//        tv_sure.setText("确定");
//        tv_sure.setEnabled(false);
//        tv_sure.setClickable(false);
//        CommonUtil.showSoftInputMethod(editText);
//
//        iv_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonUtil.hideSoftInputMethod(context, editText);
//                dialog.dismiss();
//            }
//        });
//        iv_clear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.setText("");
//            }
//        });
//        tv_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(editText.getText().toString())) {
//                    ToastUtils.showShort("请输入规格");
//                    return;
//                }
//                if (listen != null) {
//                    if (TextUtils.isEmpty(specText)) {
//                        listen.onCreate(position, editText.getText().toString());
//                    } else {
//                        listen.onUpdate(position, editText.getText().toString());
//                    }
//                }
//                CommonUtil.hideSoftInputMethod(context, editText);
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show();
//    }


}
