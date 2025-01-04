import java.awt.Color;

public class Renderer {
    private final RenderFrame frame;
    private final Field field;

    public Renderer(int rows, int cols, Field field) {
        this.frame = new RenderFrame(rows, cols);
        this.field = field;
        updateFields();
    }

    public void updateField(int row, int col, Color color) {
        frame.setColor(row, col, color);
    }

    public void updateFields() {
        field.renderLock();
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {
                updateField(i, j, field.getValue(i, j));
            }
        }
        field.renderUnlock();
    }
}
