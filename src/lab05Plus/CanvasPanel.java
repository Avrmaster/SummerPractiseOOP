package lab05Plus;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class CanvasPanel extends JComponent {

    private java.util.List<ShapeDrawable> allShapes;

    private BufferedImage paintImage;
    private Color currentColor = Color.RED;
    private Shape currentShape = Shape.values()[0];
    private Point currentStart, currentEnd;
    private boolean currentFillMode = false;
    private int currentWeight = 3;

    CanvasPanel() {
        allShapes = new LinkedList<>();
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
                allShapes.add(
                        new ShapeDrawable(currentStart, currentEnd, currentColor,
                                currentWeight, currentShape, currentFillMode));
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
        this.setComponentPopupMenu(createPopupMenu());
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    public void setCurrentFillMode(boolean fillMode) {
        this.currentFillMode = fillMode;
    }

    public void setCurrentShape(final Shape shape) {
        currentShape = shape;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Rectangle bounds = new Rectangle();
        g.getClipBounds(bounds);
        if (paintImage == null) {
            paintImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_3BYTE_BGR);
        }
        Graphics imageGraphics = paintImage.getGraphics();
        imageGraphics.setColor(Color.WHITE);
        imageGraphics.fillRect(0, 0, paintImage.getWidth(), paintImage.getHeight());
        for (ShapeDrawable shapeDrawable : allShapes) {
            shapeDrawable.drawShape(imageGraphics);
        }
        new ShapeDrawable(currentStart, currentEnd, currentColor, currentWeight, currentShape, currentFillMode)
                .drawShape(imageGraphics);

        g.drawImage(paintImage, 0, 0, null);
        g.setColor(Color.GREEN);
        g.drawRect(2, 0, bounds.width - 4, bounds.height - 4);
    }

    private JPopupMenu createPopupMenu() {
        final JPopupMenu menu = new JPopupMenu();

        JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMinimum(1);
        slider.setMaximum(10);
        slider.setValue(currentWeight);
        slider.addChangeListener(e -> {
            currentWeight = slider.getValue();
        });
        menu.add(new JLabel("Weight"));
        menu.add(slider);

        return menu;
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

}
