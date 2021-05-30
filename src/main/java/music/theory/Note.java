package music.theory;

public class Note {


    Pitch pitch;
    NoteLength length;
    int startTick;
    int vol = 100;

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

    public int getStartTick() {
        return startTick;
    }



    public void setStartTick(int startInTick) {
        this.startTick = startInTick;
    }

    @Override
    public Note clone() {
        Note n = new Note();
        n.setLength(getLength());
        n.setPitch(getPitch());
        n.setStartTick(getStartTick());
        n.setVol(getVol());

        return n;
    }

}
