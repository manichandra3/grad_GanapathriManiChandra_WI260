import java.util.concurrent.*;

public class RaceEngine {
    private final int numBikes;
    private final int trackLength;
    private final ExecutorService executor;
    private final CompletionService<RaceResult> completionService;
    private final CountDownLatch startingPistol =  new CountDownLatch(1);

    public RaceEngine(int numBikes, int trackLength) {
        this.numBikes = numBikes;
        this.trackLength = trackLength;
        this.executor = Executors.newFixedThreadPool(numBikes);
        this.completionService = new ExecutorCompletionService<>(executor);
    }

    public void startRace() {
        System.out.println("Starting Race with " + numBikes + " bikes...");

        for(int i = 0; i < numBikes; i++) {
            completionService.submit(new Bike("Rider-" + i, trackLength, startingPistol));
        }
        System.out.println("All riders at the Gate 3... 2... 1.... GO!");
        startingPistol.countDown();

        processFinishLine();

        executor.shutdown();
    }

    private void processFinishLine() {
        try {
            for (int i = 1; i <= numBikes; i++) {
                Future<RaceResult> finishedBike = completionService.take();
                RaceResult result = finishedBike.get();
                System.out.println("---------------------------------------------");
                System.out.printf("RANK %d: %s | Time: %dms | Max Speed: %d m/s%n",
                        i, result.bikeName(), result.durationMs(), result.topSpeed());
                System.out.println("---------------------------------------------");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RaceEngine(5, 100).startRace();
    }
}