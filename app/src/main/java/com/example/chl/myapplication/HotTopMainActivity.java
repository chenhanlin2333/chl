package com.example.chl.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.chl.cpbmyapplication.MovieBean;
import com.example.chl.cpbmyapplication.Subject;
import com.example.chl.cpbmyapplication.WeeklySubject;
import com.example.chl.cpbmyapplication.WeeklySubject.MySubject;
import com.example.chl.utils.DrawableCenterLeftEditView;
import com.example.chl.utils.HttpUtils;
import com.example.chl.utils.HttpUtils.OnHttpResult;
import com.example.chl.utils.InterceptRecycleView;
import com.example.chl.utils.SceneManager;
import com.example.chl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主界面
 */
public class HotTopMainActivity extends BaseActivity {

    @BindView(R.id.rv_hot)
    RecyclerView mRvHot;
    @BindView(R.id.tv_top)
    TextView mTvTop;
    @BindView(R.id.rv_top)
    InterceptRecycleView mRvTop;
    @BindView(R.id.et_search)
    DrawableCenterLeftEditView mEtSearch;
    @BindView(R.id.rv_mounth_list)
    RecyclerView mRvMounthList;
    @BindView(R.id.rv_coming_soon)
    InterceptRecycleView mRvComingSoon;
    @BindView(R.id.rl_progress)
    RelativeLayout mRlProgress;
    @BindView(R.id.sv_content)
    NestedScrollView mSvContent;
    private Context mContext;
    private List<MovieBean> mHotList = new ArrayList();
    private List<MovieBean> mTopList = new ArrayList();
    private List<MovieBean> mMounthList = new ArrayList();
    private List<MovieBean> mComingSoonList = new ArrayList();
    private MainHotMovieAdapter mHotMovieAdapter;
    private MainTopMovieAdapter mTopMovieAdapter;
    private MainHotMovieAdapter mMounthListAdapter;
    private MainTopMovieAdapter mComingSoonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hottopactivity);
        mContext = this;
        ButterKnife.bind(this);
        //设置热门电影的list控件
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRvHot = findViewById(R.id.rv_hot);
        mRvHot.setLayoutManager(gridLayoutManager);
        mHotMovieAdapter = new MainHotMovieAdapter(mContext, mHotList);
        mRvHot.setAdapter(mHotMovieAdapter);

        //设置top250的list控件
        mRvTop = findViewById(R.id.rv_top);
        mRvTop.setLayoutManager(new LinearLayoutManager(mContext));
        mTopMovieAdapter = new MainTopMovieAdapter(mContext, mTopList);
        mRvTop.setAdapter(mTopMovieAdapter);

        //设置口碑榜的list控件
        GridLayoutManager gridLayoutManagerTwo = new GridLayoutManager(mContext, 2);
        gridLayoutManagerTwo.setOrientation(GridLayoutManager.HORIZONTAL);
        mRvMounthList = findViewById(R.id.rv_mounth_list);
        mRvMounthList.setLayoutManager(gridLayoutManagerTwo);
        mMounthListAdapter = new MainHotMovieAdapter(mContext, mMounthList);
        mRvMounthList.setAdapter(mMounthListAdapter);

        //设置即将上映的list控件
        mRvComingSoon = findViewById(R.id.rv_coming_soon);
        mRvComingSoon.setLayoutManager(new LinearLayoutManager(mContext));
        mComingSoonAdapter = new MainTopMovieAdapter(mContext, mComingSoonList);
        mRvComingSoon.setAdapter(mComingSoonAdapter);

        mRlProgress.setVisibility(View.VISIBLE);
        mSvContent.setVisibility(View.GONE);

        initData();
    }

    /**
     * 获取数据
     */
    private void initData() {
        getHotData();
        getTopData();
        getMounthListData();
        getComingSoonData();
    }

    /**
     * 获取热门电影数据
     */
    private void getHotData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("apikey", Constants.API_KEY);
        HttpUtils.getData(Constants.HOT_MOVIE_API, map, Subject.class, new OnHttpResult<Subject>() {
            @Override
            public void onFail(int code) {
                ToastUtils.show("数据加载失败!");
            }
            @Override
            public void onSuccess(Subject subject) {
                mHotList.clear();
                if (subject == null || subject.subjects == null)
                    return;
                mHotList.addAll(subject.subjects);
                mHotMovieAdapter.notifyDataSetChanged();
                mRlProgress.setVisibility(View.GONE);
                mSvContent.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 获取top250数据
     */
    private void getTopData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("apikey", Constants.API_KEY);
        map.put("start", "0");
        map.put("count", "6");
        HttpUtils.getData(Constants.TOP_API, map, Subject.class, new OnHttpResult<Subject>() {
            @Override
            public void onFail(int code) {
                ToastUtils.show("数据加载失败!");
            }

            @Override
            public void onSuccess(Subject subject) {
                mTopList.clear();
                if (subject == null || subject.subjects == null)
                    return;
                mTopList.addAll(subject.subjects);
                mTopMovieAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取口碑榜数据
     */
    private void getMounthListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("apikey", Constants.API_KEY);
        map.put("start", "0");
        map.put("count", "10");
        HttpUtils.getData(Constants.MOUNTH_LIST_API, map, WeeklySubject.class, new OnHttpResult<WeeklySubject>() {
            @Override
            public void onFail(int code) {
                ToastUtils.show("数据加载失败!");
            }

            @Override
            public void onSuccess(WeeklySubject subject) {
                mMounthList.clear();
                if (subject == null || subject.subjects == null)
                    return;
                for (MySubject mySubject : subject.subjects) {
                    if (mySubject == null)
                        continue;
                    mMounthList.add(mySubject.subject);
                }
                mMounthListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 获取即将上映数据
     */
    private void getComingSoonData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("apikey", Constants.API_KEY);
        map.put("start", "0");
        map.put("count", "6");
        HttpUtils.getData(Constants.COMING_SOON_API, map, Subject.class, new OnHttpResult<Subject>() {
            @Override
            public void onFail(int code) {
                ToastUtils.show("数据加载失败!");
            }

            @Override
            public void onSuccess(Subject subject) {
                mComingSoonList.clear();
                if (subject == null || subject.subjects == null)
                    return;
                mComingSoonList.addAll(subject.subjects);
                mComingSoonAdapter.notifyDataSetChanged();
            }
        });
    }


    @OnClick({R.id.tv_hot_more, R.id.tv_top_more, R.id.tv_mouth_list_more, R.id.tv_coming_soon_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_hot_more:
                SceneManager.toScene(mContext, HotMovieActivity.class, null);
                break;
            case R.id.tv_top_more:
                SceneManager.toScene(mContext, TopMovieActivity.class, null);
                break;
            case R.id.tv_mouth_list_more:
                break;
            case R.id.tv_coming_soon_more:
                break;
        }
    }

    public void startsearch(View view) {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
    }
}
