package lab05Plus;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CanvasPanel extends JComponent {

    private BufferedImage paintImage;
    private Color currentColor = Color.RED;
    private Shape currentShape = Shape.values()[0];
    private Point currentStart, currentEnd;

    CanvasPanel() {
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currentEnd = e.getPoint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentStart = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawShape(paintImage.getGraphics());
                currentStart = currentEnd = null;
            }
        });

        new Thread(() -> {
            while (!Thread.interrupted()) {
                repaint();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Rectangle bounds = new Rectangle();
        g.getClipBounds(bounds);
        if (paintImage == null) {
            paintImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics imageGraphics = paintImage.getGraphics();
            imageGraphics.setColor(Color.WHITE);
            imageGraphics.fillRect(0, 0, paintImage.getWidth(), paintImage.getHeight());
        }
        g.drawImage(paintImage, 0, 0, null);
        drawShape(g);
        g.setColor(Color.GREEN);
        g.drawRect(2, 0, bounds.width - 4, bounds.height - 4);
    }

    private void drawShape(final Graphics graphics) {
        if (currentStart == null || currentEnd == null)
            return;
        graphics.setColor(currentColor);

        final int startX = Math.min(currentStart.x, currentEnd.x);
        final int startY = Math.min(currentStart.y, currentEnd.y);
        final int deltaX = currentStart.x - currentEnd.x;
        final int deltaY = currentStart.y - currentEnd.y;

        switch (currentShape) {
            case LINE:
                drawLine(graphics, currentStart, currentEnd);
                break;
            case OVAL:
                graphics.drawOval(startX, startY, Math.abs(deltaX), Math.abs(deltaY));
                break;
            case TRIANGLE:
                final Point p1 = new Point(currentStart.x, currentEnd.y);
                final Point p2 = new Point(currentEnd.x, currentEnd.y);
                final Point p3 = new Point((currentStart.x+currentEnd.x)/2, currentStart.y);
                drawLine(graphics, p1, p2);
                drawLine(graphics, p1, p3);
                drawLine(graphics, p2, p3);
                break;
            case RECTANGLE:
                graphics.drawRect(startX, startY, Math.abs(deltaX), Math.abs(deltaY));
                break;
        }
    }

    private void drawLine(final Graphics graphics, final Point start, final Point end) {
        graphics.drawLine(start.x, start.y, end.x, end.y);
    }

    public synchronized void saveToFile(final File file) {
        try {
            ImageIO.write(paintImage, "PNG", file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Can't save", JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void loadFromFile(final File file) {
        try {
            paintImage = ImageIO.read(file);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Can't load", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCurrentShape(final Shape shape) {
        currentShape = shape;
    }
}
