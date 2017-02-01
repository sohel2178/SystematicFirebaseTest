package com.baudiabatash.systematicfirebasetest.Navigation;


import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baudiabatash.systematicfirebasetest.AppUtility.Constant;
import com.baudiabatash.systematicfirebasetest.AppUtility.MyUtils;
import com.baudiabatash.systematicfirebasetest.CustomView.MyEditText;
import com.baudiabatash.systematicfirebasetest.DialogFragment.UploadImageDialog;
import com.baudiabatash.systematicfirebasetest.Interfaces.NavDrawerListener;
import com.baudiabatash.systematicfirebasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener{


    private ActionBar actionBar;

    private TextView tvChangeImage;
    private CircleImageView ivProfileImage;

    private ImageView ivTick,ivClose;

    private MyEditText etName,etEmail;

    private Button btnVerify;

    private FirebaseUser firebaseUser;

    private Uri imageUri;

    private NavDrawerListener navDrawerListener;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        navDrawerListener = (NavDrawerListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);

        if(firebaseUser!=null){
            setData();
        }
        return view;
    }

    private void setData() {
        Uri uri = firebaseUser.getPhotoUrl();
        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();

        if(uri!=null){
            Picasso.with(getActivity())
                    .load(uri)
                    .into(ivProfileImage);
        }else{
            ivProfileImage.setImageResource(R.drawable.placeholder);
        }

        if(name!=null){
            etName.setText(name);
        }

        if(email!=null){
            etEmail.setText(email);
        }
    }

    private void initView(View view) {

        etName = (MyEditText) view.findViewById(R.id.name);
        etEmail = (MyEditText) view.findViewById(R.id.email);

        tvChangeImage = (TextView) view.findViewById(R.id.change_image);

        ivClose = (ImageView) view.findViewById(R.id.close);
        ivTick = (ImageView) view.findViewById(R.id.tick);

        btnVerify = (Button) view.findViewById(R.id.verify);


        ivProfileImage = (CircleImageView) view.findViewById(R.id.profile_image);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivClose.setOnClickListener(this);
        ivTick.setOnClickListener(this);

        tvChangeImage.setOnClickListener(this);

        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        actionBar.hide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                getFragmentManager().popBackStack();
                break;
            case R.id.tick:

                UserProfileChangeRequest userProfileChangeRequest = null;

                if(imageUri!=null){
                    userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(imageUri)
                            .setDisplayName(etName.getText().toString().trim())
                            .build();


                }else{
                    userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etName.getText().toString().trim())
                            .build();
                }

                firebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d("SOHEl", "User profile updated.");

                            if(navDrawerListener!= null){
                                navDrawerListener.setImage(imageUri);
                                navDrawerListener.setName(etName.getText().toString().trim());
                            }
                            getFragmentManager().popBackStack();
                        }

                    }
                });

                break;

            case R.id.change_image:
                UploadImageDialog uploadImageDialog = new UploadImageDialog();
                uploadImageDialog.setTargetFragment(this,Constant.DIALOGFRAGMENT_REQUEST);
                uploadImageDialog.show(getFragmentManager(), Constant.DEFAULT_FRAGMENT_TAG);
                break;

            case R.id.verify:
                firebaseUser.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Sohel", "Email sent.");
                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constant.DIALOGFRAGMENT_REQUEST:

                Bundle bundle = data.getExtras();
                String path = bundle.getString(Constant.URI);

                imageUri = MyUtils.pathToUri(path);

                ivProfileImage.setImageURI(imageUri);

                Log.d("Path",path);
                /*imageBitmap = MyUtils.resizeBitmap(path,150,150);
                ivProfileImage.setImageBitmap(imageBitmap);*/
                break;
        }
    }
}
