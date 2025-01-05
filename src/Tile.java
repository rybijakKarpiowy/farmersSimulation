import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Tile {
    private final Float CARROT_GROWTH_PROBABILITY = 0.1f;

    public final ExtendedReentrantReadWriteLock lock = new ExtendedReentrantReadWriteLock(true);

    private volatile TileState state = TileState.EMPTY;
    private volatile Float carrot_coverage = 0.0f;
    private final Coordinates coordinates;

    private final List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

    public Tile(Integer y, Integer x) {
        this.coordinates = new Coordinates(x, y);
    }

    public boolean isRWLockedByCurrentThread() {
        return lock.isRWLockedByCurrentThread();
    }

    public Color getColor() {
        assert isRWLockedByCurrentThread();
        // if tile is empty and no actors, return green
        if (this.state == TileState.EMPTY && this.actors.isEmpty()) {
            return Color.GREEN;
        }
        // if tile is empty and has actors, return yellow
        if (this.state == TileState.EMPTY && !this.actors.isEmpty()) {
            return Color.YELLOW;
        }
        // else return white
        return Color.WHITE;
    }

    public void setState(TileState state) {
        assert lock.isWriteLockedByCurrentThread();
        this.state = state;
    }

    public TileState getState() {
        assert isRWLockedByCurrentThread();
        try {
            return this.state;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Float getCarrotCoverage() {
        assert isRWLockedByCurrentThread();
        lock.readLock().lock();
        try {
            return this.carrot_coverage;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void tickCarrotGrowth() {
        assert lock.isWriteLockedByCurrentThread();
        TileState state = this.getState();
        if (state == TileState.EMPTY || state == TileState.DAMAGED || state == TileState.CARROT_MATURE) {
            // pass
        } else if (state == TileState.CARROT_PLANTED) {
            if (Math.random() < CARROT_GROWTH_PROBABILITY) {
                this.setState(TileState.CARROT_GROWTH_1);
            }
        } else if (state == TileState.CARROT_GROWTH_1) {
            if (Math.random() < CARROT_GROWTH_PROBABILITY) {
                this.setState(TileState.CARROT_GROWTH_2);
            }
        } else if (state == TileState.CARROT_GROWTH_2) {
            if (Math.random() < CARROT_GROWTH_PROBABILITY) {
                this.setState(TileState.CARROT_MATURE);
            }
        }
    }

    public void updateDamaged() {
        assert lock.isWriteLockedByCurrentThread();
        if (this.carrot_coverage < 0.3) {
            this.setState(TileState.DAMAGED);
        }
    }

    public List<ActorAbstract> getActors() {
        assert lock.isRWLockedByCurrentThread();
        return this.actors;
    }

    public void addActor(ActorAbstract actor) {
        assert lock.isWriteLockedByCurrentThread();
        this.actors.add(actor);
    }

    public void removeActor(ActorAbstract actor) {
        assert lock.isWriteLockedByCurrentThread();
        this.actors.remove(actor);
    }

    public void plantCarrot() {
        assert lock.isWriteLockedByCurrentThread();
        this.setState(TileState.CARROT_PLANTED);
        this.carrot_coverage = 1.0f;
    }

    public void eatCarrot() {
        assert lock.isWriteLockedByCurrentThread();
        // tile is locked so we don't have to worry
        //noinspection NonAtomicOperationOnVolatileField
        this.carrot_coverage -= 0.1f;
        if (this.carrot_coverage < 0.0f) {
            this.carrot_coverage = 0.0f;
        }
        updateDamaged();
    }
}