package music.event.tracks;

import music.theory.Note;

public class AddNotesToTrackEvent {

    Note[] notes;

    public AddNotesToTrackEvent(Note[] notes) {
        super();
        this.notes = notes;
    }

    public Note[] getNotes() {
        return notes;
    }

    public void setNotes(Note[] notes) {
        this.notes = notes;
    }


}
