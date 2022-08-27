package com.example.tvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.tvshows.R;
import com.example.tvshows.adapters.TVShowAdapter;
import com.example.tvshows.databinding.ActivityMainBinding;
import com.example.tvshows.listenner.TVShowListener;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowAdapter tvShowAdapter;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        activityMainBinding.tvShowRecycleView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowAdapter = new TVShowAdapter(tvShows, this);
        activityMainBinding.tvShowRecycleView.setAdapter(tvShowAdapter);
        activityMainBinding.tvShowRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activityMainBinding.tvShowRecycleView.canScrollVertically(1)){
                    if (currentPage <= totalPages) {
                    currentPage += 1;
                    getMostPopularTVShows();
                    }
                }
            }
        });
        getMostPopularTVShows();
    }


    private  void  getMostPopularTVShows(){
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowsResponse -> {
            toggleLoading();
            if (mostPopularTVShowsResponse != null) {
                totalPages = mostPopularTVShowsResponse.getPages();
                if (mostPopularTVShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                    tvShowAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }
            }
        });
    }

    private void toggleLoading(){
        if (currentPage == 1){
            activityMainBinding.setIsLoading(activityMainBinding.getIsLoading() == null || !activityMainBinding.getIsLoading());
        } else {
            activityMainBinding.setIsLoadingMore(activityMainBinding.getIsLoadingMore() == null || !activityMainBinding.getIsLoadingMore());
        }
    }

    @Override
    public void onTVShowClick(TVShow tvshow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvshow);
        startActivity(intent);
    }
}