package com.smuzdev.quicknotes.model;

public class Note {

    public String title;
    public String noteText;
    public  String noteDate;
    public byte[] byteImage;

    public Note(String title, String noteText, String noteDate, byte[] byteImage) {
        this.title = title;
        this.noteText = noteText;
        this.noteDate = noteDate;
        this.byteImage = byteImage;
    }
}
