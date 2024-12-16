package com.my.myapplication.admin.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
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

import com.my.myapplication.admin.adapter.AdminChaKanAdapter;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

//管理查看详情页
public class AdminChaKanActivity extends AppCompatActivity {

    private RecyclerView mRecyclerview;
    private AdminChaKanAdapter mAdapter;
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
        getSupportActionBar().setTitle("Backstage management-Details");
        //获取数据的id 只有在查看和编辑时候才会携带id数据
        mId = getIntent().getLongExtra("beanId", 0);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
//        找控件
        TextView tv_msg = findViewById(R.id.tv_msg);
        tv_msg.setText("Click on the seat to modify the status");
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

    //接收AdminMainActivity里EventBus发的消息 更具界面 主要更新座位的使用请情况 如预约的时间到了 将更新数据库 在此页面再查询数据库 更新界面
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
//        有预约
        if (event.getFlag() == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        // 这里是调用耗时操作方法 查询当前登录的账号是否有预约
                        List<ItemBean> usernameItemBean = MyHelper.getInstance(AdminChaKanActivity.this).getItemDataByUserName(SpUtils.getInstance(AdminChaKanActivity.this).getString("username"));
                        if (usernameItemBean.size() > 0) {
//                            有预约 再查预约的信息
                            BookingBean dataById = MyHelper.getInstance(AdminChaKanActivity.this).getDataById(usernameItemBean.get(0).getTid());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    计算预约剩余时间 结果文本形式显示到界面
                                    Long yuyueshijian = event.getYuyueshijian();
                                    long l = 60 - ((System.currentTimeMillis() - yuyueshijian) / 1000);
                                    tv_yuyueshijian.setVisibility(View.VISIBLE);
                                    if (l < 0) {
                                        l = 0;
                                    }
                                    tv_yuyueshijian.setText("You have reserved a seat:" + dataById.getLouceng() + ",Seat number: " + (usernameItemBean.get(0).getBianhao() + 1) + " \nSimulate the remaining return time," + l + "seconds, the system will automatically reclaim the right to use the seat. If you need to renew, please click the seat again to renew.");

                                }
                            });
                        }

                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).start();

        }
//        没有预约
        if (event.getFlag() == 1) {
            tv_yuyueshijian.setText("");
            tv_yuyueshijian.setVisibility(View.GONE);
        }

//        有预约或没有预约 都重新查数据库数据更新界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 这里是调用耗时操作方法
                    mBeanList = MyHelper.getInstance(AdminChaKanActivity.this).getItemDataById(mId);

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
                            Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();

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
                    mBean = MyHelper.getInstance(AdminChaKanActivity.this).getDataById(mId);
                    //切换ui线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //不为空 则把数据显示在对应的控件上
                            if (mBean != null) {

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
                                            Toast.makeText(AdminChaKanActivity.this, "仅支持.mp4格式视频", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
//查座位数据 有多少个座位
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法
                                            mBeanList = MyHelper.getInstance(AdminChaKanActivity.this).getItemDataById(mId);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //设置Recyclerview 布局管理器
                                                    try {
                                                        if (mAdapter == null) {
                                                            mAdapter = new AdminChaKanAdapter(AdminChaKanActivity.this, mBeanList, mId, mBean.getTupian());
                                                            mRecyclerview.setLayoutManager(new GridLayoutManager(AdminChaKanActivity.this, mBeanList.get(0).getShuliangH()));
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
                                                    Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }).start();


    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    //菜单栏 点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        返回箭头
        if (android.R.id.home == item.getItemId()) {
            finish();
        }

//        修改按钮
        if (item.getItemId() == R.id.id1) {
            Intent intent = new Intent(AdminChaKanActivity.this, AddAndEditActivity.class);
            intent.putExtra("type", "edit");
            intent.putExtra("beanId", mId);
            startActivity(intent);
            finish();
        }
//删除按钮
        if (item.getItemId() == R.id.id2) {
            new AlertDialog.Builder(AdminChaKanActivity.this)//绑定当前窗口
                    .setTitle("hint")//设置标题
                    .setMessage("Are you sure you want to delete?")//设置提示细信息
                    .setIcon(R.mipmap.ic_launcher)//设置图标
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点确定按钮 开线程 删数据
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        // 这里是调用耗时操作方法 调数据库删数据 根据id
                                        int result = MyHelper.getInstance(AdminChaKanActivity.this).delDataById(mId);
                                        MyHelper.getInstance(AdminChaKanActivity.this).delItemDataById(mId);
                                        //切ui线程  判断删除结果 更新界面
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (result != -1) {
                                                        Toast.makeText(AdminChaKanActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(AdminChaKanActivity.this, "Deletion failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    })//取消按钮什么都不做
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })//添加取消按钮
                    .create()//创建对话框
                    .show();//显示对话框
        }
        if (item.getItemId() == R.id.id3) {
            new AlertDialog.Builder(AdminChaKanActivity.this)//绑定当前窗口
                    .setTitle("hint")//设置标题
                    .setMessage("Are you sure you want to reset all scheduled appointments to free status?")//设置提示细信息
                    .setIcon(R.mipmap.ic_launcher)//设置图标
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //点确定按钮 开线程 删数据
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        // 这里是调用耗时操作方法 调数据库删数据 根据id
                                        MyHelper.getInstance(AdminChaKanActivity.this).resetZhuangTai(mId);
                                        mBeanList = MyHelper.getInstance(AdminChaKanActivity.this).getItemDataById(mId);

                                        //切ui线程  判断删除结果 更新界面
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.upDate(mBeanList);
                                            }
                                        });

                                    } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AdminChaKanActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    })//取消按钮什么都不做
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })//添加取消按钮
                    .create()//创建对话框
                    .show();//显示对话框
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