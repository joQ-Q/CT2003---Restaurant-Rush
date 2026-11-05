package entity;

/**
 * A food critic: impatient (loses 12 satisfaction per waiting turn) and
 * pays full price.
 * <p>
 * Documented special behaviour (order preference): a critic always orders
 * the most expensive dish on the menu, ignoring the regular choice rule.
 * In the base menu that is Pasta, the dish that takes longest to cook, so
 * the manager must give the critic's order kitchen priority.
 */
public class CriticCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 12;

    /**
     * Creates a critic.
     *
     * @param id          customer number
     * @param arrivalTurn the turn the critic arrived
     */
    public CriticCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }

    /**
     * Chooses the most expensive dish. If two dishes cost the same, the
     * one listed first on the menu is chosen.
     *
     * @param menu the restaurant menu
     * @return the most expensive dish
     */
    @Override
    public MenuOffering chooseOffering(Menu menu) {
        MenuItem best = menu.getItem(0);
        for (MenuItem item : menu.getItems()) {
            if (item.getPriceCents() > best.getPriceCents()) {
                best = item;
            }
        }
        return best;
    }

    @Override
    public String getTypeLabel() {
        return "Critic";
    }
}
