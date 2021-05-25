package music.theory;

public class Scale {

    public static Pitch[] majorScale(NoteName root) {
        int[] codes = getMajorCodes(root);
        Pitch[] retVal = new Pitch[8];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);

        }
        return retVal;
    }

    public static Pitch[] minorScale(NoteName root) {
        int[] codes = getMinorCodes(root);
        Pitch[] retVal = new Pitch[8];
        for(int i = 0; i < codes.length; i++) {
            retVal[i] = new Pitch(codes[i]);
        }
        return retVal;
    }


    private static int[] getMinorCodes(NoteName root) {

        return new int[] { 0 + root.getMidiCode(),
                2 + root.getMidiCode(),
                3 + root.getMidiCode(),
                5 + root.getMidiCode(),
                7 + root.getMidiCode(),
                8 + root.getMidiCode(),
                10 + root.getMidiCode()
        };

    }

    private static int[] getMajorCodes(NoteName root) {
        return new int[]{ 0 + root.getMidiCode(),
                2 + root.getMidiCode(),
                4 + root.getMidiCode(),
                5 + root.getMidiCode(),
                7 + root.getMidiCode(),
                9 + root.getMidiCode(),
                11 + root.getMidiCode()
        };

    }


}
