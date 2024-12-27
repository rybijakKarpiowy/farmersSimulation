import java.util.LinkedList;
import java.util.List;

public class SimulationManager {
    List<ActorAbstract> actors;
    Field field;

    public SimulationManager(int fieldSize, int farmerCount, double carrotGrowthProbability, double rabbitSpawnProbability, boolean shouldContinue) {
        if (shouldContinue) {
            continueSimulation();
        } else {
            field = new Field(fieldSize, carrotGrowthProbability);
            field.field_rw_lock.lock();
            actors = new LinkedList<ActorAbstract>();

            for (int i = 0; i < farmerCount; i++) {
                Farmer farmer = new Farmer();
                Dog dog = new Dog();
                farmer.setDog(dog);
                dog.setFarmer(farmer);

                field.addToTile(farmer);
            }
        }
    }

    void continueSimulation() {
        throw new UnsupportedOperationException("Not implemented");
    }

    void saveSimulation() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Field getField() {
        return field;
    }

    void updateRabbits() {
        throw new UnsupportedOperationException("Not implemented");
    }

    void spawnRabbit() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void exitSimulation() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void init() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
