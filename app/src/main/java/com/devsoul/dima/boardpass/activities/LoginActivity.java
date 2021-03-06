package com.devsoul.dima.boardpass.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.app.AppConfig;
import com.devsoul.dima.boardpass.app.AppController;
import com.devsoul.dima.boardpass.helper.SQLiteHandler;
import com.devsoul.dima.boardpass.helper.SessionManager;
import com.devsoul.dima.boardpass.model.Client;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The Login Activity is the main activity of the application.
 */
public class LoginActivity extends Activity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    private ImageButton imgbtnLogin;

    @InjectView(R.id.input_email)
    EditText inputEmail;
    @InjectView(R.id.input_password)
    EditText inputPassword;
    @InjectView(R.id.link_SignUp)
    TextView btnLinkToRegister;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imgbtnLogin = (ImageButton) findViewById(R.id.img_btn_login);

        // Inject the ButterKnife design
        ButterKnife.inject(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {

            // User is already logged in. Take him to user screen
            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        imgbtnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        // Link to register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Start the registration activity
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This function performs the login operation.
     */
    public void login()
    {
        Log.d(TAG, "login");

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        // One of the fields is invalid
        if (!validate(email, password))
        {
            onLoginFailed("login failed");
            return;
        }

        // login user
        checkLogin(email, password);
    }

    /**
     * This function shows a message to the user that the login has failed.
     */
    public void onLoginFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * This is a validation function that checks two fields: Email and Password.
     * @param email - The email that was entered.
     * @param password - The password that was entered.
     * @return boolean - This returns true if all the fields are valid, false if one of the fields is invalid.
     */
    public boolean validate(final String email, final String password)
    {
        boolean valid = true;

        // Email validation
        if (email.isEmpty())
        {
            inputEmail.setError("Enter a valid email address");
            requestFocus(inputEmail);
            valid = false;
        }
        else
        {
            inputEmail.setError(null);
        }

        // Password validation
        if (password.isEmpty())
        {
            inputPassword.setError("Enter a valid password");
            if (valid == true)
                requestFocus(inputPassword);
            valid = false;
        }
        else
        {
            inputPassword.setError(null);
        }

        return valid;
    }

    /**
     * Set focus on view
     * @param view
     */
    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Function to verify login details in mysql db using volley http request
     * @param email
     * @param password
     */
    private void checkLogin(final String email, final String password)
    {
        // Tag used to cancel the request
        String tag_string_req = "login_request";

        pDialog.setMessage("Logging in ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.SERVER_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "login Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        // user successfully logged in

                        // Now store the user in SQLite
                        JSONObject user = jObj.getJSONObject("user");

                        Client user_model = new Client();
                        user_model.SetFullName(user.getString("full_name"));
                        user_model.SetBirthDate(user.getString("birth_date"));
                        user_model.SetID(user.getString("id_card"));
                        user_model.SetVIP(user.getString("vip_pref"));
                        user_model.SetEmail(user.getString("email"));
                        user_model.SetCreated_At(user.getString("created_at"));

                        // Inserting row in users table
                        db.addUser(user_model);

                        // Create login session
                        session.setLogin(true);

                        // Get the type of user to know if it User or Admin
                        int userType = Integer.parseInt(user.getString("user_type"));
                        if (userType == 1)
                            // Create user type session
                            session.setType(1);
                        else
                            // Create admin type session
                            session.setType(2);

                        // Launch user activity
                        Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        onLoginFailed(errorMsg);
                    }
                }
                catch (JSONException e)
                {
                    // JSON error
                    e.printStackTrace();
                    onLoginFailed("Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, "login Error: " + error.getMessage());
                onLoginFailed(error.getMessage());
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Show the progress dialog
     */
    private void showDialog()
    {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**
     * Hide the progress dialog
     */
    private void hideDialog()
    {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
