import Controller.ChessGame;
import Controller.SimpleAiPlayerHandler;
import Model.Node;
import View.CamelotGui;

/**
 * Created by weitao on 4/29/15.
 */
public class main {
    public static void main(String[] args) {

        // first we create the game
        ChessGame chessGame = new ChessGame();

        // then we create the clients/players
        CamelotGui chessGui = new CamelotGui(chessGame);
        //ChessConsole chessConsole = new ChessConsole(chessGame);
//        SimpleAiPlayerHandler ai1 = new SimpleAiPlayerHandler(chessGame);
//        SimpleAiPlayerHandler ai2 = new SimpleAiPlayerHandler(chessGame);

        // set strength of AI
//        ai1.maxDepth = 10;
//        int depth = ai1.getRecordDepth();
//        int choose  = chessGui.getGameState();
//        ai2.maxDepth = 2;


//        ai1.maxDepth=chessGui.getDepth();
//        if (chessGui.getSetGameState()==chessGame.GAME_STATE_WHITE){
//            chessGame.setPlayer(Node.COLOR_WHITE,ai1);
//            chessGame.setPlayer(Node.COLOR_BLACK,chessGui);
//
//        } else {
//            chessGame.setPlayer(Node.COLOR_WHITE, chessGui);
//            chessGame.setPlayer(Node.COLOR_BLACK, ai1);
//        }

//
//        chessGame.setPlayer(Node.COLOR_WHITE, chessGui);
//        chessGame.setPlayer(Node.COLOR_BLACK, ai1);
        // in the end we start the game
        new Thread(chessGame).start();
    }
}
