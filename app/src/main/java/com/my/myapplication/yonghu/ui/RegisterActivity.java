package com.my.myapplication.yonghu.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.my.myapplication.bean.UserBean;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;

//注册
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    //定义变量
    private EditText mEtName;
    private EditText mEtPassword;
    private EditText mEtPasswordAgain;
    private Button mBtRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //设置ActionBar返回箭头 和 标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("注册");
        initView();
    }

    private void initView() {
        //查找控件id 注册点击事件
        mEtName = findViewById(R.id.et_name);
        mEtPassword = findViewById(R.id.et_password);
        mEtPasswordAgain = findViewById(R.id.et_password_again);
        mBtRegister = findViewById(R.id.bt_register);
        mBtRegister.setOnClickListener(this);
    }

    //返回箭头点击事件 销毁退出页面
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return true;
    }

    //注册按钮点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                register();
                break;
        }
    }

    //注册逻辑
    private void register() {
//登录注册账号密码
        String userName = mEtName.getText().toString().trim();
        String userPassword = mEtPassword.getText().toString().trim();
        String userPasswordAgain = mEtPasswordAgain.getText().toString().trim();
        // 判断是为空 如果有一个时空 则退出这个函数 不再执行以下代码
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(userPasswordAgain)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!userPassword.equals(userPasswordAgain)) {
            Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        //开个线程进行注册
        new Thread(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {

                try {
                    // 这里是调用耗时操作方法 根据用户名查找用户是否被注册
                    if (MyHelper.getInstance(RegisterActivity.this).isRegister(userName)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //如果被注册 则注册失败 并提示用户
                                Toast.makeText(RegisterActivity.this, "注册失败，账号已被注册", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        //如果没有被注册 则创建个用户类
                        UserBean user = new UserBean();
                        user.setUsername(userName);
                        user.setPassword(userPassword);
                        user.setGuanli(0);
                        //调用数据库方法 将注册数据插入数据库 并判断插入返回值
                        long result = MyHelper.getInstance(RegisterActivity.this).addUser(user);
                        if (result != -1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //注册成功提示用户 销毁界面 退出界面
                                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //注册失败提示用户
                                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }


                    }

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        }).start();

    }

}
