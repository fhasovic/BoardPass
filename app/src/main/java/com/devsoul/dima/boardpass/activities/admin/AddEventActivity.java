package com.devsoul.dima.boardpass.activities.admin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.activities.UserActivity;
import com.devsoul.dima.boardpass.app.AppConfig;
import com.devsoul.dima.boardpass.app.AppController;
import com.devsoul.dima.boardpass.helper.SQLiteHandler;
import com.devsoul.dima.boardpass.model.MyEvent;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The Add Event Activity is for the admin to add new event.
 */
public class AddEventActivity extends Activity {
    private static final String TAG = AddEventActivity.class.getSimpleName();

    private static final int PLACE_PICKER_REQUEST = 1;  // Address
    private static final int PICK_IMAGE_REQUEST = 2;    // To get Image from gallery
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    //An ArrayList for Spinner Items
    private ArrayList<String> CURRENCY_LIST;
    //Declaring Spinners
    private MaterialBetterSpinner dropdownCurrencies;

    @InjectView(R.id.input_EventName) EditText inputEventName;
    @InjectView(R.id.input_PlaceName) EditText inputPlaceName;
    @InjectView(R.id.input_Date) EditText inputDate;
    @InjectView(R.id.input_Time) EditText inputTime;
    @InjectView(R.id.input_Address) EditText inputAddress;
    @InjectView(R.id.input_Price) EditText inputPrice;

    private ImageButton btnImgChoose, img_btnBack, img_btnSubmit;
    private ImageView imageView;         // To show the selected image
    private String image_path = null;           // Path of the image

    private ProgressDialog pDialog;
    private SQLiteHandler db;

    private MyEvent my_event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //Initializing the ArrayList
        CURRENCY_LIST = new ArrayList<String>();

        //Initializing Spinner
        dropdownCurrencies = (MaterialBetterSpinner)findViewById(R.id.android_material_design_spinner);

        // Image choose button
        btnImgChoose = (ImageButton) findViewById(R.id.btn_EventPic);
        // View of the image
        imageView = (ImageView) findViewById(R.id.imageView);
        // Back button
        img_btnBack = (ImageButton) findViewById(R.id.img_btn_back);
        // Submit button
        img_btnSubmit = (ImageButton) findViewById(R.id.img_btn_submit);

        // Inject the ButterKnife design
        ButterKnife.inject(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // This method will load the Currencies data to the spinner
        loadCurrenciesSpinnerData();

        // Date Button Click event
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        /* Your code to get date and time */
                        selectedmonth = selectedmonth + 1;
                        inputDate.setText(selectedday + "/" + selectedmonth + "/" + selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        // Time Button Click event
        inputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        inputTime.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        // Address Button Click event
        inputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Place Picker
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(AddEventActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        // Image choose Button Click event
        btnImgChoose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Show images in gallery
                showFileChooser();
            }
        });

