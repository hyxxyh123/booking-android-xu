package com.my.myapplication.yonghu.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.helper.MyHelper;
import com.my.myapplication.ui.R;
import com.my.myapplication.yonghu.adapter.YongHuMainAdapter;

import java.util.ArrayList;
import java.util.List;


//分类数据页 循环创建
public class Home_item_yonghu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //recyclerview 适配器
    private YongHuMainAdapter mMyAdapter;
    //recyclerview 数据
    private List<BookingBean> mBeanList = new ArrayList<>();

    //RecyclerView
    private RecyclerView mRecyclerview;

    public Home_item_yonghu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_item1.
     */
    // TODO: Rename and change types and number of parameters
    public static Home_item_yonghu newInstance(String param1, String param2) {
        Home_item_yonghu fragment = new Home_item_yonghu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        接收创建该类参数
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_yonghu, container, false);
//查找Recyclerview控件
        mRecyclerview = view.findViewById(R.id.recyclerview);
        //绑定Recyclerview适配器
        mMyAdapter = new YongHuMainAdapter(getActivity(), mBeanList);
        //设置Recyclerview 布局管理器
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //  mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        //设置Recyclerview适配器
        mRecyclerview.setAdapter(mMyAdapter);
        return view;
    }

    //界面重新可见时候调用 添加数据返回界面 从新刷新界面数据
    @Override
    public void onResume() {
        super.onResume();
        //开线程
        new Thread(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {
                try {
                    // 这里是调用耗时操作方法 获取该分类数据
                    mBeanList = MyHelper.getInstance(getActivity()).getDataByLeiXing(mParam1);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //更新recyclerview 显示数据
                            mMyAdapter.upDate(mBeanList);
                        }
                    });

                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        }).start();


    }

}