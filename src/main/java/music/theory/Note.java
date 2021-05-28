package music.theory;

public class Note {


    Pitch pitch;
    NoteLength length;
    int relativStartTick;
    int absoluteStartTick;
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



    public int getRelativStartTick() {
        return relativStartTick;
    }



    public void setRelativStartTick(int startInTick) {
        this.relativStartTick = startInTick;
    }



    public int getAbsoluteStartTick() {
        return absoluteStartTick;
    }



    public void setAbsoluteStartTick(int absoluteStartTick) {
        this.absoluteStartTick = absoluteStartTick;
    }

    @Override
    public Note clone() {
        Note n = new Note();
        n.setLength(getLength());
        n.setPitch(getPitch());
        n.setRelativStartTick(getRelativStartTick());
        n.setVol(getVol());

        return n;
    }

}
