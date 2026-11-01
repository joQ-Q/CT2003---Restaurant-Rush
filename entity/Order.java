package entity;

/**
 * One customer's order. The bill is locked when the order is taken.
 * The order tracks how many preparation units the Chef has done and
 * on which turn it became READY.
 */
public class Order {

    private final int id;
    private final MenuItem item;
    private final Customer customer;
    private final int billCents;
    private int unitsDone;
    private OrderStatus status;
    private int readyTurn = -1;

    /**
     * Creates a new order in the IN_PREPARATION state.
     *
     * @param id        order number
     * @param item      the dish ordered
     * @param customer  the customer who placed the order
     * @param billCents the locked bill in cents
     */
    public Order(int id, MenuItem item, Customer customer, int billCents) {
        this.id = id;
        this.item = item;
        this.customer = customer;
        this.billCents = billCents;
        this.status = OrderStatus.IN_PREPARATION;
    }

    /**
     * Completes one preparation unit. When the last unit is done the order
     * becomes READY and remembers the turn.
     *
     * @param turn the current turn number
     * @return {@code true} if a unit was done, {@code false} if the order was not in preparation
     */
    public boolean prepareOneUnit(int turn) {
        if (status != OrderStatus.IN_PREPARATION) {
            return false;
        }
        unitsDone++;
        if (unitsDone >= item.getPrepUnits()) {
            status = OrderStatus.READY;
            readyTurn = turn;
        }
        return true;
    }

    /**
     * An order can be served only if it was already READY before this turn,
     * so a dish finished this turn is served next turn at the earliest.
     *
     * @param turn the current turn number
     * @return {@code true} if the Waiter may serve this order now
     */
    public boolean isServable(int turn) {
        return status == OrderStatus.READY && readyTurn < turn;
    }

    /** Marks the order as served. */
    public void markServed() {
        status = OrderStatus.SERVED;
    }

    /** Marks the order as paid. */
    public void markPaid() {
        status = OrderStatus.PAID;
    }

    /** Cancels the order if it has not been served yet. */
    public void cancel() {
        if (status == OrderStatus.IN_PREPARATION || status == OrderStatus.READY) {
            status = OrderStatus.CANCELLED;
        }
    }

    /**
     * Returns the order number.
     *
     * @return the order number
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the dish ordered.
     *
     * @return the dish ordered
     */
    public MenuItem getItem() {
        return item;
    }

    /**
     * Returns the customer who placed this order.
     *
     * @return the customer who placed this order
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the locked bill in cents.
     *
     * @return the locked bill in cents
     */
    public int getBillCents() {
        return billCents;
    }

    /**
     * Returns the number of preparation units completed.
     *
     * @return the number of preparation units completed
     */
    public int getUnitsDone() {
        return unitsDone;
    }

    /**
     * Returns the current status.
     *
     * @return the current status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Returns the turn the order became READY, or -1 if it is not ready yet.
     *
     * @return the turn the order became READY, or -1 if it is not ready yet
     */
    public int getReadyTurn() {
        return readyTurn;
    }

    @Override
    public String toString() {
        return item.getName() + " for customer #" + customer.getId() + " [" + unitsDone + "/"
                + item.getPrepUnits() + " units, " + status + ", bill " + Money.format(billCents) + "]";
    }
}