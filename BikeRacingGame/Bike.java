import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Bike implements Callable<RaceResult> {
    private final String name;
    private final int trackLength;
    private final CountDownLatch startSignal;

    public Bike(String name, int trackLength, CountDownLatch startSignal) {
        this.name = name;
        this.trackLength = trackLength;
        this.startSignal = startSignal;
    }

    @Override
    public RaceResult call() throws Exception {
        // 1. Wait for the start sig
        startSignal.await();

        long startTime = System.currentTimeMillis();
        int distanceCovered = 0;
        int maxSpeed = 0;

        // 2. Race Loop
        while(distanceCovered < trackLength) {
            int currentSpeed = ThreadLocalRandom.current().nextInt(10, 25);
            maxSpeed = Math.max(maxSpeed, currentSpeed);
            distanceCovered += currentSpeed;

            System.out.printf("[%s] >>> %d/%d meters%n", name, Math.min(distanceCovered, trackLength), trackLength);

            // Delay (Time passes)
            Thread.sleep(ThreadLocalRandom.current().nextInt(150, 400));
        }

        long endTime = System.currentTimeMillis();
        return new RaceResult(name, (endTime - startTime), maxSpeed);
    }
}