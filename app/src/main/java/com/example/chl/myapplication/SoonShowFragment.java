package com.example.chl.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SoonShowFragment extends Fragment implements View.OnClickListener {//即将上映的fragment
    private boolean isSee = true;//判断当前fragment是否可见
    private ArrayList<ChlssMovie> movies = new ArrayList<>();//存储数据
    private ArrayList<ChlssMovie> moviesbylike = new ArrayList<>();
    private ArrayList<GroupInfo> groups =new ArrayList<>();
    private String url = "http://10.0.2.2:3000/movie/coming_soon";
    private String url2 = "http://192.168.1.106:3000/movie/coming_soon";
    private Ssadapter adapter;
    private RecyclerView recyclerView;
    private RelativeLayout progresss;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
 //           additemdirect();
            addItemDecoration();
            sortbylike();
            adapter.notifyDataSetChanged();
            progresss.setVisibility(View.GONE);

        }
    };
    private Button time_button,like_button;
    private StickyItemDecoration decoration;
    private boolean state = true; //发现的一个bug,用来修正粘性头


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soon_show,container,false);
        progresss = view.findViewById(R.id.ccc2_progress);
        adapter = new Ssadapter(movies,getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.soonfragment_container);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        time_button = (Button) view.findViewById(R.id.time_button);
        like_button = (Button) view.findViewById(R.id.like_button);
        progresss.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        time_button.setOnClickListener(this);
        like_button.setOnClickListener(this);
        initData();
        time_button.setTextColor(Color.BLACK);
        like_button.setTextColor(Color.GRAY);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden){
            isSee = false;
        }else {
            isSee = true;
        }
    }
    public boolean isSee() {
        return isSee;
    }

    private void initData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                client.newBuilder().
                        connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30,TimeUnit.SECONDS);
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    ArrayList<ChlssMovie> temp;
                    temp = gson.fromJson(responseData,new TypeToken<ArrayList<ChlssMovie>>(){}.getType());
                    for (int i = 0 ; i < temp.size();i++){
                        movies.add(temp.get(i));
                    }
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

//    private void additemdirect(){
//        SectionTimeDecoration.GroupInfoCallback callback = new SectionTimeDecoration.GroupInfoCallback() {
//                @Override
//                public GroupInfo getGroupInfo(int position) {
//                    int groupId = position / 5;
//                    int index = position % 5;
//                    GroupInfo groupInfo = new GroupInfo(groupId,groupId+"");
//                    groupInfo.setPosition(index);
//                    return groupInfo;
//                }
//        };
//        SectionTimeDecoration decoration = new SectionTimeDecoration(callback);
//        recyclerView.addItemDecoration(decoration);
//    }

    private void addItemDecoration(){
        ISticky iSticky = new ISticky() {
            @Override
            public boolean isFirstPostion(int pos) {
                String date = movies.get(pos).getDate();
                for (int i = 0 ;i <pos ;i ++){
                    if (date.equals(movies.get(i).getDate())){
                        return false;
                    }
                }
                return true;
            }
            @Override
            public String getGroupTitle(int pos) {
               return   movies.get(pos).getDate();
            }
        };
        decoration =new StickyItemDecoration(getContext(),iSticky);
        recyclerView.addItemDecoration(decoration);
    }


    private boolean isExist(int position){//用于在粘性头判断是否为同组第一个
        String date = movies.get(position).getDate();
        for (int i = 0 ;i <position ;i ++){
            if (date.equals(movies.get(i).getDate())){
                return true;
            }
        }
        return false;
    }

    private void sortbylike(){//按热度排序并去掉粘性头
        for (ChlssMovie movie : movies){
            moviesbylike.add(movie);
        }
        Collections.sort(moviesbylike);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.time_button://按时间排序
                if (state == false){
                    recyclerView.removeItemDecoration(new Now_popular_fragemnt.SimpeleitemDecoration());
                    recyclerView.addItemDecoration(decoration);
                    Ssadapter ssadapter = new Ssadapter(movies,getActivity());
                    recyclerView.setAdapter(ssadapter);
                    ssadapter.notifyDataSetChanged();
                    time_button.setTextColor(Color.BLACK);
                    like_button.setTextColor(Color.GRAY);
                    state = true;
                }
                break;

            case R.id.like_button://按热度排序
                if (state == true){
                    Ssadapter adapter = new Ssadapter(moviesbylike,getActivity());
                    recyclerView.removeItemDecoration(decoration);
                    recyclerView.addItemDecoration(new Now_popular_fragemnt.SimpeleitemDecoration());
                    recyclerView.setAdapter(adapter);
                    time_button.setTextColor(Color.GRAY);
                    like_button.setTextColor(Color.BLACK);
                    adapter.notifyDataSetChanged();
                    state = false;
                }
                break;
        }
    }
}
