import java.util.List;

public class Farmer extends ActorAbstract {
    private final Dog dog;

    public Farmer(Field field) {
        super(field);
        dog = new Dog(field, this.coordinates);
    }

    @Override
    public void tick() {
        field.simulationLock();
        Tile tile = field.getTile(this.coordinates);
        tile.lock.writeLock().lock();

        // choose action
        TileState tileState = tile.getState();
        if (tileState == TileState.DAMAGED) {
            repair(tile);
        } else if (tileState == TileState.EMPTY) {
            plant(tile);
        } else {
            lookAround();
            randomWalk();
        }

        tile.lock.writeLock().unlock();
        field.simulationUnlock();
    }

    private void plant(Tile tile) {
        assert field.isRWLockedByCurrentThread();
        assert tile.lock.isWriteLocked();
        tile.plantCarrot();
    }

    private void repair(Tile tile) {
        assert field.isRWLockedByCurrentThread();
        assert tile.lock.isWriteLocked();
        tile.repairTile();
    }

    private void lookAround() {
        assert field.isRWLockedByCurrentThread();
        dog.dog_mutex.lock();
        if (dog.getTarget() != null) {
            dog.dog_mutex.unlock();
            return;
        }
        dog.dog_mutex.unlock();
        Tile farmerTile = field.getTile(coordinates);
        farmerTile.lock.writeLock().unlock();

        List<Tile> tiles = field.getTilesInViewRange(this.coordinates, 5);
        Rabbit rabbit = null;
        for (Tile tile : tiles) {
            tile.lock.readLock().lock();
            rabbit = tile.hasRabbit();
            tile.lock.readLock().unlock();
            if (rabbit != null) {
                break;
            }
        }
        farmerTile.lock.writeLock().lock();
        if (rabbit != null) {
            dog.dog_mutex.lock();
            if (dog.getTarget() == null) {
                dog.setTarget(rabbit);
            }
            dog.dog_mutex.unlock();
        }
    }
}
