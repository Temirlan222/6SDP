package edu.aitu.energy.runtime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ProducerRunner implements AutoCloseable {
    private final ExecutorService executor;
    public ProducerRunner(ExecutorService executor) { this.executor = executor; }

    public void run(List<Runnable> producers) throws InterruptedException, ExecutionException {
        List<Future<?>> futures = new ArrayList<>();
        for (Runnable producer : producers) futures.add(executor.submit(producer));
        ExecutionException failure = null;
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException error) {
                if (failure == null) failure = error;
                else failure.addSuppressed(error);
            }
        }
        if (failure != null) throw failure;
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                executor.awaitTermination(5, TimeUnit.SECONDS);
                throw new IllegalStateException("Producer shutdown timed out; remaining tasks cancelled");
            }
        } catch (InterruptedException interrupted) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Shutdown interrupted", interrupted);
        }
    }
}
