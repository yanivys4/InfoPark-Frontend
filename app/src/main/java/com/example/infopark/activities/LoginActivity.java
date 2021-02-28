package com.example.infopark.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.infopark.R;
import com.example.infopark.RESTApi.LoginForm;
import com.example.infopark.RESTApi.LoginResponse;
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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This activity allow the user to login for the application manually or via google.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String ACTION_LOGIN_ACTIVITY =
            "android.intent.action.ACTION_LOGIN_ACTIVITY";
    private static final int RC_SIGN_IN = 9000;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private GoogleSignInClient googleSignInClient;
    private SharedPreferences sharedPref;

    // View members
    private TextInputLayout textInputEmailOrUsername;
    private TextInputLayout textInputPassword;
    private Button loginButton;
    private ProgressBar progressBar;
    private ImageButton skipButton;
    private ImageButton backButton;

    /**
     * Hook method called when a new instance of Activity is
     * created. One time initialization code goes here, e.g.,
     * builds a GoogleSignInClient with the options specified by gso.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialize the views.
        initializeViews();

        // Initialize the sharedPref
        Context context = LoginActivity.this;
        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        textInputEmailOrUsername = findViewById(R.id.text_input_email_or_usernme);
        textInputPassword = findViewById(R.id.text_input_password);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        skipButton = findViewById(R.id.skip_button);
        backButton = findViewById(R.id.back_button);

        if (getCallingActivity() == null) {
            backButton.setVisibility(View.INVISIBLE);
        } else {
            skipButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * This function is the onCLick method of the login button in the login form.
     * The function run all the validate methods and if all of them returns true that means the
     * input is valid. then, the function send the data entered to the BackEnd using the login
     * method.
     * @param v the View
     */
    public void loginClick(View v) {
        String emailOrUsernameInput = Objects.requireNonNull(textInputEmailOrUsername.getEditText().getText().toString().trim());
        String passwordInput = Objects.requireNonNull(Objects.requireNonNull(textInputPassword.getEditText()).getText().toString().trim());

        if (!validateEmailOrUserName(emailOrUsernameInput) || !validatePassword(passwordInput)) {
            return;
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
                    Utils.showToast(LoginActivity.this, "Code:" + response.code());
                    return;
                }

                ResponseMessage responseMessage = response.body();
                assert responseMessage != null;
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
            public void onFailure(@NonNull Call<ResponseMessage> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(LoginActivity.this, t.getMessage());
            }
        });
    }

    /**
     * This function validate the email or userName entered by the user and shows the flaws of input if exist.
     * @param emailOrUsernameInput the string that the user entered
     * @return boolean whether the email or userName is valid.
     */
    private boolean validateEmailOrUserName(String emailOrUsernameInput) {
        if (emailOrUsernameInput.isEmpty()) {
            textInputEmailOrUsername.setError(getString(R.string.field_cant_be_empty));
            return false;
        } else {
            textInputEmailOrUsername.setError(null);
            return true;
        }
    }

    /**
     * This function validate the password entered by the user and shows the flaws of input if exist.
     * @param passwordInput the string that the user entered
     * @return boolean whether the password is valid.
     */
    private boolean validatePassword(String passwordInput) {
        if (passwordInput.isEmpty()) {
            textInputPassword.setError(getString(R.string.field_cant_be_empty));
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    /**
     * This function using the RestApi to set the login attempt (manual or via google) to be
     * approved by the user
     * @param loginForm an object that contains the login data to be sent to the server.
     */
    private void login(LoginForm loginForm) {
        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<LoginResponse> secondCall = restApi.login(loginForm);
        secondCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> secondCall, @NonNull Response<LoginResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                if (!response.isSuccessful()) {
                    Utils.showToast(LoginActivity.this, "Code:" + response.code());
                    return;
                }

                LoginResponse responseMessage = response.body();
                assert responseMessage != null;
                if (!responseMessage.getSuccess()) {
                    Utils.showToast(LoginActivity.this, responseMessage.getDescription());
                } else {
                    setIsLoggedInTrue();
                    setUserUniqueID(responseMessage.getUniqueID());
                    startMainActivity();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(LoginActivity.this, t.getMessage());
            }
        });
    }

    /**
     * This function is onClick method of the sign in to google button.
     * the function connects to google Oauth service and tries to sign in.
     * @param view the view.
     */
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

    /**
     * Dispatch incoming result to the correct fragment.
     */
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

    /**
     * This function is called from onActivityResult method after sign in was completed successfully.
     * The function creates a GoogleSignInAccount from the completed task of signing and according
     * to it build a register form and use it to register with the register method.
     * @param completedTask the getSignedInAccountFromIntent task.
     */
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

    /**
     * Factory method that returns an Intent for starting the RegisterActivity.
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_LOGIN_ACTIVITY);
    }

    /**
     * This function start the main activity.
     */
    private void startMainActivity() {
        Intent startIntent = MainActivity.makeIntent();
        startActivity(startIntent);
        finish();
    }

    /**
     * This function skip the login activity.
     * @param view the View
     */
    public void skipActivity(View view) {
        startMainActivity();
    }

    /**
     * This function start the register activity.
     * @param view the View
     */
    public void registerActivity(View view) {
        Intent startIntent = RegisterActivity.makeIntent();
        startActivity(startIntent);

    }



    /**
     * This function finish the activity.
     * @param view the view
     */
    public void finishActivity(View view) {
        this.finish();
    }

    /**
     * This function set the loggedIn true in the SharedPreferences.
     */
    private void setIsLoggedInTrue() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.loggedIn), true);
        editor.apply();
    }

    /**
     * This function set the user unique id in the SharedPreferences.
     * @param uniqueID the user unique id
     */
    private void setUserUniqueID(String uniqueID){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.uniqueID), uniqueID);
        editor.apply();
    }
}
