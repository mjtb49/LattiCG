package randomreverser.math.lattice;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread pool implemented specifically to support Enumerate. This handles all the forking / joining / recursion so
 * Enumerate doesn't have to.
 */
class EnumeratePool {
    private final Thread[] threads;
    private final Lock lock;
    private final Condition finished;
    private final Condition waiting;

    private volatile Queue<Enumerate.SearchInfo> queue;
    private volatile Throwable thrown;
    private volatile int active;
    private volatile boolean shutdown;

    public EnumeratePool(int threadCount) {
        this.threads = new Thread[threadCount];
        this.lock = new ReentrantLock();
        this.finished = this.lock.newCondition();
        this.waiting = this.lock.newCondition();
        this.queue = new LinkedList<>();
        this.thrown = null;
        this.active = 0;
        this.shutdown = false;

        for (int i = 0; i < threadCount; ++i) {
            this.threads[i] = new Thread(this::run);
            this.threads[i].start();
        }
    }

    private void run() {
        try {
            while (true) {
                this.lock.lock();

                while (this.queue.isEmpty()) {
                    if (this.shutdown) {
                        this.lock.unlock();
                        return;
                    }

                    this.waiting.awaitUninterruptibly();
                }

                Enumerate.SearchInfo info = this.queue.poll();

                this.lock.unlock();

                Enumerate.search(info);

                this.lock.lock();
                this.active -= 1;
                this.finished.signalAll();
                this.lock.unlock();
            }
        } catch (Throwable thrown) {
            this.lock.lock();
            this.thrown = thrown;
            this.finished.signal();
            this.lock.unlock();
        }
    }

    public void search(Enumerate.SearchInfo info) {
        this.lock.lock();

        if (this.active < this.threads.length) {
            this.queue.offer(info.copy());
            this.active += 1;
            this.waiting.signal();
            this.lock.unlock();
        } else {
            this.lock.unlock();
            Enumerate.search(info);
        }
    }

    public void start(Enumerate.SearchInfo root) {
        this.search(root);
        this.lock.lock();

        while (this.active > 0 && this.thrown == null) {
            this.finished.awaitUninterruptibly();
        }

        this.shutdown = true;
        this.waiting.signalAll();
        this.lock.unlock();

        if (this.thrown != null) {
            throw new RuntimeException(this.thrown);
        }
    }
}