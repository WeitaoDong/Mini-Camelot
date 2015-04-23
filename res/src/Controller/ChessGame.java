package Controller;

import Model.GuiNode;
import Model.Node;
import View.CamelotGui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by weitao on 4/21/15.
 */
public class ChessGame {

    private int gameState;
    public static final int GAME_STATE_WHITE = 0;
    public static final int GAME_STATE_BLACK = 1;
    public static final int GAME_STATE_END = -1;

    private HashSet<Node> nodes = new HashSet<Node>();
    private HashSet<Node> availableNodes;
    private HashMap<Node, HashSet<Node>> hashMap;

    /**
     * initialize game
     */
    public ChessGame() {
        // Add the nodes
        for (int i = 0; i < 4; i++)
            createAndAddPiece(Node.COLOR_WHITE, Node.ROW_5, Node.COLUMN_C + i);
//        System.out.println(Node.COLOR_WHITE+" ");
        for (int i = 0; i < 2; i++)
            createAndAddPiece(Node.COLOR_WHITE, Node.ROW_6, Node.COLUMN_D + i);
        for (int i = 0; i < 4; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_10, Node.COLUMN_C + i);
        for (int i = 0; i < 2; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_9, Node.COLUMN_D + i);

    }

    private void createAndAddPiece(int color, int row, int column) {
        Node node = new Node(color, row, column);
        this.nodes.add(node);
    }

    public void changeGameState() {
        System.out.println(this.gameState == 1 ? "black" : "white");
        switch (this.gameState) {
            case GAME_STATE_BLACK:
                this.gameState = GAME_STATE_WHITE;
                break;
            case GAME_STATE_WHITE:
                this.gameState = GAME_STATE_BLACK;
                break;
            case GAME_STATE_END:
                break;
            default:
                throw new IllegalStateException("unknown game state: " + this.gameState);
        }
    }

    public HashSet<Node> getNodes() {
        return this.nodes;
    }

    public int getGameState() {
        return this.gameState;
    }

