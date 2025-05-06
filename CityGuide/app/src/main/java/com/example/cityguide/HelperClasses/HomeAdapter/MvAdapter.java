package com.example.cityguide.HelperClasses.HomeAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityguide.R;

import java.util.ArrayList;

public class MvAdapter extends RecyclerView.Adapter<MvAdapter.MvViewHolder> {

    ArrayList<MvHelperClass> mvLocations;


    public MvAdapter(ArrayList<MvHelperClass> mvLocations) {
        this.mvLocations = mvLocations;
    }

    @NonNull
    @Override
    public MvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_viewed_card_design,parent,false);
        MvAdapter.MvViewHolder mvViewHolder = new MvAdapter.MvViewHolder(view);
        return mvViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MvViewHolder holder, int position) {

        MvHelperClass mvHelperClass = mvLocations.get(position);

        holder.image.setImageResource(mvHelperClass.getImage());
        holder.title.setText(mvHelperClass.getTitle());
        holder.desc.setText(mvHelperClass.getDescription());

    }

    @Override
    public int getItemCount() {
        return mvLocations.size();
    }

    public static class MvViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView title,desc;

        public MvViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.mv_image);
            title = itemView.findViewById(R.id.mv_title);
            desc = itemView.findViewById(R.id.mv_desc);
        }
    }
}
