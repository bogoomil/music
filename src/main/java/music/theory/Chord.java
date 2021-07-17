package music.theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

public class Chord {

    // **Augmented**: Caug (root, major 3rd, augmented 5th)
    private static Chord augmented(Pitch root) {
        return new Chord(ChordType.AUG, root, root.majorThird(), root.augmentedFifth());
    }

    // **Augmented Seventh**: Caug7 (root, major 3rd, augmented 5th, minor 7th)
    private static Chord augmentedSeventh(Pitch root) {
        return new Chord(ChordType.AUG7, root, root.majorThird(), root.augmentedFifth(), root.minorSeventh());
    }

    // **Diminished**: Cdim (root, minor 3rd, diminished 5th)
    private static Chord diminished(Pitch root) {
        return new Chord(ChordType.DIM, root, root.minorThird(), root.diminishedFifth());
    }

    // **Diminished Seventh**: Cdim7 (root, minor 3rd, diminished 5th,
    // diminished 7th)
    private static Chord diminished7th(Pitch root) {
        return new Chord(ChordType.DIM7, root, root.minorThird(), root.diminishedFifth(), root.diminishedSeventh());
    }

    // **Dominant Seventh**: Cdom7 (root, major 3rd, perfect 5th, minor 7th)
    private static Chord dominantSeventh(Pitch root) {
        return new Chord(ChordType.DOM7, root, root.majorThird(), root.perfectFifth(), root.minorSeventh());
    }

    public static Chord getChordInversion(Chord ch) {

        Pitch[] pitches = new Pitch[ch.getPitches().length];
        for (int i = 0; i < ch.getPitches().length; i++) {
            pitches[i] = new Pitch(ch.getPitches()[i].getMidiCode());
        }
        pitches[0] = pitches[0].shift(1);
        return new Chord(ch.getChordType(), pitches);
    }

    public static Optional<Chord> getChordCounterPart(Chord c) {
        if (c.getChordType() == ChordType.MAJ) {
            return Optional.of(getChord(new Pitch(c.pitches[0].getMidiCode() + 9), ChordType.MIN));
        } else if (c.getChordType() == ChordType.MIN) {
            return Optional.of(getChord(new Pitch(c.pitches[0].getMidiCode() - 9), ChordType.MAJ));
        }
        return Optional.empty();

    }

    public static Chord getChord(Pitch pitch, ChordType chordType) {
        switch (chordType) {
        case MAJ:
            return major(pitch);
        case MIN:
            return minor(pitch);
        case AUG:
            return augmented(pitch);
        case DIM:
            return diminished(pitch);
        case DIM7:
            return diminished7th(pitch);
        case MAJ7B5:
            return majorSeventhFlatFive(pitch);
        case MIN7:
            return minorSeventh(pitch);
        case MINMAJ7:
            return minorMajorSeventh(pitch);
        case DOM7:
            return dominantSeventh(pitch);
        case MAJ7:
            return majorSeventh(pitch);
        case AUG7:
            return augmentedSeventh(pitch);
        case MAJ7S5:
            return majorSeventhSharpFive(pitch);
        case MAJ6:
            return majorSixth(pitch);
        case MIN6:
            return minorSixth(pitch);
        case MAJ9:
            return majorNinth(pitch);
        case MIN9:
            return minorNinth(pitch);
        case DOM9:
            return dominantNinth(pitch);
        }

        throw new UnsupportedOperationException("pitch: " + pitch + ", type: " + chordType.name());
    }

    // Triads
    // ------
    //
    // Three note chords. These consists of the root, a third and a fifth.
    // The third and fifth intervals are shifted slightly to vary the chord's
    // sound.

