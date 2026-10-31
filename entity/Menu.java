package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The restaurant menu. The menu owns its {@link MenuItem}s (composition).
 */
public class Menu {

    private final List<MenuItem> items = new ArrayList<>();

    /**
     * Creates the base-setting menu: Salad, Burger and Pasta.
     */
    public Menu() {
        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));
    }

    /**
     * Returns the menu items in a read-only list.
     *
     * @return all menu items
     */
    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the item at a position in the menu.
     *
     * @param index zero-based position
     * @return the menu item at that position
     */
    public MenuItem getItem(int index) {
        return items.get(index);
    }
}