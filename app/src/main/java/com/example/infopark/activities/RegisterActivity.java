package com.example.infopark.activities;



import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.infopark.Utils.InputValidator;
import com.example.infopark.RESTApi.LatLng;
import com.example.infopark.R;
import com.example.infopark.RESTApi.RegisterForm;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private static final String ACTION_REGISTER_ACTIVITY =
            "android.intent.action.ACTION_REGISTER_ACTIVITY";
    private static final int RC_SIGN_IN = 9000;
    private static final String TAG = "RegisterActivity";
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUserName;
    private TextInputLayout textInputPassword;
    private ProgressBar progressBar;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputUserName = findViewById(R.id.text_input_userName);
        textInputPassword = findViewById(R.id.text_input_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    public static Intent makeIntent() {
        return new Intent(ACTION_REGISTER_ACTIVITY);
    }

    private boolean validateEmail(){
        String emailInput = Objects.requireNonNull(textInputEmail.getEditText()).getText().toString().trim();
        if(emailInput.isEmpty()){
            textInputEmail.setError("Field can't be empty");
            return false;
        }else if(!InputValidator.isEmailValid((emailInput))) {
            textInputEmail.setError("Email is invalid");
            return false;
        }
        textInputEmail.setError(null);
        return true;
    }

    private boolean validateUserName(){
        String userNameInput = Objects.requireNonNull(textInputUserName.getEditText()).getText().toString().trim();
        if(userNameInput.isEmpty()){
            textInputUserName.setError("Field can't be empty");
            return false;
        }else if(userNameInput.length() > 15){
            textInputUserName.setError("Username too long");
            return false;
        }else{
            textInputUserName.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String passwordInput = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString().trim();
        if(passwordInput.isEmpty()){
            textInputPassword.setError("Field can't be empty");
            return false;
        }else if(!InputValidator.isPasswordValid(passwordInput)){
            textInputPassword.setError(InputValidator.getPasswordMistakes(passwordInput));
            return false;
        }
        else{
            textInputPassword.setError(null);
            return true;
        }
    }

    public void confirmInput(View v){
        if(!validateEmail() | !validateUserName() | !validatePassword()){
            return;
        }
        else{

            String userName = Objects.requireNonNull(textInputUserName.getEditText()).getText().toString();
            String email = Objects.requireNonNull(textInputEmail.getEditText()).getText().toString();
            String password = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString();

            // when current location will work it will be extracted from it
            LatLng latlng = new LatLng(35.896544f, 74.52323f);
            RegisterForm registerForm = new RegisterForm(userName, email,
                    password, latlng, 1, 1,false,false);
            register(registerForm,false);
        }
    }


    private void register(RegisterForm registerForm,boolean googleUser){
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<ResponseMessage> call = restApi.register(registerForm);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseMessage>() {

            @Override
            public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {

                    Utils.showToast(RegisterActivity.this,"Code:" + response.code());
                    return;
                }

                ResponseMessage responseMessage = response.body();
                if (!responseMessage.getSuccess()) {
                    Utils.showToast(RegisterActivity.this,responseMessage.getDescription());
                } else {
                    showDialog(googleUser);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(RegisterActivity.this,t.getMessage());
            }
        });
    }



    private void showDialog(boolean googleUser){
        AlertDialog.Builder builder = new AlertDialog.Builder
                (RegisterActivity.this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_blue_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        if(!googleUser){
            ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.dialog_title));
            ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialog_message));
        }else{
            ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.dialog_title_google));
            ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialog_message_google));
        }


        ((Button) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.dialog_button));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_done);

        AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                alertDialog.dismiss();
                finishActivity(view);
            }
        });

        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();

    }

    public void signInToGoogle(View view){
        signIn();
    }

    private void signIn(){
        googleSignInClient.revokeAccess()
          .addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            LatLng latlng = new LatLng(35.896544f, 74.52323f);
            RegisterForm registerForm = new RegisterForm(account.getDisplayName(),account.getEmail(),
                    null,latlng,1,1,false,true);
            register(registerForm,true);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    public void finishActivity(View view) {
        this.finish();
    }
}
