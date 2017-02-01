package com.baudiabatash.systematicfirebasetest;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;

import com.baudiabatash.systematicfirebasetest.Fragments.LoginFragment;
import com.baudiabatash.systematicfirebasetest.Interfaces.FragmentListener;
import com.baudiabatash.systematicfirebasetest.Interfaces.NavDrawerListener;
import com.baudiabatash.systematicfirebasetest.Navigation.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavDrawerListener{

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private FragmentManager manager;
    private NavigationDrawer drawerFragment;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MobileAds.initialize(getApplicationContext(), "ca-app-pub-7247754216255668/4255369636");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Set Navigation Drawer
        setUpNavigationDrawer();

        firebaseAuth =FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new LoginFragment()).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment()).commit();

            Log.d("Sohel",String.valueOf(firebaseUser.isEmailVerified()));

        }


    }

    private void setUpNavigationDrawer(){
        manager = getSupportFragmentManager();

        //Toolbar Code
        toolbar = (Toolbar) findViewById(R.id.tabanim_toolbar);
        setSupportActionBar(toolbar);

        //Drawer Layout Code
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);



        drawerFragment =
                (NavigationDrawer) manager.findFragmentById(R.id.fragment_navigation_drawer);


        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, toolbar);

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }


    @Override
    public void setName(String name) {
        drawerFragment.setUserName(name);

    }

    @Override
    public void setImage(Uri image) {
        drawerFragment.setUserImage(image);

    }
}
