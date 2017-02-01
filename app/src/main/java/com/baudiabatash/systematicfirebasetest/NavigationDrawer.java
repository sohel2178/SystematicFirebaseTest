package com.baudiabatash.systematicfirebasetest;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baudiabatash.systematicfirebasetest.Navigation.HomeFragment;
import com.baudiabatash.systematicfirebasetest.Navigation.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawer extends Fragment implements View.OnClickListener{

    //private RelativeLayout rlHome,rlProfile,rlStores,rlViewAllManager,rlAllCustomers,rlEmployees,rlTransaction,rlLogout;


    public static final String PREF_NAME ="mypref";
    public static final String KEY_USER_LEARNED_DRAWERR="user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private CircleImageView profileImage;
    private TextView tvName,tvEmail;

    private View containerView;

    FirebaseAuth firebaseAuth;

    private FirebaseUser firebaseUser;

    private RelativeLayout rlHome,rlProfile,rlLogout;








    public NavigationDrawer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
/*
        userLocalStore = new UserLocalStore(getActivity());
        user = userLocalStore.getUser();*/





        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(),KEY_USER_LEARNED_DRAWERR,"false"));

        // if saveInstanceState is not null its coming back from rotation
        if(savedInstanceState!=null){
            mFromSavedInstanceState=true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_navigation_drawer, container, false);


        //Initialize View

        initView(view);

        if(firebaseUser!=null){
            updateTopSection();
        }

        return view;
    }

    private void updateTopSection() {
        Uri imageUrl = firebaseUser.getPhotoUrl();
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();

        if(imageUrl!=null){
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(profileImage);
        }

        if(name!=null){
            tvName.setText(name);
        }

        if(email!=null){
            tvEmail.setText(email);
        }



    }

    private void initView(View view) {
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        tvName = (TextView) view.findViewById(R.id.name);
        tvEmail = (TextView) view.findViewById(R.id.email);

        rlHome = (RelativeLayout) view.findViewById(R.id.home);
        rlProfile = (RelativeLayout) view.findViewById(R.id.profile);
        rlLogout = (RelativeLayout) view.findViewById(R.id.logout);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rlHome.setOnClickListener(this);
        rlProfile.setOnClickListener(this);
        rlLogout.setOnClickListener(this);




    }

    public void setUp(int fragmentId, DrawerLayout layout, final Toolbar toolbar) {

        containerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = layout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(),mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //if user gonna not seen the drawer before thats mean the drawer is open for the first time

                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer=true;
                    // save it in sharedpreferences
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWERR,mUserLearnedDrawer+"");

                    getActivity().invalidateOptionsMenu();
                }

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

               /* getView().setTranslationX(.3f * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();*/

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //Log.d("GGGG",slideOffset+"");

/*
                    getView().setTranslationX(slideOffset * drawerView.getWidth());
                    mDrawerLayout.bringChildToFront(drawerView);
                    mDrawerLayout.requestLayout();

                super.onDrawerSlide(drawerView, slideOffset);*/



            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context, String key, String prefValue){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key,prefValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String key, String defaultValue){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return pref.getString(key,defaultValue);
    }





    public void setUserName(String name){
        tvName.setText(name);
    }

    public void setUserImage(String url){
        Picasso.with(getActivity())
                .load(url)
                .into(profileImage);
    }

    public void setUserImage(Uri uri){
        profileImage.setImageURI(uri);
    }

    public void setUserEmail(String email){
        tvEmail.setText(email);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                getFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment())
                        .addToBackStack(null).commit();
                break;

            case R.id.profile:


                mDrawerLayout.closeDrawer(Gravity.LEFT);
                getFragmentManager().beginTransaction().replace(R.id.main_container,new ProfileFragment())
                        .addToBackStack(null).commit();


                break;

            case R.id.logout:


               firebaseAuth.signOut();
                getActivity().finish();


                break;
        }
    }
}
