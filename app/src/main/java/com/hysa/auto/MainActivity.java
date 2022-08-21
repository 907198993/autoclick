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
    private int[] normalIcon = {R.mipmap.server_menu_unhome,R.mipmap.server_menu_uncontact};
    //
    private int[] selectIcon ={R.mipmap.server_menu_home,R.mipmap.server_menu_contact,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return 0;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        initFragment();
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
                .normalTextColor(Color.parseColor("#999999"))   //Tab未选中时字体颜色
                .selectTextColor(Color.parseColor("#0189FF"))   //Tab选中时字体颜色
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
        mFragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        problemFragment = new ProblemFragment();
        mFragments.add(homeFragment);
        mFragments.add(problemFragment);
        //默认选中第一个
    }

}