package music.theory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Note implements Cloneable{


    Pitch pitch;
    NoteLength length;
    int startTick;
    int vol = 100;

    static int ID;

    private int id;

    public Note() {
        this.id = ID;
        ID++;
    }

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

    @JsonIgnore
    public int getMidiCode() {
        return this.pitch.getMidiCode();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        if (id != other.id) {
            return false;
        }
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }





}
