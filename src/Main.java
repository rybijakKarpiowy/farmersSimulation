import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Main {
    static void printUsage() {
        System.out.println("Usage: java Main <field_size> <farmer_count>");
        System.out.println("Flags: -c <carrot_growth_probability> -r <rabbit_spawn_probability> --continue");
    }

    public static void main(String[] args) throws InterruptedException {
        Field field = new Field(25,25);
        RenderFrame renderFrame = new RenderFrame(25, 25, field);

        Settings settings = Settings.getInstance();
        System.out.println(settings.getSetting("rabbit", "spawn_probability"));

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