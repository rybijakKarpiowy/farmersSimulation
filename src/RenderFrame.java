import javax.swing.*;
import java.awt.*;

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
                add(panels[i][j]);
            }
        }

        setVisible(true);
    }

    public void setColor(int row, int col, Color color) {
        panels[row][col].setBackground(color);
    }

}
