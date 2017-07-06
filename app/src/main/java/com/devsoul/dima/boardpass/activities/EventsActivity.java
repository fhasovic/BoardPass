package com.devsoul.dima.boardpass.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.beans.RowItem;
import com.devsoul.dima.boardpass.recycler.DividerItemDecoration;
import com.devsoul.dima.boardpass.recycler.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The Events Activity show all the events to the user.
 */
public class EventsActivity extends Activity
{

    public static final String[] titles = new String[] { "HoneyBlood", "Sylvain Armand", "Sacha Muki", "Toy Room"};
    public static final String[] places = new String[] { "Koko London",
                                                         "Project London",
                                                         "Haig Club:Sophie Lee",
                                                         "Club London"};
    public static final Integer[] images = { R.drawable.event1, R.drawable.event2, R.drawable.event3, R.drawable.event4};

    private List<RowItem> rowItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new RecyclerAdapter(rowItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // set line separator
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // set the adapter
        recyclerView.setAdapter(mAdapter);


        for (int i = 0; i < titles.length; i++)
        {
            RowItem item = new RowItem(images[i], titles[i], places[i]);
            rowItems.add(item);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed()
    {
        // Returning to user activity
        Intent intent = new Intent(EventsActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }
}
