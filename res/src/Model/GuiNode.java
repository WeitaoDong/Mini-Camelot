package Model;

import View.CamelotGui;

import java.awt.*;

/**
 * Created by weitao on 4/20/15.
 */
public class GuiNode {

    private Image image;
    private int x;
    private int y;
    private Node node;

    public GuiNode(Image image, Node node) {
        this.image = image;
        this.node = node;

        this.resetToUnderlyingNodePosition();
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return image.getWidth(null);
    }

    public int getHeight() {
        return image.getHeight(null);
    }

    public int getColor() {
        return this.node.getColor();
    }

    @Override
    public String toString() {
        return this.node+" "+x+"/"+y;
    }

    /**
     * move the gui node back to the coordinates that
     * correspond with the underlying node's row and column
     */
    public void resetToUnderlyingNodePosition() {
        this.x = CamelotGui.convertColumnToX(node.getColumn());
        this.y = CamelotGui.convertRowToY(node.getRow());
    }


    public Node getnode() {
        return node;
    }

    public boolean isCaptured() {
        return this.node.isCaptured();
    }
}
