package music.gui.measureeditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import music.event.MeasureSelectedEvent;
import music.event.TrackSelectedEvent;
import music.gui.MainFrame;
import music.theory.Measure;

public class MeasureButton extends JButton {
    private Measure measure;
    private int trackId;

    public MeasureButton(Measure measure, int trackId) {
        super(trackId + "/" + measure.getNum());
        this.measure = measure;
        this.trackId = trackId;
        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.eventBus.post(new MeasureSelectedEvent(measure));
                MainFrame.eventBus.post(new TrackSelectedEvent(trackId));

            }
        });

    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }


}
