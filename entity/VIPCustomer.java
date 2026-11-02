package restaurantrush.entity;

/**
 * A VIP customer: more patient than a regular (loses 5 satisfaction per
 * waiting turn) and gets 10% off the bill.
 */
public class VIPCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 5;
    private static final int DISCOUNT_PERCENT = 10;

    /**
     * Creates a VIP customer.
     *
     * @param id          customer number
     * @param arrivalTurn the turn the customer arrived
     */
    public VIPCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }

    /**
     * Applies the 10% VIP discount, rounded to the nearest cent.
     * For example, a $15.00 Burger costs $13.50.
     *
     * @param basePriceCents the menu price in cents
     * @return the discounted bill in cents
     */
    @Override
    public int calculateBill(int basePriceCents) {
        return (basePriceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    @Override
    public String getTypeLabel() {
        return "VIP";
    }
}
