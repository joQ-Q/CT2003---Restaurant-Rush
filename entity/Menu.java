package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The restaurant menu. The menu owns its {@link MenuItem}s (composition).
 */
public class Menu {

    private final List<MenuItem> items = new ArrayList<>();
    private final List<ComboMeal> combos = new ArrayList<>();

    /**
     * Creates the base-setting menu: Salad, Burger and Pasta, plus one combo
     * for every pair of different items (Salad + Burger, Salad + Pasta,
     * Burger + Pasta).
     */
    public Menu() {
        items.add(new MenuItem("Salad", 900, 1));
        items.add(new MenuItem("Burger", 1500, 1));
        items.add(new MenuItem("Pasta", 1800, 2));
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                combos.add(new ComboMeal(items.get(i), items.get(j)));
            }
        }

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

       /**
     * Returns the combo meals in a read-only list.
     *
     * @return all combos
     */
    public List<ComboMeal> getCombos() {
        return Collections.unmodifiableList(combos);
    }

    /**
     * Returns everything a customer can order: the single items first,
     * then the combos, in menu order.
     *
     * @return all offerings
     */
    public List<MenuOffering> getOfferings() {
        List<MenuOffering> all = new ArrayList<>(items);
        all.addAll(combos);
        return all;
    }
}