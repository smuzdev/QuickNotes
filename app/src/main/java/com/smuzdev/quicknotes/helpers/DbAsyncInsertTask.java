package com.smuzdev.quicknotes.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.smuzdev.quicknotes.model.Note;

public class DbAsyncInsertTask extends AsyncTask<Note, Void, Void> {

    private static final String TAG = "QN_DEBUG";
    Context context;
    SQLiteDatabase db;

    public DbAsyncInsertTask(Context context) {
        this.context = context;
    }

    //Код, предшествующий выполнению задачи
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        db = new DatabaseHelper(context).getWritableDatabase();
        Log.d(TAG, "onPreExecute: TASK WILL START SOON...");
    }

    //Код, выполняемый при завершении задачи
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        db.close();
        Log.d(TAG, "onPostExecute: TASK COMPLETE!");
    }

    //Код, передающий информацию о ходе выполнения задачи
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "onProgressUpdate: TASK IN PROCESS...");
    }

    //Код, выполняемый в фоновом потоке
    @Override
    protected Void doInBackground(Note... noteModels) {
        Log.d(TAG, "onProgressUpdate: TASK STARTED!");
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_TITLE, noteModels[0].title);
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, noteModels[0].noteText);
        cv.put(DatabaseHelper.COLUMN_DISCOVERED_PLACE, noteModels[0].noteDate);
        cv.put(DatabaseHelper.COLUMN_IMAGE, noteModels[0].byteImage);

        db.insert(DatabaseHelper.TABLE_NAME, null, cv);
        return null;
    }
}
