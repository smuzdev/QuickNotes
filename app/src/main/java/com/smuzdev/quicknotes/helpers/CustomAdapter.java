package com.smuzdev.quicknotes.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.smuzdev.quicknotes.R;
import com.smuzdev.quicknotes.activities.UpdateActivity;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList note_id, note_title, note_text, note_date, note_image;

    Animation translate_anim;

    public CustomAdapter(Activity activity, Context context,
                         ArrayList note_id,
                         ArrayList note_title,
                         ArrayList note_text,
                         ArrayList note_date,
                         ArrayList note_image) {
        this.activity = activity;
        this.context = context;
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_text = note_text;
        this.note_date = note_date;
        this.note_image = note_image;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_raw, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {
        holder.note_id_txt.setText(String.valueOf(note_id.get(position)));
        holder.note_title_txt.setText(String.valueOf(note_title.get(position)));
        holder.note_text_txt.setText(String.valueOf(note_text.get(position)));
        holder.note_date_txt.setText(String.valueOf(note_date.get(position)));

        Bitmap bitmapImage = DbBitmapUtility.getImage((byte[]) note_image.get(position));
        holder.note_image.setImageBitmap(bitmapImage);

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("id", String.valueOf(note_id.get(position)));
                intent.putExtra("title", String.valueOf(note_title.get(position)));
                intent.putExtra("noteText", String.valueOf(note_text.get(position)));
                intent.putExtra("noteDate", String.valueOf(note_date.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return note_id.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView note_id_txt, note_title_txt, note_text_txt, note_date_txt;
        ImageView note_image;
        CardView mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            note_id_txt = itemView.findViewById(R.id.note_id_txt);
            note_title_txt = itemView.findViewById(R.id.note_title_txt);
            note_text_txt = itemView.findViewById(R.id.note_text_txt);
            note_date_txt = itemView.findViewById(R.id.note_date_txt);
            note_image = itemView.findViewById(R.id.ivNoteImage);
            mainLayout = itemView.findViewById(R.id.cardView);
            translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }
    }
}
