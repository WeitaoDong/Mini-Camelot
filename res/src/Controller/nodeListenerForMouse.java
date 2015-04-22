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

    private HashSet<GuiNode> guiNodes;
    private CamelotGui camelotGui;

    private GuiNode dragNode;
    private int dragOffsetX;
    private int dragOffsetY;


    public nodeListenerForMouse(HashSet<GuiNode> guiNodes, CamelotGui camelotGui) {
        this.guiNodes = guiNodes;
        this.camelotGui = camelotGui;
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        int x = evt.getPoint().x;
        int y = evt.getPoint().y;

        for (GuiNode guinode:guiNodes){
            if (mouseOverNode(guinode,x,y)){
                if( (this.camelotGui.getGameState() == ChessGame.GAME_STATE_WHITE
                        && guinode.getColor() == Node.COLOR_WHITE
                ) ||
                        (this.camelotGui.getGameState() == ChessGame.GAME_STATE_BLACK
                                && guinode.getColor() == Node.COLOR_BLACK
                        )
                        ) {
                    // calculate offset, because we do not want the drag piece
                    // to jump with it's upper left corner to the current mouse
                    // position
                    //
                    this.dragOffsetX = x - guinode.getX();
                    this.dragOffsetY = y - guinode.getY();
                    this.dragNode = guinode;
                    break;
                }
            }
        }
        if (this.dragNode != null) {
            this.guiNodes.remove(this.dragNode);
            this.guiNodes.add(this.dragNode);
        }
    }

    /**
     * check whether the mouse is currently over this piece
     * @param guiNode the playing piece
     * @param x x coordinate of mouse
     * @param y y coordinate of mouse
     * @return true if mouse is over the piece
     */
    private boolean mouseOverNode(GuiNode guiNode, int x, int y) {
        return guiNode.getX() <= x
                && guiNode.getX()+guiNode.getWidth() >= x
                && guiNode.getY() <= y
                && guiNode.getY()+guiNode.getHeight() >= y;
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
