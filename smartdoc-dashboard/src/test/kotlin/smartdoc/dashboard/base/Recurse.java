package smartdoc.dashboard.base;

public class Recurse {

    public static void main(String[] args) {
        System.err.println(new Recurse().recurse(5));
    }

    // 5+4+3+2+1
    private long recurse(int n) {
        if (n <= 0) return n;
        return n + recurse(n - 1);
    }
}
