import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.units.qual.s;
import org.junit.Test;
import org.omg.CORBA.ServerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link java.util.function.Function}
 */
public class LambdaTest {


    private static class NormalFunction {
        private void consume(Function<Integer, String> lambdaFunc) {
            System.err.println(lambdaFunc.apply(1));
        }
    }

    @Test
    public void testNormal() {

        // Stream
        ImmutableList<String> list = ImmutableList.of("H", "L");

        List<String> afterCollectList = list.stream()
                // Predicate
                .filter((s) -> s.startsWith("H"))
                // Transform  mapper
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        System.err.println(afterCollectList);


        List<String> jdk7List = new ArrayList<>();
        for (String s : list) {
            if (s.startsWith("H")) {
                jdk7List.add(s.toLowerCase());
            }
        }
        System.err.println(jdk7List);

        // NormalFunction
        ImmutableList<String> lambdaList = ImmutableList.of("Hello");

        List<String> strings = lambdaList.stream()
                .map(string -> {
                    System.err.println(string);
                    return string.toLowerCase();
                }).collect(Collectors.toList());

    }

    @Test
    public void testTable() {
        List<User> users = new ArrayList<>();
        users.add(new User("jack", 20));
        users.add(new User("maple", 22));
        users.add(new User("overman", 20));

        // 1 select * from users where age > 20 (Filter)
        List<User> gt20Users = users.stream().filter(u -> u.age > 21).collect(Collectors.toList());

        // 2 Group by
        Map<Integer, Long> groupByAgeCounting = users.stream().collect(Collectors.groupingBy(u -> u.getAge(), Collectors.counting()));

        // 3 join
        System.err.println(gt20Users);
        System.err.println(groupByAgeCounting);

        // To Map
        Map<String, User> toMapUsers = users.stream().collect(Collectors.toMap(User::getName, Function.identity()));
        System.err.println(toMapUsers);

        // Guava
        FluentIterable<User> afterTransformUsers = FluentIterable.<User>from(users)
                .transform(t -> {
                    t.setName(t.getName().toUpperCase());
                    return t;
                });

        System.err.println(afterTransformUsers);

    }

    // MySQL Table

    private static class User {

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    private static class Version implements Comparable<Version>{
        private int v;

        public Version(int v) {
            this.v = v;
        }

        @Override
        public int compareTo(Version o) {
            return 0;
        }
    }

    public static void main(String[] args) {
        Version v1 = new Version(1);
        Version v2 = new Version(2);

        // GT 1
        // EQ 0
        // LT -1
        System.err.println(v1.compareTo(v2));
    }
}
