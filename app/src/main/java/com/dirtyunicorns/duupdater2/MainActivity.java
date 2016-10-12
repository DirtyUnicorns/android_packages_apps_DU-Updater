package com.dirtyunicorns.duupdater2;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.dirtyunicorns.duupdater2.fragments.FragmentGappsBanks;
import com.dirtyunicorns.duupdater2.fragments.FragmentGappsTBO;
import com.dirtyunicorns.duupdater2.fragments.FragmentOfficial;
import com.dirtyunicorns.duupdater2.fragments.FragmentTest;
import com.dirtyunicorns.duupdater2.fragments.FragmentWeeklies;
import com.dirtyunicorns.duupdater2.objects.CurrentVersion;
import com.dirtyunicorns.duupdater2.services.CheckService;
import com.dirtyunicorns.duupdater2.utils.NetUtils;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private View view;
    private final static int REQUEST_READ_STORAGE_PERMISSION = 1;
    private Fragment frag;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent checkService = new Intent(this, CheckService.class);
        startService(checkService);

        InitInterface();
        InitPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitInterface() {
        view = findViewById(R.id.content_frame);

        assert toolbar != null;
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        actionBarDrawerToggle.syncState();

        if (!NetUtils.isOnline(this)) {
            Snackbar.make(view,"You must be connected to the internet to use the updater", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();
        } else {
            NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

            assert navigationView != null;
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void UpdateFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame,frag);
        ft.commit();
    }

    private void InitPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE_PERMISSION);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

            int id = item.getItemId();

            switch (id){
                case R.id.official:
                    frag = new FragmentOfficial();
                    mDrawerLayout.closeDrawers();
                    UpdateFragment();
                    break;
                case R.id.weeklies:
                    frag = new FragmentWeeklies();
                    mDrawerLayout.closeDrawers();
                    UpdateFragment();
                    break;
                case R.id.test:
                    frag = new FragmentTest();
                    mDrawerLayout.closeDrawers();
                    UpdateFragment();
                    break;
                case R.id.gappsbanks:
                    frag = new FragmentGappsBanks();
                    mDrawerLayout.closeDrawers();
                    UpdateFragment();
                    break;
                case R.id.gappstbo:
                    frag = new FragmentGappsTBO();
                    mDrawerLayout.closeDrawers();
                    UpdateFragment();
                    break;
                default:
                    frag = new FragmentOfficial();
                    UpdateFragment();
            }
            return true;
    }
}
