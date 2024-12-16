package com.my.myapplication.yonghu.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.MessageEvent;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.ScreenUtil;
import com.my.myapplication.utils.SpUtils;
import com.my.myapplication.utils.global.Config;
import com.my.myapplication.video.MyVideoView;
import com.my.myapplication.video.VideoBusiness;
import com.my.myapplication.video.VideoController;
import com.my.myapplication.yonghu.adapter.YongHuChaKanAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

//用户查看详情页
public class YongHuChaKanActivity extends AppCompatActivity {
    //定义变量
    private RecyclerView mRecyclerview;
    private YongHuChaKanAdapter mAdapter;
    private List<ItemBean> mBeanList = new ArrayList<>();
    private BookingBean mBean;
    private Long mId;
    //视频播放器相关
    private RelativeLayout rl_video_container;
    private MyVideoView videoview;
    private VideoController id_video_controller;
    private VideoBusiness mVideoBusiness;

    private LinearLayout mLlvideo;
    private TextView mTvLongceng;
    private TextView tv_yuyueshijian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cha_kan_admin_yonghu);
        //设置ActionBar返回箭头 和 标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details");
        //获取数据的id 只有在查看和编辑时候才会携带id数据
        mId = getIntent().getLongExtra("beanId", 0);
        initView();

        EventBus.getDefault().register(this);
    }

    private void initView() {
//        找控件
        TextView tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setText("Click on a seat to reserve");
        tv_yuyueshijian = findViewById(R.id.tv_yuyueshijian);

        mRecyclerview = findViewById(R.id.recyclerview);
        //视频播放器相关
        mLlvideo = findViewById(R.id.llvideo);


        rl_video_container = findViewById(R.id.rl_video_container);
        videoview = findViewById(R.id.id_videoview);
        id_video_controller = findViewById(R.id.id_video_controller);


        id_video_controller.getTv_title().setText("This is the title");
        id_video_controller.getIv_fan().setVisibility(View.INVISIBLE);
        id_video_controller.getId_fl_video_expand().setVisibility(View.INVISIBLE);
        id_video_controller.getTv_title().setVisibility(View.GONE);
        //        VideoView 只支持播放.mp4和.3GP格式

        mVideoBusiness = new VideoBusiness(this);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int screenWidth = ScreenUtil.getScreenWidth(this);
        setVideoContainerParam(screenWidth, screenWidth * 9 / 16);
        mTvLongceng = findViewById(R.id.tv_longceng);


    }

    //接收YongHuMainActivity EventBus发的消息 更新界面数据 主要更新座位的使用请情况 如预约的时间到了 将更新数据库 在此页面再查询数据库 更新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
//        YongHuMainActivity 查询到有用户预约 用EventBus发送的数据
        if (event.getFlag() == 0) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 这里是调用耗时操作方法 查当前登录账号的预约信息
                        List<ItemBean> usernameItemBean = MyHelper.getInstance(YongHuChaKanActivity.this).getItemDataByUserName(SpUtils.getInstance(YongHuChaKanActivity.this).getString("username"));
//                        如果有预约
                        if (usernameItemBean.size() > 0) {
//                            查预约详情
                            BookingBean dataById = MyHelper.getInstance(YongHuChaKanActivity.this).getDataById(usernameItemBean.get(0).getTid());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    计算预约剩余时间和详情 将结果绑定到界面
                                    Long yuyueshijian = event.getYuyueshijian();
                                    long l = 60 - ((System.currentTimeMillis() - yuyueshijian) / 1000);
                                    tv_yuyueshijian.setVisibility(View.VISIBLE);
                                    if (l < 0) {
                                        l = 0;
                                    }
                                    tv_yuyueshijian.setText("You have reserved a seat:" + dataById.getLouceng() + ",Seat number: " + (usernameItemBean.get(0).getBianhao() + 1) + " \nSimulate the remaining return time," + l + "seconds, the system will automatically reclaim the right to use the seat. If you need to renew, please click the seat again to renew.。");

                                }
                            });
                        }

                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YongHuChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).start();

        }
        //        YongHuMainActivity 查询到没有有用户预约 用EventBus发送的数据
        if (event.getFlag() == 1) {
            tv_yuyueshijian.setText("");
            tv_yuyueshijian.setVisibility(View.GONE);
        }
//在YongHuMainActivity中使用EventBus发的消息 提示是否可预约的提示 如当前时间是否在预约范围内 当前的星期是否在预约范围内 管理是否关闭了预约等提示信息
        if (event.getFlag() == 2) {
            Toast.makeText(YongHuChaKanActivity.this, "No reservations are available. The management has closed the open or unavailable reservations on certain days of the week.", Toast.LENGTH_SHORT).show();
        }
