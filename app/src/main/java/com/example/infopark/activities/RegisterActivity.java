package com.example.infopark.activities;



import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private static final String ACTION_REGISTER_ACTIVITY =
            "android.intent.action.ACTION_REGISTER_ACTIVITY";

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUserName;
    private TextInputLayout textInputPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputUserName = findViewById(R.id.text_input_userName);
        textInputPassword = findViewById(R.id.text_input_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

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

            Retrofit retrofit = RetrofitClient.getInstance();
            // retrofit create rest api according to the interface
            RestApi restApi = retrofit.create(RestApi.class);

            // when current location will work it will be extracted from it
            LatLng latlng = new LatLng(35.896544f, 74.52323f);
            RegisterForm registerForm = new RegisterForm(userName, email,
                    password, latlng, 1, 1);
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
                        showDialog();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseMessage> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Utils.showToast(RegisterActivity.this,t.getMessage());
                }
            });
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder
                (RegisterActivity.this,R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_blue_dialog,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.dialog_title));
        ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialog_message));
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

    public void finishActivity(View view) {
        this.finish();
    }
}
