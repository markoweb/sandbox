package concurrency;

import com.google.common.util.concurrent.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CreateFutureTest {

    private static DataProvider dataProvider;

    @BeforeClass
    public static void setUp() {
        dataProvider = new DataProvider();
        dataProvider.setDebug(true);
    }

    /**
     * Creates a {@link ExecutorService} with a thread pool,
     * than converts it to a {@link ListeningExecutorService},
     * than creates a {@link Callable} computation and sends it to that service
     * @throws Exception
     */
    @Test
    public void listenableFutureServiceTest() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(executorService);
        ListenableFuture<Integer> future = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return dataProvider.getInt(1);
            }
        });
        future.get();
    }

    /**
     * Creates a {@link ListenableFutureTask} that will upon running, execute the given Callable.
     * @throws Exception
     */
    @Test
    public void listenableFutureTaskTest() throws Exception {
        ListenableFutureTask<Integer> futureTask = ListenableFutureTask.create(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return dataProvider.getInt(1);
            }
        });
        futureTask.run();
        futureTask.get();
    }

    /**
     * Set the value of the future
     * @throws Exception
     */
    @Test
    public void settableFutureTest() throws Exception {
        final SettableFuture<Integer> future = SettableFuture.create();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int result = dataProvider.getInt(1);
                    future.set(result);
                } catch (InterruptedException e) {
                    future.setException(e);
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        // wait for thread
        System.out.println("Waiting for results in thread: " + Thread.currentThread().getName());
        future.get();
    }

    public void abstractFutureTest() throws Exception {
        throw new UnsupportedOperationException();
    }

    @Test
    public void convertFutureTest() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return dataProvider.getInt(1);
            }
        });
        ListenableFuture<Integer> listenableFuture = JdkFutureAdapters.listenInPoolThread(future);
        Futures.addCallback(listenableFuture, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("SUCCESS");
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("FAILURE");
                Assert.fail(t.toString());
            }
        });
        // wait for thread
        System.out.println("Waiting for results in thread: " + Thread.currentThread().getName());
        listenableFuture.get();
    }
}