//在YongHuMainActivity中使用EventBus发的消息 提示是否可预约的提示 如当前时间是否在预约范围内 当前的星期是否在预约范围内 管理是否关闭了预约等提示信息

        if (event.getFlag() == 6) {

            Toast.makeText(YongHuChaKanActivity.this, "Not within the scheduled week", Toast.LENGTH_SHORT).show();
        }
//在YongHuMainActivity中使用EventBus发的消息 提示是否可预约的提示 如当前时间是否在预约范围内 当前的星期是否在预约范围内 管理是否关闭了预约等提示信息

        if (event.getFlag() == 3 || event.getFlag() == 4) {
            Toast.makeText(YongHuChaKanActivity.this, "No reservation is available, not in the reservation time period", Toast.LENGTH_SHORT).show();
        }
//在YongHuMainActivity中使用EventBus发的消息 提示是否可预约的提示 暂停使用状态

        if (event.getFlag() == 5) {
            Toast.makeText(YongHuChaKanActivity.this, "No reservations, temporarily unavailable", Toast.LENGTH_SHORT).show();
        }
//        YongHuMainActivity  用EventBus发送的数据 查预约状态 有的预约到期 会在YongHuMainActivity处理 然后再此界面中重新查数据 更新界面
        if (event.getFlag() == 0 || event.getFlag() == 1) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 这里是调用耗时操作方法
                        mBeanList = MyHelper.getInstance(YongHuChaKanActivity.this).getItemDataById(mId);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //设置Recyclerview 布局管理器
                                try {
                                    if (mAdapter != null) {
                                        mAdapter.upDate(mBeanList);
                                    }

                                } catch (Exception e) {

                                }

                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YongHuChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).start();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoBusiness != null) {

            if (mVideoBusiness.isPause()) {
                mVideoBusiness.resume();
            }


        }

        //开线程 根据id查对应的数据
        new Thread(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {

                try {
                    // 这里是调用耗时操作方法 调用数据方法 根据id查数据
                    mBean = MyHelper.getInstance(YongHuChaKanActivity.this).getDataById(mId);
                    //切换ui线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //不为空 则把数据显示在对应的控件上
                            if (mBean != null) {
//绑定数据
                                mTvLongceng.setText(mBean.getLouceng());


//设置视频播放内容
                                if (mBean.getShipin() != null) {
                                    String shipin = mBean.getShipin();
                                    if (shipin.contains(".")) {
                                        String suffix = shipin.substring(shipin.lastIndexOf("."));
                                        if (".mp4".equals(suffix)) {
                                            Uri uri = Uri.parse(Config.HTTP_BASE_URL + shipin);
                                            mVideoBusiness.initVideo(videoview, id_video_controller, uri);
                                            mLlvideo.setVisibility(View.VISIBLE);

                                        } else {
                                            Toast.makeText(YongHuChaKanActivity.this, "仅支持.mp4格式视频", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
//查座位数据
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法 查所有的座位数据
                                            mBeanList = MyHelper.getInstance(YongHuChaKanActivity.this).getItemDataById(mId);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //设置Recyclerview 布局管理器 绑定到recyclerview列表控件中 显示到界面
                                                    try {
                                                        if (mAdapter == null) {
                                                            mAdapter = new YongHuChaKanAdapter(YongHuChaKanActivity.this, mBeanList, mId, mBean.getTupian());
                                                            mRecyclerview.setLayoutManager(new GridLayoutManager(YongHuChaKanActivity.this, mBeanList.get(0).getShuliangH()));
                                                            mRecyclerview.setAdapter(mAdapter);
                                                        } else {
                                                            mAdapter.upDate(mBeanList);
                                                        }

                                                    } catch (Exception e) {

                                                    }

                                                }
                                            });
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(YongHuChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).start();

                            }

                        }
                    });


                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(YongHuChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }).start();


    }


    //菜单栏 点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        返回箭头
        if (android.R.id.home == item.getItemId()) {
            finish();
        }


        return true;
    }

    //视频比例
    private void setVideoContainerParam(int w, int h) {
        ViewGroup.LayoutParams params = rl_video_container.getLayoutParams();
        params.width = w;
        params.height = h;
        rl_video_container.setLayoutParams(params);
    }

    //退出 释放视频资源
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoBusiness != null) {
            mVideoBusiness.release();
        }
        EventBus.getDefault().unregister(this);
    }

    //界面不可见 暂停播放视频
    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoBusiness != null) {


            mVideoBusiness.pause();

        }
    }
}