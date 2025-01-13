import javax.swing.*;

public class Main {

    public static void main() throws InterruptedException {
        Settings settings = Settings.getInstance();

        System.out.println("Do you want to input settings? (y/n)");
        String input = System.console().readLine();
        if (input.equals("y") || input.equals("Y")) {
            InputTaker.InputAndSave();
            settings.saveSettings();
        }
        else {
            System.out.println("Using default settings");
        }

        int gridSize = Integer.parseInt(settings.getSetting("Grid", "Size"));
        Field field = new Field(gridSize);

        int initialRabbitCount = Integer.parseInt(settings.getSetting("Rabbit", "Count"));
        for (int i = 0; i < initialRabbitCount; i++) {
            new Rabbit(field);
        }

        int initialFarmerCount = Integer.parseInt(settings.getSetting("Farmer", "Count"));
        for (int i = 0; i < initialFarmerCount; i++) {
            new Farmer(field);
        }

        RenderFrame renderFrame = new RenderFrame(gridSize, field);

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