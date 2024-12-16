package com.my.myapplication.yonghu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.ui.R;
import com.my.myapplication.yonghu.ui.YongHuChaKanActivity;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Adapter for the RecyclerView in the user's main interface
public class YongHuMainAdapter extends RecyclerView.Adapter<YongHuMainAdapter.VH> {
    private final Context context;
    private List<BookingBean> mBeanList;

    // Constructor to initialize data
    public YongHuMainAdapter(Context context, List<BookingBean> beanList) {
        this.context = context;
        this.mBeanList = beanList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // Load layout using LayoutInflater
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new VH(v);
    }

    // Implement three methods in the Adapter
    @Override
    public void onBindViewHolder(final VH holder, @SuppressLint("RecyclerView") int position) {
        // Bind data
        String xingqi = mBeanList.get(position).getXingqi();
        holder.tv_dianhua.setText("Contact Number: " + mBeanList.get(position).getDianhua());
        holder.tv_dizhi.setText("Address: " + mBeanList.get(position).getDizhi());
        holder.tv_qita.setText("Others: " + mBeanList.get(position).getQita());

        holder.tv_shuliang.setText("Number of Seats: " + "Horizontal: " + mBeanList.get(position).getShuliangH() + " units, "
                + mBeanList.get(position).getShuliangZ() + " rows, "
                + "Total: " + mBeanList.get(position).getShuliangH() * mBeanList.get(position).getShuliangZ() + " units ");
        holder.tv_meitian.setText("Daily Opening Hours: " + mBeanList.get(position).getShijianKaishi() + " - " + mBeanList.get(position).getShijianJieshu());
        holder.tv_zhouji.setText("Open Days: " + xingqi.replace(",", " "));

        // Handle whether the current week is within the reservation range and manage whether reservations are closed
        String zhuangtai = "";
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

        if ("".equals(xingqi) || mBeanList.get(position).getZhuangtai() == 1) {
            zhuangtai = zhuangtai + "Reservations are closed by management or no days are open for reservations.";
            holder.tv_msg.setText("Not available for reservation, " + zhuangtai);
        } else if (!xingqi.contains(weekday)) {
            zhuangtai = zhuangtai + "Not within the reservation days.";
            holder.tv_msg.setText("Not available for reservation, " + zhuangtai);
        } else {
            holder.tv_msg.setText("Available for reservation");
        }
        if (mBeanList.get(position).getZhuangtai() == 0) {
            holder.tv_zhuangtai.setText("Reservation Status: Reservations Open");
        } else {
            holder.tv_zhuangtai.setText("Reservation Status: Reservations Closed");
        }

        // Check if the current system time is within the reservation time
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        try {
            // Convert time string to LocalTime object
            LocalTime time1 = LocalTime.parse(mBeanList.get(position).getShijianKaishi());
            LocalTime time2 = LocalTime.parse(mBeanList.get(position).getShijianJieshu());
            LocalTime time3 = LocalTime.parse(currentTime);

            // Compare two LocalTime objects
            int compareResult = time1.compareTo(time3);

            // Output comparison result
            if (compareResult < 0) {
                int compareResult1 = time2.compareTo(time3);
                if (compareResult1 > 0) {
                    // Within the reservation time
                } else {
                    // Not within the reservation time, set message as not available
                    zhuangtai = zhuangtai + "Not within the reservation time period.";
                    holder.tv_msg.setText("Not available for reservation, " + zhuangtai);
                    System.out.println("---------- Greater than or equal to not within reservation time period ");
                }
                System.out.println("----------- Greater than ");
            } else {
                // Not within the reservation time, set message as not available
                zhuangtai = zhuangtai + "Not within the reservation time period.";
                holder.tv_msg.setText("Not available for reservation, " + zhuangtai);
                System.out.println("---------- Less than or equal to not within reservation time period ");
            }
        } catch (Exception e) {
            System.out.println("---------------------" + e.getMessage());
        }

        // Handle the date when this data was published
        try {
            // Get current time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = new Date(Long.parseLong(mBeanList.get(position).getShijianFabu()));
            String str = formatter.format(curDate);
            holder.tv_shijian.setText(str);
        } catch (Exception e) {
            holder.tv_shijian.setText("");
        }

        // Item click event to navigate to another activity, passing the data ID
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, YongHuChaKanActivity.class);
                intent.putExtra("beanId", mBeanList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    // Update data
    public void upDate(List<BookingBean> beanList) {
        this.mBeanList = beanList;
        notifyDataSetChanged();
    }

    // ViewHolder class to find controls
    public class VH extends RecyclerView.ViewHolder {
        LinearLayout ll_main;
        TextView tv_zhuangtai, tv_shuliang, tv_meitian, tv_zhouji, tv_dizhi, tv_dianhua, tv_qita, tv_shijian, tv_msg;

        public VH(View v) {
            super(v);
            tv_zhuangtai = v.findViewById(R.id.tv_zhuangtai);
            tv_shuliang = v.findViewById(R.id.tv_shuliang);
            tv_meitian = v.findViewById(R.id.tv_meitian);
            ll_main = v.findViewById(R.id.ll_main);
            tv_zhouji = v.findViewById(R.id.tv_zhouji);
            tv_dizhi = v.findViewById(R.id.tv_dizhi);
            tv_dianhua = v.findViewById(R.id.tv_dianhua);
            tv_qita = v.findViewById(R.id.tv_qita);
            tv_shijian = v.findViewById(R.id.tv_shijian);
            tv_msg = v.findViewById(R.id.tv_msg);
        }
    }
}
