package com.example.sateeknews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.View;
import android.widget.Toast;

import com.example.sateeknews.api.ApiClient;
import com.example.sateeknews.api.ApiInterface;
import com.example.sateeknews.models.Article;
import com.example.sateeknews.models.News;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY="af67df31b0c14c22ae4fb4c94d3ca79f";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh();
    }

    public void LoadJson() {
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        //String country = Utils.getCountry(); //It will get country from device locale.
        //I am hardcoding country value for India as IN
        String country = "in";

        retrofit2.Call<News> call;
        call = apiInterface.getNews(country,"technology",API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(retrofit2.Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new Adapter(articles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    swipeRefreshLayout.setRefreshing(false);

                } else {

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"No Result!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initListener() {
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,NewsDetailActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img",article.getUrlToImage());
                intent.putExtra("date",article.getPublishedAt());
                intent.putExtra("source",article.getSource().getName());
                intent.putExtra("author",article.getAuthor());

                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        LoadJson();

    }

    private void onLoadingSwipeRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                LoadJson();
            }
        });
    }
}
