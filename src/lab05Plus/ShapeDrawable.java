package lab05Plus;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class ShapeDrawable implements Serializable {
    final int ID;

    public Point start, end;
    public int weight;
    public Shape shape;
    public boolean filled;
    public Color color;

    public ShapeDrawable(int ID, Point start, Point end, Color color, int weight, Shape shape, boolean filled) {
        if (start != null && end != null) {
            this.start = new Point(start);
            this.end = new Point(end);
        }
        this.weight = weight;
        this.shape = shape;
        this.filled = filled;
        this.color = color;
        this.ID = ID;
    }

    public void drawShape(final Graphics graphics, final boolean outline) {
        if (start == null || end == null)
            return;
        graphics.setColor(color);

        final int startX = Math.min(start.x, end.x);
        final int startY = Math.min(start.y, end.y);
        final int deltaX = start.x - end.x;
        final int deltaY = start.y - end.y;

        switch (shape) {
            case LINE:
                drawLine(graphics, start, end);
                break;
            case OVAL:
                Stroke oldStroke = ((Graphics2D) graphics).getStroke();
                ((Graphics2D) graphics).setStroke(new BasicStroke(weight));
                if (filled)
                    graphics.fillOval(startX, startY, Math.abs(deltaX), Math.abs(deltaY));
                else
                    graphics.drawOval(startX, startY, Math.abs(deltaX), Math.abs(deltaY));
                ((Graphics2D) graphics).setStroke(oldStroke);
                break;
            case TRIANGLE: {
                final Point p1 = new Point(start.x, end.y);
                final Point p2 = new Point(end.x, end.y);
                final Point p3 = new Point((start.x + end.x) / 2, start.y);
                drawLine(graphics, p1, p2);
                drawLine(graphics, p1, p3);
                drawLine(graphics, p2, p3);
                if (filled) {
                    int xPoints[] = {p1.x, p2.x, p3.x};
                    int yPoints[] = {p1.y, p2.y, p3.y};
                    graphics.fillPolygon(xPoints, yPoints, 3);
                }
            }
            break;
            case RECTANGLE: {
                final Point p1 = new Point(startX, startY);
                final Point p2 = new Point(startX + Math.abs(deltaX), startY);
                final Point p3 = new Point(startX + Math.abs(deltaX), startY + Math.abs(deltaY));
                final Point p4 = new Point(startX, startY + Math.abs(deltaY));
                drawLine(graphics, p1, p2);
                drawLine(graphics, p2, p3);
                drawLine(graphics, p3, p4);
                drawLine(graphics, p4, p1);
                if (filled) {
                    graphics.fillRect(startX, startY, Math.abs(deltaX), Math.abs(deltaY));
                }
            }
            break;
        }

        if (outline) {
            graphics.setColor(Color.CYAN);
            Rectangle outlineRect = getOutline();
            graphics.drawRect(outlineRect.x, outlineRect.y, outlineRect.width, outlineRect.height);
        }
    }

    private void drawLine(final Graphics graphics, final Point start, final Point end) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        AffineTransform affineTransform = graphics2D.getTransform();

        graphics2D.translate(start.x, start.y);
        int dx = end.x - start.x;
        int dy = end.y - start.y;
        graphics2D.rotate(Math.atan2(dy, dx));
        graphics2D.fillRect(0, -this.weight / 2, (int) Math.sqrt(dx * dx + dy * dy), this.weight);

        graphics2D.setTransform(affineTransform);
    }

    public boolean isInside(final Point point) {
        return getOutline().contains(point);
    }

    public int getEditMode(final Point point) {
        if (isInOutline(point)) {
            if (point.y > getOutline().getCenterY())
                return 0;

            return 1;
        } else if (isInside(point)) return 2;
        return -1;
    }

    public boolean isInOutline(final Point point) {
        final Rectangle outline = getOutline();
        final Rectangle outOutline = new Rectangle(outline);
        outOutline.grow(10, 10);
        final Rectangle innerOutline = new Rectangle(outline);
        innerOutline.grow(-10, -10);
        return outOutline.contains(point) && !innerOutline.contains(point);
    }

    private Rectangle getOutline() {
        if (start != null && end != null) {
            final int startX = Math.min(start.x, end.x) - 5;
            final int startY = Math.min(start.y, end.y) - 5;
            final int deltaX = start.x - end.x;
            final int deltaY = start.y - end.y;
            return new Rectangle(startX, startY, Math.abs(deltaX) + 10, Math.abs(deltaY) + 10);
        } else return new Rectangle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShapeDrawable that = (ShapeDrawable) o;
        return ID == that.ID;
    }

    @Override
    public int hashCode() {
        return ID;
    }
}
