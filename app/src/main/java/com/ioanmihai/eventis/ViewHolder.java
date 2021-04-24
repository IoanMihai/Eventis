package com.ioanmihai.eventis;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView title, description;
    public ImageView image;
    public LinearLayout root;
    public Button button;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.list_title);
        root = itemView.findViewById(R.id.list_root);
        description = itemView.findViewById(R.id.list_desc);
        image = itemView.findViewById(R.id.list_image);
    }

    public void setTitle(String string){
        title.setText(string);
    }

    public void setDescription(String string){
        description.setText(string);
    }


    public ImageView getImage(){
        return image;
    }

    public void setImage(Context ctx, String img){
        Glide.with(ctx).load(img).into(image);
    }
}
