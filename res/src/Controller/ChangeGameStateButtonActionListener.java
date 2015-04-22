package Controller;

import View.CamelotGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by weitao on 4/20/15.
 */
public class ChangeGameStateButtonActionListener implements ActionListener {
    private CamelotGui camelotGui;

    public ChangeGameStateButtonActionListener(CamelotGui camelotGui){
        this.camelotGui = camelotGui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.camelotGui.changeGameState();
    }
}
