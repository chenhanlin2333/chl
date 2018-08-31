package com.example.chl.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;


import com.google.gson.Gson;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager manager = getSupportFragmentManager();
        MainPopularShowFragment fragment =(MainPopularShowFragment) manager.findFragmentById(R.id.Main_popular_fragment);//id为我的热映fragment的id
        fragment.onActivityResult(requestCode,resultCode,data);//因为fragment没有onActiviyResult 说以需要借助Mainacyivity的方法

    }

    public void hottop(View view) {
        Intent intent = new Intent(this,HotTopMainActivity.class);
        startActivity(intent);
    }
}
