import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Dog extends ActorAbstract {
    public ReentrantLock dog_mutex = new ReentrantLock();
    private volatile Rabbit target = null;

    public Dog(Field field, Coordinates coordinates) {
        super(field, coordinates);
    }

    @Override
    public void tick() {
        field.simulationLock();
        Tile tile = field.getTile(this.coordinates);
        tile.lock.writeLock().lock();
        dog_mutex.lock();

        if (tile.hasRabbit(target) != null) {
            killRabbit();
        } else if (target != null) {
            chaseRabbit();
        } else {
            lookAround();
            randomWalk();
        }

        dog_mutex.unlock();
        tile.lock.writeLock().unlock();
        field.simulationUnlock();
    }

    private void chaseRabbit() {
        assert field.isRWLockedByCurrentThread();
        assert field.getTile(this.coordinates).lock.isWriteLocked();
        assert dog_mutex.isHeldByCurrentThread();
        assert target != null;
        // lock rabbit
        target.rabbit_mutex.lock();
        // check in which direction the rabbit is
        int dx = target.coordinates.x - this.coordinates.x;
        int dy = target.coordinates.y - this.coordinates.y;
        if (dx == 0 && dy == 0) {
            assert target.getIsDead();
            // rabbit is dead, stop chasing it
            target.rabbit_mutex.unlock();
            removeTarget();
            return;
        }
        // move in the direction of the rabbit
        Coordinates new_coordinates;
        if (Math.abs(dx) >= Math.abs(dy)) {
            // move in x direction
            new_coordinates = new Coordinates(this.coordinates.x + Integer.signum(dx), this.coordinates.y);
        } else {
            // move in y direction
            new_coordinates = new Coordinates(this.coordinates.x, this.coordinates.y + Integer.signum(dy));
        }
        // unlock rabbit
        target.rabbit_mutex.unlock();
        field.moveActor(this, new_coordinates);
    }

    private void lookAround() {
        assert field.isRWLockedByCurrentThread();
        assert dog_mutex.isHeldByCurrentThread();
        List<Tile> tiles = field.getTilesInViewRange(this.coordinates, 2);
        Rabbit rabbit = null;
        for (Tile tile : tiles) {
            tile.lock.readLock().lock();
            rabbit = tile.hasRabbit();
            tile.lock.readLock().unlock();
            if (rabbit != null) {
                break;
            }
        }
        if (rabbit != null) {
            setTarget(rabbit);
        }
    }

    private void killRabbit() {
        assert field.isRWLockedByCurrentThread();
        assert dog_mutex.isHeldByCurrentThread();
        assert field.getTile(target.coordinates).lock.isWriteLocked();
        field.killRabbit(target, this);
    }

    public void setTarget(Rabbit rabbit) {
        assert field.isRWLockedByCurrentThread();
        assert dog_mutex.isHeldByCurrentThread();
        assert target == null;
        target = rabbit;
    }

    public void removeTarget() {
        assert field.isRWLockedByCurrentThread();
        assert dog_mutex.isHeldByCurrentThread();
        assert target != null;
        target = null;
    }

    public Rabbit getTarget() {
        assert field.isRWLockedByCurrentThread();
        assert dog_mutex.isHeldByCurrentThread();
        return target;
    }
}
