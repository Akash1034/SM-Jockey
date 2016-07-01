package com.app.smjockey.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

    private final String TAG=LoginActivity.class.getSimpleName();

    private String user_token=null;
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    Button loginButton;

    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences(Constants.TOKEN_FILE, 0);

        mUserNameView = (AutoCompleteTextView) findViewById(R.id.user);
        mPasswordView = (EditText) findViewById(R.id.password);
        loginButton=(Button)findViewById(R.id.login_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        mUserNameView.setError(null);
        mPasswordView.setError(null);

        String email = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mUserNameView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String encoded= email+":"+password;
            String base64=null;

            try {
                base64 = Base64.encodeToString(encoded.getBytes("UTF-8"), Base64.DEFAULT);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            authenticate("Basic "+base64);
        }
    }

    public void authenticate(final String auth) {

        NetworkCalls.authUser(new Responses() {
            @Override
            public void onSuccessResponse(JSONObject response) {

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
                Toast.makeText(getApplicationContext(),"User Not Authenticated",Toast.LENGTH_SHORT).show();

            }
        },auth,Constants.auth_url);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}
