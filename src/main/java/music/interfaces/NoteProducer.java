package music.interfaces;

import java.util.List;

import music.theory.Note;

public interface NoteProducer {
    List<Note> getNotes();
    List<Note> getNotes(int midiCode);

}
