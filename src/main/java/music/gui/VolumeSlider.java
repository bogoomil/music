package music.gui;

import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VolumeSlider extends JSlider {
    public VolumeSlider() {
        super();
        final TitledBorder tb = new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null);


        this.setMaximum(127);
        this.setMinimum(0);
        this.setSnapToTicks(true);
        this.setPaintTicks(true);
        this.setPaintLabels(true);
        this.setMajorTickSpacing(50);
        this.setMinorTickSpacing(5);
        this.setBorder(tb);

        this.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tb.setTitle("Volume: " + getValue());

            }
        });


    }
}
