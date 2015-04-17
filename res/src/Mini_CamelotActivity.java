import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by weitao on 4/16/15.
 */
public class Mini_CamelotActivity extends JFrame implements ActionListener {

    int step;
    JButton butnStart = new JButton("restart");
    Chess chess = new Chess();
    FlowLayout flow = new FlowLayout();
    JPanel panelPaint=new JPanel(),panelEast=new JPanel();
    int length = 40;
    int height = 40;

    Mini_CamelotActivity(String s){
        super(s);
        this.setBounds(200, 100, 500, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
//        this.setResizable(false);

        // add the restart button
        ListenerForButton listenerForButton = new ListenerForButton();
        butnStart.addActionListener(listenerForButton);
        panelEast.add(butnStart);
        this.add(panelEast, BorderLayout.EAST);

        panelPaint.setLayout(null);

//        this.repaint();
//        this.validate();

        //使窗口可以鼠标拖动
        MouseDraggedAdapter adapter = new MouseDraggedAdapter(this);
        this.addMouseListener(adapter);
        this.addMouseMotionListener(adapter);

        this.add(panelPaint);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//    }
}

    private class ListenerForButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()==butnStart){
                    if (step != 0) {
                        int option = JOptionPane.showConfirmDialog(Mini_CamelotActivity.this, "Do you want to play again?",
                                "Mini Camelot", JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            clearExist();
                            chess.restate();
                            step = 0;
                            repaint();
                        }
                    } else {
                        JOptionPane.showMessageDialog(Mini_CamelotActivity.this,"This is the new game.\n Continuing!","Mini Camelot",JOptionPane.INFORMATION_MESSAGE);
                    }
            }
        }
    }

    private void PaintCircle(Graphics g){
        Color color=Color.white;
        JLabel label;

        for(int i=0;i<8;i++)
            for(int j=0;j<14;j++) {

            }
    }

class MouseDraggedAdapter extends MouseAdapter {
    private Point lastPoint = null;

    private Container container;

    public MouseDraggedAdapter(Container container) {
        this.container = container;
    }

    public void mousePressed(MouseEvent e) {
        lastPoint = e.getLocationOnScreen();
    }

    public void mouseDragged(MouseEvent e) {
        Point point = e.getLocationOnScreen();
        int offsetX = point.x - lastPoint.x;
        int offsetY = point.y - lastPoint.y;
        Rectangle bounds = container.getBounds();
        bounds.x += offsetX;
        bounds.y += offsetY;
        container.setBounds(bounds);
        lastPoint = point;
    }
}
    public void paint(Graphics gg) {// Graphics是在整个窗体描绘
        super.paint(gg);
        Graphics g = panelPaint.getGraphics();
        g.setColor(Color.black);
        for (int i = 1; i <= Chess.number_column; i++) {
            if(i==1) {
                g.drawLine(i * Chess.interval, 160, i * Chess.interval, 480);
                g.drawLine((i+8) * Chess.interval, 160, (i+8) * Chess.interval, 480);
                g.drawLine(160, i * Chess.interval, 240, i * Chess.interval);
                g.drawLine(160, (i+14) * Chess.interval, 240, (i+14) * Chess.interval);
            } else if(i==2){
                g.drawLine(i * Chess.interval, 120, i * Chess.interval, 160);
                g.drawLine(i * Chess.interval, 480, i * Chess.interval, 520);
                g.drawLine((i+6) * Chess.interval, 120, (i+6) * Chess.interval, 160);
                g.drawLine((i+6) * Chess.interval, 480, (i+6) * Chess.interval, 520);
                g.drawLine(120, i * Chess.interval, 160, i * Chess.interval);
                g.drawLine(120, (i+12) * Chess.interval, 160, (i+12) * Chess.interval);
                g.drawLine(240, i * Chess.interval, 280, i * Chess.interval);
                g.drawLine(240, (i+12) * Chess.interval, 280, (i+12) * Chess.interval);
            } else if(i==3){
                g.drawLine(i * Chess.interval, 80, i * Chess.interval, 120);
                g.drawLine(i * Chess.interval, 520, i * Chess.interval, 560);
                g.drawLine((i+4) * Chess.interval, 80, (i+4) * Chess.interval, 120);
                g.drawLine((i+4) * Chess.interval, 520, (i+4) * Chess.interval, 560);
                g.drawLine(80, i * Chess.interval, 120, i * Chess.interval);
                g.drawLine(80, (i+10) * Chess.interval, 120, (i+10) * Chess.interval);
                g.drawLine(280, i * Chess.interval, 320, i * Chess.interval);
                g.drawLine(280, (i+10) * Chess.interval, 320, (i+10) * Chess.interval);
            } else if(i==4){
                g.drawLine(i * Chess.interval, 40, i * Chess.interval, 80);
                g.drawLine(i * Chess.interval, 560, i * Chess.interval, 600);
                g.drawLine((i+2) * Chess.interval, 40, (i+2) * Chess.interval, 80);
                g.drawLine((i+2) * Chess.interval, 560, (i+2) * Chess.interval, 600);
                g.drawLine(40, i * Chess.interval, 80, i * Chess.interval);
                g.drawLine(40, (i+8) * Chess.interval, 80, (i+8) * Chess.interval);
                g.drawLine(320, i * Chess.interval, 360, i * Chess.interval);
                g.drawLine(320, (i+8) * Chess.interval, 360, (i+8) * Chess.interval);
            }
            if(i>5) {
                g.setColor(Color.LIGHT_GRAY);

                //draw the row line
                for (int j=0;j<3;j++)
                    g.drawLine((4-j) * Chess.interval, (2+j) * Chess.interval, (6+j) * Chess.interval, (2+j) * Chess.interval);
                for (int j=0;j<7;j++)
                    g.drawLine(1 * Chess.interval, (5+j) * Chess.interval, 9 * Chess.interval, (5+j) * Chess.interval);
                for (int j=0;j<3;j++)
                    g.drawLine((2+j) * Chess.interval, (12+j) * Chess.interval, (8-j) * Chess.interval, (12+j) * Chess.interval);

                // draw the column line
                for (int j=0;j<4;j++)
                    g.drawLine((2+j) * Chess.interval, (4-j) * Chess.interval, (2+j) * Chess.interval, (12+j) * Chess.interval);
                for (int j=0;j<3;j++)
                    g.drawLine((6+j) * Chess.interval, (2+j) * Chess.interval, (6+j) * Chess.interval, (14-j) * Chess.interval);


            }
        }
        drawExistNode();
    }
    void drawExistNode() {
        Graphics g = panelPaint.getGraphics();
        for (int i = 0; i < Chess.number_column; i++)
            for (int j = 0; j < Chess.number_column; j++) {
                if (chess.node[i][j].state == 1 || chess.node[i][j].state == 2) {
                    if (chess.node[i][j].state == 1)
                        g.setColor(Color.black);
                    else if (chess.node[i][j].state == 2)
                        g.setColor(Color.white);
                    g.drawOval(chess.node[i][j].x - 6, chess.node[i][j].y - 6,
                            12, 12);
                    g.fillOval(chess.node[i][j].x - 6, chess.node[i][j].y - 6,
                            12, 12);
                }
            }
    }

    public void clearExist() {
        Graphics g = panelPaint.getGraphics();
        g.clearRect(0, 0, 600, 600);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        new Mini_CamelotActivity("Mini Camelot");
    }

}
