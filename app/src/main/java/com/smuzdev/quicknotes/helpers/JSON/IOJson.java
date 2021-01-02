package com.smuzdev.quicknotes.helpers.JSON;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smuzdev.quicknotes.helpers.DatabaseHelper;
import com.smuzdev.quicknotes.model.IOJsonModel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class IOJson {
    File file;
    DatabaseHelper dbHelper;
    Context context;

    public IOJson(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "QuickNotes.backup");
    }

    public void Serialize() {

        IOJsonModel ioJsonModel = new IOJsonModel(dbHelper.getAllNotesForSerialize());

        if (ioJsonModel.noteArrayList.size() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

            try {
                objectMapper.writeValue(file, ioJsonModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }

    }

    public IOJsonModel Deserialize() {
        IOJsonModel ioJsonModel = new IOJsonModel();
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        try {
            FileReader fr = new FileReader(file);
            ioJsonModel = objectMapper.readValue(fr, IOJsonModel.class);
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ioJsonModel;
    }


}

