package Controller;

import Model.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by weitao on 4/27/15.
 */

public class SimpleAiPlayerHandler implements IPlayerHandler {

    private ChessGame chessGame;
//    private MoveValidator validator;

    /**
     * number of moves to look into the future
     */
    public int maxDepth = 2;
    Move bestMove = new Move(0,0,0,0);


    public SimpleAiPlayerHandler(ChessGame chessGame) {
        this.chessGame = chessGame;
//        this.validator = this.chessGame.getMoveValidator();
    }

    @Override
    public Move getMove() {
        return alpha_beta_search();
//        System.out.println("ttt= "+ttt);
//        return ttt;
    }

    /**
     * get best move for current game situation
     * @return a valid Move instance
     */

    @Override
    public void moveSuccessfullyExecuted(Move move) {
        // we are using the same chessGame instance, so no need to do anything here.
        System.out.println("executed: "+move);
    }

    /**
     * undo specified move
     */
    private void undoMove(Move move) {
        //System.out.println("undoing move");
        this.chessGame.undoMove(move);
        //state.changeGameState();
    }

    /**
     * Execute specified move. This will also change the game state after the
     * move has been executed.
     */
    private void executeMove(Move move) {
        //System.out.println("executing move");
        this.chessGame.moveNode(move);
        this.chessGame.changeGameState(move);
    }

    /*ALPHA-BETA-SEARCH*/
    private Move alpha_beta_search(){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int v = MAX_VALUE(maxDepth, alpha, beta);
        System.out.println(v);
        return bestMove;
    }

    private int MAX_VALUE(int Depth, int alpha, int beta) {
        // Cutoff-test
        // if ()
        int v = Integer.MIN_VALUE;
        if (Depth<=0){
            return Integer.MAX_VALUE;
        }
//        chessGame.availableNodes
        List<Move> validMoves = generateMoves(false);
        for (Move move: validMoves){
            executeMove(move);
            v = evaluateState(move);
            v = Math.max(v, MIN_VALUE(Depth-1, alpha, beta));
            undoMove(move);
            if (v>=beta) {
                bestMove = move;
                return v;
            }
            if (v>=alpha){
                bestMove = move;
                alpha = v;
            }
        }
        return v;
    }
    private int MIN_VALUE(int Depth, int alpha, int beta){
        // Cutoff-test
        // if ()
        int v = Integer.MAX_VALUE;
        if (Depth<=0){
            return Integer.MIN_VALUE;
        }
        List<Move> validMoves = generateMoves(false);
        for (Move move : validMoves){
            executeMove(move);
            v = evaluateState(move);
            v = Math.min(v, MAX_VALUE(Depth-1,alpha,beta));
            undoMove(move);
            if (v<=alpha) return v;
            beta = Math.min(beta,v);
        }
        return v;
    }
    /**
     * generate all possible/valid moves for the specified game
     * @param debug - game state for which the moves should be generated
     * @return list of all possible/valid moves
     */
    private List<Move> generateMoves(boolean debug) {

//        HashSet<Node> nodes = this.chessGame.getNodes();
        List<Move> validMoves = new ArrayList<Move>();
        Move testMove = new Move(0,0,0,0);

        int nodeColor = this.chessGame.getGameState()==ChessGame.GAME_STATE_WHITE?Node.COLOR_WHITE:Node.COLOR_BLACK;

        // iterate over all non-captured pieces
        for (Node node : chessGame.nodes) {

            // only look at pieces of current players color
            if (nodeColor == node.getColor()
                    && !node.isCaptured()) {

                // start generating move
                testMove.sourceRow = node.getRow();
                testMove.sourceColumn = node.getColumn();

                // find the valid path
                for (int targetRow = node.getRow()-2;targetRow<node.getRow()+3;targetRow++){
                    for (int targetColumn = node.getColumn()-2;targetColumn<node.getColumn()+3;targetColumn++) {

                        // finish generating move
                        testMove.targetRow = targetRow;
                        testMove.targetColumn = targetColumn;

                        if(debug) System.out.println("testing move: "+testMove);

                        // check if generated move is valid
                        if (this.chessGame.valid(testMove.targetRow,testMove.targetColumn)
                                && this.chessGame.judgeMove(testMove)
                                && this.chessGame.judgeMoveNode(node, testMove.targetRow,testMove.targetColumn)) {
                            // valid move
                            validMoves.add(testMove.clone());
                        } else {
                            // generated move is invalid, so we skip it
                        }
                    }
                }
            }
        }
        return validMoves;
    }

