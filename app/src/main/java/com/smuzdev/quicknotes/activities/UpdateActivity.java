package com.smuzdev.quicknotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smuzdev.quicknotes.R;
import com.smuzdev.quicknotes.helpers.DatabaseHelper;
import com.smuzdev.quicknotes.helpers.DbBitmapUtility;

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, note_text_input;
    TextView note_date_tv;
    Button update_button, delete_button, select_image_button;
    DatabaseHelper databaseHelper;
    String id, title, note_text, note_date;
    byte[] byteImage, newByteImage;
    ImageView image;
    Uri uri;
    Boolean isNewByteImage = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle((Html.fromHtml("<font color=\"#ada07d\">" + getString(R.string.update_page_title) + "</font>")));
        }

        title_input = findViewById(R.id.title_input2);
        note_text_input = findViewById(R.id.note_text_input2);
        note_date_tv = findViewById(R.id.note_date_tv2);
        image = findViewById(R.id.image_view2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        select_image_button = findViewById(R.id.select_image_button);

        getAndSetIntentData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(UpdateActivity.this);
                title = title_input.getText().toString().trim();
                note_text = note_text_input.getText().toString().trim();
                note_date = note_date_tv.getText().toString().trim();
                if (isNewByteImage) {
                    byteImage = newByteImage;
                } else {
                    BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    byteImage = DbBitmapUtility.getBytes(bitmap);
                }
                databaseHelper.updateData(id, title, note_text, note_date, byteImage);
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });

        select_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            uri = data.getData();
            image.setImageURI(uri);

            BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            newByteImage = DbBitmapUtility.getBytes(bitmap);
            isNewByteImage = true;
        } else Toast.makeText(this, "You haven't picked image", Toast.LENGTH_LONG).show();
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title") &&
                getIntent().hasExtra("noteText") && getIntent().hasExtra("noteDate")) {

            //Getting Intent Data
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            note_text = getIntent().getStringExtra("noteText");
            note_date = getIntent().getStringExtra("noteDate");

            databaseHelper = new DatabaseHelper(UpdateActivity.this);
            ArrayList<byte[]> note_image = databaseHelper.selectImageById(id);

            byte[] byteImage = note_image.get(0);
            Bitmap bitmapImage = DbBitmapUtility.getImage(byteImage);

            //Setting Intent Data
            title_input.setText(title);
            note_text_input.setText(note_text);
            note_date_tv.setText(note_date);
            image.setImageBitmap(bitmapImage);

        } else {
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + "?");
        builder.setMessage("Are you sure you want to delete " + title + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper databaseHelper = new DatabaseHelper(UpdateActivity.this);
                databaseHelper.deleteOneRaw(id);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}