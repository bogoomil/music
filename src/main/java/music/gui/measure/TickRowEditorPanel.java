package music.gui.measure;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

public class TickRowEditorPanel extends JPanel {

    public TickRowEditorPanel() {

        super();
        setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setLayout(null);

        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(MouseEvent e) {


            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int cellIndex = getCellIndexByX(e.getX());
                NoteLabel l = new NoteLabel(TickRowEditorPanel.this);
                l.setBounds(getCellXByIndex(cellIndex), 0, getCellWidth(), TickRowEditorPanel.this.getHeight());
                add(l);
                validate();
                repaint();
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int incr = this.getWidth() / 32;
        int counter = 0;
        int x = 0;
        while(counter < 32) {
            if(counter % 8 == 0) {
                g.drawLine(x - 1, 0, x-1, this.getHeight());
                g.drawLine(x + 1, 0, x+1, this.getHeight());
            }
            g.drawLine(x, 0, x, this.getHeight());
            g.drawString("" + counter, x, 10);
            x += incr;
            counter++;
        }
        g.drawLine(x, 0, x, this.getHeight());
    }

    private int getCellIndexByX(int x) {
        int incr = this.getWidth() / 32;

        int cellIndex = x / incr;
        System.out.println("width: " + this.getWidth() + ", x: " + x + " cell: " + cellIndex);

        return cellIndex;
    }

    public int getCellXByIndex(int index) {
        int incr = this.getWidth() / 32;
        return incr * index;

    }

    public int getCellWidth() {
        return this.getWidth() / 32;
    }
}
