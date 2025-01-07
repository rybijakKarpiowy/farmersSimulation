public class Farmer extends ActorAbstract {
//    private final Dog dog;

    public Farmer(Field field) {
        super(field);
//        dog = new Dog(field);
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
        // TODO: look for rabbits and update dog chasing rabbit

    }
}
