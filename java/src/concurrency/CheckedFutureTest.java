package concurrency;

import com.google.common.base.Function;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class CheckedFutureTest {

    private static DataProvider dataProvider;

    @BeforeClass
    public static void setUp() {
        dataProvider = new DataProvider();
        dataProvider.setDebug(true);
    }

    @Test
    public void createTest() throws Exception {
        ListenableFuture<Integer> listenableFuture = dataProvider.getIntFuture(1, false);
        CheckedFuture<Integer, Exception> checkedFuture =
                Futures.makeChecked(listenableFuture, new Function<Exception, Exception>() {
            @Override
            public Exception apply(Exception input) {
                MyExecutionException exception = new MyExecutionException();
                exception.initCause(input);
                return exception;
            }
        });
        checkedFuture.checkedGet();
    }

    class MyExecutionException extends ExecutionException {}
}
