public abstract class ThreadAbstract extends Thread {
    private final Integer threadId;
    protected boolean running = true;
    static private final int tickInterval = 500;

    public ThreadAbstract() {
        this.threadId = this.hashCode();
        this.start();
    }

    public ThreadAbstract(Tile tile) {
        assert tile.lock.isWriteLocked();
        assert this instanceof ActorAbstract;
        tile.addActor((ActorAbstract) this);
        this.threadId = this.hashCode();
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

    public Integer getThreadId() {
        return threadId;
    }
}
