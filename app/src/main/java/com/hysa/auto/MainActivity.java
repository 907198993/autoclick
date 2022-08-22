package com.hysa.auto;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.hysa.auto.activity.HomeFragment;
import com.hysa.auto.activity.ProblemFragment;
import com.hysa.auto.databinding.ActivityMainBinding;
import com.hysa.auto.model.MainModel;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainModel> {

    public static final String ACTION_BROADCAST_FREEZE_ACCOUNT = "com.fengnan.newzdzf.freeze_account";  //冻结账号

    private List<Fragment> mFragments;
    private int[] normalIcon = {R.drawable.tab_per_nor,R.drawable.tab_com_nor};
    //
    private int[] selectIcon ={R.drawable.tab_per_sel,R.drawable.tab_com_sel,};

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.mainModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        mFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        problemFragment = new ProblemFragment();
        mFragments.add(homeFragment);
        mFragments.add(problemFragment);
        String[] tabText = {
                "首页",
                "工作",
               };
        binding.navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .fragmentList(mFragments)
                .fragmentManager(getSupportFragmentManager())
                .iconSize(28)
                .navigationBackground(Color.parseColor("#ffffff"))
                .tabTextSize(12)   //Tab文字大小
                .tabTextTop(2)     //Tab文字距Tab图标的距离
                .scaleType(ImageView.ScaleType.CENTER_INSIDE)
                .navigationHeight(48)  //导航栏高度
                .lineHeight(1)         //分割线高度  默认1px
                .msgPointLeft(-8)  //调节数字消息的位置msgPointLeft msgPointTop（看文档说明）
                .msgPointTop(-15)
                .msgPointTextSize(8)  //数字消息中字体大小
                .msgPointSize(16)    //数字消息红色背景的大小
                .lineColor(Color.parseColor("#dfdfdf"))
                .normalTextColor(Color.parseColor("#333333"))   //Tab未选中时字体颜色
                .selectTextColor(Color.parseColor("#333333"))   //Tab选中时字体颜色
//                .onTabClickListener(new EasyNavigationBar.OnTabClickListener() {
//                    @Override
//                    public boolean onTabClickEvent(View view, int position) {
//                        if(position!=0 && StringUtils.isTrimEmpty(SPUtils.getInstance().getString(AppConstant.User.TOKEN))){
//                            ToastUtils.showLong("需要登录，请重新登录");
//                            ActivityUtils.startActivity(LoginActivity.class);
//                            return true;
//                        }
//                        return false;
//                    }
//                })
                .canScroll(false)
                .build();
    }

    private HomeFragment homeFragment;
    private ProblemFragment problemFragment;

    private void initFragment() {

        //默认选中第一个
    }

}