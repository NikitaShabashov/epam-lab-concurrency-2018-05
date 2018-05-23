package lesson_2018_05_22.stack;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


public class BlockingStackTest {
    private final BlockingStack<String> stringBlockingStack = new BlockingStack<>(5, true);
    private final AtomicInteger counter = new AtomicInteger(0);

    @Test
    public void stackTest() throws Exception {
        ExecutorService executorPusher = Executors.newFixedThreadPool(1);
        ExecutorService executorGeter = Executors.newFixedThreadPool(1);

        Runnable pusher = () -> {
            try {
                Thread.sleep(100);
                int i = counter.getAndIncrement();
                System.out.println("Try push num-" + i);
                stringBlockingStack.push("num-" + i);
                System.out.println("Success pushed num-" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Runnable getter = () -> {
            try {
                Thread.sleep(1000);
                System.out.println("Try pop");
                System.out.println("success pop-{" + stringBlockingStack.pop() + "}");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        for (int i = 0; i < 10; i++) {
            executorPusher.execute(pusher);
            executorGeter.execute(getter);
        }
        System.out.println("First pusher and getter loaded");
        Thread.sleep(10000);
        executorPusher = Executors.newFixedThreadPool(2);
        executorGeter = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorGeter.execute(getter);
        }
        System.out.println("Second getter loaded");
        Thread.sleep(5000);
        for (int i = 0; i < 5; i++) {
            executorPusher.execute(pusher);
        }
        System.out.println("Second pusher loaded");
        Thread.sleep(10000);
    }


}