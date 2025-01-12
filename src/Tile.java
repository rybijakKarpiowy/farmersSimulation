import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Tile {
    private final Float CARROT_GROWTH_PROBABILITY;
    private final Float CARROT_PLANT_INCREASE;
    private final Float CARROT_REPAIR_INCREASE;
    public final ExtendedReentrantReadWriteLock lock = new ExtendedReentrantReadWriteLock(true);
    private volatile TileState state = TileState.EMPTY;
    private volatile Float carrot_coverage = 0.0f;
    private final Coordinates coordinates;
    private final List<ActorAbstract> actors = new LinkedList<>();
    private static final Settings settings = Settings.getInstance();

    public Tile(Integer y, Integer x) {
        this.coordinates = new Coordinates(x, y);
        CARROT_GROWTH_PROBABILITY = Float.parseFloat(settings.getSetting("Carrot", "Grow_probability"));
        CARROT_PLANT_INCREASE = Float.parseFloat(settings.getSetting("Farmer", "Plant_increase"));
        CARROT_REPAIR_INCREASE = Float.parseFloat(settings.getSetting("Farmer", "Repair_increase"));
    }

    public boolean isRWLockedByCurrentThread() {
        return lock.isRWLockedByCurrentThread();
    }

    public Color getBackgroundColor() {
        assert isRWLockedByCurrentThread();

        if (this.state == TileState.EMPTY) {
            return Color.DARK_GRAY;
        } else if (this.state == TileState.DAMAGED) {
            return Color.BLACK;
        } else if (this.state == TileState.CARROT_PLANTED) {
            return new Color(255, 69, 0, 50);
        } else if (this.state == TileState.CARROT_GROWTH_1) {
            return new Color(255, 69, 0, 100);
        } else if (this.state == TileState.CARROT_GROWTH_2) {
            return new Color(255, 69, 0, 150);
        } else if (this.state == TileState.CARROT_MATURE) {
            return new Color(255, 69, 0, 200);
        } else {
            return Color.WHITE;
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

    public boolean canRabbitsEat() {
        assert lock.isRWLockedByCurrentThread();
        return this.state == TileState.CARROT_MATURE || this.state == TileState.CARROT_GROWTH_2 || this.state == TileState.CARROT_GROWTH_1 || this.state == TileState.CARROT_PLANTED || (this.carrot_coverage > 0.0f && this.state == TileState.DAMAGED);
    }

    private boolean isRabbitOnTile() {
        assert lock.isRWLockedByCurrentThread();
        return this.actors.stream().anyMatch(actor -> actor instanceof Rabbit);
    }

    public void tickCarrotGrowth() {
        assert lock.isWriteLockedByCurrentThread();
        if (isRabbitOnTile()) {
            return;
        }
        if (state == TileState.CARROT_PLANTED) {
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
        assert this.state == TileState.EMPTY;
        // tile is locked so we don't have to worry
        //noinspection NonAtomicOperationOnVolatileField
        this.carrot_coverage += CARROT_PLANT_INCREASE;
        if (this.carrot_coverage >= 1.0f) {
            this.carrot_coverage = 1.0f;
            this.state = TileState.CARROT_PLANTED;
        }
    }

    public void repairTile() {
        assert lock.isWriteLockedByCurrentThread();
        assert this.state == TileState.DAMAGED;
        // tile is locked so we don't have to worry
        //noinspection NonAtomicOperationOnVolatileField
        this.carrot_coverage += CARROT_REPAIR_INCREASE;
        if (this.carrot_coverage >= 1.0f) {
            this.carrot_coverage = 1.0f;
            this.state = TileState.CARROT_GROWTH_1;
        }
    }

    public void eatCarrot() {
        assert lock.isWriteLockedByCurrentThread();
        // tile is locked so we don't have to worry
        //noinspection NonAtomicOperationOnVolatileField
        this.carrot_coverage -= 0.2f;
        if (this.carrot_coverage < 0.0f) {
            this.carrot_coverage = 0.0f;
            this.state = TileState.EMPTY;
        } else if (this.carrot_coverage < 0.5) {
            this.state = TileState.DAMAGED;
        }
    }

    public void killRabbitOnTile(Rabbit rabbit) {
        assert lock.isWriteLockedByCurrentThread();
        assert this.actors.contains(rabbit);
        assert rabbit != null;
        assert rabbit.rabbit_mutex.isHeldByCurrentThread();
        rabbit.turnDead();
        // remove the rabbit from the tile
        this.removeActor(rabbit);
    }

    public Rabbit hasRabbit(Rabbit target) {
        assert lock.isRWLockedByCurrentThread();
        for (ActorAbstract actor : this.actors) {
            if (actor == target) {
                return (Rabbit) actor;
            }
        }
        return null;
    }

    public Rabbit hasRabbit() {
        assert lock.isRWLockedByCurrentThread();
        for (ActorAbstract actor : this.actors) {
            if (actor instanceof Rabbit) {
                return (Rabbit) actor;
            }
        }
        return null;
    }
}