package com.example.tvshows.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvshows.database.TVShowsDatabase;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.repositories.TVShowDetailsRepository;
import com.example.tvshows.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.annotations.NonNull;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel (@NonNull Application application) {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId) {
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().addToWatchList(tvShow);
    }

}
