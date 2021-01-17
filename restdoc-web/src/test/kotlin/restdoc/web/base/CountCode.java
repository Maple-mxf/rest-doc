package restdoc.web.base;


import com.google.common.collect.ImmutableList;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * @see File
 */
public class CountCode {

    private static long countResult = 0;

    /**
     * @param filePath 代码文件夹
     */
    private static void countNumber(String filePath) throws IOException {

        // 1  new成文件
        File file = new File(filePath);
        // 2
        if (!file.isDirectory()) {

            // 如果是Java文件  则统计行数   如果是Class文件  则不读取（字节码文件无法读取）
            if (file.getName().endsWith(".java") || file.getName().endsWith(".kt") || file.getName().endsWith(".html")) {

                // 读取所有的内容
                List<String> result = Files.readAllLines(file.toPath());

                for (String s : result) {
                    if (!("".equals(s) || s == null)) {
                        countResult++;
                    }
                }
            }
        } else {
            //
            File[] files = file.listFiles();

            //
            assert files != null;

            for (File var : files) {
                countNumber(var.getAbsolutePath());
            }
        }
    }

    //
    public static void main(String[] args) throws IOException {
       /* countNumber("E:/jw/rest-doc");
        System.err.println(countResult);
        // Fluent
        FluentIterable<String> iterable = FluentIterable.<String>of("1", "2", "3");

        FluentIterable<String> collect0 = iterable.transform(t -> t.toLowerCase());

        Set<String> collect = iterable.stream()
                .map((t) -> t.toLowerCase())
                .peek((t) -> System.err.println(t))
                .collect(Collectors.toSet());

        Supplier<String> supplier = () -> "A";*/

        // Source
        Flux.fromIterable(ImmutableList.<Integer>of(1, 2))

                // Sink
                .doOnNext(System.err::println)
                .map(d -> d * 2)
                .take(3)
                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return (Publisher<Integer>) subscriber -> {
                    };
                })
                .subscribe(System.out::println);

    }
}
