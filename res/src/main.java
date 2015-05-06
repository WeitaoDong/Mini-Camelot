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

        CamelotGui chessGui = new CamelotGui(chessGame);

        new Thread(chessGame).start();
    }
}
