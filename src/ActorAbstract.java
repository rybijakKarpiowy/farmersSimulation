import java.util.List;

public abstract class ActorAbstract extends ThreadAbstract {
    protected volatile Coordinates coordinates;
    protected final Field field;

    public ActorAbstract(Field field) {
        Coordinates coordinates = field.getRandomCoordinates();
        this.field = field;
        this.coordinates = coordinates;

        boolean shouldUnlock = false;
        if (!field.isRWLockedByCurrentThread()) {
            field.simulationLock();
            shouldUnlock = true;
        }
        Tile tile = field.getTile(coordinates);
        tile.lock.writeLock().lock();

        // start the thread
        super(tile);

        tile.lock.writeLock().unlock();
        if (shouldUnlock) {
            field.simulationUnlock();
        }
    }

    public ActorAbstract(Field field, Coordinates coordinates) {
        this.field = field;
        this.coordinates = coordinates;

        field.simulationLock();
        Tile tile = field.getTile(coordinates);
        tile.lock.writeLock().lock();

        // start the thread
        super(tile);

        tile.lock.writeLock().unlock();
        field.simulationUnlock();
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void randomWalk() {
        assert field.isRWLockedByCurrentThread();
        // get neighbors
        List<Coordinates> neighbors = field.getNeighbors(coordinates);
        // select a random neighbor
        Coordinates new_coordinates = neighbors.get((int) (Math.random() * neighbors.size()));
        // move
        field.moveActor(this, new_coordinates);
    }
}
