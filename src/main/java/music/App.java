package music;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.gui.MainFrame;


public class App {

    private static int TEMPO = 120;

    public static int getTEMPO() {
        return TEMPO;
    }

    public static void setTEMPO(int tEMPO) {
        TEMPO = tEMPO;
    }

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        //        printNotes();

        MainFrame f = new MainFrame("Music");

    }

}

