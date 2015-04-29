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
        SimpleAiPlayerHandler ai1 = new SimpleAiPlayerHandler(chessGame);
//        SimpleAiPlayerHandler ai2 = new SimpleAiPlayerHandler(chessGame);

        // set strength of AI
//        ai1.maxDepth = 1;
//        ai2.maxDepth = 2;

        // then we attach the clients/players to the game
        //chessGame.setPlayer(Piece.COLOR_WHITE, chessGui);
        //chessGame.setPlayer(Piece.COLOR_WHITE, chessConsole);
        chessGame.setPlayer(Node.COLOR_WHITE, ai1);
        //chessGame.setPlayer(Piece.COLOR_BLACK, ai1);
        chessGame.setPlayer(Node.COLOR_BLACK, chessGui);

        // in the end we start the game
        new Thread(chessGame).start();
    }
}
