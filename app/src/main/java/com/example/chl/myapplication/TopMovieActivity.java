package com.example.chl.myapplication;

import android.support.v7.widget.RecyclerView.Adapter;

/**
 * top250页面
 */

public class TopMovieActivity extends HotMovieActivity {
    @Override
    public Adapter getAdapter() {
        return new TopMovieAdapter(this,mList);
    }

    @Override
    public String getTitleName() {
        return "豆瓣 Top250";
    }

    @Override
    public String getAPi() {
        return Constants.TOP_API;
    }
}
