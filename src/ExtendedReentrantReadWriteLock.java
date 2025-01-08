import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashSet;

public class ExtendedReentrantReadWriteLock extends ReentrantReadWriteLock {
    private final HashSet<Thread> readerThreads = new HashSet<Thread>();
    private final ReentrantLock lock = new ReentrantLock();

    public ExtendedReentrantReadWriteLock(boolean fair) {
        super(fair);
    }

    @Override
    public ReadLock readLock() {
        return new ExtendedReadLock(this);
    }

    public boolean isReadLockedByCurrentThread() {
        lock.lock();
        boolean out = readerThreads.contains(Thread.currentThread());
        lock.unlock();
        return out;
    }

    public boolean isRWLockedByCurrentThread() {
        return isReadLockedByCurrentThread() || isWriteLockedByCurrentThread() || Objects.equals(Thread.currentThread().getName(), "AWT-EventQueue-0");
    }

    public class ExtendedReadLock extends ReadLock {
        ExtendedReadLock(ReentrantReadWriteLock lock) {
            super(lock);
        }

        @Override
        public void lock() {
            super.lock();
            lock.lock();
            readerThreads.add(Thread.currentThread());
            lock.unlock();
        }

        @Override
        public void unlock() {
            lock.lock();
            readerThreads.remove(Thread.currentThread());
            lock.unlock();
            super.unlock();
        }
    }
}
