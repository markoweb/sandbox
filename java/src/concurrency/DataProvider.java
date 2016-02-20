package concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataProvider {

    private static final int THREADS = 10;
    private final ListeningExecutorService executor;

    public DataProvider() {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        this.executor = MoreExecutors.listeningDecorator(pool);
    }

    public int getInt(int input) {
        return ++input;
    }

    public ListenableFuture<Integer> getIntFuture(final int input) {
        return executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return getInt(input);
            }
        });
    }
}
