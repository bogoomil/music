package music;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import music.gui.AppFrame;


public class App {

    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);

    private static int TEMPO = 120;

    public static final EventBus eventBus = new EventBus();


    public static int getTEMPO() {
        return TEMPO;
    }

    public static void setTEMPO(int tEMPO) {
        TEMPO = tEMPO;
    }

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static final Color DEFAULT_NOTE_LABEL_COLOR = Color.CYAN;
    public static final Color SELECT_COLOR = Color.ORANGE;
    public static final Color PLAYING_COLOR = Color.PINK;
    public static final Color DISABLED_COLOR = Color.DARK_GRAY;

    public static void main(String[] args) {

        //        printNotes();

        //MainFrame f = new MainFrame("Music");

        AppFrame af = new AppFrame();

    }

}

