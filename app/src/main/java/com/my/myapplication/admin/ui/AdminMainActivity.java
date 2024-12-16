package com.my.myapplication.admin.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.my.myapplication.admin.fragment.Home_item_admin;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.MessageEvent;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//后台管理主界面
public class AdminMainActivity extends AppCompatActivity {
    //定义变量
    private final ArrayList<Fragment> tabFragmentList = new ArrayList<>();

    private String[] mTitles_3;
    private TabLayout mTablayout;
    private ViewPager mViewpager;
    private boolean run = true;
    private TextView mTvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin_yonghu);
//        设置标题
        getSupportActionBar().setTitle("Backstage Management");
//        初始化控件
        initView();
//        定时循环查询 到期的预约 如果到期则用EventBus发个消息出去 在AdminChaKanActivity里接收消息更新ui
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (run) {
                    try {
//                        查到期预约 如果到期 将更新数据库里的值
                        List<ItemBean> allYuYue = MyHelper.getInstance(AdminMainActivity.this).getAllYuYue();
                        for (ItemBean bean : allYuYue) {
                            if (System.currentTimeMillis() - Long.parseLong(bean.getYuyueshijian()) > 1000 * 60) {
                                MyHelper.getInstance(AdminMainActivity.this).updateItemZhuangTaiAndUserNameById(bean.getId(), 0, "", "0");
                            }

                        }
//查当前登录账号有没有预约 如果有则 把到期时间通过EventBus发送出去 在AdminChaKanActivity里接收消息更新ui 如果没有则只发消失 flag 0 1区分 有没有预约
                        List<ItemBean> username = MyHelper.getInstance(AdminMainActivity.this).getItemDataByUserName(SpUtils.getInstance(AdminMainActivity.this).getString("username"));
                        if (username.size() > 0) {
                            EventBus.getDefault().post(new MessageEvent(Long.parseLong(username.get(0).getYuyueshijian()), 0));
                        } else {
                            EventBus.getDefault().post(new MessageEvent(0L, 1));
                        }
                        Thread.sleep(1000 * 3);
                    } catch (Exception e) {

                    }

                }

            }
        }).start();
//开线程 显示当前的时间和星期
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (run) {
                    try {
                        // 这里是调用耗时操作方法
                        // 获取当前日期时间
                        Calendar calendar = Calendar.getInstance();

                        // 创建一个24小时格式的SimpleDateFormat
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                        // 获取当前时间
                        String currentTime = timeFormat.format(calendar.getTime());


                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                        String weekday;
                        switch (dayOfWeek) {
                            case Calendar.SUNDAY:
                                weekday = "Sunday";
                                break;
                            case Calendar.MONDAY:
                                weekday = "Monday";
                                break;
                            case Calendar.TUESDAY:
                                weekday = "Tuesday";
                                break;
                            case Calendar.WEDNESDAY:
                                weekday = "Wednesday";
                                break;
                            case Calendar.THURSDAY:
                                weekday = "Thursday";
                                break;
                            case Calendar.FRIDAY:
                                weekday = "Friday";
                                break;
                            case Calendar.SATURDAY:
                                weekday = "Saturday";
                                break;
                            default:
                                weekday = "unknown";
                                break;
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvTime.setText("Current time: " + currentTime + " Today is: " + weekday);
                            }
                        });
                        Thread.sleep(500);
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminMainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }


            }
        }).start();

    }

    //查找控件 绑定点击事件
    private void initView() {
//        找控件
        mViewpager = findViewById(R.id.viewpager);
        mTablayout = findViewById(R.id.tablayout);
        mTitles_3 = getResources().getStringArray(R.array.data1);
//        追加全部类型 页面
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(mTitles_3));
        list.add(0, "全部");
        mTitles_3 = list.toArray(new String[list.size()]);

//        获取分类数据
        if (mTitles_3.length == 0) {
            Toast.makeText(this, "Resource file classification data cannot be empty", Toast.LENGTH_SHORT).show();
        }
//        根据分类的数据 初始化页面
        for (int i = 0; i < mTitles_3.length; i++) {
            Home_item_admin home_item1 = Home_item_admin.newInstance(mTitles_3[i], "1");
            tabFragmentList.add(home_item1);
        }
//        绑定 viewpager fragment Tablelayout
        mViewpager.setOffscreenPageLimit(tabFragmentList.size());
        mViewpager.setAdapter(new MPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, tabFragmentList));
        mTablayout.setupWithViewPager(mViewpager);

        mTvTime = findViewById(R.id.tv_time);
    }


    //    viewpager适配器
    class MPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList;

        public MPagerAdapter(FragmentManager fm, int behavior, List<Fragment> fragmentList) {
            super(fm, behavior);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        //返回tablayout的标题文字;
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles_3[position];
        }
    }

    @Override
    protected void onDestroy() {
        run = false;
        super.onDestroy();
    }
}