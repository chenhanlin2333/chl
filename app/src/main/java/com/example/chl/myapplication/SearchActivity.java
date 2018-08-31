package com.example.chl.myapplication;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    public ArrayList<Movie> list = new ArrayList<>();
    public ArrayList<String> list_h = new ArrayList<>();

    public static final int resposeSuccessful = 1;
    public static final int refresh = 2;

    public RecyclerView recyclerView ;
    public LinearLayoutManager manager;

    public static MovieAdapter adapter;

    public List<String> id_list = new ArrayList<String>();

    /**
     * 需要在MovieAdapter中调用数据库
     * 所以HistorySaveHelper需要为public static
     */
    public static HistorySaveHelper historySave;
    public SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        /**
         * 设置回车键监听
         * 将输入框内容存进数据库
         */
        final EditText editText = (EditText) findViewById(R.id.search_edit);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {

                    // TODO: 2018/8/3  往数据库添加数据（historyData）
                    /**
                     * 完成像数据库提交数据
                     */
                    historySave = new HistorySaveHelper(SearchActivity.this, "history.db", null, 1 );
                    db = historySave.getWritableDatabase();

                    String historyData = editText.getText().toString();

                    if(!historyData.isEmpty()){

                        ContentValues values = new ContentValues();
                        values.put("name", historyData);
                        db.insert("Book", null, values);
                        /**
                         * 传入数据后重置输入框，并刷新列表
                         * 使用handle消息机制，还是不便，最后还是在Adpater内加载布局时定义点击事件
                         */
                        editText.setText("");
                        adapter.notifyDataSetChanged();

//                        Toast.makeText(SearchActivity.this, "history + 1", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

/**
 * 隐藏原生标题栏
 */
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }

        recyclerView = (RecyclerView) findViewById(R.id.movie_list);
        manager = new LinearLayoutManager(this);

/**
 *
 * 联网请求数据
 */
        HttpUtil.sendHttpRequest("https://api.douban.com/v2/movie/in_theaters", new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String getData = response.body().string();
                parseJSONWithGSON(getData);

                if(response.isSuccessful()){
                    Message message = new Message();
                    message.what = resposeSuccessful;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call call, IOException e){
                Toast.makeText(SearchActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    // FIXME: 2018/7/27   此处尝试过Google官方的推荐写法，未能加载出RecyclerView，无果，暂不深究    No adapter attached; skipping layout
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case resposeSuccessful:

                    historySave = new HistorySaveHelper(SearchActivity.this, "history.db", null, 1 );
                    db = historySave.getWritableDatabase();

                    recyclerView.setLayoutManager(manager);
                    adapter = new MovieAdapter(list, id_list);
                    recyclerView.setAdapter(adapter);

                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 获取json解析得到电影的列表list
     */
    public void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();

        JavaBean.Status l = gson.fromJson(jsonData, JavaBean.Status.class);
        List<Movie> movie_List = l.getSubjects();

        for(int i = 0; i < 10   ; i++){
            id_list.add(movie_List.get(i).getId());
            list.add(movie_List.get(i));
        }
    }
}
