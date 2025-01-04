public abstract class ThreadAbstract extends Thread {
    private final Integer threadId;
    protected boolean running = true;

    public ThreadAbstract() {
        this.threadId = this.hashCode();
        this.start();
    }

    public void run() {
        while (running) {
            try {
                sleep(200);
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
