package com.example.tvshows.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tvshows.R;
import com.example.tvshows.adapters.EpisodeAdapter;
import com.example.tvshows.adapters.ImageSliderAdapter;
import com.example.tvshows.databinding.ActivityTvshowDetailsBinding;
import com.example.tvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodeBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization(){
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTVShowDetails();
    }

    private void getTVShowDetails(){
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            activityTvshowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null ) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                activityTvshowDetailsBinding.setTvShowImageUrl(
                        tvShowDetailsResponse.getTvShowDetails().getImagePath()
                );
                activityTvshowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(
                                        tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                        )
                );
                activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.textReadMore.setOnClickListener(view -> {
                    if(activityTvshowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                        activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_less);
                    } else {
                        activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                        activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_more);
                    }
                });
                activityTvshowDetailsBinding.setRating(
                        String.format(Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                )
                );
                if(tvShowDetailsResponse.getTvShowDetails().getGenres()!= null){
                    activityTvshowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    activityTvshowDetailsBinding.setGenre("N/A");
                }
                activityTvshowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                activityTvshowDetailsBinding.viewDivider.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });
                activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(view -> {
                    if (episodeBottomSheetDialog == null) {
                        episodeBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer),
                                false
                        );

                    }
                    episodeBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                    layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                            new EpisodeAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                    );
                    layoutEpisodesBottomSheetBinding.textTitle.setText(
                            String.format("Episode | %s", tvShow.getName())
                    );
                    layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(view1 -> {
                        episodeBottomSheetDialog.dismiss();
                    });
                    FrameLayout frameLayout = episodeBottomSheetDialog.findViewById( com.google.android.material.R.id.design_bottom_sheet );
                    if(frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    episodeBottomSheetDialog.show();
                });

                activityTvshowDetailsBinding.imageWatchList.setOnClickListener(view -> {


                });
                loadBasicTVShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderImage){
        activityTvshowDetailsBinding.sliderViewPage.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPage.setAdapter(new ImageSliderAdapter(sliderImage));
        activityTvshowDetailsBinding.sliderViewPage.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImage.length);
        activityTvshowDetailsBinding.sliderViewPage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicator = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i < indicator.length; i++ ){
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicator[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicator.addView(indicator[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }


    private void setCurrentSliderIndicator(int position){
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_slider_indicator_inactive)
                );
            }

        }
    }

    private void loadBasicTVShowDetails(){

        String name = tvShow.getName();
        String networkCountry = tvShow.getNetwork() + "(" + tvShow.getCountry() + ")";
        String status = tvShow.getStatus();
        String start = tvShow.getStartDate();
        activityTvshowDetailsBinding.setTvShowName(name);
        activityTvshowDetailsBinding.setNetworkCountry(networkCountry);
        activityTvshowDetailsBinding.setStatus(status);
        activityTvshowDetailsBinding.setStartedDate(start);
        activityTvshowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStarted.setVisibility(View.VISIBLE);


    }

}