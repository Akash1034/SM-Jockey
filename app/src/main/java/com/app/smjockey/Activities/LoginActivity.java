package com.app.smjockey.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.app.smjockey.R;
import com.app.smjockey.Utils.Constants;
import com.app.smjockey.Volley.NetworkCalls;
import com.app.smjockey.Volley.Responses;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    Button loginButton;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    ProgressBar progressBar;
    boolean cancel;
    View focusView;
    private String user_token = null;
    private AutoCompleteTextView userNameView;
    private EditText passwordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences(Constants.TOKEN_FILE, 0);
        initView();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        userNameView.setError(null);
        passwordView.setError(null);

        String email = userNameView.getText().toString();
        String password = passwordView.getText().toString();

        cancel = false;
        focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            userNameView.setError(getString(R.string.error_invalid_email));
            focusView = userNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }

        else {
            String encoded = email + ":" + password;
            String base64 = null;

            try {
                base64 = Base64.encodeToString(encoded.getBytes("UTF-8"), Base64.DEFAULT);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            authenticate("Basic " + base64);
        }
    }

    public void authenticate(final String auth) {

        NetworkCalls.authUser(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

                progressBar.setVisibility(View.INVISIBLE);

                Log.d(TAG, "Authenication response :" + response.toString());
                try {
                    user_token = response.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor = pref.edit();
                editor.putString("user_token", user_token);
                editor.putBoolean("loggedIn", true);
                editor.apply();
                Intent userActivityIntent = new Intent(LoginActivity.this, StreamActivity.class);
                startActivity(userActivityIntent);
                finish();

            }

            @Override
            public void onSuccessResponse(String response) {

            }

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Volley Error: " + error.toString());
                Toast.makeText(getApplicationContext(), "User Not Authenticated", Toast.LENGTH_SHORT).show();
                userNameView.setText(null);
                passwordView.setText(null);
                focusView = userNameView;
                focusView.requestFocus();

                progressBar.setVisibility(View.INVISIBLE);


            }
        }, auth, Constants.auth_url);

    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void initView()
    {
        userNameView = (AutoCompleteTextView) findViewById(R.id.user);
        passwordView = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_in_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


}
