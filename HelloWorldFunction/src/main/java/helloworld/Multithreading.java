package helloworld;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Multithreading {

    EventList rows = GlazedLists.threadSafeList(new BasicEventList());

    public Multithreading() {
        rows.add(new Object());
        rows.add(new Object());
        rows.add(new Object());
    }
    public EventList getRows() {
        return rows;
    }

    public void setRows(int i, TestObject x) {
        try {
            System.out.println(x.getCount());
            this.rows.getReadWriteLock().writeLock().lock();
            this.rows.set(i, x);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            this.rows.getReadWriteLock().writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Multithreading multithreading = new Multithreading();

        int threadcount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadcount);
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadcount);
        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        Future<Boolean> future = executorService.submit(() -> {
            countDownLatch1.await();
            multithreading.setRows(counter.getAndIncrement(), new TestObject(counter.get()));
            countDownLatch.countDown();;
            return true;
        });

        Future<Boolean> future2 = executorService.submit(() -> {
            countDownLatch1.await();
            multithreading.setRows(counter.getAndIncrement(), new TestObject(counter.get()));
            countDownLatch.countDown();;
            return true;
        });

        Future<Boolean> future3 = executorService.submit(() -> {
            countDownLatch1.await();
            multithreading.setRows(counter.getAndIncrement(), new TestObject(counter.get()));
            countDownLatch.countDown();;
            return true;
        });

        countDownLatch1.countDown();;
        countDownLatch.await();
        System.out.println(multithreading.getRows());
    }
}
