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

    public int getMidiCode() {
        return this.pitch.getMidiCode();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((length == null) ? 0 : length.hashCode());
        result = prime * result + ((pitch == null) ? 0 : pitch.hashCode());
        result = prime * result + startTick;
        result = prime * result + vol;
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Note other = (Note) obj;
        if (length != other.length) {
            return false;
        }
        if (pitch == null) {
            if (other.pitch != null) {
                return false;
            }
        } else if (!pitch.equals(other.pitch)) {
            return false;
        }
        if (startTick != other.startTick) {
            return false;
        }
        if (vol != other.vol) {
            return false;
        }
        return true;
    }



}
