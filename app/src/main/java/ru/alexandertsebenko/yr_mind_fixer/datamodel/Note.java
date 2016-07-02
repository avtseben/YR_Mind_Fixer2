package ru.alexandertsebenko.yr_mind_fixer.datamodel;

public class Note {
    private long id;
    private String note;
    private String noteTitle;
    private String noteType;
    private long creationDate;

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTextNote() {
        return note;
    }

    public void setTextNote(String note) {
        this.note = note;
    }
    @Override
    public String toString() {
        return note;
    }
}
