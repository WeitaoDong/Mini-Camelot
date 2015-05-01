package Controller;

import Model.GuiNode;
import Model.Node;
import View.CamelotGui;
import sun.security.x509.IPAddressName;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by weitao on 4/21/15.
 */
public class ChessGame implements Runnable{

    private int gameState = GAME_STATE_WHITE;
    public static final int GAME_STATE_WHITE = 0;
    public static final int GAME_STATE_BLACK = 1;
//    public static final int GAME_STATE_END = -1;
    public static final int GAME_STATE_END_BLACK_WON = 2;
    public static final int GAME_STATE_END_WHITE_WON = 3;

    protected HashSet<Node> nodes = new HashSet<Node>();
    protected HashSet<Node> availableNodes;
    protected HashMap<Node, HashSet<Node>> hashMap;
    private  HashMap<Node, HashSet<Node>> tmp;
    private IPlayerHandler white_player;
    private IPlayerHandler black_player;
    private IPlayerHandler active_player;
    private CamelotGui camelotGui;
    protected ArrayList<Node> capturedNodes = new ArrayList<Node>();
    private IPlayerHandler blackPlayerHandler;
    private IPlayerHandler whitePlayerHandler;
    private IPlayerHandler activePlayerHandler;
//    private MoveValidator moveValidator;

    /**
     * initialize game
     */
    public ChessGame() {
        // Add the nodes
        for (int i = 0; i < 4; i++)
            createAndAddNode(Node.COLOR_WHITE, Node.ROW_5, Node.COLUMN_C + i);
        for (int i = 0; i < 2; i++)
            createAndAddNode(Node.COLOR_WHITE, Node.ROW_6, Node.COLUMN_D + i);
        for (int i = 0; i < 4; i++)
            createAndAddNode(Node.COLOR_BLACK, Node.ROW_10, Node.COLUMN_C + i);
        for (int i = 0; i < 2; i++)
            createAndAddNode(Node.COLOR_BLACK, Node.ROW_9, Node.COLUMN_D + i);

    }