        // Previous page Button Click event
        img_btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to user activity
                Intent intent = new Intent(AddEventActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Submit Button Click event
        img_btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    // Activity result
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(this, data);
                inputAddress.setText(place.getAddress());
                inputPlaceName.setText(place.getName());
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Uri image_path = data.getData();
            try
            {
                //final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                //getContentResolver().takePersistableUriPermission(image_path, takeFlags);

                // Show event image in image view
                Picasso.with(getApplicationContext()).load(image_path).error(R.mipmap.ic_launcher)
                        .resize(300, 300)
                        .into(imageView);

                this.image_path = image_path.toString();
                // Save the picture path in MyEvent object
                //my_event.setPicture(image_path.toString());
            }
            catch (Exception e)
            {
                //handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * Load all currencies spinner
     */
    private void loadCurrenciesSpinnerData()
    {
        CURRENCY_LIST.clear();
        CURRENCY_LIST.add("$");
        CURRENCY_LIST.add("€");
        CURRENCY_LIST.add("£");
        //Setting adapter to show the items in the spinner
        dropdownCurrencies.setAdapter(new ArrayAdapter<String>(AddEventActivity.this, android.R.layout.simple_spinner_dropdown_item, CURRENCY_LIST));
    }

    /**
     * This method for choosing image from gallery
     */
    private void showFileChooser()
    {
        Intent intent;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        // The current version is Kitkat or higher
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        }
        else
        // The current version is lower than Kitkat
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }

        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * This function performs the submit operation.
     */
    public void submit()
    {
        Log.d(TAG, "AddEvent");

        String event_name = inputEventName.getText().toString().trim();
        String event_date = inputDate.getText().toString().trim();
        String event_time = inputTime.getText().toString().trim();
        String address = inputAddress.getText().toString().trim();
        String place_name = inputPlaceName.getText().toString().trim();
        String price = inputPrice.getText().toString().trim();
        String price_currency = dropdownCurrencies.getText().toString().trim();

        // Create event object
        my_event = new MyEvent(event_name, place_name, event_date, event_time, address,
                               price + " " + price_currency, this.image_path);

        // One of the fields is invalid
        if (!validate())
        {
            ToastMessage("Creation of new event failed");
            return;
        }

        registerEvent();
    }

    /**
     * This function shows a toast message to the user.
     */
    public void ToastMessage(String message)
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

        // Event Name validation
        if (my_event.getEvent_Name().isEmpty())
        {
            inputEventName.setError("Enter event name !");
            requestFocus(inputEventName);
            valid = false;
        }
        else
        {
            inputEventName.setError(null);
        }

        // Date validation
        try
        {
            if (inputDate.getText().toString().isEmpty() ||
                    (formatter.parse(inputDate.getText().toString()).before(new Date())))
            {
                inputDate.setError("Enter the date of the event !");
                if (valid == true)
                    requestFocus(inputDate);
                valid = false;
            }
            else
            {
                inputDate.setError(null);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Time validation
        if (my_event.getEvent_Time().isEmpty())
        {
            inputTime.setError("Enter the time of the event !");
            if (valid == true)
                requestFocus(inputTime);
            valid = false;
        }
        else
        {
            inputTime.setError(null);
        }

        // Address validation
        if (my_event.getAddress().isEmpty())
        {
            inputAddress.setError("Enter the address of the event !");
            if (valid == true)
                requestFocus(inputAddress);
            valid = false;
        }
        else
        {
            inputAddress.setError(null);
        }

        // Place Name validation
        if (my_event.getPlace_Name().isEmpty())
        {
            inputPlaceName.setError("Enter the place where the event will be !");
            if (valid == true)
                requestFocus(inputPlaceName);
            valid = false;
        }
        else
        {
            inputPlaceName.setError(null);
        }

        // Price validation
        if (inputPrice.getText().toString().isEmpty())
        {
            inputPrice.setError("Enter the price of the event !");
            if (valid == true)
                requestFocus(inputPrice);
            valid = false;
        }
        else
        {
            inputPrice.setError(null);
        }

        // Price currency validation
        if (dropdownCurrencies.getText().toString().isEmpty())
        {
            dropdownCurrencies.setError("Choose price currency !");
            if (valid == true)
                requestFocus(dropdownCurrencies);
            valid = false;
        }
        else
        {
            dropdownCurrencies.setError(null);
        }

        // Picture validation
        if (this.image_path == null)
        {
            ToastMessage("Enter event picture !");
            valid = false;
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
     * Function to store event in MySQL database,
     * will post all params to server url
     */
    private void registerEvent()
    {
        // Tag used to cancel the request
        String tag_string_req = "register_request";

        pDialog.setMessage("Creating new event ...");
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
                        /*
                        // Parent user successfully stored in MySQL
                        // Now store the parent user in SQLite
                        JSONObject user = jObj.getJSONObject("user");
                        parent.SetID(user.getString("ID"));
                        parent.SetFirstName(user.getString("firstname"));
                        parent.SetLastName(user.getString("lastname"));
                        parent.SetAddress(user.getString("address"));
                        parent.SetPhone(user.getString("phone"));
                        parent.SetEmail(user.getString("email"));
                        parent.SetCreatedAt(user.getString("created_at"));

                        // Inserting row in parents table
                        db.addParent(parent);

                        // Now store the child user in SQLite
                        child.SetName(user.getString("kid_name"));
                        child.SetBirthDate(user.getString("kid_birthdate"));
                        child.SetPicture(user.getString("kid_photo"));
                        child.SetClass(user.getString("kid_class"));
                        Gan.SetName(user.getString("kindergan_name"));
                        child.SetParentID(user.getString("ID"));
                        child.SetCreatedAt(user.getString("created_at"));
                        child.SetPresence(user.getString("presence"));

                        // Inserting row in kids table
                        db.addKid(child, Gan);

                        session.setLogin(true);
                        // Create type session
                        session.setType(2);

                        Toast.makeText(getApplicationContext(), "User successfully registered.", Toast.LENGTH_LONG).show();

                        // Launch user activity
                        Intent intent = new Intent(SignupParentGanActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                        */
                    }
                    else
                    {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        ToastMessage(errorMsg);
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
                ToastMessage(error.getMessage());
                hideDialog();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                // Posting parameters to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "add_event");
                params.put("event_name", my_event.getEvent_Name());
                params.put("event_date", my_event.getEvent_Date());
                params.put("event_time", my_event.getEvent_Time());
                params.put("Address", my_event.getAddress());
                params.put("place_name", my_event.getPlace_Name());
                params.put("price", my_event.getPrice());
                /*
                //Converting Bitmap to String
                String image = bmpHandler.getStringImage(bmpHandler.decodeSampledBitmapFromStream(Uri.parse(my_event.getPicture()), 300, 300));
                params.put("picture", image);
                */
                return params;
            }
        };

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
        //Volley does retry for you if you have specified the policy.
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
