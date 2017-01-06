package com.bohdan.youtube.fragment;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Created by Bodia on 06.01.2017.
 */

public class FragmentVideo extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.player = youTubePlayer;
        player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        player.setOnFullscreenListener((YouTubePlayer.OnFullscreenListener) getActivity());

        if (!b && videoId != null){
            player.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        this.player = null;
    }

    @Override
    public void onDestroy() {
        if (player != null){
            player.release();
        }
        super.onDestroy();
    }

    public void setVideoId(String videoId) {
        if (videoId != null && !videoId.equals(this.videoId)){
            this.videoId = videoId;
            if (player == null){
                player.cueVideo(videoId);
            }
        }
    }

    public void backNormal(){
        player.setFullscreen(false);
    }
}