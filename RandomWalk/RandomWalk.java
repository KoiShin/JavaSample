import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class RandomWalk extends Applet implements Runnable, ActionListener {
    Thread thread = null;
    Image workImage;
    Graphics workGraphics;

    Button rewriteButton;

    int appletWidth, appletHeight;
    int currentX, currentY;

    public void init() {
        appletWidth  = getSize().width;
        appletHeight = getSize().height;
        workImage = createImage(appletWidth, appletHeight);
        workGraphics = workImage.getGraphics();

        workGraphics.drawLine(0, appletHeight / 2, appletWidth, appletHeight / 2);
        workGraphics.drawLine(10, 0, 10, appletHeight);

        rewriteButton = new Button("Rewrite");
        add(rewriteButton);
        rewriteButton.addActionListener(this);
        rewriteButton.setBounds(10, 10, 50, 20);

        currentX = 10;
        currentY = appletHeight / 2;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void paint(Graphics g) {
        g.drawImage(workImage, 0, 0, this);
    }

    public void run() {
        while (thread != null) {
            repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                showStatus(" " + e);
            }
        }
    }

    public void walk() {
        workGraphics.fillRect(currentX, currentY, 1, 1);
        currentX++;
        currentY += (int)(Math.random() * 159 % 2) == 1 ? 1 : -1;

        // int direction = (int)(Math.random() * 159 % 3);
        // switch (direction) {
        // case 0:
        //     break;
        // case 1:
        //     currentY++;
        //     break;
        // case 2:
        //     currentY--;
        // }
    }

    public void actionPerformed(ActionEvent evt) {
        Button button = (Button)evt.getSource();
        if (button == rewriteButton) {
            currentX = 10;
            currentY = appletHeight / 2;
            for (int i = 0; i < 1000; i++) {
                walk();
            }
        }
    }
}
