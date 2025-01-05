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

    public Color getBackgroundColor() {
        assert isRWLockedByCurrentThread();

        if (this.state == TileState.EMPTY) {
            return Color.WHITE;
        } else if (this.state == TileState.DAMAGED) {
            return Color.RED;
        } else if (this.state == TileState.CARROT_PLANTED) {
            return new Color(255, 69, 0, 50);
        } else if (this.state == TileState.CARROT_GROWTH_1) {
            return new Color(255, 69, 0, 100);
        } else if (this.state == TileState.CARROT_GROWTH_2) {
            return new Color(255, 69, 0, 150);
        } else if (this.state == TileState.CARROT_MATURE) {
            return new Color(255, 69, 0, 200);
        } else {
            return Color.BLACK;
        }
    }

    public void setState(TileState state) {
        assert lock.isWriteLockedByCurrentThread();
        this.state = state;
    }

    public TileState getState() {
        assert isRWLockedByCurrentThread();
        return this.state;
    }

    public Float getCarrotCoverage() {
        assert isRWLockedByCurrentThread();
        return this.carrot_coverage;
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void tickCarrotGrowth() {
        assert lock.isWriteLockedByCurrentThread();
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

    public boolean killRabbitOnTile(Rabbit rabbit
//            , Dog dog // TODO: implement dog
    ) {
        assert lock.isWriteLockedByCurrentThread();
        // check if the rabbit is still here and alive
        if (!this.actors.contains(rabbit)) {
            // the rabbit is not here anymore, maybe it is dead
            if (rabbit.getIsAlive()) {
                // the rabbit is alive, it must have moved to another tile
                System.err.println("Rabbit is not here anymore, maybe it moved to another tile");
                return false;
            }
            // the rabbit is dead, stop chasing it
            // TODO: implement dog
            return false;
        }
        // the rabbit is here, check if it is alive
        if (!rabbit.getIsAlive()) {
            // the rabbit is dead, stop chasing it
            // TODO: implement dog
            return false;
        }
        rabbit.rabbit_mutex.lock();
        rabbit.turnDead();
        rabbit.rabbit_mutex.unlock();
        // remove the rabbit from the tile
        this.removeActor(rabbit);
        // stop chasing the rabbit
        // TODO: implement dog
        return true;
    }
}