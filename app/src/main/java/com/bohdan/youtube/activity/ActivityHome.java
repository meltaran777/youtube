package com.bohdan.youtube.activity;

import com.bohdan.youtube.R;
import com.bohdan.youtube.fragment.FragmentChannelVideo;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bohdan.youtube.fragment.FragmentVideo;
import com.bohdan.youtube.util.Utils;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ActivityHome extends AppCompatActivity implements YouTubePlayer.OnFullscreenListener,
        FragmentChannelVideo.OnVideoSelectedListener {

    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final int ADDITIONAL_ITEM_NUMBER = 2;
    private FragmentVideo fragmentVideo;
    private boolean isFullscreen;

    private DrawerBuilder drawerBuilder = null;
    private Drawer drawer = null;
    private Toolbar toolbar;
    private View decorView;

    private String[] channelNames;
    private String[] channelId;
    private String[] videoTypes;
    private String[] videoCategory;

    private int selectedDrawerItem = 0;

    private FrameLayout layoutList;

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layoutList = (FrameLayout) findViewById(R.id.fragment_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        decorView = getWindow().getDecorView();
        fragmentVideo = (FragmentVideo) getFragmentManager().findFragmentById(R.id.video_fragment_container);

        checkYouTubeApi();

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        drawerBuilder = new DrawerBuilder(this)
                .withActivity(ActivityHome.this)
                .withAccountHeader(accountHeader)
                .withDisplayBelowStatusBar(true)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSavedInstance(savedInstanceState)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.about))
                                .withIdentifier(111)
                                //.withIdentifier(channelId.length - 1)
                                .withSelectable(false)
                )
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true);

        videoCategory = getResources().getStringArray(R.array.video_category);

        PrimaryDrawerItem[] primaryDrawerItems;

        int videoCategoryId = 0;
        int videoIdFull = 0;
        int lengthBest  = 0;
        int length2016  = 0;
        int lengthNew   = 0;

        for (int i = 0; i < videoCategory.length; i++) {

            switch (i) {

                case 2 :
                    channelNames = getResources().getStringArray(R.array.channel_names);
                    channelId = getResources().getStringArray(R.array.channel_id);
                    videoTypes = getResources().getStringArray(R.array.video_types);
                    primaryDrawerItems = new PrimaryDrawerItem[channelId.length];
                    lengthBest = channelNames.length;
                    videoCategoryId = 2;
                    break;

                case 1 :
                    channelNames = getResources().getStringArray(R.array.channel_names_2016);
                    channelId = getResources().getStringArray(R.array.channel_id_2016);
                    videoTypes = getResources().getStringArray(R.array.video_types_2016);
                    primaryDrawerItems = new PrimaryDrawerItem[channelId.length];
                    length2016 = channelNames.length;
                    videoCategoryId = 1;
                    break;

                case 0 :
                    channelNames = getResources().getStringArray(R.array.channel_names_new);
                    channelId = getResources().getStringArray(R.array.channel_id_new);
                    videoTypes = getResources().getStringArray(R.array.video_types_new);
                    primaryDrawerItems = new PrimaryDrawerItem[channelId.length];
                    lengthNew = channelNames.length;
                    videoCategoryId = 0;
                    break;

                default:
                    primaryDrawerItems = new PrimaryDrawerItem[i];
            }

            if (i != 0) videoIdFull += ADDITIONAL_ITEM_NUMBER;

            for (int j = 0; j < channelId.length; j++) {

               if (i !=0 ) videoIdFull++;

                primaryDrawerItems[j] = new PrimaryDrawerItem()
                        .withName(channelNames[j])
                        .withIdentifier(videoIdFull)
                        .withTag(videoCategoryId)
                        .withIcon(R.drawable.ic_video)
                        .withSelectable(false);

                Log.d("DrawerDebug", "onCreate: " + channelNames[j] + " " + String.valueOf(videoIdFull));
            }

                    if ( i != 0 ) {
                        drawerBuilder.addDrawerItems(
                                new SectionDrawerItem().withName(videoCategory[i]).withTextColorRes(R.color.accent_color).withDivider(true))
                                .addDrawerItems(new DividerDrawerItem());
                    }

            drawerBuilder.addDrawerItems(primaryDrawerItems);

        }

        final int finalLength201 = length2016;
        final int finalLengthNew = lengthNew;
        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                selectedDrawerItem = position;

                if (drawerItem != null) {
                    if (drawerItem.getIdentifier() >= 0 && selectedDrawerItem != -1) {

                        int videoCategory = (int) drawerItem.getTag();
                        Log.d("DrawerDebug", "onItemClick: " + String.valueOf(position));

                        switch (videoCategory) {

                            case 2 :
                                channelNames = getResources().getStringArray(R.array.channel_names);
                                channelId = getResources().getStringArray(R.array.channel_id);
                                videoTypes = getResources().getStringArray(R.array.video_types);
                                selectedDrawerItem -= (finalLength201 + finalLengthNew + ADDITIONAL_ITEM_NUMBER * videoCategory);
                                break;

                            case 1 :
                                channelNames = getResources().getStringArray(R.array.channel_names_2016);
                                channelId = getResources().getStringArray(R.array.channel_id_2016);
                                videoTypes = getResources().getStringArray(R.array.video_types_2016);
                                selectedDrawerItem -= (finalLengthNew + ADDITIONAL_ITEM_NUMBER * videoCategory);
                                break;

                            case 0 :
                                channelNames = getResources().getStringArray(R.array.channel_names_new);
                                channelId = getResources().getStringArray(R.array.channel_id_new);
                                videoTypes = getResources().getStringArray(R.array.video_types_new);
                                break;
                        }

                        setToolbarAndSelectedDrawerItem(
                                channelNames[selectedDrawerItem-1],
                                (position - 1)
                        );

                        Bundle bundle = new Bundle();
                        bundle.putString(Utils.TAG_VIDEO_TYPE,
                                videoTypes[selectedDrawerItem-1]);
                        bundle.putString(Utils.TAG_CHANNEL_ID,
                                channelId[selectedDrawerItem-1]);

                        fragment = new FragmentChannelVideo();
                        fragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .commit();

                    } else if (selectedDrawerItem == -1) {
                        Intent aboutIntent = new Intent(getApplicationContext(),
                                ActivityAbout.class);
                        startActivity(aboutIntent);
                        overridePendingTransition(R.anim.open_next, R.anim.close_main);
                    }
                }
                return false;
            }
        });

        drawer = drawerBuilder.build();

        setToolbarAndSelectedDrawerItem(channelNames[0], 0);


        Bundle bundle = new Bundle();
        bundle.putString(Utils.TAG_VIDEO_TYPE,
                videoTypes[selectedDrawerItem]);
        bundle.putString(Utils.TAG_CHANNEL_ID,
                channelId[selectedDrawerItem]);

        fragment = new FragmentChannelVideo();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (f != null) {
                    updateTitleAndDrawer(f);
                }

            }
        });

        if (savedInstanceState == null) {
            drawer.setSelection(0, false);
        }


    }

    private void checkYouTubeApi() {
        YouTubeInitializationResult errorReason =
                YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
            String errorMessage =
                    String.format(getString(R.string.error_player),
                            errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void setToolbarAndSelectedDrawerItem(String title, int selectedDrawerItem){
        toolbar.setTitle(title);
        drawer.setSelection(selectedDrawerItem, false);
    }

    private void updateTitleAndDrawer (Fragment mFragment){
        String fragClassName = mFragment.getClass().getName();

        if (fragClassName.equals(FragmentChannelVideo.class.getName())){
            setToolbarAndSelectedDrawerItem(channelNames[selectedDrawerItem ],
                    (selectedDrawerItem ));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                Intent aboutIntent = new Intent(getApplicationContext(),
                        ActivityAbout.class);
                startActivity(aboutIntent);
                overridePendingTransition(R.anim.open_next, R.anim.close_main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            recreate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        layout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        layout();
    }


    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (isFullscreen) {

            toolbar.setVisibility(View.GONE);
            layoutList.setVisibility(View.GONE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            setLayoutSize(fragmentVideo.getView(), MATCH_PARENT, MATCH_PARENT);


        } else if (isPortrait) {

            toolbar.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.VISIBLE);
            setLayoutSize(fragmentVideo.getView(), WRAP_CONTENT, WRAP_CONTENT);


        } else {

            toolbar.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.VISIBLE);
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
            setLayoutSize(fragmentVideo.getView(), videoWidth, WRAP_CONTENT);
        }
    }

    @Override
    public void onVideoSelected(String ID) {
        FragmentVideo fragmentVideo = (FragmentVideo) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        fragmentVideo.setVideoId(ID);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen){
            fragmentVideo.backNormal();
        } else{
            super.onBackPressed();
        }
    }
}
