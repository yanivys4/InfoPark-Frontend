package com.example.infopark.activities;

import android.content.Context;
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
import com.example.infopark.RESTApi.LatitudeLongitude;
import com.example.infopark.R;
import com.example.infopark.RESTApi.RegisterForm;
import com.example.infopark.RESTApi.ResponseMessage;
import com.example.infopark.RESTApi.RestApi;
import com.example.infopark.RESTApi.RetrofitClient;
import com.example.infopark.Utils.PasswordUtils;
import com.example.infopark.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * This activity allow the user to register for the application manually or via google.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String ACTION_REGISTER_ACTIVITY =
            "android.intent.action.ACTION_REGISTER_ACTIVITY";
    private static final int RC_SIGN_IN = 9000;
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private GoogleSignInClient googleSignInClient;
    // view members
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUserName;
    private TextInputLayout textInputPassword;
    private TextInputEditText emailEditText;
    private TextInputEditText userNameEditText;
    private TextInputEditText passwordEditText;
    private ProgressBar progressBar;
    private Context context;
    private TextView backButton;


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

        setContentView(R.layout.register);
        // Initialize the views.
        initializeViews();
        context = RegisterActivity.this;
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    //============================================================================================

    /**
     * Initialize the views.
     */
    private void initializeViews() {
        textInputEmail = findViewById(R.id.text_input_email);
        textInputUserName = findViewById(R.id.text_input_userName);
        textInputPassword = findViewById(R.id.text_input_password);
        emailEditText = findViewById(R.id.email_edit_text);
        userNameEditText = findViewById(R.id.user_name_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        ConstraintLayout register_layout = findViewById(R.id.register_layout);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getBaseContext().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.MULTIPLY);

        progressBar.setVisibility(View.INVISIBLE);
        backButton = findViewById(R.id.back_button);
        textInputEmail.setHint(getString(R.string.email));
        textInputUserName.setHint(getString(R.string.username));
        textInputPassword.setHint(getString(R.string.password));

        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus || Objects.requireNonNull(emailEditText.getText()).length()!=0){
                    textInputEmail.setHint(null);
                }else{
                    textInputEmail.setHint(getString(R.string.email));
                }
            }
        });

        userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus || Objects.requireNonNull(userNameEditText.getText()).length()!=0){
                    textInputUserName.setHint(null);
                }else{
                    textInputUserName.setHint(getString(R.string.username));
                }
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus || Objects.requireNonNull(passwordEditText.getText()).length()!=0){
                    textInputPassword.setHint(null);
                }else{
                    textInputPassword.setHint(getString(R.string.password));

                }
            }
        });


    }
    //============================================================================================

    /**
     * Factory method that returns an Intent for starting the RegisterActivity.
     *
     * @return Intent
     */
    public static Intent makeIntent() {
        return new Intent(ACTION_REGISTER_ACTIVITY);
    }
    //============================================================================================

    /**
     * This function validates the email entered by the user and shows the flaws of input if exist.
     *
     * @return boolean whether the email is valid.
     */
    private boolean validateEmail() {
        String emailInput = Objects.requireNonNull(textInputEmail.getEditText()).getText().toString().trim();
        if (emailInput.isEmpty()) {
            textInputEmail.setError(getString(R.string.field_cant_be_empty));
            return false;
        } else if (!InputValidator.isEmailValid((emailInput))) {
            textInputEmail.setError(getString(R.string.email_invalid));
            return false;
        }
        textInputEmail.setError(null);
        return true;
    }
    //============================================================================================

    /**
     * This function validates the userName entered by the user and shows the flaws of input if exist.
     *
     * @return boolean whether the userName is valid.
     */
    private boolean validateUserName() {
        String userNameInput = Objects.requireNonNull(textInputUserName.getEditText()).getText().toString().trim();
        if (userNameInput.isEmpty()) {
            textInputUserName.setError(getString(R.string.field_cant_be_empty));
            return false;
        } else if (userNameInput.length() > 15) {
            textInputUserName.setError(getString(R.string.username_too_long));
            return false;
        } else {
            textInputUserName.setError(null);
            return true;
        }
    }
    //============================================================================================

    /**
     * This function validates the password entered by the user and shows the flaws of input if exist.
     *
     * @return boolean whether the password is valid.
     */
    private boolean validatePassword() {
        String passwordInput = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString().trim();
        if (passwordInput.isEmpty()) {
            textInputPassword.setError(getString(R.string.field_cant_be_empty));
            return false;
        } else if (!InputValidator.isPasswordValid(passwordInput)) {
            textInputPassword.setError(InputValidator.getPasswordMistakes(passwordInput, RegisterActivity.this));
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
    //============================================================================================

    /**
     * This function is the onCLick method of the confirm button in the register form.
     * The function run all the validate methods and if all of them returns true that means the
     * input is valid. then, the function send the data entered to the BackEnd using the register
     * method.
     *
     * @param v the View
     */
    public void confirmInput(View v) {
        if (!validateEmail() | !validateUserName() | !validatePassword()) {
            return;
        } else {

            String userName = Objects.requireNonNull(textInputUserName.getEditText()).getText().toString();
            String email = Objects.requireNonNull(textInputEmail.getEditText()).getText().toString();
            String userPassword = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString();

            String salt = PasswordUtils.getSalt(30);
            String securedPassword = PasswordUtils.generateSecurePassword(userPassword, salt);
            RegisterForm registerForm = new RegisterForm(userName, email,
                    securedPassword, salt, new LatitudeLongitude(-100.0, -100.0), false, false, UUID.randomUUID().toString());
            register(registerForm);
        }
    }
    //============================================================================================

    /**
     * This function using the RestApi to set the register attempt (manual or via google) to be approved
     * by the server.
     *
     * @param registerForm an object that contains the register data to be sent to the server.
     */
    private void register(RegisterForm registerForm) {

        Retrofit retrofit = RetrofitClient.getInstance();
        // retrofit create rest api according to the interface
        RestApi restApi = retrofit.create(RestApi.class);
        Call<ResponseMessage> call = restApi.register(registerForm);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ResponseMessage>() {

            @Override
            public void onResponse(@NonNull Call<ResponseMessage> call, @NonNull Response<ResponseMessage> response) {

                if (!response.isSuccessful()) {
                    if (response.code() != 409) {
                        Utils.showToast(context, getString(R.string.network_error));
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }
                    assert response.errorBody() != null;
                    ResponseMessage responseMessage = Utils.convertJsonToResponseObject(response.errorBody(),
                            ResponseMessage.class);
                    String message = "";

                    switch (Objects.requireNonNull(responseMessage).getDescription()) {
                        case "something_went_wrong":
                            message = getString(R.string.something_went_wrong);
                            break;
                        case "username_already_exist":
                            message = getString(R.string.username_already_exist);
                            break;
                        case "email_already_exist":
                            message = getString(R.string.email_already_exist);
                            break;
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                    Utils.showToast(context, message);

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    // registration have succeeded.
                    showDialog(registerForm.getGoogleUser());
                }
            }

            // the communication with the server have failed.
            @Override
            public void onFailure(@NonNull Call<ResponseMessage> call, Throwable t) {

                progressBar.setVisibility(View.INVISIBLE);
                Utils.showToast(context, t.getMessage());
            }
        });

    }
    //============================================================================================

    /**
     * This function shows a dialog after registration has completed. the content of the dialog
     * depends on the googleUser parameter value.
     *
     * @param googleUser boolean
     *                   indicates if the user tries to register manually or via google.
     */
    private void showDialog(boolean googleUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder
                (RegisterActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.layout_blue_dialog,
                findViewById(R.id.layoutDialogContainer));
        builder.setView(view);

        if (!googleUser) {
            ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.dialog_title));
            ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialog_message));
        } else {
            ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.dialog_title_google));
            ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.dialog_message_google));
        }

        ((Button) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.dialog_button));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_done);

        AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(view1 -> {
            alertDialog.dismiss();
            finishActivity(view1);
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    //============================================================================================

    /**
     * This function is onClick method of the sign in to google button.
     * the function connects to google Oauth service and tries to sign in.
     *
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
    //============================================================================================

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
    //============================================================================================

    /**
     * This function is called from onActivityResult method after sign in was completed successfully.
     * The function creates a GoogleSignInAccount from the completed task of signing and according
     * to it build a register form and use it to register with the register method.
     *
     * @param completedTask the getSignedInAccountFromIntent task.
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            RegisterForm registerForm = new RegisterForm(account.getDisplayName(), account.getEmail(),
                    null, null, new LatitudeLongitude(-100.0, -100.0), false, true, UUID.randomUUID().toString());
            register(registerForm);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }
    //============================================================================================

    /**
     * on click function that is related to the back button and finish the activity.
     *
     * @param view the view
     */
    public void finishActivity(View view) {

        finish();
    }
    //============================================================================================
}
