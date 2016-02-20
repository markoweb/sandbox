package concurrency;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class SuccessfulnessTest {

    private DataProvider dataProvider;

    @Before
    public void setUp() {
        this.dataProvider = new DataProvider();
    }

    @Test
    public void successTest() {
        ListenableFuture<Integer> future = dataProvider.getIntFuture(1, true);
        Futures.addCallback(future, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("SUCCESS");
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("FAILURE");
                Assert.fail("Computation should not fail");
            }
        });
        // wait for threads
        try {
            future.get();
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    @Test(expected = ExecutionException.class)
    public void failTest() throws Exception {
        ListenableFuture<Integer> future = dataProvider.getIntFuture(1, false);
        Futures.addCallback(future, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("SUCCESS");
                Assert.fail("Computation should fail");
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("FAILURE");
            }
        });
        // wait for threads
        future.get();
    }
}