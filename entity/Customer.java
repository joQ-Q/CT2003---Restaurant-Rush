package entity;

/**
 * Abstract base class of all customers. It holds the data and the behaviour
 * shared by every customer type: satisfaction, state, table, order, the
 * default bill and the deterministic dish choice. Subclasses define how much
 * satisfaction is lost per waiting turn and how the type is labelled, so
 * different behaviour is dispatched through the customer objects.
 */
public abstract class Customer {

    /** Satisfaction every customer starts with. */
    public static final int MAX_SATISFACTION = 100;

    private final int id;
    private final int arrivalTurn;
    private int satisfaction = MAX_SATISFACTION;
    private int finalSatisfaction = -1;
    private CustomerState state = CustomerState.WAITING;
    private int lastStateChangeTurn;
    private Table table;
    private Order order;

    /**
     * Creates a customer who starts in the WAITING state with full
     * satisfaction.
     *
     * @param id          the unique customer id
     * @param arrivalTurn the turn on which the customer arrives
     */
    protected Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.lastStateChangeTurn = arrivalTurn;
    }

    /**
     * Returns the satisfaction this customer type loses per waiting turn.
     *
     * @return the loss in satisfaction points
     */
    public abstract int getSatisfactionLoss();

    /**
     * Returns the label shown to the player for this customer type.
     *
     * @return the type label, for example {@code "Regular"}
     */
    public abstract String getTypeLabel();

    /**
     * Calculates the bill for a dish. The default is the full price;
     * subclasses may override this to apply a discount.
     *
     * @param basePriceCents the menu price in cents
     * @return the bill in cents
     */
    public int calculateBill(int basePriceCents) {
        return basePriceCents;
    }

    /**
     * Chooses a dish from the menu. The choice is deterministic: customer 1
     * takes the first menu item, customer 2 the second, and so on, cycling
     * through the menu. The manager never chooses the food.
     *
     * @param menu the restaurant menu
     * @return the dish this customer wants
     */
    public MenuItem chooseMenuItem(Menu menu) {
        int index = (id - 1) % menu.getItems().size();
        return menu.getItem(index);
    }

    /**
     * Applies one turn of waiting loss, with a minimum of 0. It only affects
     * customers still waiting for a table or food.
     *
     * @return {@code true} if the customer's satisfaction has reached 0 and
     *         the customer should leave
     */
    public boolean applyWaitingLoss() {
        if (!isAwaitingService()) {
            return false;
        }
        satisfaction = Math.max(0, satisfaction - getSatisfactionLoss());
        return satisfaction == 0;
    }

    /**
     * Tells whether the customer is still waiting for a table or for food.
     *
     * @return {@code true} if the state is WAITING, SEATED or ORDERED
     */
    public boolean isAwaitingService() {
        return state == CustomerState.WAITING || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    /**
     * Moves the customer to a new state and remembers the turn of the change.
     *
     * @param s    the new state
     * @param turn the current turn number
     */
    public void changeState(CustomerState s, int turn) {
        this.state = s;
        this.lastStateChangeTurn = turn;
    }

    /**
     * Records the current satisfaction as the final value. It is called when
     * the customer pays, and this value is used for the average satisfaction
     * of paid customers.
     */
    public void recordFinalSatisfaction() {
        this.finalSatisfaction = satisfaction;
    }

    /**
     * Returns the customer id.
     *
     * @return the unique customer id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the arrival turn.
     *
     * @return the turn on which the customer arrived
     */
    public int getArrivalTurn() {
        return arrivalTurn;
    }

    /**
     * Returns the current satisfaction.
     *
     * @return satisfaction from 0 to 100
     */
    public int getSatisfaction() {
        return satisfaction;
    }

    /**
     * Returns the satisfaction recorded at payment.
     *
     * @return the recorded satisfaction, or -1 if the customer has not paid
     */
    public int getFinalSatisfaction() {
        return finalSatisfaction;
    }

    /**
     * Returns the current state.
     *
     * @return the customer's state
     */
    public CustomerState getState() {
        return state;
    }

    /**
     * Returns the turn of the most recent state change.
     *
     * @return the turn number of the last state change
     */
    public int getLastStateChangeTurn() {
        return lastStateChangeTurn;
    }

    /**
     * Returns the table the customer occupies.
     *
     * @return the table, or {@code null} if the customer has none
     */
    public Table getTable() {
        return table;
    }

    /**
     * Sets the table the customer occupies.
     *
     * @param table the table, or {@code null} when it is released
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Returns the customer's order.
     *
     * @return the order, or {@code null} if none has been taken
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the customer's order.
     *
     * @param order the order that was taken
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Returns a short description with id, type and current satisfaction.
     *
     * @return a text such as {@code "#1 Regular (satisfaction 100)"}
     */
    @Override
    public String toString() {
        return "#" + id + " " + getTypeLabel() + " (satisfaction " + satisfaction + ")";
    }
}
