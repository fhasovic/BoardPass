package com.devsoul.dima.boardpass.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.activities.admin.AddEventActivity;
import com.devsoul.dima.boardpass.helper.SQLiteHandler;
import com.devsoul.dima.boardpass.helper.SessionManager;

import java.util.HashMap;

/**
 * The User Activity fetching the logged user information from SQLite and displaying it on the screen.
 * The logout button will logout the user by clearing the session and deleting the user from SQLite table.
 */
public class UserActivity extends AppCompatActivity
{
    private TextView txtName;
    private TextView txtEmail;
    private ImageButton btnEvents;
    private ImageButton btnLogout;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnEvents = (ImageButton) findViewById(R.id.btnEvents);
        btnLogout = (ImageButton) findViewById(R.id.btnLogout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn())
        {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("full_name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Events button click
        btnEvents.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Launch events activity
                Intent intent = new Intent(UserActivity.this, EventsActivity.class);
                startActivity(intent);
                finish();
            }

        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                logoutUser();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Get the user type from session
        if (session.getType() == 2)
        // Admin
        {
            // Inflate the menu; this adds items to the action bar if it is present.  
            getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu  
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.item1:
                Toast.makeText(getApplicationContext(),"Add event selected",Toast.LENGTH_SHORT).show();
                // Go to add event activity
                Intent intent = new Intent(UserActivity.this, AddEventActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Update event selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item3:
                Toast.makeText(getApplicationContext(),"Delete event selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logging out the user.
     * Will set isLoggedIn flag to false and Type to 1 in shared preferences
     * and clears the user data from SQLite users table
     */
    private void logoutUser()
    {
        session.setLogin(false);
        session.setType(1);

        db.deleteTable(db.TABLE_USER);

        // Launching the login activity
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
