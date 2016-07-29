import java.applet.*;
import java.awt.*;
import java.awt.image.PixelGrabber;
import java.awt.event.*;

public class ComplementaryColorSquare extends Applet implements MouseMotionListener, AdjustmentListener, Runnable {
    Thread thread = null;
    int AppletWidth, AppletHeight;
    Image WorkImage;
    Graphics WorkGraphics;
    Scrollbar RScrollBar, GScrollBar, BScrollBar;
    float RValue, GValue, BValue;

    public void init() {
        AppletWidth = getSize().width;
        AppletHeight = getSize().height;

        WorkImage = createImage(AppletWidth, AppletHeight);
        WorkGraphics = WorkImage.getGraphics( );

        WorkGraphics.setColor(new Color(255, 255, 255));
        WorkGraphics.fillRect(0, 0, AppletWidth, AppletHeight);

        RScrollBar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 100);
        GScrollBar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 100);
        BScrollBar = new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 100);

        setLayout(null);
        RScrollBar.setBounds(0,  150, 10, 100); // バーの設定
        GScrollBar.setBounds(AppletWidth / 6, 150, 10, 100); // バーの設定
        BScrollBar.setBounds(AppletWidth / 6 * 2, 150, 10, 100); // バーの設定

        add(RScrollBar);
        add(GScrollBar);
        add(BScrollBar);

        RScrollBar.addAdjustmentListener(this);
        GScrollBar.addAdjustmentListener(this);
        BScrollBar.addAdjustmentListener(this);
    }

    public void start() {
        thread = new Thread(this);                    // スレッド生成
        thread.start();
    }

    public void paint(Graphics g) {
        g.drawImage(WorkImage, 0, 0, this);
        g.drawRect(50, 30, 50, 50);
        g.drawRect(250, 30, 50, 50);
    }

    public void run() {
        while (thread != null) {
            Color c = new Color(RValue, GValue, BValue);
            // Color c2 = new Color(255 - RValue, 255 - GValue, 255 - BValue);
            WorkGraphics.setColor(c);
            WorkGraphics.fillRect(50, 30, 50, 50);
            // WorkGraphics.setColor(c2);
            // WorkGraphics.fillRect(250, 30, 50, 50);
            repaint();
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void mouseMoved(MouseEvent evt) {}

    public void mouseDragged(MouseEvent evt) {}

    public void adjustmentValueChanged(AdjustmentEvent evt) {
        Scrollbar scrollbar = (Scrollbar)evt.getSource();
        if (scrollbar == RScrollBar) {
            RValue = RScrollBar.getValue() / (float)100.0;
        } else if (scrollbar == GScrollBar) {
            GValue = GScrollBar.getValue() / (float)100.0;
        } else if (scrollbar == BScrollBar) {
            BValue = BScrollBar.getValue() / (float)100.0;
        }
        repaint();
    }
}
