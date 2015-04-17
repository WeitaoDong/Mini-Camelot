import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by weitao on 4/16/15.
 */
public class Mini_CamelotActivity extends JFrame implements ActionListener {

    int step;
    JButton butnStart = new JButton("重新开始");
    Chess chess = new Chess();
    FlowLayout flow = new FlowLayout();
    JPanel panelPaint=new JPanel(),panelEast=new JPanel();

    @Override
    public void actionPerformed(ActionEvent e) {

    }

}