    /**
     * evaluate the current game state from the view of the
     * current player. High numbers indicate a better situation for
     * the current player.
     *
     * @return integer score of current game state
     */
    private int evaluateState(Move move) {

        // add up score
        //
        int scoreWhite = 0;
        int scoreBlack = 0;
        Node temp = chessGame.getNonCapturedNodeAtLocation(move.targetRow, move.targetColumn);

//        if (temp==null)
//            System.out.println(move+" nodes= "+chessGame.nodes);
        for (Node piece : this.chessGame.getNodes()) {
            if (temp.getColor()==ChessGame.GAME_STATE_WHITE) {
                scoreWhite += temp.getScore()*getScoreForNodePosition(move,temp.getColor());
            } else if (temp.getColor()==ChessGame.GAME_STATE_BLACK){
                scoreBlack += temp.getScore()*getScoreForNodePosition(move,temp.getColor());
            }else{
                throw new IllegalStateException(
                        "unknown piece color found: "+piece.getColor());
            }
        }


//        for (Node node : this.chessGame.getNodes()) {
//            if(node.getColor() == Node.COLOR_BLACK){
//
//                scoreBlack +=
//                        node.getScore()*getScoreForNodePosition(move);
//            }else if( node.getColor() == Node.COLOR_WHITE){
//
//                scoreWhite +=
//                        node.getScore()*getScoreForNodePosition(move);
//            }else{
//                throw new IllegalStateException(
//                        "unknown piece color found: "+node.getColor());
//            }
//        }

        // return evaluation result depending on who's turn it is
        int gameState = this.chessGame.getGameState();

        if (gameState == ChessGame.GAME_STATE_BLACK){
            return scoreWhite - scoreBlack;

        }else if (gameState == ChessGame.GAME_STATE_WHITE){
            return scoreBlack - scoreWhite;

        }else if (gameState == ChessGame.GAME_STATE_END_WHITE_WON){
            return Integer.MIN_VALUE + 1;

        }else if (gameState == ChessGame.GAME_STATE_END_BLACK_WON) {
            return Integer.MAX_VALUE - 1;
        }else {
            throw new IllegalStateException("unknown game state: "+gameState);
        }
    }

    /**
     * get the evaluation bonus for the specified position
     * @param move - one of Node.positon_..
     * @param color - one of Node.Color_..
     * @return integer score
     */
    private int getScoreForNodePosition(Move move, int color) {
//        Node node = chessGame.getNonCapturedNodeAtLocation(move.targetRow,move.targetColumn);
        if (color==chessGame.GAME_STATE_WHITE) {
            byte[][] positionWeight =
                    {         {0, 0, 0, 1, 1, 0, 0, 0}
                            , {0, 0, 2, 2, 2, 2, 0, 0}
                            , {0, 2, 3, 3, 3, 3, 2, 0}
                            , {2, 2, 3, 4, 4, 3, 2, 2}
                            , {2, 3, 4, 4, 4, 4, 3, 2}
                            , {3, 4, 5, 6, 6, 5, 4, 3}
                            , {4, 5, 6, 7, 7, 6, 5, 4}
                            , {5, 6, 7, 8, 8, 7, 6, 5}
                            , {6, 7, 8, 9, 9, 8, 7, 6}
                            , {7, 8, 9, 10, 10, 9, 8, 7}
                            , {8, 9, 10, 11, 11, 10, 9, 8}
                            , {0, 10, 11, 12, 12, 11, 10, 0}
                            , {0, 0, 13, 13, 13, 13, 0, 0}
                            , {0, 0, 0, 100, 100, 0, 0, 0}
                    };
            return positionWeight[move.targetRow][move.targetColumn];
        }
        else {
            byte[][] positionWeight =
                    {         {0, 0, 0, 100, 100, 0, 0, 0}
                            , {0, 0, 13, 13, 13, 13, 0, 0}
                            , {0, 10, 11, 12, 12, 11, 10, 0}
                            , {8, 9, 10, 11, 11, 10, 9, 8}
                            , {7, 8, 9, 10, 10, 9, 8, 7}
                            , {6, 7, 8, 9, 9, 8, 7, 6}
                            , {5, 6, 7, 8, 8, 7, 6, 5}
                            , {4, 5, 6, 7, 7, 6, 5, 4}
                            , {3, 4, 5, 6, 6, 5, 4, 3}
                            , {2, 3, 4, 4, 4, 4, 3, 2}
                            , {2, 2, 3, 4, 4, 3, 2, 2}
                            , {0, 2, 3, 3, 3, 3, 2, 0}
                            , {0, 0, 2, 2, 2, 2, 0, 0}
                            , {0, 0, 0, 1, 1, 0, 0, 0}
                    };
            return positionWeight[move.targetRow][move.targetColumn];
        }

    }


    public static void main(String[] args) {
        ChessGame ch = new ChessGame();
        SimpleAiPlayerHandler ai = new SimpleAiPlayerHandler(ch);
    }
}
