package restaurantrush.entity;

// A regular customer: loses 8 satisfaction per waiting turn and pays full price.
public class RegularCustomer extends Customer {

    private static final int SATISFACTION_LOSS = 8;

    public RegularCustomer(int id, int arrivalTurn) {
        super(id, arrivalTurn);
    }

    @Override
    public int getSatisfactionLoss() {
        return SATISFACTION_LOSS;
    }

    @Override
    public String getTypeLabel() {
        return "Regular";
    }
}
