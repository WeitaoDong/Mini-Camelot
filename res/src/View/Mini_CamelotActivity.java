//package View;
//
//import Model.Chess;
//import Model.Node;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import javax.swing.JFrame;
//import java.util.HashSet;
//import java.util.Iterator;
//
//import javax.imageio.ImageIO;
//import java.awt.event.MouseEvent;
//import java.io.IOException;
//
//import java.awt.Color;
//import java.awt.Graphics;
//import javax.swing.JPanel;
//
///**
// * Created by weitao on 4/16/15.
// */
//public class Mini_CamelotActivity extends JFrame implements ActionListener {
//
//    int step;
//    JButton butnStart = new JButton("restart");
//    JLayeredPane panelPaint=new JLayeredPane();
//    JPanel panelEast=new JPanel();
//
//    private Chess whiteChess = new Chess();
//    HashSet<Node> white_chess = whiteChess.Chess(1);
//    JLabel white_horse, white_horse1, white_horse2, white_horse3, white_horse4, white_horse5, white_horse6, white_horse7, white_horse8;
//    JLabel black_horse, black_horse1, black_horse2, black_horse3, black_horse4, black_horse5, black_horse6, black_horse7, black_horse8;
//
//    protected Chess blackChess = new Chess();
//    HashSet<Node> black_chess = whiteChess.Chess(2);
//
//
//    Mini_CamelotActivity(String s) {
//        super(s);
//        this.setBounds(200, 100, 500, 680);
//        this.setLocationRelativeTo(null);
//
//        // add the restart button
//        ListenerForButton listenerForButton = new ListenerForButton();
//        butnStart.addActionListener(listenerForButton);
//        panelEast.add(butnStart);
//        this.add(panelEast, BorderLayout.EAST);
//
//
//
//        ImageIcon icon1 = null;
//
//        try {
//            icon1 = new ImageIcon(ImageIO.read(getClass().getResource("w.png")));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        ImageIcon icon2 = null;
//        try {
//            icon2 = new ImageIcon(ImageIO.read(getClass().getResource("b.png")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//        Iterator tmp_w = white_chess.iterator();
//        MouseHandler handler1 = new MouseHandler();
//        for (int i=0;i<6;i++) {
//            white_horse = new JLabel(icon1);
//            white_horse.setSize(Chess.interval, Chess.interval);
//            Node temp = (Node) tmp_w.next();
//            white_horse.setLocation(temp.getX()*Chess.interval,temp.getY()*Chess.interval);
//            handler1.makeDraggable(white_horse);
//            panelPaint.add(white_horse);
//        }
//        Iterator tmp_b = black_chess.iterator();
//        for (int i=0;i<6;i++) {
//            black_horse = new JLabel(icon2);
//            black_horse.setSize(Chess.interval, Chess.interval);
//            Node temp = (Node) tmp_b.next();
//            black_horse.setLocation(temp.getX()*Chess.interval,temp.getY()*Chess.interval);
//            handler1.makeDraggable(black_horse);
//            panelPaint.add(black_horse);
//        }
//        //TODO make the horse can move
//        this.add(panelPaint);
//        this.setVisible(true);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//}
//
//
//    protected class MouseHandler implements MouseListener,MouseMotionListener {
//
////        private boolean active = false;
//        private Point lastLocation;
//        private Component draggedComponent;
////        private Chess chess;
//        private Node node;
//        private int drageOffsetX;
//        private int drageOffsetY;
//
//
//        @Override
//        public void mousePressed(MouseEvent e) {
//            int x = e.getPoint().x;
//            int y = e.getPoint().y;
//            if (mouseOverNode(node,x,y)) {
//                this.drageOffsetX = x - node.getX();
//                this.drageOffsetY = y - node.getY();
//                node.setX(drageOffsetX);
//                node.setY(drageOffsetY);
//            }
//
//            draggedComponent = e.getComponent();
//            lastLocation = SwingUtilities.convertPoint(draggedComponent, e.getPoint(), draggedComponent.getParent());
//        }
//
//        private boolean mouseOverNode(Node node, int x, int y){
//            return node.getX()<=x
//                    && node.getX()+Chess.interval>=x
//                    && node.getY()<=y
//                    && node.getY()+Chess.interval>=y;
//        }
//
//        @Override
//        public void mouseDragged (MouseEvent e) {
//            Point point = e.getLocationOnScreen();
//
//
//
//
//            Point location = SwingUtilities.convertPoint(draggedComponent, e.getPoint(), draggedComponent.getParent());
//            if (draggedComponent.getParent().getBounds().contains(location)) {
//                Point newLocation = draggedComponent.getLocation();
//                newLocation.x = (newLocation.x/Chess.interval)*Chess.interval;
//                newLocation.x = Math.max(newLocation.x, Chess.interval);
//                newLocation.x = Math.min(newLocation.x, 8 * Chess.interval);
//
//                newLocation.y = (newLocation.y/Chess.interval)*Chess.interval;
//                newLocation.y = Math.max(newLocation.y, Chess.interval);
//                newLocation.y = Math.min(newLocation.y, 15 * Chess.interval);
//                System.out.println("y= "+newLocation.y+" x= "+newLocation.x);
//                newLocation.translate(location.x - lastLocation.x, location.y - lastLocation.y);
//                draggedComponent.setLocation(newLocation);
//                lastLocation = location;
//
//            }
//        }
//
//        @Override
//        public void mouseReleased(MouseEvent e) {
////                lastLocation = null;
////                draggedComponent = null;
//            node = null;
//            }
//
//
//        public void makeDraggable(Component component) {
//            component.addMouseListener(this);
//            component.addMouseMotionListener(this);
//        }
//    }
//
//    public class ListenerForButton implements ActionListener{
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            if(e.getSource()==butnStart){
//                    if (step != 0) {
//                        int option = JOptionPane.showConfirmDialog(Mini_CamelotActivity.this, "Do you want to play again?",
//                                "Mini Camelot", JOptionPane.YES_NO_OPTION);
//                        if (option == JOptionPane.YES_OPTION) {
//                            clearExist();
//                            new Mini_CamelotActivity("Mini_Camelot");
//                            step = 0;
//                            panelPaint.repaint();
//                        }
//                    } else {
//                        JOptionPane.showMessageDialog(Mini_CamelotActivity.this,"This is the new game.\n Continuing!","Mini Camelot",JOptionPane.INFORMATION_MESSAGE);
//                    }
//            }
//        }
//    }
//
//    //draw the panel
//    public void paint(Graphics gg) {
//        super.paint(gg);
//        Graphics g = panelPaint.getGraphics();
//        g.setColor(Color.black);
//        for (int i = 1; i <= Chess.number_column; i++) {
//            if(i==1) {
//                g.drawLine(i * Chess.interval, 4*Chess.interval, i * Chess.interval, 12*Chess.interval);
//                g.drawLine((i+8) * Chess.interval, 4*Chess.interval, (i+8) * Chess.interval, 12*Chess.interval);
//                g.drawLine(4*Chess.interval, i * Chess.interval, 6*Chess.interval, i * Chess.interval);
//                g.drawLine(4*Chess.interval, (i+14) * Chess.interval, 6*Chess.interval, (i+14) * Chess.interval);
//            } else if(i==2){
//                g.drawLine(i * Chess.interval, 3*Chess.interval, i * Chess.interval, 4*Chess.interval);
//                g.drawLine(i * Chess.interval, 12*Chess.interval, i * Chess.interval, 13*Chess.interval);
//                g.drawLine((i+6) * Chess.interval, 3*Chess.interval, (i+6) * Chess.interval, 4*Chess.interval);
//                g.drawLine((i+6) * Chess.interval, 12*Chess.interval, (i+6) * Chess.interval, 13*Chess.interval);
//                g.drawLine(3*Chess.interval, i * Chess.interval, 4*Chess.interval, i * Chess.interval);
//                g.drawLine(3*Chess.interval, (i+12) * Chess.interval, 4*Chess.interval, (i+12) * Chess.interval);
//                g.drawLine(6*Chess.interval, i * Chess.interval, 7*Chess.interval, i * Chess.interval);
//                g.drawLine(6*Chess.interval, (i+12) * Chess.interval, 7*Chess.interval, (i+12) * Chess.interval);
//            } else if(i==3){
//                g.drawLine(i * Chess.interval, 2*Chess.interval, i * Chess.interval, 3*Chess.interval);
//                g.drawLine(i * Chess.interval, 13*Chess.interval, i * Chess.interval, 14*Chess.interval);
//                g.drawLine((i+4) * Chess.interval, 2*Chess.interval, (i+4) * Chess.interval, 120);
//                g.drawLine((i+4) * Chess.interval, 13*Chess.interval, (i+4) * Chess.interval, 14*Chess.interval);
//                g.drawLine(2*Chess.interval, i * Chess.interval, 3*Chess.interval, i * Chess.interval);
//                g.drawLine(2*Chess.interval, (i+10) * Chess.interval, 3*Chess.interval, (i+10) * Chess.interval);
//                g.drawLine(7*Chess.interval, i * Chess.interval, 8*Chess.interval, i * Chess.interval);
//                g.drawLine(7*Chess.interval, (i+10) * Chess.interval, 8*Chess.interval, (i+10) * Chess.interval);
//            } else if(i==4){
//                g.drawLine(i * Chess.interval, Chess.interval, i * Chess.interval, 2*Chess.interval);
//                g.drawLine(i * Chess.interval, 14*Chess.interval, i * Chess.interval, 15*Chess.interval);
//                g.drawLine((i+2) * Chess.interval, Chess.interval, (i+2) * Chess.interval, 2*Chess.interval);
//                g.drawLine((i+2) * Chess.interval, 14*Chess.interval, (i+2) * Chess.interval, 15*Chess.interval);
//                g.drawLine(Chess.interval, i * Chess.interval, 2*Chess.interval, i * Chess.interval);
//                g.drawLine(Chess.interval, (i+8) * Chess.interval, 2*Chess.interval, (i+8) * Chess.interval);
//                g.drawLine(8*Chess.interval, i * Chess.interval, 9*Chess.interval, i * Chess.interval);
//                g.drawLine(8*Chess.interval, (i+8) * Chess.interval, 9*Chess.interval, (i+8) * Chess.interval);
//            }
//            if(i>5) {
//                g.setColor(Color.LIGHT_GRAY);
//
//                //draw the row line
//                for (int j=0;j<3;j++)
//                    g.drawLine((4-j) * Chess.interval, (2+j) * Chess.interval, (6+j) * Chess.interval, (2+j) * Chess.interval);
//                for (int j=0;j<7;j++)
//                    g.drawLine(Chess.interval, (5+j) * Chess.interval, 9 * Chess.interval, (5+j) * Chess.interval);
//                for (int j=0;j<3;j++)
//                    g.drawLine((2+j) * Chess.interval, (12+j) * Chess.interval, (8-j) * Chess.interval, (12+j) * Chess.interval);
//
//                // draw the column line
//                for (int j=0;j<4;j++)
//                    g.drawLine((2+j) * Chess.interval, (4-j) * Chess.interval, (2+j) * Chess.interval, (12+j) * Chess.interval);
//                for (int j=0;j<3;j++)
//                    g.drawLine((6+j) * Chess.interval, (2+j) * Chess.interval, (6+j) * Chess.interval, (14-j) * Chess.interval);
//            }
//        }
//    }
//
//    public void clearExist() {
//        Graphics g = panelPaint.getGraphics();
//        g.clearRect(0, 0, 600, 600);
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                    new Mini_CamelotActivity("Mini Camelot");
//
//            }
//        });
//    }
//}