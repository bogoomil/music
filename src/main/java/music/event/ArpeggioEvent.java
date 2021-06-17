package music.event;

import music.theory.NoteLength;

public class ArpeggioEvent {
    int shift;
    NoteLength hossz;
    NoteLength szunet;
    public ArpeggioEvent(int shift, NoteLength hossz, NoteLength szunet) {
        super();
        this.shift = shift;
        this.hossz = hossz;
        this.szunet = szunet;
    }
    public int getShift() {
        return shift;
    }
    public void setShift(int shift) {
        this.shift = shift;
    }
    public NoteLength getHossz() {
        return hossz;
    }
    public void setHossz(NoteLength hossz) {
        this.hossz = hossz;
    }
    public NoteLength getSzunet() {
        return szunet;
    }
    public void setSzunet(NoteLength szunet) {
        this.szunet = szunet;
    }


}
