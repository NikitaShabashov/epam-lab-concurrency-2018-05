package lesson_2018_05_22;

import lombok.SneakyThrows;
import lombok.Value;

import java.util.concurrent.*;

public class Example8 {

    @Value
    private static class Box implements Comparable<Box> {
        String destination;
        int priority;

        @Override
        public int compareTo(Box o) {
            return Integer.compare(priority, o.priority);
        }
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);

        BlockingQueue<Box> desk = new PriorityBlockingQueue<>();
        int numBoxes = 10;

        Runnable operator = new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                for (int i = 0; i < numBoxes; ++i) {
                    System.out.println("Prepare box with number - " + i);
                    TimeUnit.SECONDS.sleep(1);
                    Box box = new Box(String.valueOf(i), numBoxes - i);

                    System.out.println("Trying put " + i + " on the desk");
                    desk.put(box);
                    System.out.println("Sucessfully put " + i + " on the desk");
                }
            }
        };

        Runnable courier = new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                for (int i = 0; i < numBoxes; ++i) {
                    System.err.println("Trying take box from the desk");
                    Box box = desk.take();
                    System.err.println("Took the " + box + ", going to deliver it!");
                    TimeUnit.SECONDS.sleep(5);
                }
            }
        };

        service.execute(operator);
        service.execute(courier);
        service.shutdown();
    }
}