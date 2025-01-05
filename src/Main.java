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
        Field field = new Field(5, 5);
        Renderer renderer = new Renderer(5, 5, field);
        List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

        for (int i = 0; i < 5; i++) {
            actors.add(new Rabbit(field));
        }

//        sleep(1000);
//        renderer.updateField(0, 0, Color.RED);
//        sleep(1000);
//        renderer.updateField(0, 1, Color.RED);
//        sleep(1000);
//        renderer.updateField(4, 4, Color.RED);

        int counter = 0;

        while (true) {
            field.renderLock();
            // if rabbits are dead, remove them
            actors.removeIf(actor -> actor.getClass().isInstance(Rabbit.class) && !((Rabbit) actor).getIsAlive());
            renderer.updateFields();

            counter++;
            if (counter % 50 == 0 && actors.size() == 5) {
                // kill a rabbit
                Rabbit rabbit = (Rabbit) actors.getFirst();
//                System.out.println("Killing rabbit on " + rabbit.getCoordinates().x + " " + rabbit.getCoordinates().y + " " + rabbit.getIsAlive());
                boolean killed = field.killRabbit(rabbit);
                if (killed) {
                    System.out.println("Rabbit killed on " + rabbit.getCoordinates().x + " " + rabbit.getCoordinates().y + " " + rabbit.getIsAlive());
                }
//                System.out.println("There were " + actors.size() + " rabbits");
            }

            // remove dead rabbits
            for (int i = actors.size() - 1; i >= 0; i--) {
                ActorAbstract actor = actors.get(i);
                if (actor instanceof Rabbit && !((Rabbit) actor).getIsAlive()) {
                    System.out.println("Actors: " + actors.size());
                    actors.remove(actor);
                    System.out.println("Removed dead rabbit");
                    System.err.println("Actors: " + actors.size());

                    // count the number of rabbit on the field
                    int rabbitCount = 0;
                    for (int j = 0; j < field.getRows(); j++) {
                        for (int k = 0; k < field.getCols(); k++) {
                            Tile tile = field.getTile(new Coordinates(j, k));
                            tile.lock.readLock().lock();
                        }
                    }
                    for (int j = 0; j < field.getRows(); j++) {
                        for (int k = 0; k < field.getCols(); k++) {
                            Tile tile = field.getTile(new Coordinates(j, k));
                            for (ActorAbstract tileActor : tile.getActors()) {
                                if (tileActor instanceof Rabbit) {
                                    rabbitCount++;
                                }
                            }
                            tile.lock.readLock().unlock();
                        }
                    }
                    System.out.println("Rabbit count: " + rabbitCount);
                }
            }

            field.renderUnlock();
        }
    }
}