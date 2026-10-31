package entity;

/**
 * A single dish on the menu. Menu items are immutable once created.
 */
public class MenuItem {

    private final String name;
    private final int priceCents;
    private final int prepUnits;

    /**
     * Creates a menu item.
     *
     * @param name       display name of the dish
     * @param priceCents price in cents
     * @param prepUnits  number of Chef preparation units needed (at least 1)
     * @throws IllegalArgumentException if the price is negative or prepUnits is below 1
     */
    public MenuItem(String name, int priceCents, int prepUnits) {
        if (priceCents < 0 || prepUnits < 1) {
            throw new IllegalArgumentException("Invalid menu item: " + name);
        }
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    /**
     * Returns the dish name.
     *
     * @return the dish name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the price in cents.
     *
     * @return the price in cents
     */
    public int getPriceCents() {
        return priceCents;
    }

    /**
     * Returns the number of preparation units the Chef must complete.
     *
     * @return the number of preparation units the Chef must complete
     */
    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public String toString() {
        return name + " (" + Money.format(priceCents) + ", " + prepUnits + " unit"
                + (prepUnits > 1 ? "s" : "") + ")";
    }
}