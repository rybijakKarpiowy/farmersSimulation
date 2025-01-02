import java.awt.Color;

public class Renderer {
    private final RenderFrame frame;

    public Renderer(int rows, int cols, Grid grid) {
        this.frame = new RenderFrame(rows, cols);
        updateFields(grid);

    }

    public void updateField(int row, int col, Color color) {
        frame.setColor(row, col, color);
    }

    public void updateFields(Grid grid) {
        grid.renderLock();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                updateField(i, j, grid.getValue(i, j));
            }
        }
        grid.renderUnlock();
    }
}
