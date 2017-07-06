package com.devsoul.dima.boardpass.activities.admin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.activities.UserActivity;
import com.devsoul.dima.boardpass.helper.SQLiteHandler;

import java.util.Calendar;

/**
 * The Add Event Activity is for the admin to add new event.
 */
public class AddEventActivity extends Activity
{
    private static final String TAG = AddEventActivity.class.getSimpleName();

    private static final int PICK_IMAGE_REQUEST = 1; // To get Image from gallery

    @InjectView(R.id.input_EventName) EditText inputEventName;
    @InjectView(R.id.input_PlaceName) EditText inputPlaceName;
    @InjectView(R.id.input_Date) EditText inputDate;
    @InjectView(R.id.input_Time) EditText inputTime;
    @InjectView(R.id.input_Address) EditText inputAddress;
    @InjectView(R.id.input_Price) EditText inputPrice;

    private ImageButton btnImgChoose, img_btnBack, img_btnSubmit;
    private ImageView imageView;         // To show the selected image
    private Uri image_path;              // Path of the image

    private ProgressDialog pDialog;
    private SQLiteHandler db;
    //private BitmapHandler bmpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

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

        // Bitmap handler
        //bmpHandler = new BitmapHandler(getApplicationContext());

        // Date Button Click event
        inputDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                    {
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
        inputTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        inputTime.setText(selectedHour + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        // Previous page Button Click event
        img_btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Go to user activity
                Intent intent = new Intent(AddEventActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Submit Button Click event
        img_btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //submit();
            }
        });
    }
}
