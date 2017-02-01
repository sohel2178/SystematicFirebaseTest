package com.baudiabatash.systematicfirebasetest.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baudiabatash.systematicfirebasetest.AppUtility.MyUtils;
import com.baudiabatash.systematicfirebasetest.CustomView.MyEditText;
import com.baudiabatash.systematicfirebasetest.MainActivity;
import com.baudiabatash.systematicfirebasetest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{


    private ActionBar actionBar;

    private ImageView ivClose;
    private MyEditText etEmail,etPassword;

    private Button btnRegister;

    private FirebaseAuth firebaseAuth;


    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_register, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {

        ivClose = (ImageView) view.findViewById(R.id.close);
        etEmail = (MyEditText) view.findViewById(R.id.email);
        etPassword = (MyEditText) view.findViewById(R.id.password);
        btnRegister = (Button) view.findViewById(R.id.register);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ivClose.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:
                getFragmentManager().popBackStack();
                break;

            case R.id.register:

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.requestFocus();
                    Toast.makeText(getActivity(), "Email is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!MyUtils.validateEmail(email)){
                    etEmail.requestFocus();
                    Toast.makeText(getActivity(), "Email is Valid", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    etPassword.requestFocus();
                    Toast.makeText(getActivity(), "Password Field is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(email,password);

                break;
        }
    }

    private void registerUser(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getActivity().finish();

                    startActivity(new Intent(getActivity(), MainActivity.class));
                }else{
                    Toast.makeText(getActivity(), "Register is not Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
