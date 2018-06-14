package lab05Plus;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class ShapeDrawable {
    public Point start, end;
    public int weight;
    public Shape shape;
    public boolean filled;
    public Color color;

    public ShapeDrawable(Point start, Point end, Color color, int weight, Shape shape, boolean filled) {
        if (start != null && end != null) {
            this.start = new Point(start);
            this.end = new Point(end);
        }
        this.weight = weight;
        this.shape = shape;
        this.filled = filled;
        this.color = color;
    }

    public void drawShape(final Graphics graphics) {
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

}
