package concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProvider {

    private static final int THREADS = 10;
    private static final int COMPUTATION_DELAY = 3000;
    private final ListeningExecutorService executor;
    private boolean debug = false;

    public DataProvider() {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        this.executor = MoreExecutors.listeningDecorator(pool);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getInt(int input) throws InterruptedException {
        if (debug)
            System.out.println("Start computation, thread: " + Thread.currentThread().getName());
        Thread.sleep(COMPUTATION_DELAY);
        if (debug)
            System.out.println("Finish computation, thread: " + Thread.currentThread().getName());
        return ++input;
    }

    public ListenableFuture<Integer> getIntFuture(final int input) {
        return getIntFuture(input, true);
    }

    public ListenableFuture<Integer> getIntFuture(final int input, final boolean success) {
        if (debug)
            System.out.println("Init computation, thread: " + Thread.currentThread().getName());
        return executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                if (! success)
                    throw new Exception("Unsuccessful computation");
                return getInt(input);
            }
        });
    }
}
