package music.theory;

public class Scale {

    public static Pitch[] getScale(Pitch root, Tone hangnem) {
        Pitch[] retVal = null;

        switch (hangnem) {
        case MAJ:
            retVal = majorScale(root.getName());
            break;

        case MIN:
            retVal = minorScale(root.getName());
            break;

        case LYDIAN:
            retVal = lydianScale(root.getName());
            break;

        case MIXOLYDIAN:
            retVal = mixolydianScale(root.getName());
            break;

        case DORIAN:
            retVal = dorianScale(root.getName());
            break;

        case PHRYGIAN:
            retVal = phrygianScale(root.getName());
            break;

        case LOCRIAN:
            retVal = locrianScale(root.getName());
            break;

        }

        for(int i = 0; i < retVal.length; i++) {
            retVal[i] = retVal[i].shift(root.getOctave());
        }
        return retVal;
    }

    public static Pitch[] majorScale(NoteName root) {
        int[] codes = getMajorCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);

        }
        return retVal;
    }

    public static Pitch[] minorScale(NoteName root) {
        int[] codes = getMinorCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }

    public static Pitch[] lydianScale(NoteName root) {
        int[] codes = getLydianCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }

    private static Pitch[] mixolydianScale(NoteName root) {
        int[] codes = getMixolydianCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }

    private static Pitch[] dorianScale(NoteName root) {
        int[] codes = getDorianCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }

    private static Pitch[] phrygianScale(NoteName root) {
        int[] codes = getPhrygianCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }

    private static Pitch[] locrianScale(NoteName root) {
        int[] codes = getLocrianCodes(root);
        Pitch[] retVal = new Pitch[7];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }


    private static int[] getMinorCodes(NoteName root) {

        return new int[] { NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.D.getMidiCode() + root.getMidiCode(),
                NoteName.Eb.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.Ab.getMidiCode() + root.getMidiCode(),
                NoteName.Bb.getMidiCode() + root.getMidiCode()
        };

    }

    private static int[] getMajorCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.D.getMidiCode() + root.getMidiCode(),
                NoteName.E.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.A.getMidiCode() + root.getMidiCode(),
                NoteName.B.getMidiCode() + root.getMidiCode()
        };

    }

    private static int[] getLydianCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.D.getMidiCode() + root.getMidiCode(),
                NoteName.E.getMidiCode() + root.getMidiCode(),
                NoteName.Fs.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.A.getMidiCode() + root.getMidiCode(),
                NoteName.B.getMidiCode() + root.getMidiCode()
        };

    }

    private static int[] getMixolydianCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.D.getMidiCode() + root.getMidiCode(),
                NoteName.E.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.A.getMidiCode() + root.getMidiCode(),
                NoteName.Bb.getMidiCode() + root.getMidiCode()
        };
    }

    private static int[] getDorianCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.D.getMidiCode() + root.getMidiCode(),
                NoteName.Eb.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.A.getMidiCode() + root.getMidiCode(),
                NoteName.Bb.getMidiCode() + root.getMidiCode()
        };
    }

    private static int[] getPhrygianCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.Cs.getMidiCode() + root.getMidiCode(),
                NoteName.Eb.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.G.getMidiCode() + root.getMidiCode(),
                NoteName.Ab.getMidiCode() + root.getMidiCode(),
                NoteName.Bb.getMidiCode() + root.getMidiCode()
        };
    }

    private static int[] getLocrianCodes(NoteName root) {
        return new int[]{ NoteName.C.getMidiCode() + root.getMidiCode(),
                NoteName.Cs.getMidiCode() + root.getMidiCode(),
                NoteName.Eb.getMidiCode() + root.getMidiCode(),
                NoteName.F.getMidiCode() + root.getMidiCode(),
                NoteName.Fs.getMidiCode() + root.getMidiCode(),
                NoteName.Ab.getMidiCode() + root.getMidiCode(),
                NoteName.Bb.getMidiCode() + root.getMidiCode()
        };
    }


}
