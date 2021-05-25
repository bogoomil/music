package music.theory;

public enum NoteName {

    C(0), Cs(1), D(2), Eb(3), E(4), F(5), Fs(6), G(7), Ab(8), A(9), Bb(10), B(11);//c2(12),Cs2(13),D2(14)

    private int midiCode;

    private NoteName(int midiCode) {
        this.midiCode = midiCode;
    }

    public int getMidiCode() {
        return midiCode;
    }

    public static NoteName byCode(int code) throws Exception {
        if (code > 11) {
            code = code % 12;
        }
        for (int i = 0; i < NoteName.values().length; i++) {

            if (NoteName.values()[i].getMidiCode() == code) {
                return NoteName.values()[i];
            }
        }
        throw new Exception("Hibás kód: " + code);
    }
}
