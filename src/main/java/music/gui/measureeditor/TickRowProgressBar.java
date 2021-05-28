package music.gui.measureeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.google.common.eventbus.Subscribe;

import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.event.TickRowResizedEvent;
import music.gui.MainFrame;

public class TickRowProgressBar extends JPanel {

    private JPanel labels = new JPanel(new GridLayout(1, 0, 0, 0));



    public TickRowProgressBar() {
        super();

        MainFrame.eventBus.register(this);

        FlowLayout flowLayout = (FlowLayout) getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);

        JLabel placeHolder = new JLabel("");
        placeHolder.setOpaque(true);
        placeHolder.setBackground(Color.CYAN);
        placeHolder.setForeground(Color.BLACK);
        placeHolder.setPreferredSize(new Dimension(114, 20));

        this.add(placeHolder);
        labels.setBorder(new LineBorder(new Color(0, 0, 0)));
        labels.setBackground(Color.BLACK);

        this.add(labels);

        for(int i = 0; i < 128; i++) {
            JLabel l = new JLabel();
            l.setPreferredSize(new Dimension(10, 10));
            l.setBackground(Color.GREEN);
            labels.add(l);
        }
    }
    @Subscribe
    public void handleTickOnEvent(TickOnEvent e) {
        ((JLabel)this.labels.getComponent(e.getTick())).setOpaque(true);
        ((JLabel)this.labels.getComponent(e.getTick())).repaint();
    }
    @Subscribe
    public void handleTickOffEvent(TickOffEvent e) {
        ((JLabel)this.labels.getComponent(e.getTick())).setOpaque(false);
        ((JLabel)this.labels.getComponent(e.getTick())).repaint();
    }
    @Subscribe
    public void handleTickRowResized(TickRowResizedEvent e) {
        this.setPreferredSize(new Dimension(e.getWidth(), this.getHeight()));
        this.validate();
    }

}
