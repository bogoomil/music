package music.gui.trackeditor;

import java.awt.Color;

import javax.swing.JButton;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.MeasureStartedEvent;

public class MeasureButton extends JButton {
    private int measureNum;
    private static Color origColor;

    public MeasureButton(int measureNum) {
        super("" + measureNum);
        App.eventBus.register(this);
        this.measureNum = measureNum;
        origColor = this.getBackground();

    }

    @Subscribe
    private void handleMeasureStartedEvent(MeasureStartedEvent e) {
        if(e.getMeasureNum() == this.measureNum) {
            System.out.println("Measure button, measureStartEvent: " + e.getMeasureNum());
            this.setBackground(App.RED);
            this.doClick();
        }else {
            this.setBackground(origColor);
        }
    }


}
