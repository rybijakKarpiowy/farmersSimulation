import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashSet;

public class ExtendedReentrantReadWriteLock extends ReentrantReadWriteLock {
    private final HashSet<Thread> readerThreads = new HashSet<Thread>();

    public ExtendedReentrantReadWriteLock(boolean fair) {
        super(fair);
    }

    @Override
    public ReadLock readLock() {
        return new ExtendedReadLock(this);
    }

    public boolean isReadLockedByCurrentThread() {
        return readerThreads.contains(Thread.currentThread());
    }

    public class ExtendedReadLock extends ReadLock {
        ExtendedReadLock(ReentrantReadWriteLock lock) {
            super(lock);
        }

        @Override
        public void lock() {
            super.lock();
            readerThreads.add(Thread.currentThread());
        }

        @Override
        public void unlock() {
            super.unlock();
            readerThreads.remove(Thread.currentThread());
        }
    }
}
