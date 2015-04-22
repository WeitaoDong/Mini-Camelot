package Controller;

import Model.GuiNode;
import Model.Node;
import View.CamelotGui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by weitao on 4/20/15.
 */
public class nodeListenerForMouse implements MouseListener, MouseMotionListener {

    private HashSet<GuiNode> nodes;
    private CamelotGui camelotGui;

    private GuiNode dragNode;
    private int dragOffsetX;
    private int dragOffsetY;


    public nodeListenerForMouse(HashSet<GuiNode> nodes, CamelotGui camelotGui) {
        this.nodes = nodes;
        this.camelotGui = camelotGui;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        int x = evt.getPoint().x;
        int y = evt.getPoint().y;

        // find out which piece to move.
        // we check the list from top to buttom
        // (therefore we itereate in reverse order)
        //
        Iterator tmp = nodes.iterator();
        while (tmp.hasNext()) {
            GuiNode temp = (GuiNode) tmp.next();
            if (mouseOverNode(temp,x,y)){
                // calculate offset, because we do not want the drag piece
                // to jump with it's upper left corner to the current mouse
                // position
                //
                this.dragOffsetX = x - temp.getX();
                this.dragOffsetY = y - temp.getY();
                this.dragNode = temp;
                break;
            }
//            if (this.dragNode != null) {
//                this.nodes.remove(this.dragNode);
//                this.nodes.add(this.dragNode);
//            }
        }
    }

    /**
     * check whether the mouse is currently over this piece
     * @param node the playing piece
     * @param x x coordinate of mouse
     * @param y y coordinate of mouse
     * @return true if mouse is over the piece
     */
    private boolean mouseOverNode(GuiNode node, int x, int y) {
        return node.getX() <= x
                && node.getX()+node.getWidth() >= x
                && node.getY() <= y
                && node.getY()+node.getHeight() >= y;
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        if( this.dragNode != null){
            int x = evt.getPoint().x - this.dragOffsetX;
            int y = evt.getPoint().y - this.dragOffsetY;

            // set game piece to the new location if possible
            //
            camelotGui.setNewNodeLocation(dragNode, x, y);
            this.camelotGui.repaint();
            this.dragNode = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if(this.dragNode != null){
            this.dragNode.setX(evt.getPoint().x - this.dragOffsetX);
            this.dragNode.setY(evt.getPoint().y - this.dragOffsetY);
            this.camelotGui.repaint();
        }

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mouseMoved(MouseEvent arg0) {}

}