    public static Chord getChordDegree(Pitch pitch, Tone chordType, ChordDegree degree) {

        Pitch[] p = Scale.getScale(pitch, chordType);

        switch (chordType) {
        case MAJ: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MAJ);
            case ii:
                return getChord(p[1], ChordType.MIN);
            case iii:
                return getChord(p[2], ChordType.MIN);
            case iv:
                return getChord(p[3], ChordType.MAJ);
            case v:
                return getChord(p[4], ChordType.MAJ);
            case vi:
                return getChord(p[5], ChordType.MIN);
            case vii:
                return getChord(p[6], ChordType.DIM);
            }
            break;
        }
        case MIN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MIN);
            case ii:
                return getChord(p[1], ChordType.DIM);
            case iii:
                return getChord(p[2], ChordType.MAJ);
            case iv:
                return getChord(p[3], ChordType.MIN);
            case v:
                return getChord(p[4], ChordType.MIN);
            case vi:
                return getChord(p[5], ChordType.MAJ);
            case vii:
                return getChord(p[6], ChordType.MAJ);
            }
            break;
        }
        case LYDIAN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MAJ);
            case ii:
                return getChord(p[1], ChordType.MAJ);
            case iii:
                return getChord(p[2], ChordType.MIN);
            case iv:
                return getChord(p[3], ChordType.DIM);
            case v:
                return getChord(p[4], ChordType.MAJ);
            case vi:
                return getChord(p[5], ChordType.MIN);
            case vii:
                return getChord(p[6], ChordType.MIN);
            }
            break;
        }
        case MIXOLYDIAN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MAJ);
            case ii:
                return getChord(p[1], ChordType.MIN);
            case iii:
                return getChord(p[2], ChordType.DIM);
            case iv:
                return getChord(p[3], ChordType.MAJ);
            case v:
                return getChord(p[4], ChordType.MAJ);
            case vi:
                return getChord(p[5], ChordType.MIN);
            case vii:
                return getChord(p[6], ChordType.MAJ);
            }
            break;
        }
        case DORIAN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MIN);
            case ii:
                return getChord(p[1], ChordType.MIN);
            case iii:
                return getChord(p[2], ChordType.MAJ);
            case iv:
                return getChord(p[3], ChordType.MAJ);
            case v:
                return getChord(p[4], ChordType.MIN);
            case vi:
                return getChord(p[5], ChordType.DIM);
            case vii:
                return getChord(p[6], ChordType.MAJ);
            }
            break;
        }
        case PHRYGIAN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.MIN);
            case ii:
                return getChord(p[1], ChordType.MAJ);
            case iii:
                return getChord(p[2], ChordType.MAJ);
            case iv:
                return getChord(p[3], ChordType.MIN);
            case v:
                return getChord(p[4], ChordType.DIM);
            case vi:
                return getChord(p[5], ChordType.MAJ);
            case vii:
                return getChord(p[6], ChordType.MIN);
            }
            break;
        }
        case LOCRIAN: {
            switch (degree) {
            case i:
                return getChord(p[0], ChordType.DIM);
            case ii:
                return getChord(p[1], ChordType.MAJ);
            case iii:
                return getChord(p[2], ChordType.MIN);
            case iv:
                return getChord(p[3], ChordType.MIN);
            case v:
                return getChord(p[4], ChordType.MAJ);
            case vi:
                return getChord(p[5], ChordType.MAJ);
            case vii:
                return getChord(p[6], ChordType.MIN);
            }
            break;
        }
        }
        throw new UnsupportedOperationException("pitch: " + pitch + ", type: " + chordType.name() + ", degree: " + degree.name());
    }

    public static List<Chord> getChordProgressions(ChordDegree currentDegree, Pitch root, ChordType chordType) {
        List<Chord> retVal = new ArrayList<>();
        switch (chordType) {
        case MAJ: {
            switch (currentDegree) {
            case i: {
                retVal.add(getChord(root, ChordType.MAJ));
                retVal.add(getChord(root, ChordType.MAJ6));
                retVal.add(getChord(root, ChordType.MAJ7));
                retVal.add(getChord(root, ChordType.MAJ9));
                break;
            }
            case ii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ6));
                break;
            }
            case iii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 4), ChordType.MAJ6));
                break;
            }
            case iv: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MIN7));
                break;
            }
            case v: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ7S5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 1), ChordType.DOM7));
                break;
            }
            case vi: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MIN9));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MAJ6));
                break;
            }
            case vii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ7B5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2),  ChordType.MIN6));
                break;
            }
            }
            break;
        }
        case MIN: {
            switch (currentDegree) {
            case i: {
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN6));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN7));
                retVal.add(getChord(new Pitch(root.getMidiCode()), ChordType.MIN9));
                break;
            }
            case ii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 2), ChordType.MAJ7B5));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN6));
                break;
            }
            case iii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 3), ChordType.MAJ7S5));
                break;
            }
            case iv: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 5), ChordType.MIN7));
                break;
            }
            case v: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 7), ChordType.DOM9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 1), ChordType.DOM7));
                break;
            }
            case vi: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 8), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 9), ChordType.MAJ7B5));
                break;
            }
            case vii: {
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ6));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ7));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 10), ChordType.MAJ9));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.DIM));
                retVal.add(getChord(new Pitch(root.getMidiCode() + 11), ChordType.MAJ7B5));
                break;
            }
            }
        }
        }
        return retVal;

    }

    public static List<Chord> getChordsOfPitch(Pitch p) {
        List<Chord> retVal = new ArrayList<>();
        for (ChordType t : Arrays.asList(ChordType.values())) {
            retVal.add(getChord(p, t));

        }
        return retVal;
    }

    public static List<ChordDegree> getPossibleDegrees(ChordDegree currentDegree, ChordType type) {
        List<ChordDegree> retVal = new ArrayList<>();
        switch (type) {
        case MAJ: {
            switch (currentDegree) {
            case i: {
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.iii);
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vi);
                retVal.add(ChordDegree.vii);

                break;
            }
            case ii: {
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vii);
                break;
            }
            case iii: {
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.vi);
                break;
            }
            case iv: {
                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vii);
                break;
            }
            case v: {
                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.vi);
                break;
            }
            case vi: {
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                break;
            }
            case vii: {
                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.iii);
                break;
            }
            }
            break;
        }
        case MIN: {
            switch (currentDegree) {
            case i: {
                // eredetileg üres

                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.iii);
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vi);
                retVal.add(ChordDegree.vii);
                break;
            }
            case ii: {
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vii);
                break;
            }
            case iii: {
                retVal.add(ChordDegree.vi);
                retVal.add(ChordDegree.vii);
                break;
            }
            case iv: {
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vii);
                break;
            }
            case v: {
                retVal.add(ChordDegree.i);
                break;
            }
            case vi: {
                retVal.add(ChordDegree.ii);
                retVal.add(ChordDegree.iv);
                retVal.add(ChordDegree.v);
                retVal.add(ChordDegree.vii);
                break;
            }
            case vii: {
                retVal.add(ChordDegree.i);
                retVal.add(ChordDegree.v);
                break;
            }
            }
            break;
        }
        }
        retVal.add(currentDegree);
        return retVal;

    }

    // **Major**: Cmaj or C (root, major 3rd, perfect 5th)
    private static Chord major(Pitch root) {
        return new Chord(ChordType.MAJ, root, root.majorThird(), root.perfectFifth());
    }

    // **Major Seventh**: Cmaj7 (root, major 3rd, perfect 5th, major 7th)
    private static Chord majorSeventh(Pitch root) {
        return new Chord(ChordType.MAJ7, root, root.majorThird(), root.perfectFifth(), root.majorSeventh());
    }

    // **Major Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, major 7th, major
    // 9th)
    private static Chord majorNinth(Pitch root) {
        return new Chord(ChordType.MAJ9, root, root.majorThird(), root.perfectFifth(), root.majorSeventh(), root.majorNinth());
    }

    // **minor Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, minor 7th, minor
    // 9th)
    private static Chord minorNinth(Pitch root) {//1, ♭3, 5, ♭7, 9
        return new Chord(ChordType.MIN9, root, root.minorThird(), root.perfectFifth(), root.minorSeventh(), root.majorNinth());
    }

    // **Major Seventh Flat Five**: Cmaj7b5 (root, minor 3rd, diminished 5th,
    // minor 7th)
    private static Chord majorSeventhFlatFive(Pitch root) {
        return new Chord(ChordType.MAJ7B5, root, root.minorThird(), root.diminishedFifth(), root.minorSeventh());
    }

    // Sixths
    // ------
    //
    // Triads with an added fourth note that is a sixth interval
    // above the root.

    // **Augmented Major Seventh**: Cmaj7s5 (root, major 3rd, augmented 5th,
    // major 7th)
    private static Chord majorSeventhSharpFive(Pitch root) {
        return new Chord(ChordType.MAJ7S5, root, root.majorThird(), root.augmentedFifth(), root.majorSeventh());
    }

    // **Major Sixth**: Cmaj6 (root, major 3rd, perfect 5th, major 6th)
    private static Chord majorSixth(Pitch root) {
        return new Chord(ChordType.MAJ6, root, root.majorThird(), root.perfectFifth(), root.majorSixth());
    }

    // Sevenths
    // --------
    //
    // Triads with an added fourth note that is a seventh interval
    // above the root.

    // **Minor**: Cmin (root, minor 3rd, perfect 5th)
    private static Chord minor(Pitch root) {
        return new Chord(ChordType.MIN, root, root.minorThird(), root.perfectFifth());
    }

    // **Minor Major Seventh**: Cminmaj7 (root, minor 3rd, perfect 5th, major
    // 7th)
    private static Chord minorMajorSeventh(Pitch root) {
        return new Chord(ChordType.MINMAJ7, root, root.minorThird(), root.perfectFifth(), root.majorSeventh());
    }

    // **Minor Seventh**: Cmin7 (root, minor 3rd, perfect 5th, minor 7th)
    private static Chord minorSeventh(Pitch root) {
        return new Chord(ChordType.MIN7, root, root.minorThird(), root.perfectFifth(), root.minorSeventh());
    }

    // **DOM Ninth**: Cmaj9 (root, minor 3rd, perfect 5th, minor 7th, major 9th)
    private static Chord dominantNinth(Pitch root) {
        return new Chord(ChordType.DOM9, root, root.majorThird(), root.perfectFifth(), root.minorSeventh(), root.majorNinth());
    }

    // **Minor Sixth**: Cmin6 (root, minor 3rd, perfect 5th, major 6th)
    private static Chord minorSixth(Pitch root) {
        return new Chord(ChordType.MIN6, root, root.minorThird(), root.perfectFifth(), root.majorSixth());
    }

    private ChordType chordType;

    private Pitch[] pitches;

    public Chord(String chordTypeName, Integer... midiCodes) {
        Pitch[] pitches = new Pitch[midiCodes.length];
        for (int i = 0; i < midiCodes.length; i++) {
            pitches[i] = new Pitch(midiCodes[i]);
        }
        this.pitches = pitches;
        this.chordType = ChordType.valueOf(chordTypeName);
    }

    private Chord(ChordType chordType, Pitch... pitches) {
        this.pitches = pitches;
        this.chordType = chordType;
    }

    public ChordType getChordType() {
        return chordType;
    }

    public Pitch[] getPitches() {
        return this.pitches;
    }

    public Collection<ShortMessage> noteOff(int channel, int velocity) throws InvalidMidiDataException {
        Collection<ShortMessage> messages = new ArrayList<>(pitches.length);
        for (Pitch p : pitches) {
            messages.add(new ShortMessage(ShortMessage.NOTE_OFF, channel, p.getMidiCode(), velocity));
        }
        return messages;
    }

    public Collection<ShortMessage> noteOn(int channel, int velocity) throws InvalidMidiDataException {
        Collection<ShortMessage> messages = new ArrayList<>(pitches.length);
        for (Pitch p : pitches) {
            messages.add(new ShortMessage(ShortMessage.NOTE_ON, channel, p.getMidiCode(), velocity));
        }
        return messages;
    }

    public Chord shiftOctave(int octaveShift) {
        Pitch[] pitches = new Pitch[this.pitches.length];
        for (int i = 0; i < this.pitches.length; i++) {
            pitches[i] = this.pitches[i].shift(octaveShift);
        }
        return new Chord(this.getChordType(), pitches);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        try {
            NoteName nn = NoteName.byCode(this.pitches[0].getMidiCode());
            sb.append(nn.name()).append(" ").append(this.chordType.name()).append(" [");
            for (int i = 0; i < this.pitches.length; i++) {

                sb.append(NoteName.byCode(this.pitches[i].getMidiCode())).append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // sb.append("new
        // Chord(").append("\"").append(this.chordType.name()).append("\"").append(",
        // ");
        // for (int i = 0; i < this.pitches.length; i++) {
        // sb.append(this.pitches[i].getMidiCode()).append(", ");
        // }
        // sb.deleteCharAt(sb.length()-1);
        // sb.deleteCharAt(sb.length()-1);
        // sb.append("),");
        // try {
        // sb.append("// ").append().name());
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chordType == null) ? 0 : chordType.hashCode());
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
        Chord other = (Chord) obj;
        if (chordType != other.chordType || !this.getNoteName().equals(other.getNoteName())) {
            return false;
        }
        return true;
    }

    public NoteName getNoteName() {
        return this.pitches[0].getName();
    }

}
