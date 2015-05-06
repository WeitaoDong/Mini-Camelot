package Controller;

import Model.Node;
import sun.rmi.runtime.Log;

import java.util.*;

/**
 * Created by weitao on 4/27/15.
 */

public class SimpleAiPlayerHandler implements IPlayerHandler {

    private ChessGame chessGame;

    /**
     * number of moves to look into the future
     */
    public int maxDepth = 7;
    private int recordDepth;
    private long totalNumber = 1;
    Date sysdate = new Date();

    public int getRecordDepth(){
        return this.recordDepth;
    }
    public long getTotalNumber(){
        return this.totalNumber;
    }

    public int log_max_pruning = 0;
    public int log_min_pruning = 0;





    public SimpleAiPlayerHandler(ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    @Override
    public Move getMove() {
        Move ttt= alpha_beta_search();
        System.out.println("ttt= "+ttt);
        return ttt;
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
        this.chessGame.undoMove(move);
    }

    /**
     * Execute specified move. This will also change the game state after the
     * move has been executed.
     */
    private void executeMove(Move move) {
        this.chessGame.moveNode(move);
        this.chessGame.changeGameState(move);
    }

    /*ALPHA-BETA-SEARCH*/
    private Move alpha_beta_search(){
        log_min_pruning = 0;
        log_max_pruning = 0;
        recordDepth = 0;
        totalNumber = 1;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        sysdate = new Date();
        List<Integer> bestMove = MAX_VALUE(maxDepth, recordDepth, sysdate, alpha, beta);
        System.out.println("log_max_pruning= " + log_max_pruning);
        System.out.println("log_min_pruning= " + log_min_pruning);
        System.out.println("bestMove= " + bestMove);

        Move best_ = new Move(bestMove.get(1),bestMove.get(2),bestMove.get(3),bestMove.get(4));
        return best_;
    }

    private boolean terminalTest(int depth, Date sysdate){
        if (this.chessGame.getGameState()==ChessGame.GAME_STATE_END_BLACK_WON)
            return true;
        Date date = new Date();
        long current = date.getTime();
        long sysTime = sysdate.getTime();
        if (depth<=0||current-sysTime>10*1000) {
            return true;
        } else return false;

    }

    private List<Integer> MAX_VALUE(int Depth, int recordDepth, Date sysdate, int alpha, int beta) {
        recordDepth++;
        if (terminalTest(Depth,sysdate))
            return evaluateState();
        int v = Integer.MIN_VALUE;
        List<Move> validMoves = generateMoves();
        List<Integer> bestMove = new ArrayList<Integer>();
        Move best;
        for (Move move: validMoves){
            executeMove(move);
            int temp = MIN_VALUE(Depth-1, recordDepth, sysdate, alpha, beta);
            undoMove(move);
            if (temp>v) {
                best = move;
                bestMove.add(0,temp);
                bestMove.add(1,best.sourceRow);
                bestMove.add(2,best.sourceColumn);
                bestMove.add(3,best.targetRow);
                bestMove.add(4,best.targetColumn);
                v = temp;
            }
            if (v>=beta) {
                log_max_pruning++;
                return bestMove;
            }
            alpha = Math.max(v, alpha);
        }
        return bestMove;
    }
    private int MIN_VALUE(int Depth, int recordDepth, Date sysdate, int alpha, int beta){
        recordDepth++;
        if (terminalTest(Depth,sysdate))
            return evaluateState().get(0);
        int v = Integer.MAX_VALUE;
        List<Move> validMoves = generateMoves();
        for (Move move : validMoves){
            executeMove(move);
            int temp = MAX_VALUE(Depth - 1, recordDepth, sysdate, alpha, beta).get(0);
            v = Math.min(v,temp);
            undoMove(move);
            if (v<=alpha) {
                log_min_pruning++;
                return v;
            }
            beta = Math.min(beta, v);
        }
        return v;
    }
    /**
     * generate all possible/valid moves for the specified game
     * @param  - game state for which the moves should be generated
     * @return list of all possible/valid moves
     */
    private List<Move> generateMoves() {

        List<Move> validMoves = new ArrayList<Move>();
        Move testMove = new Move(0,0,0,0);

        int nodeColor = this.chessGame.getGameState()==ChessGame.GAME_STATE_WHITE?Node.COLOR_WHITE:Node.COLOR_BLACK;

        // iterate over all non-captured pieces
        for (Node node : this.chessGame.nodes) {

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

//                        if(debug) System.out.println("testing move: "+testMove);

                        // check if generated move is valid
                        if (chessGame.valid(testMove.targetRow,testMove.targetColumn)
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
    private List<Integer> evaluateState() {
        int myScore = 0;
        int OpScore = 0;
        for (Node node : chessGame.nodes) {
            if (!node.isCaptured() && node.getColor() == chessGame.getGameState()) {
                if (node.getColor() == chessGame.GAME_STATE_WHITE) {
                    myScore += node.getScore() * node.getRow();
                } else if (node.getColor() == chessGame.GAME_STATE_BLACK) {
                    myScore += node.getScore() * Math.abs(node.getRow() - 13);
                }
            } else if (!node.isCaptured() && node.getColor() != chessGame.getGameState()) {
                if (node.getColor() == chessGame.GAME_STATE_WHITE) {
                    OpScore += node.getScore() * node.getRow();
                } else if (node.getColor() == chessGame.GAME_STATE_BLACK)
                    OpScore += node.getScore() * Math.abs(node.getRow() - 13);
            }
        }
        int node_diff = myScore - OpScore;
        int node_sum = myScore + OpScore;

        List<Integer> evaluate = new ArrayList<Integer>();
        evaluate.add(0, node_diff >= 0 ? node_diff * 1 - node_sum : node_diff * 1 + node_sum);
        int i = 4;
        while (i > 0) {
            evaluate.add(0);
            i--;
        }
        return evaluate;
    }
    private int evaluateState(Move move) {
        // TODO
        // add up score
        //
        int scoreWhite = 0;
        int scoreBlack = 0;
        Node temp = chessGame.getNonCapturedNodeAtLocation(move.targetRow, move.targetColumn);

//        if (temp==null)
//            System.out.println(move+" nodes= "+chessGame.nodes);
        for (Node node : this.chessGame.getNodes()) {
            if (!temp.isCaptured()&&temp.getColor()==ChessGame.GAME_STATE_WHITE) {
                scoreWhite += temp.getScore()*getScoreForNodePosition(move,temp.getColor());
            } else if (!temp.isCaptured()&&temp.getColor()==ChessGame.GAME_STATE_BLACK){
                scoreBlack += temp.getScore()*getScoreForNodePosition(move,temp.getColor());
            }else{
                throw new IllegalStateException(
                        "unknown piece color found: "+node.getColor());
            }
        }

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
