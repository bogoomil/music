package music.event;

import music.theory.Note;

public class NoteSelectionEvent {
    private Note note;

    public NoteSelectionEvent(Note note) {
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
