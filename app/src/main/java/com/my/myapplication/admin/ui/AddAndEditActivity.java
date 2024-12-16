package com.my.myapplication.admin.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.makeramen.roundedimageview.RoundedImageView;
import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.FileUtils;
import com.my.myapplication.utils.ImageUtils;
import com.my.myapplication.utils.LoadProgressDialog;
import com.my.myapplication.utils.PathUirls;
import com.my.myapplication.utils.PermissionUtils;
import com.my.myapplication.utils.ScreenUtil;
import com.my.myapplication.utils.global.Config;
import com.my.myapplication.video.MyVideoView;
import com.my.myapplication.video.VideoBusiness;
import com.my.myapplication.video.VideoController;

import java.io.File;
import java.time.LocalTime;
import java.util.Calendar;

//添加 编辑 页 共用
public class AddAndEditActivity extends AppCompatActivity {
    //各种权限申请回调
    public static final int PHOTO_REQUEST_CODE = 1;
    public static final int SD_REQUEST_CODE = 2;
    public static final int CAMERA_PHOTO_REQUEST_CODE = 3;

    public static final int XUANZESHIPIN = 6;
    public static final int PAISHIPIN = 7;

    //写入编辑按钮
    private Button mBtWrite;
    //存图片地址数据
    private String tupian = "";
    private String shipin = "";


    //类型 判断是添加 还是编辑修改
    private String type = "";
    //数据的id
    private Long mId;
    private BookingBean mBean;

    private TextView mTvAddimg;

    private ImageView mIvShanchu;
    private RoundedImageView mIvShipin;

    //视频播放器相关
    private RelativeLayout rl_video_container;
    private MyVideoView videoview;
    private VideoController id_video_controller;
    private VideoBusiness mVideoBusiness;

    private LinearLayout mLlvideo;
    private Spinner mSpinOne1;

    private EditText mEtDizhi;
    private EditText mEtDianhua;
    private EditText mEtQita;

    private EditText mEtHShuliang;
    private EditText mEtZShuliang;
    private ImageView mIvTupian;
    private CheckBox mCb1;
    private CheckBox mCb2;
    private CheckBox mCb3;
    private CheckBox mCb4;
    private CheckBox mCb5;
    private CheckBox mCb6;
    private CheckBox mCb7;
    private EditText mEtKaishishijian;
    private TextView mTvKaishishijian;
    private EditText mEtJieshushijian;
    private TextView mTvJieshushijian;
    private RadioButton mRb1;
    private RadioButton mRb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_edit);
        //设置ActionBar返回箭头 和 标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Backstage Management-New");

        initView();
    }

    private void initView() {
//        找控件
        mBtWrite = findViewById(R.id.bt_write);
        mTvAddimg = findViewById(R.id.tv_addimg);
        mIvShanchu = findViewById(R.id.iv_shanchu);
        mIvShipin = findViewById(R.id.iv_shipin);
        mSpinOne1 = findViewById(R.id.spin_one1);
        mEtDizhi = findViewById(R.id.et_dizhi);
        mEtDianhua = findViewById(R.id.et_dianhua);
        mEtQita = findViewById(R.id.et_qita);

        mEtHShuliang = findViewById(R.id.et_h_shuliang);
        mEtZShuliang = findViewById(R.id.et_z_shuliang);
        mIvTupian = findViewById(R.id.iv_tupian);
        mCb1 = findViewById(R.id.cb_1);
        mCb2 = findViewById(R.id.cb_2);
        mCb3 = findViewById(R.id.cb_3);
        mCb4 = findViewById(R.id.cb_4);
        mCb5 = findViewById(R.id.cb_5);
        mCb6 = findViewById(R.id.cb_6);
        mCb7 = findViewById(R.id.cb_7);
        mEtKaishishijian = findViewById(R.id.et_kaishishijian);
        mTvKaishishijian = findViewById(R.id.tv_kaishishijian);
        mEtJieshushijian = findViewById(R.id.et_jieshushijian);
        mTvJieshushijian = findViewById(R.id.tv_jieshushijian);
        mRb1 = findViewById(R.id.rb_1);
        mRb2 = findViewById(R.id.rb_2);

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

        //        选择视频点击事件
        mIvShipin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹对话框 选择
                final String[] items = {"选择视频", "拍视频"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAndEditActivity.this);
                builder.setTitle("选择方式");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
//选择视频 跳转到手机存储
                            case 0:
//                                跳转选择视频
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                                startActivityForResult(intent, XUANZESHIPIN);
                                break;
                            case 1:
                                //拍照跳 拍视频
                                intent = new Intent();
                                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);//相机action
                                startActivityForResult(intent, PAISHIPIN);
                                break;

                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        //        添加图片按钮点击
        mTvAddimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //弹对话框 是选择还是拍照
                final String[] items = {"相册", "拍照", "取消图片"};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddAndEditActivity.this);
                builder.setTitle("选择方式");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
