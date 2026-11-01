package restaurantrush.entity;

public final class Money {

    private Money() {
        // utility class, not instantiated
    }

    public static String format(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}