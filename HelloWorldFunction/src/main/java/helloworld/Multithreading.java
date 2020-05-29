package helloworld;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Multithreading {

    EventList<TestObject> rows = GlazedLists.threadSafeList(new BasicEventList<>(1000));

    public Multithreading() {
    }
    public EventList getRows() {
        return rows;
    }

    public void setRows(int i, TestObject x) {
        try {
            System.out.println(x.getCount() + " " + i + " " + Thread.currentThread().getName());
            this.rows.getReadWriteLock().writeLock().lock();
            this.rows.set(i, x);
        } catch (Exception e) {
            System.out.println(e + Thread.currentThread().getName());
        } finally {
            this.rows.getReadWriteLock().writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Multithreading multithreading = new Multithreading();

        int threadcount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadcount);
        AtomicInteger counter = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadcount);
        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        for(int x = 0; x < 1000; x++ ) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            Future<Boolean> future = executorService.submit(() -> {
                countDownLatch1.await();
                multithreading.setRows(counter.getAndIncrement(), new TestObject(atomicInteger.incrementAndGet()));
                countDownLatch.countDown();;
                return true;
            });
        }

        countDownLatch1.countDown();;
        countDownLatch.await();
        System.out.println(multithreading.getRows());
    }
}
