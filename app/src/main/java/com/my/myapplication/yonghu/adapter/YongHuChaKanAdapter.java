package com.my.myapplication.yonghu.adapter;

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

import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.MessageEvent;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.utils.ImageUtils;
import com.my.myapplication.utils.SpUtils;
import com.my.myapplication.yonghu.ui.YongHuChaKanActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Adapter for the RecyclerView to view details
public class YongHuChaKanAdapter extends RecyclerView.Adapter<YongHuChaKanAdapter.VH> {
    private final Context context;
    private List<ItemBean> mBeanList;
    private Long tid;
    private String tupian = "";

    // Constructor to initialize data
    public YongHuChaKanAdapter(Context context, List<ItemBean> beanList, Long tid, String tupian) {
        this.context = context;
        this.mBeanList = beanList;
        this.tid = tid;
        this.tupian = tupian;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Load layout using LayoutInflater
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chakan, parent, false);
        return new VH(v);
    }

    // Implement three methods in the Adapter
    @Override
    public void onBindViewHolder(final VH holder, @SuppressLint("RecyclerView") int position) {
        // Bind data
        holder.tv_bianhao.setText("Seat " + (position + 1));

        // Handle image
        if ("".equals(tupian)) {
            holder.iv_img.setImageResource(R.mipmap.zuowei);
        } else {
            ImageUtils.displayImage(context, holder.iv_img, tupian);
        }

        // Display text based on status
        switch (mBeanList.get(position).getZhuangtai()) {
            case 0:
                holder.tv_zhuangtai.setText("Available");
                holder.tv_zhuangtai.setTextColor(Color.BLACK);
                holder.tv_bianhao.setTextColor(Color.BLACK);
                break;
            case 1:
                if (mBeanList.get(position).getUsername().equals(SpUtils.getInstance(context).getString("username"))) {
                    holder.tv_zhuangtai.setText("My Reservation");
                    holder.tv_zhuangtai.setTextColor(Color.BLUE);
                    holder.tv_bianhao.setTextColor(Color.BLUE);
                } else {
                    holder.tv_zhuangtai.setText("Already Reserved");
                    holder.tv_zhuangtai.setTextColor(Color.BLUE);
                    holder.tv_bianhao.setTextColor(Color.BLUE);
                }
                break;
            case 2:
                holder.tv_zhuangtai.setText("Temporarily Unavailable");
                holder.tv_zhuangtai.setTextColor(Color.RED);
                holder.tv_bianhao.setTextColor(Color.RED);
                break;
        }

        // Item click event - general flow
        // 1. Handle whether reservation is possible
        // 2. When clicking on a seat, first check if the current account has a reservation. If not, check the status of the clicked seat. If available, reserve it; if already reserved, notify the user.
        // If there is a reservation, check the status of the clicked seat. If available, check previous reservation data and prompt whether to switch to the current seat. If already reserved, check if the current reservation belongs to the user. If so, extend the reservation; if not, inform that it is already reserved.
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YongHuChaKanActivity chaKanActivity = (YongHuChaKanActivity) context;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Call time-consuming operation method
                            // Handle whether reservation is possible
                            BookingBean dataById = MyHelper.getInstance(chaKanActivity).getDataById(tid);
                            String xingqi = dataById.getXingqi();
                            // Get current date and time
                            Calendar calendar = Calendar.getInstance();
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
                                    weekday = "Unknown";
                                    break;
                            }

                            // Handle whether reservation is possible
                            if ("".equals(xingqi) || dataById.getZhuangtai() == 1) {
                                EventBus.getDefault().post(new MessageEvent(0L, 2));
                            } else if (!xingqi.contains(weekday)) {
                                EventBus.getDefault().post(new MessageEvent(0L, 6));
                            }

                            // Handle whether reservation is possible
                            if (mBeanList.get(position).getZhuangtai() == 2) {
                                EventBus.getDefault().post(new MessageEvent(0L, 5));
                            }

                            // Handle whether reservation is possible
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                            String currentTime = sdf.format(new Date());

                            try {
                                // Convert time string to LocalTime object
                                LocalTime time1 = LocalTime.parse(dataById.getShijianKaishi());
                                LocalTime time2 = LocalTime.parse(dataById.getShijianJieshu());
                                LocalTime time3 = LocalTime.parse(currentTime);

                                // Compare two LocalTime objects
                                int compareResult = time1.compareTo(time3);

                                // Output comparison result
                                if (compareResult < 0) {
                                    int compareResult1 = time2.compareTo(time3);
                                    if (compareResult1 > 0) {
                                        // Open for reservation
                                        if (xingqi.contains(weekday) && dataById.getZhuangtai() == 0) {
                                            // 1. Check if the current account has a reservation
                                            List<ItemBean> usernameItemBean = MyHelper.getInstance(chaKanActivity).getItemDataByUserName(SpUtils.getInstance(context).getString("username"));
                                            // 2. If the current account has no reservation
                                            if (usernameItemBean.size() == 0) {
                                                // 3. Check if the clicked seat is available. If so, reserve it
                                                if (mBeanList.get(position).getZhuangtai() == 0) {
                                                    chaKanActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new AlertDialog.Builder(context) // Bind to the current window
                                                                    .setTitle("Notice") // Set title
                                                                    .setMessage("Are you sure you want to reserve? The validity period is 1 hour. After expiration, the system will automatically reclaim it. Before expiration, you can click the seat to continue the reservation. If it is not renewed before expiration and is reserved by others, please voluntarily give up the right to use it. Thank you for your cooperation!") // Set message
                                                                    .setIcon(R.mipmap.ic_launcher) // Set icon
                                                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            // Confirm button - start a thread to update data
                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        // Call time-consuming operation method
                                                                                        MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 1, SpUtils.getInstance(context).getString("username"), System.currentTimeMillis() + "");

                                                                                        mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                notifyDataSetChanged();
                                                                                                Toast.makeText(chaKanActivity, "Reservation successful", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e) {
                                                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }).start();
                                                                        }
                                                                    }) // Cancel button does nothing
                                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }) // Add cancel button
                                                                    .create() // Create dialog
                                                                    .show(); // Show dialog
                                                        }
                                                    });
                                                }
                                                // 4. If not available, notify that it is already reserved
                                                if (mBeanList.get(position).getZhuangtai() == 1) {
                                                    chaKanActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(chaKanActivity, "Already Reserved", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            } else {
                                                // 5. If the current account has a reservation and the clicked seat is available, prompt whether to switch to the current seat
                                                if (mBeanList.get(position).getZhuangtai() == 0) {
                                                    BookingBean dataById1 = MyHelper.getInstance(chaKanActivity).getDataById(usernameItemBean.get(0).getTid());

                                                    chaKanActivity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new AlertDialog.Builder(context) // Bind to the current window
                                                                    .setTitle("Notice") // Set title
                                                                    .setMessage("You already have a reserved seat: " + dataById1.getLouceng() + ", seat number: " + (usernameItemBean.get(0).getBianhao() + 1) + "\nDo you want to cancel the previous reservation and reserve this seat?") // Set message
                                                                    .setIcon(R.mipmap.ic_launcher) // Set icon
                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            // Confirm button - start a thread to update data
                                                                            new Thread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    try {
                                                                                        // Call time-consuming operation method
                                                                                        MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(usernameItemBean.get(0).getId(), 0, "", "0");
                                                                                        MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 1, SpUtils.getInstance(context).getString("username"), System.currentTimeMillis() + "");

                                                                                        mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                notifyDataSetChanged();
                                                                                                Toast.makeText(chaKanActivity, "Reservation successful", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    } catch (Exception e) {
                                                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }).start();
                                                                        }
                                                                    }) // Cancel button does nothing
                                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                        }
                                                                    }) // Add cancel button
                                                                    .create() // Create dialog
                                                                    .show(); // Show dialog
                                                        }
                                                    });
                                                }
                                                // 6. If the current account has a reservation and the clicked seat is reserved, check if the reservation belongs to the user. If so, prompt whether to extend; if not, inform that it is already reserved.
                                                if (mBeanList.get(position).getZhuangtai() == 1) {
                                                    // Call time-consuming operation method
                                                    if (mBeanList.get(position).getUsername().equals(SpUtils.getInstance(context).getString("username"))) {
                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new AlertDialog.Builder(context) // Bind to the current window
                                                                        .setTitle("Notice") // Set title
                                                                        .setMessage("This is your reserved seat. Do you want to extend the reservation? The extension time is 1 hour.") // Set message
                                                                        .setIcon(R.mipmap.ic_launcher) // Set icon
                                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                // Confirm button - start a thread to update data
                                                                                new Thread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        try {
                                                                                            // Call time-consuming operation method
                                                                                            MyHelper.getInstance(chaKanActivity).updateItemZhuangTaiAndUserNameById(mBeanList.get(position).getId(), 1, SpUtils.getInstance(context).getString("username"), System.currentTimeMillis() + "");

                                                                                            mBeanList = MyHelper.getInstance(chaKanActivity).getItemDataById(tid);
                                                                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    notifyDataSetChanged();
                                                                                                    Toast.makeText(chaKanActivity, "Extension successful", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                        } catch (Exception e) {
                                                                                            chaKanActivity.runOnUiThread(new Runnable() {
                                                                                                @Override
                                                                                                public void run() {
                                                                                                    Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                }).start();
                                                                            }
                                                                        }) // Cancel button does nothing
                                                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                dialog.dismiss();
                                                                            }
                                                                        }) // Add cancel button
                                                                        .create() // Create dialog
                                                                        .show(); // Show dialog
                                                            }
                                                        });
                                                    } else {
                                                        // 7. Else for case 6
                                                        chaKanActivity.runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(chaKanActivity, "Already Reserved", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        System.out.println("---------- Greater than or equal to not within reservation time period ");
                                        EventBus.getDefault().post(new MessageEvent(0L, 3));
                                    }
                                } else {
                                    EventBus.getDefault().post(new MessageEvent(0L, 4));
                                    System.out.println("---------- Less than or equal to not within reservation time period ");
                                }
                            } catch (Exception e) {
                                System.out.println("---------------------" + e.getMessage());
                            }
                            chaKanActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } catch (Exception e) {
                            chaKanActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Unknown error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    // Update data
    public void upDate(List<ItemBean> beanList) {
        this.mBeanList = beanList;
        notifyDataSetChanged();
    }

    // ViewHolder class to find controls
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
