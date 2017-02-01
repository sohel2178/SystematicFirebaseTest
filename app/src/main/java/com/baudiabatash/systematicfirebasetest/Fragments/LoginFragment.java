package com.baudiabatash.systematicfirebasetest.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.baudiabatash.systematicfirebasetest.AppUtility.Constant;
import com.baudiabatash.systematicfirebasetest.AppUtility.MyUtils;
import com.baudiabatash.systematicfirebasetest.CustomView.MyEditText;
import com.baudiabatash.systematicfirebasetest.MainActivity;
import com.baudiabatash.systematicfirebasetest.R;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private ActionBar actionBar;

    private ImageView ivClose;
    private MyEditText etEmail,etPassword;

    private Button btnLogin,btnRegister;

    private SignInButton btnGSignIn;


    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authListener;

    FirebaseUser firebaseUser;


    private GoogleApiClient googleApiClient;



    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        firebaseAuth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(getActivity(),MainActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);
        
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private void initView(View view) {
        ivClose = (ImageView) view.findViewById(R.id.close);
        etEmail = (MyEditText) view.findViewById(R.id.email);
        etPassword = (MyEditText) view.findViewById(R.id.password);
        btnLogin = (Button) view.findViewById(R.id.login);
        btnRegister = (Button) view.findViewById(R.id.register);
        btnGSignIn = (SignInButton) view.findViewById(R.id.btn_google_sign_in);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnGSignIn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(actionBar!= null){
            actionBar.hide();
        }

    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, Constant.GOOGLE_SIGN_IN_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:

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

                login(email,password);




                break;

            case R.id.register:
                getFragmentManager().beginTransaction().replace(R.id.main_container,new RegisterFragment()).addToBackStack(null).commit();
                break;

            case R.id.btn_google_sign_in:
                googleSignIn();
                break;


        }
    }

    private void login(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Sohel", "signInWithEmail:onComplete:" + task.isSuccessful());

                        getActivity().finish();

                        startActivity(new Intent(getActivity(),MainActivity.class));

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                       /* if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());

                        }*/

                        // ...
                    }
                });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Constant.GOOGLE_SIGN_IN_REQUEST:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    Log.d("Sohel","Success");
                    Log.d("Sohel",account.getEmail());

                    firebaseAuthWithGoogle(account);
                } else {
                    // Google Sign In failed, update UI appropriately
                    // ...
                }
                break;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d("IDTOKEN",account.getIdToken());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("Sohel","Successful");
                }else{
                    Log.d("Sohel","Un Successful");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Sohel",e.getMessage());
            }
        });
    }
}
