package com.my.myapplication.admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.my.myapplication.admin.ui.AdminChaKanActivity;
import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.ui.R;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

// Main screen RecyclerView list adapter
public class AdminMainAdapter extends RecyclerView.Adapter<AdminMainAdapter.VH> {
    private final Context context;
    private List<BookingBean> mBeanList;

    // Constructor method to initialize data
    public AdminMainAdapter(Context context, List<BookingBean> beanList) {
        this.context = context;
        this.mBeanList = beanList;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // LayoutInflater to load layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(final VH holder, @SuppressLint("RecyclerView") int position) {
        // Bind data
        String weekdays = mBeanList.get(position).getXingqi();
        holder.tv_dianhua.setText("Contact: " + mBeanList.get(position).getDianhua());
        holder.tv_dizhi.setText("Address: " + mBeanList.get(position).getDizhi());
        holder.tv_qita.setText("Other: " + mBeanList.get(position).getQita());

        holder.tv_shuliang.setText("Seat count: " + "Rows: " + mBeanList.get(position).getShuliangH() + " seats, "
                + mBeanList.get(position).getShuliangZ() + " rows, "
                + "Total: " + mBeanList.get(position).getShuliangH() * mBeanList.get(position).getShuliangZ() + " seats ");
        holder.tv_meitian.setText("Opening hours every day: " + mBeanList.get(position).getShijianKaishi() + " - " + mBeanList.get(position).getShijianJieshu());
        holder.tv_zhouji.setText("Open on weekdays: " + weekdays.replace(",", " "));

        // Process display of reservation status based on different values
        String status = "";
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String currentWeekday;
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                currentWeekday = "Sunday";
                break;
            case Calendar.MONDAY:
                currentWeekday = "Monday";
                break;
            case Calendar.TUESDAY:
                currentWeekday = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                currentWeekday = "Wednesday";
                break;
            case Calendar.THURSDAY:
                currentWeekday = "Thursday";
                break;
            case Calendar.FRIDAY:
                currentWeekday = "Friday";
                break;
            case Calendar.SATURDAY:
                currentWeekday = "Saturday";
                break;
            default:
                currentWeekday = "Unknown";
                break;
        }

        // Check if reservation is closed or unavailable on selected weekdays
        if ("".equals(weekdays) || mBeanList.get(position).getZhuangtai() == 1) {
            status = status + "Reservations are closed by management or no weekday reservation is enabled";
            holder.tv_msg.setText("Not available for reservation, " + status);
        } else if (!weekdays.contains(currentWeekday)) {
            // Check if the current weekday is within the reservation days
            status = status + "Not within the available reservation days";
            holder.tv_msg.setText("Not available for reservation, " + status);
        } else {
            holder.tv_msg.setText("Available for reservation");
        }

        // Reservation status (enabled or closed)
        if (mBeanList.get(position).getZhuangtai() == 0) {
            holder.tv_zhuangtai.setText("Reservation status: Reservation enabled");
        } else {
            holder.tv_zhuangtai.setText("Reservation status: Reservation closed");
        }

        // Compare current time with reservation time range
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        try {
            // Convert the time string to LocalTime objects
            LocalTime startTime = LocalTime.parse(mBeanList.get(position).getShijianKaishi());
            LocalTime endTime = LocalTime.parse(mBeanList.get(position).getShijianJieshu());
            LocalTime currentLocalTime = LocalTime.parse(currentTime);

            // Compare the times
            int compareStart = startTime.compareTo(currentLocalTime);

            if (compareStart < 0) {
                int compareEnd = endTime.compareTo(currentLocalTime);
                if (compareEnd > 0) {
                    // Reservation is available
                } else {
                    status = status + "Not within the reservation time range";
                    holder.tv_msg.setText("Not available for reservation, " + status);
                    System.out.println("---------- Outside reservation time range ");
                }
                System.out.println("----------- Within time range ");
            } else {
                status = status + "Not within the reservation time range";
                holder.tv_msg.setText("Not available for reservation, " + status);
                System.out.println("---------- Outside reservation time range ");
            }
        } catch (Exception e) {
            System.out.println("---------------------" + e.getMessage());
        }

        // Time when the information was published
        try {
            // Get the current time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date publishDate = new Date(Long.parseLong(mBeanList.get(position).getShijianFabu()));
            String formattedDate = formatter.format(publishDate);
            holder.tv_shijian.setText(formattedDate);
        } catch (Exception e) {
            holder.tv_shijian.setText("");
        }

        // Item click event with ID
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminChaKanActivity.class);
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

    // ViewHolder class to find views
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
