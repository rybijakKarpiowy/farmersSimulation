import java.util.concurrent.locks.ReentrantLock;

public class Rabbit extends ActorAbstract {
    private boolean isAlive = true;
    public ReentrantLock rabbit_mutex = new ReentrantLock();

    public Rabbit(Field field) {
        super(field);
    }

    @Override
    public void tick() {
        field.simulationLock();
        rabbit_mutex.lock();
        if (!isAlive) {
            rabbit_mutex.unlock();
            field.simulationUnlock();
            return;
        }
        Tile tile = field.getTile(this.coordinates);
//        tile.lock.writeLock();
//        tile.isCarrotOnTile();

        randomWalk();
        rabbit_mutex.unlock();
        field.simulationUnlock();
    }

    public boolean getIsDead() {
        assert rabbit_mutex.isHeldByCurrentThread();
        return !isAlive;
    }

    public void turnDead() {
        assert rabbit_mutex.isHeldByCurrentThread();
        isAlive = false;
    }

    private void eat() {
        assert field.isRWLockedByCurrentThread();
        assert rabbit_mutex.isHeldByCurrentThread();
        Tile tile = field.getTile(this.getCoordinates());
        assert tile.lock.isWriteLocked();
        tile.eatCarrot();
    };
}
