package com.baudiabatash.systematicfirebasetest.DialogFragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.baudiabatash.systematicfirebasetest.AppUtility.Constant;
import com.baudiabatash.systematicfirebasetest.AppUtility.MyUtils;
import com.baudiabatash.systematicfirebasetest.R;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadImageDialog extends DialogFragment implements View.OnClickListener{
    private ImageView gallery,camera;

    private static final int REQUEST_CAMERA =200;


    public UploadImageDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_upload_image_dialog, container, false);
        
        initView(view);
        return view;
    }

    private void initView(View view) {
        camera = (ImageView) view.findViewById(R.id.camera);
        gallery = (ImageView) view.findViewById(R.id.gallery);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera:
                checkCameraPermission();

                break;
            case R.id.gallery:

                RxImagePicker.with(getActivity()).requestImage(Sources.GALLERY).subscribe(new Action1<Uri>() {
                    @Override
                    public void call(Uri uri) {

                        String path = MyUtils.getPath(getActivity(),uri);

                        // Send Uri Back
                        sendBackUri(path);
                    }
                });


                break;
        }
    }


    private void sendBackUri(String path){

        Bundle bundle = new Bundle();
        bundle.putString(Constant.URI, path);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(Constant.DIALOGFRAGMENT_REQUEST, Activity.RESULT_OK,intent);
        getDialog().dismiss();

    }


    private void checkCameraPermission() {
        boolean isGranted;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();

        } else {
            takePicture();
        }
    }

    void takePicture() {

        RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA).subscribe(new Action1<Uri>() {
            @Override
            public void call(Uri uri) {

                sendBackUri(MyUtils.getPath(getActivity(),uri));

            }
        });
    }

    private void requestCameraPermission() {
        // Camera permission has not been granted yet. Request it directly.
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case REQUEST_CAMERA:

                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    takePicture();

                } else {
                    //Permission not granted
                    //Toast.makeText(MainActivity.this,"You need to grant camera permission to use camera",Toast.LENGTH_LONG).show();
                }
                break;



        }
    }
}
