package com.bohdan.youtube.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.bohdan.youtube.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class ActivityHome extends AppCompatActivity {

    private Drawer drawer = null;
    private Toolbar toolbar;

    private String[] channelName;
    private String[] channelId;
    private String[] videoTypes;

    private int selectedDrawerItem = 0;

    private FrameLayout layoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layoutList = (FrameLayout) findViewById(R.id.fragment_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        channelName = getResources().getStringArray(R.array.channel_names);
        channelId = getResources().getStringArray(R.array.channel_id);
        videoTypes = getResources().getStringArray(R.array.video_types);

        PrimaryDrawerItem[] primaryDrawerItems = new PrimaryDrawerItem[channelId.length];

        for (int i=0; i < channelId.length; i++){
            primaryDrawerItems[i] = new PrimaryDrawerItem()
                    .withName(channelName[i])
                    .withIdentifier(i)
                    .withSelectable(false);
        }

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.mipmap.ic_launcher)
                .build();

        drawer = new DrawerBuilder(this)
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withDisplayBelowStatusBar(true)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(primaryDrawerItems)
                .addStickyDrawerItems(
                        new SecondaryDrawerItem()
                                .withName(getString(R.string.about))
                                .withIdentifier(channelId.length - 1).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;

                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }
}
