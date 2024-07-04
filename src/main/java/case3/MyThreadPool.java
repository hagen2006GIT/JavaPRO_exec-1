package case3;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool {
    private final ThreadWork[] poolThread; // массив исполняемых потоков
    private final LinkedList<Runnable> innerQueue;
    volatile private boolean workingPool;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition termination = mainLock.newCondition();


    public MyThreadPool(int nThreads) {
        innerQueue = new LinkedList<Runnable>();
        poolThread = new ThreadWork[nThreads];
        workingPool = true;
        System.out.println("MyThreadPool start on " + nThreads + " threads");
        for (int i = 0; i < nThreads; i++) {
            poolThread[i] = new ThreadWork();
            poolThread[i].start();
        }
    }

    public void execute(Runnable runningTask) {
        if (workingPool) {
            synchronized (innerQueue) {
                innerQueue.addLast(runningTask);
                System.out.println("TASK put in InnerQueue (" +
                        "queue.size = " + innerQueue.size() + ")");
                innerQueue.notifyAll();
            }
        } else {
            throw new IllegalStateException("All threads is interrupted. New task cancelled");
        }
    }

    public void getStatesOfThreadPool() {
        for (ThreadWork worker : poolThread) {
            System.out.println(worker.getName() + ", state = " + worker.getState());
        }
    }

    public void shutdown() {
        for (ThreadWork worker : poolThread) {
            worker.interrupt();
        }
        workingPool = false;
    }

    private boolean allThreadInterrupted() {
        for (ThreadWork worker : poolThread) {
            if (!worker.isInterrupted()) {
                return false;
            }
        }
        return true;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            while (!allThreadInterrupted()) {
                if (nanos <= 0L)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
            return true;
        } finally {
            mainLock.unlock();
        }
    }

    private class ThreadWork extends Thread {
        @Override
        public void run() {
            Optional<Runnable> task;
            while (!isInterrupted()) {
                synchronized (innerQueue) {
                    while (innerQueue.isEmpty() && !isInterrupted()) {
                        try {
                            System.out.println(getName() + ". Waiting new task...");
                            innerQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    try {
                        task = Optional.of(innerQueue.removeFirst());
                    } catch (NoSuchElementException e) {
                        task = Optional.empty();
                    }
                }
                if (task.isPresent()) {
                    System.out.println("TASK take from InnerQueue & try RUNNING in " + getName());
                    task.get().run();
                    System.out.println("TASK finished in " + getName());
                }
            }
        }
    }
}