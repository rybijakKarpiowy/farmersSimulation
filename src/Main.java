import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public class Main {
    static void printUsage() {
        System.out.println("Usage: java Main <field_size> <farmer_count>");
        System.out.println("Flags: -c <carrot_growth_probability> -r <rabbit_spawn_probability> --continue");
    }

    private static void removeDeadRabbits(List<ActorAbstract> actors) {
        for (int i = actors.size() - 1; i >= 0; i--) {
            ActorAbstract actor = actors.get(i);
            if (actor instanceof Rabbit rabbit) {
                if (rabbit.getIsDead()) {
                    actors.remove(i);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Field field = new Field(5,5);
        RenderFrame renderFrame = new RenderFrame(5, 5, field);
        List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

        for (int i = 0; i < 5; i++) {
            actors.add(new Rabbit(field));
        }

        int counter = 0;
        while (true) {
            renderFrame.resetLatch();
            field.renderLock();
            counter++;
            try {
                field.plantCarrot(field.getRandomCoordinates());
                field.growCarrots();

                // THIS IS A DEBUG IF STATEMENT
                if (counter == 10) {
                    // kill a rabbit
                    Rabbit rabbit = (Rabbit) actors.getFirst();
                    if (field.killRabbit(rabbit)) {
                        System.out.println("Rabbit killed on " + rabbit.getCoordinates().x + " " + rabbit.getCoordinates().y);
                    }
                }

                removeDeadRabbits(actors);

                SwingUtilities.invokeLater(renderFrame::updateFields);
            } finally {
                renderFrame.await();
                field.renderUnlock();
            }

//            Thread.sleep(30);
        }
    }
}