//选择图片 跳转到相册
                            case 0:
                                intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, PHOTO_REQUEST_CODE);
                                break;
                            case 1:
                                //拍照跳 拍照
                                intent = new Intent();
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//相机action
                                startActivityForResult(intent, CAMERA_PHOTO_REQUEST_CODE);
                                break;
                            case 2:
                                tupian = "";
                                mIvTupian.setImageResource(R.mipmap.ic_launcher);
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
//删除视频按钮点击
        mIvShanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                暂停视频播放
                if (mVideoBusiness != null) {


                    mVideoBusiness.pause();

                }
//                视频地址设置为空
                shipin = "";
//                设置一下控件可见状态
                mLlvideo.setVisibility(View.GONE);
                mIvShipin.setVisibility(View.VISIBLE);
                mIvShanchu.setVisibility(View.GONE);
            }
        });


        //        设置预约开始时间按钮点击
        mTvKaishishijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 获取当前时间
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String h = hourOfDay + "";
                        String m = minute + "";
                        if (hourOfDay < 10) {
                            h = "0" + h;
                        }
                        if (minute < 10) {
                            m = "0" + m;
                        }

                        mEtKaishishijian.setText(h + ":" + m);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

//        设置预约结束时间按钮点击
        mTvJieshushijian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// 获取当前时间
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddAndEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        小时分钟不显示0处理
                        String h = hourOfDay + "";
                        String m = minute + "";
                        if (hourOfDay < 10) {
                            h = "0" + h;
                        }
                        if (minute < 10) {
                            m = "0" + m;
                        }
//                        绑定时间到控件
                        mEtJieshushijian.setText(h + ":" + m);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
        //得到类型 判断是新增 添加还是编辑
        type = getIntent().getStringExtra("type");
