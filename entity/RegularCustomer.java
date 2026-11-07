package entity;

/**
 * A regular customer: loses 8 satisfaction per waiting turn and pays full
 * price.
 */
public class RegularCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 8;

    /**
     * Creates a regular customer.
     *
     * @param id          the unique customer id
     * @param arrivalTurn the turn on which the customer arrives
     */
    public RegularCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    /**
     * Returns the satisfaction a regular customer loses per waiting turn.
     *
     * @return 8 points
     */
    @Override
    public int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }

    /**
     * Returns the label shown to the player for this customer type.
     *
     * @return {@code "Regular"}
     */
    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
