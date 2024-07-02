package case3;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyThreadPool
{
    private final ThreadWork[] poolThread; // массив исполняемых потоков
    private final LinkedList<Runnable> InnerQueue;
    private boolean workingPool;
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition termination = mainLock.newCondition();


    public MyThreadPool(int nThreads) {
        InnerQueue=new LinkedList<Runnable>();
        poolThread=new ThreadWork[nThreads];
        workingPool=true;
        System.out.println("MyThreadPool start on "+ nThreads +" threads");
        for (int i=0; i<nThreads; i++) {
            poolThread[i]=new ThreadWork();
            poolThread[i].start();
        }
    }

    public void execute(Runnable r) {
        if(workingPool) {
            synchronized (InnerQueue) {
                InnerQueue.addLast(r);
                System.out.println("TASK put in InnerQueue (" +
                        "queue.size = " + InnerQueue.size() + ")");
                InnerQueue.notify();
            }
        } else {
            throw new IllegalStateException("All threads is interrupted. New task cancelled");
        }
    }

    public void shutdown() {
        for (ThreadWork worker : poolThread) {
            worker.interrupt();
        }
        workingPool=false;
    }

    private boolean allThreadWaiting() {
        for (ThreadWork worker : poolThread) {
            if (worker.isAlive()) {
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
            while (!allThreadWaiting()) {
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
        @Override public void run() {
            Runnable task;
            while (!isInterrupted()) {
                synchronized(InnerQueue) {
                    while (InnerQueue.isEmpty() && !isInterrupted()) {
                        try {
                            System.out.println(getName()+". Waiting new task...");
                            InnerQueue.wait();
                        } catch (InterruptedException e) {
                            System.out.println(getName()+" is interrupted. New task will be cancelled");
                            interrupt();
                        }
                    }
                    try {
                        task=InnerQueue.removeFirst();
                    } catch (NoSuchElementException e) {
                        task=null;
                    }
                }
                if(task!=null) {
                    System.out.println("TASK take from InnerQueue & try RUNNING in "+getName());
                    task.run();
                    System.out.println("TASK finished in "+getName());
                }
            }
        }
    }
}