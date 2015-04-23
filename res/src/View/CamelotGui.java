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
    private static final int BOARD_START_X = 5;
    private static final int BOARD_START_Y = 5;

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
    private GuiNode guiNode;

    public CamelotGui(){
        this.setLayout(null);

        // Load the background
        URL urlBackgroundImg = getClass().getResource("img/background.png");
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
        this.lblGameState = new JLabel();
        lblGameState.setText(this.getGameStateAsText());
        lblGameState.setBounds(0, 30, 80, 30);
        this.add(lblGameState);

        JFrame f = new JFrame();
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(this);
        f.setSize(this.imgBackground.getWidth(null), this.imgBackground.getHeight(null));
    }

    private String getGameStateAsText() {
        String state = "unknown";
        switch (this.chessGame.getGameState()) {
            case ChessGame.GAME_STATE_BLACK: state = "black";break;
            case ChessGame.GAME_STATE_END: state = "end";break;
            case ChessGame.GAME_STATE_WHITE: state = "white";break;
        }

        return state;
//        return (this.chessGame.getGameState() == ChessGame.GAME_STATE_WHITE ? "white" : "black");
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
        filename += (color == Node.COLOR_WHITE ? "img/w" : "img/b");
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
        this.lblGameState.setText(this.getGameStateAsText());

    }

    public static int convertColumnToX(int column){
        return NODES_START_X+SQUARE_WIDTH*column;
    }
    public static int convertRowToY(int row){
        return NODES_START_Y + SQUARE_HEIGHT * row;
    }

    public static int convertXToColumn(int x){
        return (x - DRAG_TARGET_SQUARE_START_X)/SQUARE_WIDTH;
    }

    public static int convertYToRow(int y){
        return (y - DRAG_TARGET_SQUARE_START_Y)/SQUARE_HEIGHT;
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
        boolean success = this.chessGame.moveNode(dragNode.getnode().getRow(), dragNode.getnode().getColumn(), targetRow, targetColumn);
        if ( !valid(targetRow, targetColumn) && !success){
            // reset piece position if move is not valid
            dragNode.resetToUnderlyingNodePosition();
        }else{
            //change model and update gui piece afterwards
            System.out.println("moving " + dragNode.getColor() + " node to " + targetRow + "/" + targetColumn);
            int lastColor = dragNode.getColor();
            this.chessGame.moveNode(dragNode.getnode().getRow(), dragNode.getnode().getColumn(), targetRow, targetColumn);
            dragNode.resetToUnderlyingNodePosition();

            if (this.getGameState()==ChessGame.GAME_STATE_END) {
                if (lastColor == ChessGame.GAME_STATE_WHITE) {
                    JOptionPane.showMessageDialog(CamelotGui.this, "Over, white is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(CamelotGui.this, "Over, black is the winner!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }
    }


    public boolean valid(int targetRow, int targetColumn){
        if (targetRow < Node.ROW_1
                || targetRow > Node.ROW_14
                || targetColumn < Node.COLUMN_A
                || targetColumn > Node.COLUMN_H
                || (targetRow>Node.ROW_11&&targetColumn<Node.COLUMN_B)
                || (targetRow<Node.ROW_4&&targetColumn<Node.COLUMN_B)
                || (targetRow>Node.ROW_11&&targetColumn>Node.COLUMN_G)
                || (targetRow<Node.ROW_4&&targetColumn>Node.COLUMN_G)
                || (targetRow>Node.ROW_12&&targetColumn<Node.COLUMN_C)
                || (targetRow<Node.ROW_3&&targetColumn<Node.COLUMN_C)
                || (targetRow>Node.ROW_12&&targetColumn>Node.COLUMN_F)
                || (targetRow<Node.ROW_3&&targetColumn>Node.COLUMN_F)
                || (targetRow>Node.ROW_13&&targetColumn<Node.COLUMN_D)
                || (targetRow<Node.ROW_2&&targetColumn<Node.COLUMN_D)
                || (targetRow>Node.ROW_13&&targetColumn>Node.COLUMN_E)
                || (targetRow<Node.ROW_2&&targetColumn>Node.COLUMN_E)){
            return false;
        }
        return true;
    }

    public void setGuiNode(GuiNode guiNode) { this.guiNode = guiNode;}
    public GuiNode getGuiNode() { return this.guiNode;}
    public static void main(String[] args) {
        new CamelotGui();
    }

}
