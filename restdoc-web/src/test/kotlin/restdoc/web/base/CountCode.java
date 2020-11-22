package restdoc.web.base;


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
            if (file.getName().endsWith(".java") || file.getName().endsWith(".kt")) {

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

    public static void main(String[] args) throws IOException {
        countNumber("E:/jw/rest-doc");
        System.err.println(countResult);
    }
}
