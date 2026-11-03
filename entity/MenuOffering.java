package entity;

/**
 * Anything a customer can order: a single {@link MenuItem} or a
 * {@link ComboMeal}. Orders, the Chef and the Waiter work with this
 * interface, so a combo is cooked, served and billed like any dish.
 */
public interface MenuOffering {

    /**
     * Returns the name shown to the player.
     *
     * @return the display name
     */
    String getName();

    /**
     * Returns the menu price before any discount.
     *
     * @return the price in cents
     */
    int getPriceCents();

    /**
     * Returns how many Chef preparation units this offering needs.
     *
     * @return the number of preparation units
     */
    int getPrepUnits();
}
