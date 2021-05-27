package music.theory;

public enum NoteLength {
    //EGESZ(1), HAROMNEGYED(3), FEL(2), NEGYED(4), NYOLCAD(8), TIZENHATOD(16), HARMICKETTED(32);
    NEGYSZERES(128), HAROMSZOROS(96), DUPLA(64), EGESZ(32), HAROMNEGYED(24), FEL(16), NEGYED(8), NYOLCAD(4), TIZENHATOD(2), HARMICKETTED(1);

    int ertek;

    private NoteLength(int ertek) {
        this.ertek = ertek;
    }

    public int getErtek() {
        return this.ertek;
    }

    public static NoteLength ofErtek(int ertek) {
        for(NoteLength nl : NoteLength.values()) {
            if(nl.getErtek() == ertek) {
                return nl;
            }
        }
        return null;
    }

}