    public void startGame() {
        // check if all players are ready
        System.out.println("ChessGame: waiting for players");
        while(this.black_player==null||this.white_player==null){
            try {Thread.sleep(1000);} catch (InterruptedException e){}
        }

        this.active_player = this.white_player;
        System.out.println("ChessGame: starting game flow");
        while (this.gameState!=ChessGame.GAME_STATE_END_WHITE_WON){
            if (this.gameState==ChessGame.GAME_STATE_END_BLACK_WON) break;
            Move tmp = waitForMove();
            swapActivePlayer(tmp);
        }
        System.out.println("gameState = "+this.gameState);
        if (this.gameState == ChessGame.GAME_STATE_END_WHITE_WON) {
            JOptionPane.showMessageDialog(camelotGui, "Over, white is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        } else if (this.gameState == ChessGame.GAME_STATE_END_BLACK_WON){
            JOptionPane.showMessageDialog(camelotGui, "Over, black is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println("ChessGame: game ended");
    }
    public void setPlayer(int nodeColor, IPlayerHandler iPlayerHandler){
        switch (nodeColor) {
            case Node.COLOR_WHITE:
                this.white_player = iPlayerHandler; break;
            case Node.COLOR_BLACK:
                this.black_player = iPlayerHandler; break;
            default: throw new IllegalArgumentException("Invalid pieceColor: "+nodeColor);

        }
    }

    private void swapActivePlayer(Move move) {
        // check if game end condition has been reached
        //
        if( this.active_player == this.white_player ){
            this.active_player = this.black_player;
        }else{
            this.active_player = this.white_player;
        }

        this.changeGameState(move);
    }


    public void changeGameState(Move move) {

        if (this.judgeEnd(move.targetRow, move.targetColumn)) {
            if (this.gameState == ChessGame.GAME_STATE_BLACK) {
                this.gameState = ChessGame.GAME_STATE_END_BLACK_WON;
            } else if(this.gameState == ChessGame.GAME_STATE_WHITE){
                this.gameState = ChessGame.GAME_STATE_END_WHITE_WON;
            }else{
                // leave game state as it is
            }
            return;
        }
        switch (this.gameState) {
            case GAME_STATE_BLACK:
                this.gameState = GAME_STATE_WHITE;
                break;
            case GAME_STATE_WHITE:
                this.gameState = GAME_STATE_BLACK;
                break;
            case GAME_STATE_END_BLACK_WON:
                break;
            case GAME_STATE_END_WHITE_WON:
                break;
            default:
                throw new IllegalStateException("unknown game state: " + this.gameState);
        }
    }

    private Move waitForMove(){
        Move move = null;
        // wait for the move
        do {
            move = this.active_player.getMove();
            try {Thread.sleep(100);} catch (InterruptedException e){e.printStackTrace();}
        } while (move==null||!valid(move.targetRow, move.targetColumn)||!judgeMove(move));//|| !this.moveValidator.isMoveValid(move,false)); // TODO 单独判断是否合法的
//        System.out.println(this.judgeMove(move));
        boolean success = this.moveNode(move);
        if (success){
            this.black_player.moveSuccessfullyExecuted(move);
            this.white_player.moveSuccessfullyExecuted(move);
        } else {
            throw new IllegalStateException("move was valid, but failed to execute it");
        }
        return move;
    }

    private void createAndAddNode(int color, int row, int column) {
        Node node = new Node(color, row, column);
        this.nodes.add(node);
    }



    public HashSet<Node> getNodes() {
        return this.nodes;
    }

    public int getGameState() {
        return this.gameState;
    }

    public boolean judgeMove(Move move){
        Node node = getNonCapturedNodeAtLocation(move.sourceRow, move.sourceColumn);
        if (node==null) {
            System.out.println("judgeMove false");
            return false;
        }
        int opponentColor = (node.getColor() == Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
//TODO
        HashMap<Node, HashSet<Node>> tmp = new HashMap<Node, HashSet<Node>>(judgeCaptureNode(opponentColor));
        if (judgeFirstMove(tmp)) {
            if (((Math.abs(move.targetColumn - move.sourceColumn) == 0 && Math.abs(move.targetRow - move.sourceRow) == 2)
                    || (Math.abs(move.targetColumn - move.sourceColumn) == 2 && Math.abs(move.targetRow - move.sourceRow) == 0)
                    || (Math.abs(move.targetColumn - move.sourceColumn) == 2 && Math.abs(move.targetRow - move.sourceRow) == 2))
                    && getNonCapturedNodeAtLocation(move.targetRow, move.targetColumn) == null
                    && valid(move.targetRow, move.targetColumn)) {

                // check whether it can capture opponent automatically
                int opponentRow = (move.sourceRow + move.targetRow) / 2;
                int opponentColumn = (move.sourceColumn + move.targetColumn) / 2;

                for (Node node1 : nodes) {
                    if (!node1.isCaptured()) {
                        if (node1.getRow() == opponentRow
                                && node1.getColumn() == opponentColumn
                                && node1.getColor() == opponentColor) {
                            return true;
                        }
                    }
                }
            }
        }else if (judgeMoveNode(node, move.targetRow, move.targetColumn)&&valid(move.targetRow, move.targetColumn)) {
//            System.out.println("plain and jump movement");
            return true;
        }

        return false;
    }

    public void undoMove(Move move){
        // get target node
        Node node = getNonCapturedNodeAtLocation(move.targetRow, move.targetColumn);
        if (node==null) return;
        node.setRow(move.sourceRow);
        node.setColumn(move.sourceColumn);

//        this.nodes.add(node);

        if (move.captureNode!=null){
//            System.out.println(move.captureNode.toString());
            // has already captured
            // Node node1 = getNonCapturedNodeAtLocation(move.captureNode.getRow(),move.captureNode.getColumn());
//            node1.isCaptured(false);
            move.captureNode.isCaptured(false);
            this.capturedNodes.remove(move.captureNode);
            move.captureNode = null;
//            this.nodes.add(move.captureNode);
        }

        if(node.getColor() == Node.COLOR_BLACK){
            this.gameState = ChessGame.GAME_STATE_BLACK;
        }else if (node.getColor() == Node.COLOR_WHITE){
            this.gameState = ChessGame.GAME_STATE_WHITE;
        } else if (this.gameState == ChessGame.GAME_STATE_END_BLACK_WON){  // WARNING maybe bugs here
            this.gameState = ChessGame.GAME_STATE_WHITE;
        } else {
            this.gameState = ChessGame.GAME_STATE_BLACK;
        }
    }

    public boolean moveNode(Move move) {
        Node node = getNonCapturedNodeAtLocation(move.sourceRow, move.sourceColumn);
        if (node==null) {
            System.out.println("node=null");
            return false;
        }
        // check if the move is capturing an opponent piece
        int opponentColor = (node.getColor() == Node.COLOR_BLACK ? Node.COLOR_WHITE : Node.COLOR_BLACK);
        HashMap<Node, HashSet<Node>> tmp = judgeCaptureNode(opponentColor);
        if (judgeFirstMove(tmp)) {
            if (((Math.abs(move.targetColumn - move.sourceColumn) == 0 && Math.abs(move.targetRow - move.sourceRow) == 2)
                    || (Math.abs(move.targetColumn - move.sourceColumn) == 2 && Math.abs(move.targetRow - move.sourceRow) == 0)
                    || (Math.abs(move.targetColumn - move.sourceColumn) == 2 && Math.abs(move.targetRow - move.sourceRow) == 2))
                    && getNonCapturedNodeAtLocation(move.targetRow, move.targetColumn) == null
                    && valid(move.targetRow,move.targetColumn)) {

                // check whether it can capture opponent automatically
                int opponentRow = (move.sourceRow + move.targetRow) / 2;
                int opponentColumn = (move.sourceColumn + move.targetColumn) / 2;

                for (Node node1 : nodes) {
                    if (!node1.isCaptured()
                            && node1.getRow() == opponentRow
                            && node1.getColumn() == opponentColumn
                            && node1.getColor() == opponentColor) {
                        node1.isCaptured(true);
                        node.setRow(move.targetRow);
                        node.setColumn(move.targetColumn);
                        move.captureNode = node1;
                        this.capturedNodes.add(node1);
//                        this.nodes.remove(node1);
                        return true;
//                        System.out.println("Row = " + node.getRow() + " Column = " + node.getColumn());
                    }
                }
            }
        } else if (judgeMoveNode(node, move.targetRow, move.targetColumn)&&valid(move.targetRow, move.targetColumn)) {
//            System.out.println("plain and jump movement");
            node.setRow(move.targetRow);
            node.setColumn(move.targetColumn);
            return true;
//                this.changeGameState();
        }
        // TODO check whether it can kill enemy continually
        return false;
    }
//    public void judgeWin(int row, int column) {
//        if (row == Node.ROW_1 && (column == Node.COLUMN_D || column == Node.COLUMN_E)&&this.gameState==Node.COLOR_BLACK){
//            this.gameState=ChessGame.GAME_STATE_END_BLACK_WON;
//        }
//        if (row == Node.ROW_14 && (column == Node.COLUMN_D || column == Node.COLUMN_E&&this.gameState==Node.COLOR_WHITE)) {
//            this.gameState=ChessGame.GAME_STATE_END_WHITE_WON;
//        }
//        int Color = this.getGameState();
//        int numberBlack = 0;
//        int numberWhite = 0;
//        for (Node node : nodes){
//            if (!node.isCaptured()&&node.getColor()==ChessGame.GAME_STATE_BLACK) numberBlack++;
//            if (numberBlack==6) this.gameState=ChessGame.GAME_STATE_END_WHITE_WON;
//            if (!node.isCaptured()&&node.getColor()==ChessGame.GAME_STATE_WHITE) numberWhite++;
//            if (numberWhite==6) this.gameState=ChessGame.GAME_STATE_END_BLACK_WON;
//        }
//    }

    public boolean judgeEnd(int row, int column) {
        if (row == Node.ROW_1
                && (column == Node.COLUMN_D || column == Node.COLUMN_E)
                && this.gameState==ChessGame.GAME_STATE_BLACK)
            return true;
        if (row == Node.ROW_14
                && (column == Node.COLUMN_D || column == Node.COLUMN_E)
                && this.gameState==ChessGame.GAME_STATE_WHITE) {
            return true;
        }
        int Color = this.getGameState();
        for (Node node : nodes){
            if (!node.isCaptured()&&node.getColor()!=Color) return false;
        }
        return true;
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

    public Node getNonCapturedNodeAtLocation(int row, int column) {
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
    protected boolean judgeMoveNode(Node node, int targetRow, int targetColumn) {
        for (Node node_tmp : nodes) {
            if (node_tmp.getRow() == targetRow
                    && node_tmp.getColumn() == targetColumn
                    && !node_tmp.isCaptured()) {
//                System.out.println(false);
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

//    public MoveValidator getMoveValidator(){
//        return this.moveValidator;
//    }

    @Override
    public void run() {
        this.startGame();
    }
}