    public boolean moveNode(int row, int column, int targetRow, int targetColumn) {
        Node node = getNonCapturedNodeAtLocation(row, column);
        if (node==null)
            System.out.println("row= "+row+ " column= "+ column+"\n");

//        System.out.println(" bcd"+node.toString()+"\n node "+node==null);
        // check if the move is capturing an opponent piece
        int opponentColor = (node.getColor() == Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
        HashMap<Node, HashSet<Node>> tmp = judgeCaptureNode(opponentColor);
//        System.out.println(" "+tmp+"\n abc "+judgeFirstMove(tmp));
        if (judgeFirstMove(tmp)) {

            // check whether it can capture opponent automatically
            int opponentRow = (row + targetRow) / 2;
            int opponentColumn = (column + targetColumn) / 2;
            if (((Math.abs(targetColumn - column) == 0 && Math.abs(targetRow - row) == 2)
                    || (Math.abs(targetColumn - column) == 2 && Math.abs(targetRow - row) == 0)
                    || (Math.abs(targetColumn - column) == 2 && Math.abs(targetRow - row) == 2))
                    && getNonCapturedNodeAtLocation(targetRow, targetColumn) == null) {
                for (Node node1 : nodes) {
                    if (!node1.isCaptured()) {
                        if (node1.getRow() == opponentRow
                                && node1.getColumn() == opponentColumn
                                && node1.getColor() == opponentColor) {
                            node1.isCaptured(true);
                            node.isCaptured(false);
                            node.setRow(targetRow);
                            node.setColumn(targetColumn);
                            if (judgeEnd(targetRow, targetColumn)) {
                                this.gameState = ChessGame.GAME_STATE_END;
                            } else {
                                this.changeGameState();
                            }
                            System.out.println("Row = " + node.getRow() + " Column = " + node.getColumn());
                            return true;
                            // TODO Bug is here

                        }
                    }
                }
            }
            return false;
        } else if (judgeMoveNode(node, targetRow, targetColumn)) {
            System.out.println("plain and jump movement");
            node.setRow(targetRow);
            node.setColumn(targetColumn);
            if (judgeEnd(targetRow, targetColumn)) {
                this.gameState = ChessGame.GAME_STATE_END;
            } else {
                this.changeGameState();
            }
            return true;
        }

        return false;
        // TODO check whether it can kill enemy continually

    }


    public boolean judgeEnd(int row, int column) {
        if (row == Node.ROW_1 && (column == Node.COLUMN_D || column == Node.COLUMN_E)
                || (row == Node.ROW_14 && (column == Node.COLUMN_D || column == Node.COLUMN_E))) {
            return true;
        } else return false;
    }

    public boolean valid(int targetRow, int targetColumn) {
        if (targetRow < Node.ROW_1
                || targetRow > Node.ROW_14
                || targetColumn < Node.COLUMN_A
                || targetColumn > Node.COLUMN_H
                || (targetRow > Node.ROW_11 && targetColumn < Node.COLUMN_B)
                || (targetRow < Node.ROW_4 && targetColumn < Node.COLUMN_B)
                || (targetRow > Node.ROW_11 && targetColumn > Node.COLUMN_G)
                || (targetRow < Node.ROW_4 && targetColumn > Node.COLUMN_G)
                || (targetRow > Node.ROW_12 && targetColumn < Node.COLUMN_C)
                || (targetRow < Node.ROW_3 && targetColumn < Node.COLUMN_C)
                || (targetRow > Node.ROW_12 && targetColumn > Node.COLUMN_F)
                || (targetRow < Node.ROW_3 && targetColumn > Node.COLUMN_F)
                || (targetRow > Node.ROW_13 && targetColumn < Node.COLUMN_D)
                || (targetRow < Node.ROW_2 && targetColumn < Node.COLUMN_D)
                || (targetRow > Node.ROW_13 && targetColumn > Node.COLUMN_E)
                || (targetRow < Node.ROW_2 && targetColumn > Node.COLUMN_E)) {
            return false;
        }
        return true;
    }

    private boolean judgeFirstMove(HashMap<Node, HashSet<Node>> tmp) {
        for (Node node : nodes) {
            if (tmp.containsKey(node) && !tmp.get(node).isEmpty())
                return true;
        }
        return false;
    }

    private Node getNonCapturedNodeAtLocation(int row, int column) {
        for (Node node : this.nodes) {
            if (node.getRow() == row
                    && node.getColumn() == column
                    && !node.isCaptured()) {
                return node;
            }
        }
        return null;
    }

    // capture move basic version
    private HashMap<Node, HashSet<Node>> judgeCaptureNode(int opponentColor) {
        hashMap = new HashMap<Node, HashSet<Node>>();
        for (Node nodeTarget : nodes) {
            if (!nodeTarget.isCaptured()) {
                availableNodes = new HashSet<Node>();
                for (Node node : nodes) {
                    if (node.getColor() == opponentColor
                            && node.getColor() != nodeTarget.getColor()
                            && Math.abs(node.getColumn() - nodeTarget.getColumn()) <= 1
                            && Math.abs(node.getRow() - nodeTarget.getRow()) <= 1
                            && !node.isCaptured()
                            && getNonCapturedNodeAtLocation(2 * node.getRow() - nodeTarget.getRow(), 2 * node.getColumn() - nodeTarget.getColumn()) == null
                            && valid(2 * node.getRow() - nodeTarget.getRow(), 2 * node.getColumn() - nodeTarget.getColumn())) {
                        availableNodes.add(node);
                    }
                }
                hashMap.put(nodeTarget, availableNodes);
            }
        }
        return hashMap;
    }

//    private Node judgeCaptureNode(Node node, int targetRow, int targetColumn) {
//        int opponentColor = (node.getColor()==Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
//        Node tmp = getNonCapturedNodeAtLocation(node,targetRow,targetColumn);
//        if (tmp!=null){
//            return tmp;
//        } else{
//        for (Node node_tmp:nodes){
//                if (((Math.abs(node_tmp.getRow() - node.getRow())==1 && Math.abs(node_tmp.getColumn() - node.getColumn())==1)
//                        ||(Math.abs(node_tmp.getRow() - node.getRow())==1 && node_tmp.getColumn() == node.getColumn())
//                        ||(Math.abs(node_tmp.getColumn() - node.getColumn())==1 && node_tmp.getRow() == node.getRow()))
//                        &&node_tmp.getColor()== opponentColor){
//                    return node_tmp;
//                }
//            }
//        }
//        return new Node(node.getColor(),node.getRow(),node.getColumn());
//    }


//
//    private Node getNonCapturedNodeAtLocation(Node node, int targetRow, int targetColumn) {
//        int opponentColor = (node.getColor()==Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
//        int realRow = (node.getRow()+targetRow)/2;
//        int realColumn = (node.getColumn()+targetColumn)/2;
//        for (Node node_tmp : this.nodes) {
//            if( node_tmp.getRow() == realRow
//                    && node_tmp.getColumn() == realColumn
//                    && !node_tmp.isCaptured()
//                    && node_tmp.getColor()==opponentColor){
//                return node_tmp;
//            }
//        }
//        return null;
//    }

    private boolean judgeOwnNode(int row, int column) {
        for (Node node : nodes) {
            if (node.getRow() == row
                    && node.getColumn() == column
                    && !node.isCaptured()) {
                return true;
            }
        }
        return false;
    }

    // Plain move & cantering move
    private boolean judgeMoveNode(Node node, int targetRow, int targetColumn) {
        for (Node node_tmp : nodes) {
            if (node_tmp.getRow() == targetRow
                    && node_tmp.getColumn() == targetColumn
                    && !node_tmp.isCaptured()) {
                System.out.println(false);
                return false;
            }
        }
        if (Math.abs(node.getRow() - targetRow) <= 2 && Math.abs(node.getColumn() - targetColumn) <= 2) {
            if (Math.abs(node.getRow() - targetRow) == 2 && node.getColumn() == targetColumn) {
                return node.getRow() - targetRow > 0 ? judgeOwnNode(node.getRow() - 1, node.getColumn()) : judgeOwnNode(node.getRow() + 1, node.getColumn());
            } else if (node.getRow() == targetRow && Math.abs(node.getColumn() - targetColumn) == 2) {
                return node.getColumn() - targetColumn > 0 ? judgeOwnNode(node.getRow(), node.getColumn() - 1) : judgeOwnNode(node.getRow(), node.getColumn() + 1);
            } else if (Math.abs(node.getRow() - targetRow) == 2 && Math.abs(node.getColumn() - targetColumn) == 2) {
                if (node.getRow() - targetRow == 2 && node.getColumn() - targetColumn == 2) {
                    return judgeOwnNode(node.getRow() - 1, node.getColumn() - 1);
                } else if (node.getRow() - targetRow == -2 && node.getColumn() - targetColumn == 2) {
                    return judgeOwnNode(node.getRow() + 1, node.getColumn() - 1);
                } else if (node.getRow() - targetRow == 2 && node.getColumn() - targetColumn == -2) {
                    return judgeOwnNode(node.getRow() - 1, node.getColumn() + 1);
                } else {
                    return judgeOwnNode(node.getRow() + 1, node.getColumn() + 1);
                }
            } else if (Math.abs(node.getRow() - targetRow) == 1 && Math.abs(node.getColumn() - targetColumn) == 1) {
                return true;
            } else if ((Math.abs(node.getRow() - targetRow) == 1 && node.getColumn() == targetColumn)
                    || (Math.abs(node.getColumn() - targetColumn) == 1 && node.getRow() == targetRow)) {
                return true;
            }
        } else return false;

        return false;
    }
}
