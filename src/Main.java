import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class Main {
    static void printUsage() {
        System.out.println("Usage: java Main <field_size> <farmer_count>");
        System.out.println("Flags: -c <carrot_growth_probability> -r <rabbit_spawn_probability> --continue");
    }

    public static void main(String[] args) throws InterruptedException {
        Field field = new Field(5,5);
        Renderer renderer = new Renderer(5,5, field);
        List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

        for (int i = 0; i < 5; i++) {
            actors.add(new Rabbit(field));
        }

        while (true) {
            field.renderLock();
            try {
                field.plantCarrot(field.getRandomCoordinates());
                field.growCarrots();

//                actors.removeIf(actor ->
//                        actor instanceof Rabbit && !((Rabbit) actor).getIsAlive()
//                );

                renderer.updateFields();
            } finally {
                field.renderUnlock();
            }

            Thread.sleep(100);
        }
    }
}