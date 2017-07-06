package com.devsoul.dima.boardpass.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.devsoul.dima.boardpass.R;
import com.devsoul.dima.boardpass.beans.RowItem;

import java.util.List;

/**
 * This class extends RecyclerView.Adapter to provide custom row layout and data for RecyclerView.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
{
    // Variable
    private List<RowItem> showsList;

    // ViewHolder Pattern
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private List<RowItem> showsList;

        private ImageView imageView;
        private TextView txtTitle;
        private TextView txtPlace;

        public MyViewHolder(View view, List<RowItem> showsList)
        {
            super(view);
            this.showsList = showsList;
            imageView = (ImageView) view.findViewById(R.id.image);
            txtTitle = (TextView) view.findViewById(R.id.title);
            txtPlace = (TextView) view.findViewById(R.id.place);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            RowItem item = showsList.get(getAdapterPosition());
            Toast.makeText(v.getContext(), item.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
        }

    }

    // Constructor
    public RecyclerAdapter(List<RowItem> showsList)
    {
        this.showsList = showsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_row, parent, false);

        return new MyViewHolder(itemView, showsList);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        RowItem rowItem = showsList.get(position);

        holder.imageView.setImageResource(rowItem.getImageId());
        holder.txtTitle.setText(rowItem.getTitle());
        holder.txtPlace.setText(rowItem.getPlace());
    }

    @Override
    public int getItemCount()
    {
        return showsList.size();
    }
}
