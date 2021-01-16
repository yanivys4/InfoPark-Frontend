package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infopark.R;
import com.example.infopark.RESTApi.LatLng;
import com.example.infopark.RESTApi.LoginForm;
import com.example.infopark.RESTApi.RegisterForm;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.Utils.InputValidator;
import com.example.infopark.Utils.PasswordUtils;
import com.example.infopark.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private static final String ACTION_LOGIN_ACTIVITY =
            "android.intent.action.ACTION_LOGIN_ACTIVITY";
    private static final int RC_SIGN_IN = 9000;
    private static final String TAG = "LoginActivity";
    private TextInputLayout textInputEmailOrUsername;
    private TextInputLayout textInputPassword;
    private Button loginButton;
    private ProgressBar progressBar;
    private ImageButton skipButton;
    private ImageButton backButton;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        textInputEmailOrUsername = findViewById(R.id.text_input_email_or_usernme);
        textInputPassword = findViewById(R.id.text_input_password);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        skipButton = findViewById(R.id.skip_button);
        backButton = findViewById(R.id.back_button);

        if (getCallingActivity() == null) {
            backButton.setVisibility(View.INVISIBLE);
        }else{
            skipButton.setVisibility(View.INVISIBLE);
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void loginClick(View v) {
        String emailOrUsernameInput = Objects.requireNonNull(textInputEmailOrUsername.getEditText().getText().toString().trim());
        String passwordInput = Objects.requireNonNull(textInputPassword.getEditText().getText().toString().trim());

        if (emailOrUsernameInput.isEmpty()) {
            textInputEmailOrUsername.setError("Field can't be empty");
            return;
        }
        else {
            textInputEmailOrUsername.setError(null);
        }

        if (passwordInput.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return;
        }
        else {
            textInputPassword.setError(null);
        }

        String username = "";
        String email = "";

        if (InputValidator.isEmailValid(emailOrUsernameInput))
            email = emailOrUsernameInput;
        else
            username = emailOrUsernameInput;

        LoginForm formForSalt = new LoginForm(username, email, null, false);

        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        progressBar.setVisibility(View.VISIBLE);
        Call<ResponseMessage> firstCall = restApi.getSalt(formForSalt);
        String finalUsername = username;
        String finalEmail = email;
        firstCall.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMessage> firstCall, @NonNull Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(LoginActivity.this,"Code:" + response.code());
                    return;
                }

                ResponseMessage responseMessage = response.body();
                if (!responseMessage.getSuccess()) {
                    Utils.showToast(LoginActivity.this, responseMessage.getDescription());
                    return;
                } else {
                    String salt = responseMessage.getDescription();
                    String securedPassword = PasswordUtils.generateSecurePassword(passwordInput, salt);

                    LoginForm loginForm = new LoginForm(finalUsername, finalEmail, securedPassword, false);

                    login(loginForm);


                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(LoginActivity.this, t.getMessage());
            }
        });
    }

    private void login(LoginForm loginForm) {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<ResponseMessage> secondCall = restApi.login(loginForm);
        secondCall.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(@NonNull Call<ResponseMessage> secondCall, @NonNull Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(LoginActivity.this,"Code:" + response.code());
                    return;
                }

                ResponseMessage responseMessage = response.body();
                if (!responseMessage.getSuccess()) {
                    Utils.showToast(LoginActivity.this, responseMessage.getDescription());
                    return;
                } else {
                    Context context = LoginActivity.this;
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.loggedIn),true);
                    editor.apply();
                    startMainActivity();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(LoginActivity.this, t.getMessage());
            }
        });
    }

    public void signInToGoogle(View view) {
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

            LoginForm loginForm = new LoginForm(account.getDisplayName(), account.getEmail(), null, true);
            login(loginForm);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    public static Intent makeIntent() {
        return new Intent(ACTION_LOGIN_ACTIVITY);
    }


    private void startMainActivity(){
        Intent startIntent = MainActivity.makeIntent();
        startActivity(startIntent);
        finish();
    }

    public void skipActivity(View view) {
       startMainActivity();
    }

    public void registerActivity(View view) {
        Intent startIntent = RegisterActivity.makeIntent();
        startActivity(startIntent);
    }

    public void finishActivity(View view) {
        this.finish();
    }
}
