package com.smuzdev.quicknotes.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import com.smuzdev.quicknotes.helpers.DbAsyncInsertTask;
import com.smuzdev.quicknotes.helpers.DbBitmapUtility;
import com.smuzdev.quicknotes.model.Note;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddActivity extends AppCompatActivity {

    EditText title_input, note_text_input;
    TextView note_date_tv;
    String note_date_txt;
    Button add_button, select_image_button;
    byte[] byteImage;
    ImageView image;
    Uri uri;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle((Html.fromHtml("<font color=\"#ada07d\">" + getString(R.string.add_page_title) + "</font>")));
        }

        image = findViewById(R.id.image_view);
        title_input = findViewById(R.id.title_input);
        note_text_input = findViewById(R.id.note_text_input);
        note_date_tv = findViewById(R.id.note_date_tv);
        add_button = findViewById(R.id.add_button);
        select_image_button = findViewById(R.id.select_image_button);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate localDate = LocalDate.now();
        note_date_txt = dtf.format(localDate);
        note_date_tv.setText(note_date_txt);
    }

    @Override
    protected void onStart() {
        super.onStart();

        add_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(AddActivity.this);

                if (title_input.getText().toString().isEmpty()) {
                    title_input.setError("Enter thing title");
                    return;
                }

                if (note_text_input.getText().toString().isEmpty()) {
                    note_text_input.setError("Enter Thing Description");
                    return;
                }

                if (image == null) {
                    Toast.makeText(AddActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                    return;
                }

//                //СИНХРОННЫЙ INSERT
//                databaseHelper.addThing(title_input.getText().toString().trim(),
//                        description_input.getText().toString().trim(),
//                        discoveredPlace_input.getText().toString().trim(),
//                        byteImage);

                //ASYNC INSERT
                Note noteModel = new Note(
                        title_input.getText().toString().trim(),
                        note_text_input.getText().toString().trim(),
                        note_date_txt,
                        byteImage);

                DbAsyncInsertTask asyncInsertTask = new DbAsyncInsertTask(AddActivity.this);
                asyncInsertTask.execute(noteModel);
                startActivity(new Intent(AddActivity.this, MainActivity.class));

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
            byteImage = DbBitmapUtility.getBytes(bitmap);

        } else Toast.makeText(this, "You haven't picked image", Toast.LENGTH_LONG).show();
    }

}