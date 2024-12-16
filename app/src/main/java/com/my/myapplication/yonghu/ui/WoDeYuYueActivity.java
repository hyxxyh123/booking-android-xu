package com.my.myapplication.yonghu.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.MessageEvent;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//查自己预约信息界面
public class WoDeYuYueActivity extends AppCompatActivity {

    //定义变量
    private TextView mTvMsg;
    private Button mBtQuxiao;
    private TextView mTvYuyueshijian;
    private Button mBtXuyue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wo_de_yu_yue);
        //设置ActionBar返回箭头 和 标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Appointment");

        initView();
//        注册EventBus 跨界面发送接收消息用
        EventBus.getDefault().register(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 这里是调用耗时操作方法 查当前注册的账号是否有预约信息
                    List<ItemBean> usernameItemBean = MyHelper.getInstance(WoDeYuYueActivity.this).getItemDataByUserName(SpUtils.getInstance(WoDeYuYueActivity.this).getString("username"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            如果没有预约 设置按钮的文字和点击事件
                            if (usernameItemBean.size() == 0) {
                                mTvMsg.setText("No reservations yet");
                                mBtQuxiao.setText("return");
                                mBtXuyue.setVisibility(View.GONE);
                                mBtQuxiao.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finish();
                                    }
                                });
                            } else {
//                                如果有预约信息 再根据预约信息查详情
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法 根据预约信息查详情
                                            BookingBean dataById = MyHelper.getInstance(WoDeYuYueActivity.this).getDataById(usernameItemBean.get(0).getTid());
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
//将预约信息显示到界面
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    String formattedDate = sdf.format(new Date(Long.parseLong(usernameItemBean.get(0).getYuyueshijian()) + 3600000));

                                                    mTvMsg.setText("You have reserved a seat:" + dataById.getLouceng() + ",Seat number: " + (usernameItemBean.get(0).getBianhao() + 1) + "\nValid until: " + formattedDate + "If you need to renew the seat, please click the renew button below. If it is reserved by someone else after the expiration date, please return the right to use it and let others use it.！");
                                                    mBtQuxiao.setText("Cancel Appointment");
                                                    mBtXuyue.setVisibility(View.VISIBLE);
//                                                    绑定点击事件 续约
                                                    mBtXuyue.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            new AlertDialog.Builder(WoDeYuYueActivity.this)//绑定当前窗口
                                                                    .setTitle("hint")//设置标题
                                                                    .setMessage("Confirm to renew？")//设置提示细信息
                                                                    .setIcon(R.mipmap.ic_launcher)//设置图标
                                                                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            //点确定按钮 开线程 删数据
                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {

                                                                                    try {
                                                                                        // 这里是调用耗时操作方法 重新更新 当前预约的数据
                                                                                        MyHelper.getInstance(WoDeYuYueActivity.this).updateItemZhuangTaiAndUserNameById(usernameItemBean.get(0).getId(), 1, SpUtils.getInstance(WoDeYuYueActivity.this).getString("username"), System.currentTimeMillis() + "");

                                                                                        WoDeYuYueActivity.this.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(WoDeYuYueActivity.this, "Renewal successful", Toast.LENGTH_SHORT).show();

                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e) {
                                                                                        WoDeYuYueActivity.this.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(WoDeYuYueActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
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
                                                    });
//取消预约 更新数据库里数据
                                                    mBtQuxiao.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            new AlertDialog.Builder(WoDeYuYueActivity.this)//绑定当前窗口
                                                                    .setTitle("hint")//设置标题
                                                                    .setMessage("Confirm the cancellation of the previous reservation？")//设置提示细信息
                                                                    .setIcon(R.mipmap.ic_launcher)//设置图标
                                                                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            //点确定按钮 开线程 删数据
                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {

                                                                                    try {
                                                                                        // 这里是调用耗时操作方法
                                                                                        MyHelper.getInstance(WoDeYuYueActivity.this).updateItemZhuangTaiAndUserNameById(usernameItemBean.get(0).getId(), 0, "", "0");

                                                                                        WoDeYuYueActivity.this.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(WoDeYuYueActivity.this, "Cancel Success", Toast.LENGTH_SHORT).show();
                                                                                                finish();
                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e) {
                                                                                        WoDeYuYueActivity.this.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(WoDeYuYueActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
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
                                                    });
                                                }
                                            });
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(WoDeYuYueActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(WoDeYuYueActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }

    //接收eventbus发送的消息 处理预约剩余时间
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getFlag() == 0) {
            Long yuyueshijian = event.getYuyueshijian();
            long l = 60 - ((System.currentTimeMillis() - yuyueshijian) / 1000);
            mTvYuyueshijian.setVisibility(View.VISIBLE);
            if (l < 0) {
                l = 0;
            }
            mTvYuyueshijian.setText("Simulate the remaining return time," + l + "seconds, the system will automatically reclaim the right to use the seat. If you need to renew, please click the seat again to make a reservation。");

        }
        if (event.getFlag() == 1) {
            mTvYuyueshijian.setText("");
            mTvYuyueshijian.setVisibility(View.GONE);
            mTvMsg.setText("No reservations yet");
        }


    }

    //返回箭头点击事件 销毁退出页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return true;
    }


    private void initView() {
        mTvMsg = findViewById(R.id.tv_msg);
        mBtQuxiao = findViewById(R.id.bt_quxiao);
        mTvYuyueshijian = findViewById(R.id.tv_yuyueshijian);
        mBtXuyue = findViewById(R.id.bt_xuyue);
    }

    @Override
    protected void onDestroy() {
//        注销eventbus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
