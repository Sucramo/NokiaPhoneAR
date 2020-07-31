package com.example.nokiaphonerecognizer.objectDetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nokiaphonerecognizer.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<String> titles;
    private List<String> releases;



    Adapter(Context context, List<String> titles, List<String> releases) {
        this.layoutInflater = LayoutInflater.from(context);
        this.titles = titles;
        this.releases = releases;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String title = titles.get(position);
        holder.phoneTitle.setText(title);

        String release = releases.get(position);
        holder.releaseDate.setText(String.format("Released: %s", release));

    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView phoneTitle, releaseDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneTitle = itemView.findViewById(R.id.phone_title);
            releaseDate = itemView.findViewById(R.id.released);
        }
    }
}
