package entity;

/**
 * A combo made of two existing menu items. Preparation units are the sum of
 * the two components; the price is 90% of their combined price, rounded to
 * the nearest cent. Example: Salad + Pasta = ($9 + $18) x 0.9 = $24.30, 3 units.
 * <p>
 * The combo is built from (aggregates) two {@link MenuItem}s that stay on the menu.
 */
public class ComboMeal implements MenuOffering {

    private static final int PRICE_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    /**
     * Creates a combo of two menu items.
     *
     * @param first  the first component
     * @param second the second component
     * @throws IllegalArgumentException if either component is {@code null}
     */
    public ComboMeal(MenuItem first, MenuItem second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("A combo needs two menu items");
        }
        this.first = first;
        this.second = second;
    }

    @Override
    public String getName() {
        return first.getName() + " + " + second.getName() + " Combo";
    }

    /**
     * Returns 90% of the two components' combined price, rounded to the nearest cent.
     *
     * @return the combo price in cents
     */
    @Override
    public int getPriceCents() {
        int sum = first.getPriceCents() + second.getPriceCents();
        return (sum * PRICE_PERCENT + 50) / 100;
    }

    /**
     * Returns the sum of the two components' preparation units.
     *
     * @return the number of preparation units
     */
    @Override
    public int getPrepUnits() {
        return first.getPrepUnits() + second.getPrepUnits();
    }

    /**
     * Returns the first component.
     *
     * @return the first menu item
     */
    public MenuItem getFirst() {
        return first;
    }

    /**
     * Returns the second component.
     *
     * @return the second menu item
     */
    public MenuItem getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return getName() + " (" + Money.format(getPriceCents()) + ", " + getPrepUnits() + " units)";
    }
}
