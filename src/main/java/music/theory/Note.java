package music.theory;

public class Note {


    Pitch pitch;
    NoteLength length;
    int startInTick;
    int vol;

    public Pitch getPitch() {
        return pitch;
    }



    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }



    public NoteLength getLength() {
        return length;
    }



    public void setLength(NoteLength length) {
        this.length = length;
    }



    public int getVol() {
        return vol;
    }



    public void setVol(int vol) {
        this.vol = vol;
    }



    public int getStartInTick() {
        return startInTick;
    }



    public void setStartInTick(int startInTick) {
        this.startInTick = startInTick;
    }



}
