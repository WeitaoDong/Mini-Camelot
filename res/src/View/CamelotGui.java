package View;

import Controller.ChangeGameStateButtonActionListener;
import Controller.ChessGame;
import Controller.nodeListenerForMouse;
import Model.GuiNode;
import Model.Node;
import sun.dc.pr.PRError;

import javax.print.DocFlavor;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashSet;

/**
 * Created by weitao on 4/20/15.
 */
public class CamelotGui extends JPanel {
    private int gameState = GAME_STATE_WHITE;
    static final int GAME_STATE_WHITE = 0;
    static final int GAME_STATE_BLACK = 1;
    private static final int BOARD_START_X = 301;
    private static final int BOARD_START_Y = 51;

    private static final int SQUARE_WIDTH = 50;
    private static final int SQUARE_HEIGHT = 50;

    private static final int NODE_WIDTH = 48;
    private static final int NODE_HEIGHT = 48;

    private static final int NODES_START_X = BOARD_START_X + (int)(SQUARE_WIDTH/2.0 - NODE_WIDTH/2.0);
    private static final int NODES_START_Y = BOARD_START_Y + (int)(SQUARE_HEIGHT/2.0 - NODE_HEIGHT/2.0);

    private static final int DRAG_TARGET_SQUARE_START_X = BOARD_START_X - (int)(NODE_WIDTH/2.0);
    private static final int DRAG_TARGET_SQUARE_START_Y = BOARD_START_Y - (int)(NODE_HEIGHT/2.0);

    private JLabel lblGameState;
    private ChessGame chessGame;
    private Image imgBackground;
    private HashSet<GuiNode> guiNodes = new HashSet<GuiNode>();

    public CamelotGui(){
        this.setLayout(null);

        // Load the background
        URL urlBackgroundImg = getClass().getResource("blackground.png");
        this.imgBackground = new ImageIcon(urlBackgroundImg).getImage();

        this.chessGame = new ChessGame();
        for (Node node:chessGame.getNodes()){
            createAndAddGuiNode(node);
        }

        // Add listener
        nodeListenerForMouse listener = new nodeListenerForMouse(this.guiNodes,this);
        this.addMouseListener(listener);
        this.addMouseMotionListener(listener);

        // button to change game state
        JButton btnChangeGameState = new JButton("change");
        btnChangeGameState.addActionListener(new ChangeGameStateButtonActionListener(this));
        btnChangeGameState.setBounds(0, 0, 80, 30);
        this.add(btnChangeGameState);

        // lable to display game state
        String labelText = this.getGameStateAsText();
        this.lblGameState = new JLabel(labelText);
        lblGameState.setBounds(0, 30, 80, 30);
        this.add(lblGameState);

        JFrame f = new JFrame();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.setSize(this.imgBackground.getWidth(null), this.imgBackground.getHeight(null));
    }

    private String getGameStateAsText() {
        return (this.chessGame.getGameState() == ChessGame.GAME_STATE_WHITE ? "white" : "black");
    }


    /**
     * switches between the different game states
     */
    public void changeGameState() {
        this.chessGame.changeGameState();
        this.lblGameState.setText(this.getGameStateAsText());
    }

    public int getGameState(){
        return this.chessGame.getGameState();
    }

    private Image getImageForNode(int color) {
        String filename = "";
        filename += (color == Node.COLOR_WHITE ? "w" : "b");
        filename += ".png";

        URL urlNodeImg = getClass().getResource(filename);
        return new ImageIcon(urlNodeImg).getImage();
    }

    private void createAndAddGuiNode(Node node) {
        Image img = this.getImageForNode(node.getColor());
        GuiNode guiNode = new GuiNode(img, node);
        this.guiNodes.add(guiNode);
    }


    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(this.imgBackground, 0, 0, null);
        for (GuiNode guiNode : this.guiNodes) {
            if (!guiNode.isCaptured())
                g.drawImage(guiNode.getImage(), guiNode.getX(), guiNode.getY(), null);
        }
    }

    public static int convertColumnToX(int column){
        return NODES_START_X+SQUARE_WIDTH*column;
    }
    public static int convertRowToY(int row){
        return NODES_START_Y + SQUARE_HEIGHT * (Node.ROW_8 - row);
    }

    public static int convertXToColumn(int x){
        return (x - DRAG_TARGET_SQUARE_START_X)/SQUARE_WIDTH;
    }

    public static int convertYToRow(int y){
        return Node.ROW_8 - (y - DRAG_TARGET_SQUARE_START_Y)/SQUARE_HEIGHT;
    }

    /**
     * change location of given piece, if the location is valid.
     * If the location is not valid, move the piece back to its original
     * position.
     * @param dragNode
     * @param x
     * @param y
     */
    public void setNewNodeLocation(GuiNode dragNode, int x, int y) {
        int targetRow = CamelotGui.convertYToRow(y);
        int targetColumn = CamelotGui.convertXToColumn(x);

        if( targetRow < Node.ROW_1 || targetRow > Node.ROW_8 || targetColumn < Node.COLUMN_A || targetColumn > Node.COLUMN_H){
            // reset piece position if move is not valid
            dragNode.resetToUnderlyingNodePosition();

        }else{
            //change model and update gui piece afterwards
            System.out.println("moving piece to "+targetRow+"/"+targetColumn);
            this.chessGame.moveNode(dragNode.getnode().getRow(), dragNode.getnode().getColumn(), targetRow, targetColumn);
            dragNode.resetToUnderlyingNodePosition();
        }
    }

    public static void main(String[] args) {
        new CamelotGui();
    }

}