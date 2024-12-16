package com.my.myapplication.admin.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.my.myapplication.admin.ui.AdminChaKanActivity;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.ImageUtils;
import com.my.myapplication.utils.SpUtils;

import java.util.List;

//管理查看页recyclerview列表控件适配器
public class AdminChaKanAdapter extends RecyclerView.Adapter<AdminChaKanAdapter.VH> {
    private final Context context;
    private List<ItemBean> mBeanList;
    private Long tid;
    private String tupian = "";

    //构造方法 初始化数据
    public AdminChaKanAdapter(Context context, List<ItemBean> beanList, Long tid, String tupian) {
        this.context = context;
        this.mBeanList = beanList;
        this.tid = tid;
        this.tupian = tupian;

    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法 加载布局
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chakan, parent, false);
        return new VH(v);
    }

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(final VH holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        holder.tv_bianhao.setText("seat" + (position + 1));
        if ("".equals(tupian)) {
            holder.iv_img.setImageResource(R.mipmap.zuowei);
        } else {
            ImageUtils.displayImage(context, holder.iv_img, tupian);
        }


        switch (mBeanList.get(position).getZhuangtai()) {
            case 0:

                holder.tv_zhuangtai.setText("idle");
                holder.tv_zhuangtai.setTextColor(Color.BLACK);
                holder.tv_bianhao.setTextColor(Color.BLACK);
                break;
            case 1:
                if (mBeanList.get(position).getUsername().equals(SpUtils.getInstance(context).getString("username"))) {
                    holder.tv_zhuangtai.setText("My Appointment");
                    holder.tv_zhuangtai.setTextColor(Color.BLUE);
                    holder.tv_bianhao.setTextColor(Color.BLUE);
                } else {
                    holder.tv_zhuangtai.setText("Already booked");
                    holder.tv_zhuangtai.setTextColor(Color.BLUE);
                    holder.tv_bianhao.setTextColor(Color.BLUE);
                }

                break;
            case 2:
                holder.tv_zhuangtai.setText("Suspend use");
                holder.tv_zhuangtai.setTextColor(Color.RED);
                holder.tv_bianhao.setTextColor(Color.RED);
                break;
        }


        //条目点击事件  id
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹对话框 是选择还是拍照
                    final String[] items = {"idle", "Occupancy", "Damage repair"};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Selection method");
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AdminChaKanActivity chaKanActivity = (AdminChaKanActivity) context;
                        switch (which) {
                            case 0:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法
                                            MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 0, "", "0");
                                            mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } catch (Exception e) {
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).start();
                                break;
                            case 1:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法
                                            MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 1, SpUtils.getInstance(context).getString("username"), System.currentTimeMillis() + "");
                                            mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } catch (Exception e) {
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).start();
                                break;
                            case 2:
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 这里是调用耗时操作方法
                                            MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 2, "", "0");
                                            mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } catch (Exception e) {
                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).start();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    //更新数据
    public void upDate(List<ItemBean> beanList) {
        this.mBeanList = beanList;
        notifyDataSetChanged();
    }

    //查找控件
    public class VH extends RecyclerView.ViewHolder {
        ImageView iv_img;
        LinearLayout ll_main;
        TextView tv_bianhao, tv_zhuangtai;

        public VH(View v) {
            super(v);
            tv_bianhao = v.findViewById(R.id.tv_bianhao);
            iv_img = v.findViewById(R.id.iv_img);
            ll_main = v.findViewById(R.id.ll_main);
            tv_zhuangtai = v.findViewById(R.id.tv_zhuangtai);


        }
    }
}
