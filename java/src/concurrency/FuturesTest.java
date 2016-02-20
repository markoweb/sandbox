package concurrency;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FuturesTest {

    private DataProvider dataProvider;

    @Before
    public void setUp() {
        dataProvider = new DataProvider();
    }

    @Test
    public void transformTest() {
        ListenableFuture<Integer> future1 = dataProvider.getIntFuture(2);
        AsyncFunction<Integer, Integer> asyncFunction = new AsyncFunction<Integer, Integer>() {
            @Override
            public ListenableFuture<Integer> apply(Integer input) throws Exception {
                return dataProvider.getIntFuture(input * 2);
            }
        };
        ListenableFuture<Integer> future2 = Futures.transform(future1, asyncFunction);
        Futures.addCallback(future2, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                System.out.println("SUCCESS");
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("FAILURE");
                Assert.fail();
            }
        });
        // wait for threads
        try {
            future2.get();
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    @Test
    public void allAsListTest() {
        List<ListenableFuture<Integer>> futures = new ArrayList<ListenableFuture<Integer>>();
        futures.add(dataProvider.getIntFuture(1));
        futures.add(dataProvider.getIntFuture(2));
        futures.add(dataProvider.getIntFuture(3));
        futures.add(dataProvider.getIntFuture(4));
        futures.add(dataProvider.getIntFuture(5, false));

        ListenableFuture<List<Integer>> future = Futures.allAsList(futures);
        Futures.addCallback(future, new FutureCallback<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> result) {
                System.out.println("SUCCESS");
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("FAILURE");
//                Assert.fail("Computation should not fail");
            }
        });
        // wait for threads
        try {
//            future.get();
            Thread.sleep(15000);
        } catch (Exception e) {
//            Assert.fail(e.toString());
        }
    }
}
