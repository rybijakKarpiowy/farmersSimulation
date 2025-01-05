import java.util.List;

public abstract class ActorAbstract extends ThreadAbstract {
    protected Coordinates coordinates;
    protected Field field;
    protected String type;

    public ActorAbstract(Field field, String type) {
        this.field = field;
        this.type = type;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getType() {
        return type;
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
