package music.theory;

public class Pitch {

    private int midiCode;
    public static final int DEFAULT_OCTAVE = 4;

    public Pitch(int midiCode) {
        super();
        this.midiCode = midiCode;
    }


    public NoteName getName() {
        try {
            return NoteName.byCode(this.midiCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public int getOctave() {
        return Math.floorDiv(midiCode, 12);
    }


    public int getMidiCode() {
        return this.midiCode;
    }


    public void setMidiCode(int midiCode) {
        this.midiCode = midiCode;
    }

    public Pitch shift(int shift) {
        return new Pitch(this.midiCode + (shift * 12));
    }

    @Override
    public String toString( ) {
        try {
            return new StringBuilder().append(NoteName.byCode(midiCode).name()).append(" [").append(this.getMidiCode()).append("]").toString();
            //return "" + this.midiCode;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //Intervals
    //---------
    //There are various common intervals that the Mellow D compiler heavily makes use of in
    //constructing [chords](primitives/Chord.html).

    //*majorThird*: 4 semi-tones above the root
    public Pitch majorThird() {
        return new Pitch(this.midiCode + 4);
    }

    //*minorThird*: 3 semi-tones above the root
    public Pitch minorThird() {
        return new Pitch(this.midiCode + 3);
    }

    //*perfectFifth*: 7 semi-tones above the root
    public Pitch perfectFifth() {
        return new Pitch(this.midiCode + 7);
    }

    //*augmentedFifth*: 8 semi-tones above the root
    public Pitch augmentedFifth() {
        return new Pitch(this.midiCode + 8);
    }

    //*diminishedFifth*: 6 semi-tones above the root
    public Pitch diminishedFifth() {
        return new Pitch(this.midiCode + 6);
    }

    //*diminishedFifth*: 9 semi-tones above the root
    public Pitch diminishedSeventh() {
        return new Pitch(this.midiCode + 9);
    }

    //*majorSixth*: 9 semi-tones above the root
    public Pitch majorSixth() {
        return new Pitch(this.midiCode + 9);
    }

    //*minorSeventh*: 10 semi-tones above the root
    public Pitch minorSeventh() {
        return new Pitch(this.midiCode + 10);
    }

    //*majorSeventh*: 11 semi-tones above the root
    public Pitch majorSeventh() {
        return new Pitch(this.midiCode + 11);

    }

    //*minor ninth*: 13 semi-tones above the root
    public Pitch minorNinth() {
        return new Pitch(this.midiCode + 13);

    }

    //*major ninth*: 14 semi-tones above the root
    public Pitch majorNinth() {
        return new Pitch(this.midiCode + 14);

    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + midiCode;
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
        Pitch other = (Pitch) obj;
        if (midiCode != other.midiCode) {
            return false;
        }
        return true;
    }



}