//根据类型判断是 编辑还是新增 编辑 按钮的是否可见
        if ("edit".equals(type)) {

            //类型是编辑
            mBtWrite.setText("edit");
            getSupportActionBar().setTitle("Backstage Management-Edit");
            //获取数据的id 只有在辑时候才会携带id数据
            mId = getIntent().getLongExtra("beanId", 0);
            //开线程 根据id查对应的数据
            new Thread(new Runnable() {
                @SuppressLint("WrongConstant")
                @Override
                public void run() {

                    try {
                        // 这里是调用耗时操作方法 调用数据方法 根据id查数据
                        mBean = MyHelper.getInstance(AddAndEditActivity.this).getDataById(mId);
                        //切换ui线程
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //不为空 则把数据显示在对应的控件上
                                if (mBean != null) {
//                                    获取类型所在的位置 绑定下拉控件 spinner控件默认选中选项
                                    mSpinOne1.setSelection(findString(getResources().getStringArray(R.array.data1), mBean.getLouceng()));
                                    mEtHShuliang.setText(mBean.getShuliangH() + "");
                                    mEtZShuliang.setText(mBean.getShuliangZ() + "");
//                                    绑定图片
                                    tupian = mBean.getTupian();
                                    if (!"".equals(mBean.getTupian())) {
                                        ImageUtils.displayImage(AddAndEditActivity.this, mIvTupian, mBean.getTupian());
                                    }
//                                    绑定星期复选按钮的选中状态
                                    if (mBean.getXingqi().contains(mCb1.getText().toString())) {
                                        mCb1.setChecked(true);
                                    } else {
                                        mCb1.setChecked(false);
                                    }
                                    if (mBean.getXingqi().contains(mCb2.getText().toString())) {
                                        mCb2.setChecked(true);
                                    } else {
                                        mCb2.setChecked(false);
                                    }

                                    if (mBean.getXingqi().contains(mCb3.getText().toString())) {
                                        mCb3.setChecked(true);
                                    } else {
                                        mCb3.setChecked(false);
                                    }

                                    if (mBean.getXingqi().contains(mCb4.getText().toString())) {
                                        mCb4.setChecked(true);
                                    } else {
                                        mCb4.setChecked(false);
                                    }
                                    if (mBean.getXingqi().contains(mCb5.getText().toString())) {
                                        mCb5.setChecked(true);
                                    } else {
                                        mCb5.setChecked(false);
                                    }

                                    if (mBean.getXingqi().contains(mCb6.getText().toString())) {
                                        mCb6.setChecked(true);
                                    } else {
                                        mCb6.setChecked(false);
                                    }

                                    if (mBean.getXingqi().contains(mCb7.getText().toString())) {
                                        mCb7.setChecked(true);
                                    } else {
                                        mCb7.setChecked(false);
                                    }
//绑定时间
                                    mEtKaishishijian.setText(mBean.getShijianKaishi());
                                    mEtJieshushijian.setText(mBean.getShijianJieshu());
//         单选按钮 开启状态
                                    if (mBean.getZhuangtai() == 0) {
                                        mRb1.setChecked(true);
                                    }

                                    if (mBean.getZhuangtai() == 1) {
                                        mRb2.setChecked(true);
                                    }
//地址电话 其他
                                    mEtDizhi.setText(mBean.getDizhi());
                                    mEtDianhua.setText(mBean.getDianhua());
                                    mEtQita.setText(mBean.getQita());


//设置视频播放内容
                                    if (mBean.getShipin() != null) {
                                        shipin = mBean.getShipin();
                                        if (shipin.contains(".")) {
                                            String suffix = shipin.substring(shipin.lastIndexOf("."));
                                            if (".mp4".equals(suffix)) {
                                                Uri uri = Uri.parse(Config.HTTP_BASE_URL + shipin);
                                                mVideoBusiness.initVideo(videoview, id_video_controller, uri);
                                                mLlvideo.setVisibility(View.VISIBLE);
                                                mIvShipin.setVisibility(View.GONE);
                                                mIvShanchu.setVisibility(View.VISIBLE);
                                            } else {
                                                Toast.makeText(AddAndEditActivity.this, "仅支持.mp4格式视频", Toast.LENGTH_SHORT).show();
                                            }
                                        }


                                    }

                                } else {
                                    Toast.makeText(AddAndEditActivity.this, "数据查找失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            }).start();
        }

//申请权限 sd存储权限 拍照权限
        requestPermissions();


//写入编辑按钮 点击事件
        mBtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //获取用户填写数据 校验数据
                String HShuliang = mEtHShuliang.getText().toString().trim();
                String ZShuliang = mEtZShuliang.getText().toString().trim();
                String Dizhi = mEtDizhi.getText().toString().trim();
                String Dianhua = mEtDianhua.getText().toString().trim();
                String Qita = mEtQita.getText().toString().trim();

                if (TextUtils.isEmpty(HShuliang)) {
                    Toast.makeText(AddAndEditActivity.this, "The number of rows cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(ZShuliang)) {
                    Toast.makeText(AddAndEditActivity.this, "The vertical quantity cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Dizhi)) {
                    Toast.makeText(AddAndEditActivity.this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Dianhua)) {
                    Toast.makeText(AddAndEditActivity.this, "Contact phone number cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("0".equals(HShuliang.charAt(0) + "")) {
                    Toast.makeText(AddAndEditActivity.this, "The number of rows must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(HShuliang) <= 0) {
                    Toast.makeText(AddAndEditActivity.this, "The number of rows must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("0".equals(ZShuliang.charAt(0) + "")) {
                    Toast.makeText(AddAndEditActivity.this, "The number of columns must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(ZShuliang) <= 0) {
                    Toast.makeText(AddAndEditActivity.this, "The number of columns must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(HShuliang) * Integer.parseInt(ZShuliang) > 1000) {
                    Toast.makeText(AddAndEditActivity.this, "The maximum number of seats exceeds the range, please re-enter the number of rows", Toast.LENGTH_SHORT).show();
                    return;
                }
//创建数据类
                BookingBean bean = new BookingBean();
                bean.setShuliangH(Integer.parseInt(HShuliang));
                bean.setShuliangZ(Integer.parseInt(ZShuliang));
                bean.setShijianKaishi(mEtKaishishijian.getText().toString().trim());
                bean.setShijianJieshu(mEtJieshushijian.getText().toString().trim());
//校验数据 开始时间是否大于结束时间
                try {

                    // 将时间字符串转换为LocalTime对象
                    LocalTime time1 = LocalTime.parse(mEtKaishishijian.getText().toString().trim());
                    LocalTime time2 = LocalTime.parse(mEtJieshushijian.getText().toString().trim());

                    // 比较两个LocalTime对象
                    int compareResult = time1.compareTo(time2);

// 输出比较结果
                    if (compareResult > 0) {
                        Toast.makeText(AddAndEditActivity.this, "The appointment start time cannot be greater than the end time", Toast.LENGTH_SHORT).show();

                        System.out.println("----------- Greater than ");
                        return;
                    } else if (compareResult < 0) {
                        System.out.println("---------- Less than ");
                    } else {
                        Toast.makeText(AddAndEditActivity.this, "The appointment start time cannot be equal to the end time", Toast.LENGTH_SHORT).show();

                        System.out.println("------------------ equal ");
                        return;
                    }
                } catch (Exception e) {
                    System.out.println("---------------------" + e.getMessage());
                    bean.setShijianKaishi("08:00");
                    bean.setShijianJieshu("20:00");
                }

//以下 绑定数据
                if (mRb1.isChecked()) {
                    bean.setZhuangtai(0);
                }

                if (mRb2.isChecked()) {
                    bean.setZhuangtai(1);
                }

                bean.setShipin(shipin);
                bean.setTupian(tupian);
                bean.setDizhi(Dizhi);
                bean.setDianhua(Dianhua);
                bean.setQita(Qita + "");
                bean.setLouceng(mSpinOne1.getSelectedItem().toString());
                String xingqi = "";
                if (mCb1.isChecked()) {
                    xingqi = xingqi + mCb1.getText().toString() + ",";
                }

                if (mCb2.isChecked()) {
                    xingqi = xingqi + mCb2.getText().toString() + ",";
                }
                if (mCb3.isChecked()) {
                    xingqi = xingqi + mCb3.getText().toString() + ",";
                }
                if (mCb4.isChecked()) {
                    xingqi = xingqi + mCb4.getText().toString() + ",";
                }
                if (mCb5.isChecked()) {
                    xingqi = xingqi + mCb5.getText().toString() + ",";
                }
                if (mCb6.isChecked()) {
                    xingqi = xingqi + mCb6.getText().toString() + ",";
                }
                if (mCb7.isChecked()) {
                    xingqi = xingqi + mCb7.getText().toString();
                }
                bean.setXingqi(xingqi);
                //判断类型 是新增 还是编辑修改
                if ("add".equals(type)) {
                    //新增 开线程
                    new Thread(new Runnable() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void run() {
                            try {
                                // 这里是调用耗时操作方法  调数据库方法插入新的数据 返回值是当前插入数据的id booking表
                                long result = MyHelper.getInstance(AddAndEditActivity.this).addData(bean);
//根据返回上方id值 循环遍历座位的数量 初始化座位信息  写入到数据库 item表  最终可根据booking里数据id 对比 item里的 tid 找到座位所有数据
                                for (int i = 0; i < Integer.parseInt(HShuliang) * Integer.parseInt(ZShuliang); i++) {
                                    ItemBean bean1 = new ItemBean();
//                                    上方返回的id
                                    bean1.setTid(result);
                                    bean1.setBianhao(i);
                                    bean1.setZhuangtai(0);
                                    bean1.setShuliangH(Integer.parseInt(HShuliang));
                                    bean1.setShuliangZ(Integer.parseInt(ZShuliang));
                                    MyHelper.getInstance(AddAndEditActivity.this).addItemData(bean1);
                                }

                                //判断结果 提示用户 销毁当前窗口
                                if (result != 0) {
                                    //切ui线程
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddAndEditActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                } else {
                                    //切ui线程
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddAndEditActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }


                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        }
                    }).start();
                }
                //编辑
                if ("edit".equals(type)) {
                    //设置id
                    bean.setId(mId);
                    new Thread(new Runnable() {
                        @SuppressLint("WrongConstant")
                        @Override
                        public void run() {
                            try {
//                                调数据库方法 删之前的座位信息 item表 根据item的tid
                                MyHelper.getInstance(AddAndEditActivity.this).delItemDataById(mId);
                                // 这里是调用耗时操作方法 调数据库方法更新数据 booking表
                                MyHelper.getInstance(AddAndEditActivity.this).updateDataById(bean);
                                for (int i = 0; i < Integer.parseInt(HShuliang) * Integer.parseInt(ZShuliang); i++) {
                                    ItemBean bean1 = new ItemBean();
                                    bean1.setTid(mId);
                                    bean1.setBianhao(i);
                                    bean1.setZhuangtai(0);
                                    bean1.setShuliangH(Integer.parseInt(HShuliang));
                                    bean1.setShuliangZ(Integer.parseInt(ZShuliang));
                                    MyHelper.getInstance(AddAndEditActivity.this).addItemData(bean1);
                                }

                                //切ui线程
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddAndEditActivity.this, "Modification successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }).start();
                }


            }
        });


    }

    public int findString(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
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

    //权限申请
    public void requestPermissions() {
        PermissionUtils.requestPermissions(this, PermissionUtils.PERMISSIONS, 1,
                new PermissionUtils.OnPermissionListener() {//实现接口方法

                    @Override
                    public void onPermissionGranted() {//获取权限成功
                        // 先判断有没有权限
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            // 先判断有没有权限
                            if (Environment.isExternalStorageManager()) {
                                //大于等于安卓11 并且同意了所有权限 做某事
                            } else {
                                //大于等于安卓11 没有权限 跳转到权限赋予界面
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SD_REQUEST_CODE);
                            }
                        } else {
                            //小等于安卓11  做某事
                        }
                    }

                    @Override
                    public void onPermissionDenied() {//获取权限失败
                        Toast.makeText(getApplicationContext(), "拒绝权限", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    //权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //再次判断如果大于等于安卓11跳到权限界面 返回是否赋予权限
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SD_REQUEST_CODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 检查是否有权限
            if (Environment.isExternalStorageManager()) {
                // 授权成功

            } else {
                Toast.makeText(this, "用户拒绝了SD存储权限", Toast.LENGTH_SHORT).show();
                // 授权失败
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            //选择照片返回 内容部分选择图片
            if (requestCode == PHOTO_REQUEST_CODE) {
                //等到选择的图片的真实地址
                String filePathFromUri = PathUirls.getFilePathFromUri(AddAndEditActivity.this, data.getData());
                //判断图片是否存在 如模拟器用as的工具删sd卡的图片的话 图片即使删除 但是模拟器相册中还是会显示
                if (!new File(filePathFromUri).exists()) {
                    new AlertDialog.Builder(AddAndEditActivity.this)//绑定当前窗口
                            .setCancelable(false)
                            .setTitle("错误信息")//设置标题
                            .setMessage("图片不存在,请重新选择,或者长按模拟器电源键选择restart重启模拟器,不是直接poweroff")//设置提示细信息
                            .setIcon(R.mipmap.ic_launcher)//设置图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })//添加确定按钮
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })//添加取消按钮
                            .create()//创建对话框
                            .show();//显示对话框
                    return;
                }
                //判断选择图片的大小
                long fileSize = FileUtils.getFileSize(filePathFromUri);
                //如果超过限制 则弹对话框提示用户
                if (fileSize / 1024 > FileUtils.IMAGE_MAX_SIZE) {
                    new AlertDialog.Builder(AddAndEditActivity.this)//绑定当前窗口
                            .setCancelable(false)
                            .setTitle("错误信息")//设置标题
                            .setMessage("图片大小最大限制" + FileUtils.IMAGE_MAX_SIZE + "kb")//设置提示细信息
                            .setIcon(R.mipmap.ic_launcher)//设置图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })//添加确定按钮
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })//添加取消按钮
                            .create()//创建对话框
                            .show();//显示对话框
                } else {
                    try {
                        //选择图片后处理 图片大小没有超过限制
                        tupian = FileUtils.getPath(filePathFromUri);
                        //                    上传图片到服务器 服务器返回图片地址 显示到控件
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    // 这里是调用耗时操作方法
                                    String imgUrl = MyHelper.getInstance(AddAndEditActivity.this).oneUploadFile(tupian);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //                    内容部分拍摄图片后 绑定数据 更新界面
                                            tupian = imgUrl;
                                            ImageUtils.displayImage(AddAndEditActivity.this, mIvTupian, tupian);
                                        }
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }).start();
                    } catch (Exception e) {
                        Toast.makeText(this, "图片处理错误，请重新选择图片", Toast.LENGTH_SHORT).show();
                    }

                }

            }


            //拍照回调 返回 逻辑同上 内容部分拍摄图片
            if (requestCode == CAMERA_PHOTO_REQUEST_CODE) {
                /**
                 * 通过这种方法取出的拍摄会默认压缩，因为如果相机的像素比较高拍摄出来的图会比较高清，
                 * 如果图太大会造成内存溢出（OOM），因此此种方法会默认给图片尽心压缩
                 */


                String takePhotoPath = PathUirls.getTakePhotoPath(this, data);
                long fileSize = FileUtils.getFileSize(takePhotoPath);
                if (fileSize / 1024 > FileUtils.IMAGE_MAX_SIZE) {
                    new AlertDialog.Builder(AddAndEditActivity.this)//绑定当前窗口
                            .setCancelable(false)
                            .setTitle("错误信息")//设置标题
                            .setMessage("图片大小最大限制" + FileUtils.IMAGE_MAX_SIZE + "kb")//设置提示细信息
                            .setIcon(R.mipmap.ic_launcher)//设置图标
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })//添加确定按钮
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            })//添加取消按钮
                            .create()//创建对话框
                            .show();//显示对话框
                } else {
                    try {
                        tupian = FileUtils.getPath(takePhotoPath);


                        //                    上传图片到服务器 服务器返回图片地址 显示到控件
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 这里是调用耗时操作方法
                                    String imgUrl = MyHelper.getInstance(AddAndEditActivity.this).oneUploadFile(tupian);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //                    内容部分拍摄图片后 绑定数据 更新界面
                                            tupian = imgUrl;
                                            ImageUtils.displayImage(AddAndEditActivity.this, mIvTupian, tupian);
                                        }
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }).start();

                    } catch (Exception e) {
                        Toast.makeText(this, "图片处理错误，请重新选择图片", Toast.LENGTH_SHORT).show();
                    }

                }


            }


            //拍视频或选择视频
            if (requestCode == PAISHIPIN || requestCode == XUANZESHIPIN) {
                String filePathFromUri = PathUirls.getFilePathFromUri(this, data.getData());
                if (filePathFromUri.contains(".")) {
                    String suffix = filePathFromUri.substring(filePathFromUri.lastIndexOf("."));
                    if (".mp4".equals(suffix)) {
                        //创建LoadProgressDialog
                        LoadProgressDialog loadProgressDialog = new LoadProgressDialog(AddAndEditActivity.this, "加载中……", false);
                        loadProgressDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    // 这里是调用耗时操作方法
                                    String imgUrl = MyHelper.getInstance(AddAndEditActivity.this).oneUploadFile(filePathFromUri);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shipin = imgUrl;
                                            Uri uri = Uri.parse(Config.HTTP_BASE_URL + imgUrl);

                                            mVideoBusiness.initVideo(videoview, id_video_controller, uri);
                                            mLlvideo.setVisibility(View.VISIBLE);
                                            mIvShipin.setVisibility(View.GONE);
                                            mIvShanchu.setVisibility(View.VISIBLE);
                                            clearFocus();
                                            loadProgressDialog.dismiss();
                                        }
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AddAndEditActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        }).start();


                    } else {
                        Toast.makeText(this, "仅支持.mp4后缀视频", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "仅支持.mp4后缀视频", Toast.LENGTH_SHORT).show();
                }

            }

        } else {

            Toast.makeText(this, "没有选择内容", Toast.LENGTH_SHORT).show();

        }

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
    }

    //界面重新可见 继续播放视频
    @Override
    protected void onResume() {
        super.onResume();
        clearFocus();
        if (mVideoBusiness != null) {

            if (mVideoBusiness.isPause()) {
                mVideoBusiness.resume();
            }


        }

    }

    public void clearFocus() {
        mEtQita.clearFocus();
        mEtDianhua.clearFocus();
        mEtDizhi.clearFocus();

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