import java.awt.Color;
import java.util.Objects;

public class Renderer {
    private final RenderFrame frame;
    private final Field field;

    public Renderer(int rows, int cols, Field field) {
        this.frame = new RenderFrame(rows, cols);
        this.field = field;
        updateFields();
    }

    public void updateBackgroundColor(int row, int col, Color color) {
        frame.setColor(row, col, color);
    }

    public void addRabbit(int row, int col) {
        frame.addRabbit(row, col);
    }

    public void clearPanel(int row, int col) {
        frame.clearPanel(row, col);
    }

    public void updateFields() {
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {
                clearPanel(i, j);
                updateBackgroundColor(i, j, field.getTileBackgroundColor(new Coordinates(i, j)));

                for (ActorAbstract actor : field.getActors(new Coordinates(i, j))) {
                    if (actor instanceof Rabbit) {
                        addRabbit(i, j);
                    }
                }
            }
        }
    }
}
