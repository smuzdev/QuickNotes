package com.smuzdev.quicknotes.model;

import java.util.ArrayList;

public class IOJsonModel {
    public ArrayList<Note> noteArrayList;

    public IOJsonModel(ArrayList<Note> noteArrayList) {
        this.noteArrayList = noteArrayList;
    }

    public IOJsonModel() {

    }
}
