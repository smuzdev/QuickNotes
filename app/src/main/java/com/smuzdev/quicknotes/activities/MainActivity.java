package com.smuzdev.quicknotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smuzdev.quicknotes.R;
import com.smuzdev.quicknotes.helpers.CustomAdapter;
import com.smuzdev.quicknotes.helpers.DatabaseHelper;
import com.smuzdev.quicknotes.helpers.DbAsyncInsertTask;
import com.smuzdev.quicknotes.helpers.JSON.IOJson;
import com.smuzdev.quicknotes.model.IOJsonModel;
import com.smuzdev.quicknotes.model.Note;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    ImageView empty_image_view;
    TextView no_data;

    DatabaseHelper databaseHelper;
    ArrayList<ArrayList<String>> things;
    ArrayList<String> note_id, note_title, note_text, note_date;
    ArrayList<byte[]> note_image;
    CustomAdapter customAdapter;

    IOJson ioJson;
    IOJsonModel ioJsonModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ioJson = new IOJson(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle((Html.fromHtml("<font color=\"#ada07d\">" + getString(R.string.main_page_title) + "</font>")));
        }

        recyclerView = findViewById(R.id.recyclerView);
        empty_image_view = findViewById(R.id.empty_image_view);
        no_data = findViewById(R.id.no_data);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(MainActivity.this);
        note_id = new ArrayList<>();
        note_title = new ArrayList<>();
        note_text = new ArrayList<>();
        note_date = new ArrayList<>();
        note_image = new ArrayList<>();
        things = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this, this, note_id, note_title,
                note_text, note_date, note_image);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = databaseHelper.readAllData();
        if (cursor.getCount() == 0) {
            empty_image_view.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                note_id.add(cursor.getString(0));
                note_title.add(cursor.getString(1));
                note_text.add(cursor.getString(2));
                note_date.add(cursor.getString(3));
                note_image.add(cursor.getBlob(4));
            }
            empty_image_view.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        }

        if (item.getItemId() == R.id.serialize) {
            ioJson.Serialize();
        }

        if (item.getItemId() == R.id.deserialize) {
            ioJsonModel = ioJson.Deserialize();

            if (ioJsonModel.noteArrayList.size() > 0) {
                showWarningRestoreAlertDialog();
            } else {
                Toast.makeText(this, " size < 0", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all?");
        builder.setMessage("Are you sure you want to delete all data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                databaseHelper.deleteAllData();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
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

    private void showWarningRestoreAlertDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Вы уверены?")
                .setMessage("Все существующие данные будут удалены и будет выполнено восстановление из резервной копией.")
                .setPositiveButton("Восстановить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        databaseHelper.deleteAllData();

                        for (Note note : ioJsonModel.noteArrayList) {

                            Note noteModel = new Note(
                                    note.getTitle(),
                                    note.getNoteText(),
                                    note.getNoteDate(),
                                    note.getByteImage());

                            DbAsyncInsertTask asyncInsertTask = new DbAsyncInsertTask(MainActivity.this);
                            asyncInsertTask.execute(noteModel);
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }

                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        //showSuccessDeSerializationMessage();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}