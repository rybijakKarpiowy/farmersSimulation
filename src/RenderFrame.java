import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class RenderFrame extends JFrame {
    private final JPanel[][] panels;

    public RenderFrame(int rows, int cols) {
        super("Render");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(rows, cols));

        panels = new JPanel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                panels[i][j] = new JPanel();
                panels[i][j].setBorder(new BasicBorders.FieldBorder(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK));
                add(panels[i][j]);
            }
        }

        setVisible(true);
    }

    public void setColor(int row, int col, Color color) {
        panels[row][col].setBackground(color);
    }

    public void addRabbit(int row, int col) {
        // blue dot
        JPanel rabbit = new JPanel();
        rabbit.setBackground(Color.BLUE);
        rabbit.setBorder(new EmptyBorder(10, 10, 10, 10));
        panels[row][col].add(rabbit);
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    public void clearPanel(int row, int col) {
        panels[row][col].removeAll();
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

}
