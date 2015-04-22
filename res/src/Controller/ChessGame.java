package Controller;

import Model.Node;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by weitao on 4/21/15.
 */
public class ChessGame {

    private int gameState = GAME_STATE_WHITE;
    public static final int GAME_STATE_WHITE = 0;
    public static final int GAME_STATE_BLACK = 1;

    private HashSet<Node> nodes = new HashSet<Node>();

    /**
     * initialize game
     */
    public ChessGame(){
        // Add the nodes
        for (int i=0; i<4; i++)
            createAndAddPiece(Node.COLOR_WHITE, Node.ROW_5, Node.COLUMN_C+i);
        for (int i=0; i<2; i++)
            createAndAddPiece(Node.COLOR_WHITE, Node.ROW_6, Node.COLUMN_D+i);
        for (int i=0; i<4; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_9, Node.COLUMN_C+i);
        for (int i=0; i<2; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_10, Node.COLUMN_D+i);

    }

    private void createAndAddPiece(int color, int row, int column) {
        Node node = new Node(color, row, column);
        this.nodes.add(node);
    }
    public void changeGameState() {
        System.out.println(this.gameState);
        switch (this.gameState){
            case GAME_STATE_BLACK:
                this.gameState = GAME_STATE_WHITE;
                break;
            case GAME_STATE_WHITE:
                this.gameState = GAME_STATE_BLACK;
                break;
            default:
                throw new IllegalStateException("unknow game state: " + this.gameState);
        }
    }

    public HashSet<Node> getNodes(){
        return this.nodes;
    }
    public int getGameState() {
        return this.gameState;
    }
    public void moveNode(int row, int column, int targetRow, int targetColumn) {
        Node node = getNonCapturedNodeAtLocation(row,column);

        // check if the move is capturing an opponent piece
        int opponentColor = (node.getColor()==Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
        if (judegeMoveNode(node, targetRow, targetColumn)) {
            node.setRow(targetRow);
            node.setColumn(targetColumn);
        }
        if (judegeCaptureNode(node,opponentColor)) {
            Node opponentNode = getNonCapturedNodeAtLocation(node, opponentColor, targetRow, targetColumn);
            opponentNode.isCaptured(true);
            node.setRow(targetRow);
            node.setColumn(targetColumn);
        }
    // check whether it can kill enemy continually
    }

    private Node getNonCapturedNodeAtLocation(int row, int column) {
        for (Node node : this.nodes) {
            if( node.getRow() == row
                    && node.getColumn() == column
                    && !node.isCaptured() ){
                return node;
            }
        }
        return null;
    }

    // capture move basic version
    private boolean judegeCaptureNode(Node node, int color) {
        for (Node node_tmp:nodes){
            if (Math.abs(node_tmp.getRow()-node.getRow())==1
                    &&Math.abs(node_tmp.getColumn()-node.getColumn())==1
                    &&node_tmp.getColor()==color){
                int row = node_tmp.getRow()-(node.getRow()-node_tmp.getRow());
                int column = node_tmp.getColumn()-(node.getColumn()-node_tmp.getColumn());
                return judegeMoveNode(node,row,column);
            }
        }
        return false;
    }

    private Node getNonCapturedNodeAtLocation(Node node, int color, int row, int column) {
        int realRow = (node.getRow()+row)/2;
        int realColumn = (node.getColumn()+column)/2;
        return new Node(color,realRow,realColumn);
    }

    // Plain move & cantering move
    private boolean judegeMoveNode(Node node, int row, int column) {
        for (Node node_tmp : nodes) {
            if (node_tmp.getRow() == row && node_tmp.getColumn() == column) {
                return false;
            }
        }
        if (Math.abs(node.getRow() - row)<=2 && Math.abs(node.getColumn() - column)<=2) {
            if (Math.abs(node.getRow() - row) == 2 && Math.abs(node.getColumn() - column) == 0) {
                Node adjacentLeft = new Node(node.getColor(),node.getRow() - 1, node.getColumn());
                Node adjacentRight = new Node(node.getColor(),node.getRow() + 1, node.getColumn());
                return node.getRow()-row>0?nodes.contains(adjacentLeft):nodes.contains(adjacentRight);
                } else if (Math.abs(node.getRow() - row) == 0 && Math.abs(node.getColumn() - column) == 2) {
                    Node adjacentUp = new Node(node.getColor(), node.getRow(), node.getColumn() - 1);
                    Node adjacentDown = new Node(node.getColor(), node.getRow(), node.getColumn() + 1);
                    return node.getColumn()-column>0?nodes.contains(adjacentUp):nodes.contains(adjacentDown);
                } else if (Math.abs(node.getRow() - row) == 2 && Math.abs(node.getColumn() - column) == 2) {
                    Node adjacentUpLeft = new Node(node.getColor(), node.getRow()-1, node.getColumn() + 1);
                    Node adjacentUpRight = new Node(node.getColor(), node.getRow()+1, node.getColumn() + 1);
                    Node adjacentDownLeft = new Node(node.getColor(), node.getRow()-1, node.getColumn() - 1);
                    Node adjacentDownRight = new Node(node.getColor(), node.getRow()+1, node.getColumn() - 1);
                    if (node.getRow() - row == 2 && node.getColumn() - column == 2) {
                        return nodes.contains(adjacentUpLeft);
                    } else if (node.getRow() - row == -2 && node.getColumn() - column == 2) {
                        return nodes.contains(adjacentUpRight);
                    } else if (node.getRow() - row == 2 && node.getColumn() - column == -2) {
                        return nodes.contains(adjacentDownLeft);
                    } else { return nodes.contains(adjacentDownRight);}
                } else if (Math.abs(node.getRow() - row) == 1 && Math.abs(node.getColumn() - column) == 1) return true;
                else return false;
            }
        return false;
        }
}
