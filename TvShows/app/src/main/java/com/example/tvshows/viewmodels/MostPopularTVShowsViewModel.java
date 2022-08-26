package com.example.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvshows.repositories.MostPopularTVShowsRepositories;
import com.example.tvshows.responses.TVShowResponse;

public class MostPopularTVShowsViewModel extends ViewModel {
    private MostPopularTVShowsRepositories mostPopularTVShowsRepositories;

    public MostPopularTVShowsViewModel () {
        mostPopularTVShowsRepositories = new MostPopularTVShowsRepositories();
    }
    public LiveData<TVShowResponse> getMostPopularTVShows (int page){
        return mostPopularTVShowsRepositories.getMostPopularTVShows(page);
    }
}
