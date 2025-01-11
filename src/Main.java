import javax.swing.*;

public class Main {

    public static void main() throws InterruptedException {
        Settings settings = Settings.getInstance();

        int gridSize = Integer.parseInt(settings.getSetting("Grid", "Size"));
        Field field = new Field(gridSize);

        RenderFrame renderFrame = new RenderFrame(gridSize, field);

        for (int i = 0; i < 10; i++) {
            new Rabbit(field);
            new Farmer(field);
        }

        while (true) {
            field.renderLock();
            renderFrame.resetLatch();
            try {
                SwingUtilities.invokeLater(renderFrame::updateFields);
            } finally {
                renderFrame.await();
                field.renderUnlock();
            }
        }
    }
}