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
                rabbit.rabbit_mutex.lock();
                if (rabbit.getIsDead()) {
                    actors.remove(i);
                }
                rabbit.rabbit_mutex.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Field field = new Field(25,25);
        RenderFrame renderFrame = new RenderFrame(25, 25, field);
        List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

        for (int i = 0; i < 10; i++) {
            actors.add(new Rabbit(field));
            actors.add(new Farmer(field));
        }

        while (true) {
            renderFrame.resetLatch();
            field.renderLock();
            try {
                removeDeadRabbits(actors);
                SwingUtilities.invokeLater(renderFrame::updateFields);
            } finally {
                renderFrame.await();
                field.renderUnlock();
            }
        }
    }
}