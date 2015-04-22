package Controller;

import Model.Node;

import java.util.HashSet;

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
//        System.out.println(Node.COLOR_WHITE+" ");
        for (int i=0; i<2; i++)
            createAndAddPiece(Node.COLOR_WHITE, Node.ROW_6, Node.COLUMN_D+i);
        for (int i=0; i<4; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_10, Node.COLUMN_C+i);
        for (int i=0; i<2; i++)
            createAndAddPiece(Node.COLOR_BLACK, Node.ROW_9, Node.COLUMN_D+i);

    }

    private void createAndAddPiece(int color, int row, int column) {
        Node node = new Node(color, row, column);
        this.nodes.add(node);
        System.out.println("* "+nodes.contains(node)+" *");
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
                throw new IllegalStateException("unknown game state: " + this.gameState);
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
        if (judgeCaptureNode(node, opponentColor)) {
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
    private boolean judgeCaptureNode(Node node, int opponentColor) {
        for (Node node_tmp:nodes){
            if (Math.abs(node_tmp.getRow() - node.getRow())==1
                    &&Math.abs(node_tmp.getColumn() - node.getColumn())==1
                    &&node_tmp.getColor()== opponentColor){
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

    private boolean judgeOwnNode(int row, int column) {
        for (Node node : nodes){
            if (node.getRow()==row
                    &&node.getColumn()==column
                    &&!node.isCaptured()){
                return true;
            }
        }
        return false;
    }

    // Plain move & cantering move
    private boolean judegeMoveNode(Node node, int targetRow, int targetColumn) {
        for (Node node_tmp : nodes) {
            if (node_tmp.getRow() == targetRow
                    && node_tmp.getColumn() == targetColumn
                    && !node_tmp.isCaptured()) {
                return false;
            }
        }
        if (Math.abs(node.getRow() - targetRow)<=2 && Math.abs(node.getColumn() - targetColumn)<=2) {
            if (Math.abs(node.getRow() - targetRow) == 2 && node.getColumn() == targetColumn) {
                return node.getRow() - targetRow >0?judgeOwnNode(node.getRow()-1,node.getColumn()):judgeOwnNode(node.getRow()+1,node.getColumn());
            } else if (node.getRow() == targetRow && Math.abs(node.getColumn() - targetColumn) == 2) {
                return node.getColumn()- targetColumn >0?judgeOwnNode(node.getRow(),node.getColumn()-1):judgeOwnNode(node.getRow(),node.getColumn()+1);
            } else if (Math.abs(node.getRow() - targetRow) == 2 && Math.abs(node.getColumn() - targetColumn) == 2) {
                if (node.getRow() - targetRow == 2 && node.getColumn() - targetColumn == 2) {
                    return judgeOwnNode(node.getRow()-1,node.getColumn()-1);
                } else if (node.getRow() - targetRow == -2 && node.getColumn() - targetColumn == 2) {
                    return judgeOwnNode(node.getRow()+1,node.getColumn()-1);
                } else if (node.getRow() - targetRow == 2 && node.getColumn() - targetColumn == -2) {
                    return judgeOwnNode(node.getRow()-1,node.getColumn()+1);
                } else { return judgeOwnNode(node.getRow()+1,node.getColumn()+1);}
            } else if (Math.abs(node.getRow() - targetRow) == 1 && Math.abs(node.getColumn() - targetColumn) == 1) {
                return true;
            } else return true;
        }
        return false;
    }
}
