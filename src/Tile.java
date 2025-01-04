import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.List;

public class Tile {
    private final Float CARROT_GROWTH_PROBABILITY = 0.1f;

    private final ExtendedReentrantReadWriteLock rw_lock;
    public final ReentrantLock tile_lock = new ReentrantLock();

    private volatile TileState state = TileState.EMPTY;
    private volatile Float carrot_coverage = 0.0f;
    private final Coordinates coordinates;

    private final List<ActorAbstract> actors = new LinkedList<ActorAbstract>();

    public Tile(ExtendedReentrantReadWriteLock rw_lock, Integer y, Integer x) {
        this.rw_lock = rw_lock;
        this.coordinates = new Coordinates(x, y);
    }

    public Color getValue() {
        assert rw_lock.isWriteLockedByCurrentThread();
        tile_lock.lock();
        try {
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
        } finally {
            tile_lock.unlock();
        }
    }

    public void setState(TileState state) {
        assert rw_lock.isReadLockedByCurrentThread();
        tile_lock.lock();
        try {
            this.state = state;
        } finally {
            tile_lock.unlock();
        }
    }

    public TileState getState() {
        assert rw_lock.isWriteLockedByCurrentThread() || rw_lock.isReadLockedByCurrentThread();
        tile_lock.lock();
        try {
            return this.state;
        } finally {
            tile_lock.unlock();
        }
    }

    public Float getCarrotCoverage() {
        assert rw_lock.isWriteLockedByCurrentThread() || rw_lock.isReadLockedByCurrentThread();
        tile_lock.lock();
        try {
            return this.carrot_coverage;
        } finally {
            tile_lock.unlock();
        }
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void tickCarrotGrowth() {
        assert rw_lock.isWriteLockedByCurrentThread();
        tile_lock.lock();
        try {
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
        } finally {
            tile_lock.unlock();
        }
    }

    public void updateDamaged() {
        // this function updates tile state based on the carrot coverage
        assert rw_lock.isWriteLockedByCurrentThread();
        assert tile_lock.isHeldByCurrentThread();
        if (this.carrot_coverage < 0.3) {
            this.setState(TileState.DAMAGED);
        }
    }

    public List<ActorAbstract> getActors() {
        assert rw_lock.isWriteLockedByCurrentThread() || rw_lock.isReadLockedByCurrentThread();
        tile_lock.lock();
        try {
            return this.actors;
        } finally {
            tile_lock.unlock();
        }
    }

    public void addActor(ActorAbstract actor) {
        assert rw_lock.isWriteLockedByCurrentThread();
        assert tile_lock.isHeldByCurrentThread();
        this.actors.add(actor);
    }

    public void removeActor(ActorAbstract actor) {
        assert rw_lock.isWriteLockedByCurrentThread();
        assert tile_lock.isHeldByCurrentThread();
        this.actors.remove(actor);
    }

    public void plantCarrot() {
        assert rw_lock.isWriteLockedByCurrentThread();
        tile_lock.lock();
        try {
            this.setState(TileState.CARROT_PLANTED);
            this.carrot_coverage = 1.0f;
        } finally {
            tile_lock.unlock();
        }
    }

    public void eatCarrot() {
        assert rw_lock.isWriteLockedByCurrentThread();
        tile_lock.lock();
        try {
            // tile is locked so we don't have to worry
            //noinspection NonAtomicOperationOnVolatileField
            this.carrot_coverage -= 0.1f;
            if (this.carrot_coverage < 0.0f) {
                this.carrot_coverage = 0.0f;
            }
            updateDamaged();
        } finally {
            tile_lock.unlock();
        }
    }
}