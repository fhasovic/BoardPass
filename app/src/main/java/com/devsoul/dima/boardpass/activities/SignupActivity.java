package com.devsoul.dima.boardpass.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The Signup Activity enables the user to create an account in the application,
 * and is generally displayed via the link on the Login Activity.
 */
public class SignupActivity extends Activity
{
    private static final String TAG = SignupActivity.class.getSimpleName();

    private static final String PASSWORD_PATTERN =
                    "((?=.*\\d)" +        // must contains one digit from 0-9
                    "(?=.*[a-z])" +       // must contains one lowercase characters
                    "(?!.*\\s)" +         // disallow spaces
                    ".{2,})";             // length at least 2 characters
    private static final int DIALOG_ID = 0;         // Dialog for date
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    @InjectView(R.id.input_FullName) EditText inputFullName;
    @InjectView(R.id.input_BirthDate) EditText inputBirthDate;
    @InjectView(R.id.input_id) EditText inputID;
    @InjectView(R.id.input_VIPPref) EditText inputVIP;
    @InjectView(R.id.input_email) EditText inputEmail;
    @InjectView(R.id.input_password) EditText inputPassword;
    @InjectView(R.id.link_login) TextView btnLinkToLogin;

    private ImageButton img_btnRegister;

    // For birth date
    private Calendar calendar;
    private int year, month, day;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private Client user_model;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Register button
        img_btnRegister = (ImageButton) findViewById(R.id.img_btn_SignUp);

        // Inject the ButterKnife design
        ButterKnife.inject(this);

        // Set calendar date to today date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Create client object
        user_model = new Client();

        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {
            // User is already logged in. Take him to user activity
            Intent intent = new Intent(SignupActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

        // birth date Button Click event
        inputBirthDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setDate();
            }
        });

        // Sign up Button Click event
        img_btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signup();
            }
        });

        // Link to login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate()
    {
        // onCreateDialog method called
        showDialog(DIALOG_ID);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id)
    {
        if (id == DIALOG_ID)
        {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener()
    {
        /**
         * Set the date that was chosen
         * @param arg0 - The object
         * @param arg1 - Year
         * @param arg2 - Month
         * @param arg3 - Day
         */
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3)
        {
            showDate(arg1, arg2+1, arg3);
        }
    };

    /**
     * Show the date that was chosen in the client birth date input text
     * @param year
     * @param month
     * @param day
     */
    private void showDate(int year, int month, int day)
    {
        inputBirthDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    /**
     * This function performs the sign up operation.
     */
    public void signup()
    {
        Log.d(TAG, "Signup");

        String FullName = inputFullName.getText().toString().trim();
        String BirthDate = inputBirthDate.getText().toString().trim();
        String ID = inputID.getText().toString().trim();
        String VIP = inputVIP.getText().toString().trim();
        String Email = inputEmail.getText().toString().trim();
        String Password = inputPassword.getText().toString().trim();

        user_model.SetFullName(FullName);
        user_model.SetBirthDate(BirthDate);
        user_model.SetID(ID);
        user_model.SetVIP(VIP);
        user_model.SetEmail(Email);
        user_model.SetPassword(Password);

        // One of the fields is invalid
        if (!validate())
        {
            onSignupFailed("Sign up failed");
            return;
        }

        onSignupFailed("Sign up complete");
        registerUser();
    }

    /**
     * This function shows a message to the user that the sign up has failed.
     */
    public void onSignupFailed(String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * This is a validation function that checks all the fields.
     * @return boolean - This returns true if all the fields are valid, false if one of the fields is invalid.
     */
    public boolean validate()
    {
        boolean valid = true;

        // Full name validation
        if (inputFullName.getText().toString().isEmpty())
        {
            inputFullName.setError("Enter your full name !");
            if (valid == true)
                requestFocus(inputFullName);
            valid = false;
        }
        else
        {
            inputFullName.setError(null);
        }

        // Birth Date validation
        try
        {
            if (inputBirthDate.getText().toString().isEmpty() ||
                    (formatter.parse(inputBirthDate.getText().toString()).after(new Date())))
            {
                inputBirthDate.setError("Enter your birth date !");
                valid = false;
            }
            else
            {
                inputBirthDate.setError(null);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Email validation
        if (inputEmail.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches())
        {
            inputEmail.setError("Enter a valid email address !");
            if (valid == true)
                requestFocus(inputEmail);
            valid = false;
        }
        else
        {
            inputEmail.setError(null);
        }

        // Password validation
        if (!Pattern.compile(PASSWORD_PATTERN).matcher(inputPassword.getText().toString()).matches())
        {
            inputPassword.setError("Password must contain at least one number and one lower case letter !");
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
     * Function to store user in MySQL database,
     * will post all params to register url
     */
    private void registerUser()
    {
        // Tag used to cancel the request
        String tag_string_req = "register_request";

        pDialog.setMessage("Creating Account ...");
        showDialog();

        // Making the volley http request
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.SERVER_URL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Log.d(TAG, "register Response: " + response.toString());
                hideDialog();

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error)
                    {
                        // User successfully stored in MySQL
                        // Now store the user in SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        user_model.SetFullName(user.getString("full_name"));
                        user_model.SetBirthDate(user.getString("birth_date"));
                        user_model.SetID(user.getString("id_card"));
                        user_model.SetVIP(user.getString("vip_pref"));
                        user_model.SetEmail(user.getString("email"));
                        user_model.SetCreated_At(user.getString("created_at"));

                        // Inserting row in users table
                        db.addUser(user_model);

                        session.setLogin(true);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        onSignupFailed(errorMsg);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                onSignupFailed(error.getMessage());
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Posting parameters to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("full_name", user_model.GetFullName());
                params.put("birth_date", user_model.GetBirthDate());
                params.put("id_card", user_model.GetID());
                params.put("vip_pref", user_model.GetVIP());
                params.put("email", user_model.GetEmail());
                params.put("password", user_model.GetPassword());

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
