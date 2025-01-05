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

    public void addRabbitImage(int row, int col) {
        frame.addImage(row, col, "src/images/rabbit.png");
    }

    public void removeImages(int row, int col) {
        frame.removeImage(row, col);
    }

    public void updateFields() {
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {
                removeImages(i, j);
                updateBackgroundColor(i, j, field.getTileBackgroundColor(new Coordinates(i, j)));

                for (ActorAbstract actor : field.getActors(new Coordinates(i, j))) {
                    if (Objects.equals(actor.getType(), "rabbit")) {
                        addRabbitImage(i, j);
                    }
                }
            }
        }
    }
}
