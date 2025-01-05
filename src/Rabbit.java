import java.util.concurrent.locks.ReentrantLock;

public class Rabbit extends ActorAbstract {
    private boolean isAlive = true;
    public ReentrantLock rabbit_mutex = new ReentrantLock();

    public Rabbit(Field field) {
        super(field, "rabbit");
        this.coordinates = field.getRandomCoordinates();
    }

    @Override
    public void tick() {
        field.simulationLock();
        randomWalk();
        field.simulationUnlock();
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void turnDead() {
        isAlive = false;
    }

    private void eat() {};
}
