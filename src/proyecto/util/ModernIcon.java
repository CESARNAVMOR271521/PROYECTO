package proyecto.util;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;

/**
 * Dynamic vector icon generator for Barber Chupirul.
 * Draws modern, clean icons using Java 2D.
 */
public class ModernIcon implements Icon {

    public enum IconType {
        ADD,        // +
        EDIT,       // Pencil
        DELETE,     // Trash / X
        SAVE,       // Floppy / Check
        CANCEL,     // X
        SEARCH,     // Magnifier
        PRINT,      // Printer
        REFRESH,    // Arrows
        CLEAR       // Eraser / Broom
    }

    private final IconType type;
    private final int size;
    private final int strokeWidth;

    public ModernIcon(IconType type) {
        this(type, 16);
    }

    public ModernIcon(IconType type, int size) {
        this.type = type;
        this.size = size;
        this.strokeWidth = Math.max(2, size / 8);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Use component foreground by default, or fallback to white inside buttons
        g2.setColor(c.getForeground());
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Translate to position
        g2.translate(x, y);

        switch (type) {
            case ADD:
                drawAdd(g2);
                break;
            case EDIT:
                drawEdit(g2);
                break;
            case DELETE:
                drawDelete(g2);
                break;
            case SAVE:
                drawSave(g2);
                break;
            case CANCEL:
                drawCancel(g2);
                break;
            case SEARCH:
                drawSearch(g2);
                break;
            case PRINT:
                drawPrint(g2);
                break;
            case REFRESH:
                drawRefresh(g2);
                break;
            case CLEAR:
                drawClear(g2);
                break;
        }

        g2.dispose();
    }

    private void drawAdd(Graphics2D g2) {
        int pad = 3;
        g2.draw(new Line2D.Float(size/2f, pad, size/2f, size-pad));
        g2.draw(new Line2D.Float(pad, size/2f, size-pad, size/2f));
    }

    private void drawEdit(Graphics2D g2) {
        // Simple pencil
        Path2D p = new Path2D.Float();
        p.moveTo(size-4, 3);
        p.lineTo(size-1, 6);
        p.lineTo(6, size-1);
        p.lineTo(3, size-1);
        p.lineTo(3, size-4);
        p.closePath();
        g2.draw(p);
    }

    private void drawDelete(Graphics2D g2) {
        // Trash can
        int pad = 3;
        g2.draw(new Line2D.Float(pad, 4, size-pad, 4)); // Lid
        g2.draw(new Rectangle2D.Float(5, 4, size-10, size-6)); // Bin
        // Stripes
        g2.draw(new Line2D.Float(7, 7, 7, size-5));
        g2.draw(new Line2D.Float(size-7, 7, size-7, size-5));
    }

    private void drawSave(Graphics2D g2) {
        // Floppy diskish shape
        g2.draw(new Rectangle2D.Float(2, 2, size-4, size-4));
        g2.draw(new Rectangle2D.Float(4, 2, size-8, 4)); // Top part
        g2.draw(new Rectangle2D.Float(4, size-6, size-8, 4)); // Bottom shutter
    }

    private void drawCancel(Graphics2D g2) {
        int pad = 4;
        g2.draw(new Line2D.Float(pad, pad, size-pad, size-pad));
        g2.draw(new Line2D.Float(size-pad, pad, pad, size-pad));
    }

    private void drawSearch(Graphics2D g2) {
        int r = 5;
        g2.draw(new Ellipse2D.Float(3, 3, r*2, r*2));
        g2.draw(new Line2D.Float(3+r*1.4f, 3+r*1.4f, size-3, size-3));
    }

    private void drawPrint(Graphics2D g2) {
        g2.draw(new Rectangle2D.Float(3, 5, size-6, 6)); // Body
        g2.draw(new Rectangle2D.Float(5, 2, size-10, 3)); // Paper top
        g2.draw(new Rectangle2D.Float(5, 11, size-10, 3)); // Paper out
    }

    private void drawRefresh(Graphics2D g2) {
        g2.drawArc(3, 3, size-6, size-6, 45, 270);
        g2.draw(new Line2D.Float(size/2f+2, 2, size/2f+5, 5)); // Arrow head
    }

    private void drawClear(Graphics2D g2) {
         // Broom style
         g2.draw(new Line2D.Float(size-3, 3, 7, size-7)); // Handle
         Path2D br = new Path2D.Float();
         br.moveTo(7, size-7);
         br.lineTo(3, size-3);
         br.lineTo(6, size-1);
         br.lineTo(10, size-5);
         br.closePath();
         g2.draw(br);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
