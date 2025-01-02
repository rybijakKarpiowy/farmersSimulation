import java.awt.*;
import static java.lang.Thread.sleep;

public class Main {
    static void printUsage() {
        System.out.println("Usage: java Main <field_size> <farmer_count>");
        System.out.println("Flags: -c <carrot_growth_probability> -r <rabbit_spawn_probability> --continue");
    }

    public static void main(String[] args) throws InterruptedException {
        Grid grid = new Grid(5,5);
        Renderer renderer = new Renderer(5,5,grid);

        sleep(1000);
        renderer.updateField(0, 0, Color.RED);
        sleep(1000);
        renderer.updateField(0, 1, Color.RED);
        sleep(1000);
        renderer.updateField(4, 4, Color.RED);
    }
}