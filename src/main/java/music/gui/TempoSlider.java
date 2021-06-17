package music.gui;

import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TempoSlider extends JSlider {
    public TempoSlider() {
        super();
        final TitledBorder tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);


        this.setSnapToTicks(true);
        this.setMinorTickSpacing(10);
        this.setMajorTickSpacing(60);
        this.setPaintLabels(true);
        this.setPaintTicks(true);
        this.setMinimum(60);
        this.setBorder(tbTempo);
        this.setMaximum(300);

        this.setValue(140);

        this.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tbTempo.setTitle("Tempo: " + TempoSlider.this.getValue());;

            }
        });

    }
}
