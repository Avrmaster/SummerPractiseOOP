package lab05Plus;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class CanvasPanel extends JComponent implements Serializable {

    private java.util.List<ShapeDrawable> allShapes;

    private BufferedImage paintImage;
    private Color currentColor = Color.RED;
    private Shape currentShape = Shape.values()[0];
    private Point currentStart, currentEnd;
    private boolean currentFillMode = false;
    private int currentWeight = 3;

    private ShapeDrawable chosenShape = null;
    private boolean editingShape = false;
    private int editMode = 2;

    private Point lastPoint;

    CanvasPanel() {
        allShapes = new LinkedList<>();
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (editingShape && chosenShape != null) {
                    switch (editMode) {
                        case 0:
                            chosenShape.end = e.getPoint();
                            break;
                        case 1:
                            chosenShape.start = e.getPoint();
                            break;
                        case 2:
                            if (lastPoint != null) {
                                Point curPoint = e.getPoint();
                                int deltaX = curPoint.x - lastPoint.x;
                                int deltaY = curPoint.y - lastPoint.y;
                                chosenShape.start.x += deltaX;
                                chosenShape.start.y += deltaY;
                                chosenShape.end.x += deltaX;
                                chosenShape.end.y += deltaY;
                            }
                            break;
                    }

                } else currentEnd = e.getPoint();
                lastPoint = e.getPoint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int editMode = chosenShape == null ? -1 : chosenShape.getEditMode(e.getPoint());
                if (chosenShape != null && editMode != -1) {
                    editingShape = true;
                    CanvasPanel.this.editMode = editMode;
                } else {
                    chosenShape = null;
                    for (ShapeDrawable shapeDrawable : allShapes)
                        if (shapeDrawable.isInside(e.getPoint()))
                            chosenShape = shapeDrawable;
                    editingShape = false;
                }

                currentStart = (chosenShape == null && !editingShape) ? e.getPoint() : null;
                lastPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                allShapes.add(
                        new ShapeDrawable(allShapes.size(), currentStart, currentEnd, currentColor,
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
        if (chosenShape != null && editMode != -1)
            chosenShape.filled = fillMode;
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
        imageGraphics.fillRect(0, 0, paintImage.getWidth(), paintImage.getHeight());
        for (ShapeDrawable shapeDrawable : allShapes) {
            shapeDrawable.drawShape(imageGraphics, chosenShape != null && chosenShape.equals(shapeDrawable));
        }
        new ShapeDrawable(-1, currentStart, currentEnd, currentColor, currentWeight, currentShape, currentFillMode)
                .drawShape(imageGraphics, false);

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
        try (ObjectOutputStream fileWrite =
                     new ObjectOutputStream(new FileOutputStream(new File(file.getPath() + ".superProgaFile")))) {
            ImageIO.write(paintImage, "PNG", file);
            fileWrite.writeObject(allShapes);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Can't save", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadFromFile(final File file) {
        try (ObjectInputStream fileRead =
                     new ObjectInputStream(new FileInputStream(file))) {
            allShapes = (java.util.List<ShapeDrawable>) fileRead.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Wrong file",
                    "Can't load", JOptionPane.ERROR_MESSAGE);
        }
    }

}
