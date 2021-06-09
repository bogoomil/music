package music.event;

import music.theory.Note;

public class NoteDeletedEvent {
    Note note;

    public NoteDeletedEvent(Note note) {
        super();
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }


}
