
/**
 * Created by weitao on 4/16/15.
 */
public class Chess {
    boolean white;
    public static final int bound = 7;// 在离中心为7的范围内单击为有效单击(即下棋)
    public static final int number_column = 9;
    public static final int number_row = 15;
    public static final int interval = 40;//五子棋之间的距离
    Node node[][];

    public Chess() {
        init();
    }

    private void init() {
        node = new Node[number_row][number_column];
        for (int i = 0; i < number_row; i++)
            for (int j = 0; j < number_column; j++) {
                node[i][j] = new Node();
                node[i][j].x = (i + 1) * interval;
                node[i][j].y = (j + 1) * interval;
            }
    }



    void restate() {
        for (int i = 0; i < number_column; i++)
            for (int j = 0; j < number_column; j++) {
                node[i][j].state = 0;
                white = false;
            }
    }
}
class Node {
    int x;
    int y;
    int state;// 0无棋子,1黑棋,2白棋
}
