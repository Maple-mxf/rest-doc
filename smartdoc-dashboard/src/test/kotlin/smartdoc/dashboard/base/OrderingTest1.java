package smartdoc.dashboard.base;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.RateLimiter;
import org.jetbrains.annotations.NotNull;

import java.beans.Transient;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class OrderingTest1 {
    RateLimiter rateLimiter = RateLimiter.create(1);

    //
//    Pageable
    private static abstract class Task implements Comparable<Task> {
        @Override
        public abstract int compareTo(@NotNull Task o);
    }

    final transient int a = 1;

    private static class WalkTask extends Task {

        @Override
        public int compareTo(@NotNull Task o) {
            return 1;
        }
    }

    private static class EatTask extends Task {
        @Override
        public int compareTo(@NotNull Task o) {

            return -1;
        }
    }

    @Transient
    public static void main(String[] args) throws ExecutionException, InterruptedException {
       /* Pattern pattern = Pattern.compile("^[\\d](1,)[K]+$");
        Matcher matcher = pattern.matcher("1KKK");
        String kk = matcher.group();


        System.err.println(kk);*/

        Integer[] array = ImmutableList.<Integer>of(1, 2, 4).toArray(  new Integer[0]);

        IntStream.range(0,array.length).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                System.err.println(array[value]);
            }
        });
    }

    public void limit() {
        for (; ; ) {
            if (rateLimiter.tryAcquire()) {
                System.err.println(new Date().getTime());
                break;
            }
        }
//        System.err.println(new Date().getTime());
    }
}
