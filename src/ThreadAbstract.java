public abstract class ThreadAbstract extends Thread {
    protected volatile boolean running = true;
    static private final int tickInterval = Integer.parseInt(Settings.getInstance().getSetting("Thread", "Tick_interval"));

    public ThreadAbstract() {
        this.start();
    }

    public ThreadAbstract(Tile tile) {
        assert tile.lock.isWriteLocked();
        assert this instanceof ActorAbstract;
        tile.addActor((ActorAbstract) this);
        this.start();
    }

    public void run() {
        // wait a random amount to make the simulation more interesting
        try {
            Thread.sleep((long) (Math.random() * tickInterval));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while (running) {
            try {
                Thread.sleep(tickInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tick();
        }
    }

    public abstract void tick();
}
