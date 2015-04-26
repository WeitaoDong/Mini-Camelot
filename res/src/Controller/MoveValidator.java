//package Controller;
//
//import Model.Node;
//
///**
// * Created by weitao on 4/23/15.
// */
//public class MoveValidator {
//    private ChessGame chessGame;
//    private Node sourceNode;
//    private Node targetNode;
//    private boolean debug;
//
//    public MoveValidator(ChessGame chessGame) { this.chessGame = chessGame;}
//
//    public boolean isMoveValid(Move move, boolean debug) {
//        this.debug = debug;
//        int sourceRow = move.sourceRow;
//        int sourceColumn = move.sourceColumn;
//        int targetRow = move.targetRow;
//        int targetColumn = move.targetColumn;
//
//        sourceNode = this.chessGame.getNonCapturedNodeAtLocation(sourceRow, sourceColumn);
//        targetNode = this.chessGame.getNonCapturedNodeAtLocation(targetRow, targetColumn);
//
//        // source piece does not exist
//        if( sourceNode == null ){
//            log("source piece does not exist");
//            return false;
//        }
//
//        if (sourceNode.getColor()==Node.COLOR_WHITE
//                && this.chessGame.getGameState()==ChessGame.GAME_STATE_WHITE) {
//
//        } else {
//            log("it's not your turn: "
//                    +"NodeColor= "+sourceNode.getColor()
//                    +"gameState= "+this.chessGame.getGameState());
//            return false;
//        }
//
//        if (!chessGame.valid(targetRow,targetColumn)){
//            log("target row or colum out of scope");
//            return false;
//        }
//
//        if (!chessGame.moveNode(move))
//            return false;
//        else {}
//        return true;
//    }
//    private void log(String message) {
//        if(debug) System.out.println(message);
//
//    }
//}
