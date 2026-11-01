package restaurantrush.entity;

public abstract class Customer {

    // Satisfaction every customer starts with. 
    public static final int MAX_SATISFACTION = 100;

    private final int id;
    private final int arrivalTurn;
    private int satisfaction = MAX_SATISFACTION;
    private int finalSatisfaction = -1;
    private CustomerState state = CustomerState.WAITING;
    private int lastStateChangeTurn;
    private Table table;
    private Order order;

    protected Customer(int id, int arrivalTurn) {
        this.id = id;
        this.arrivalTurn = arrivalTurn;
        this.lastStateChangeTurn = arrivalTurn;
    }

    public abstract int getSatisfactionLoss();

    public abstract String getTypeLabel();

    public int calculateBill(int basePriceCents) {
        return basePriceCents;
    }

    public MenuItem chooseMenuItem(Menu menu) {
        int index = (id - 1) % menu.getItems().size();
        return menu.getItem(index);
    }

    public boolean applyWaitingLoss() {
        if (!isAwaitingService()) {
            return false;
        }
        satisfaction = Math.max(0, satisfaction - getSatisfactionLoss());
        return satisfaction == 0;
    }

    public boolean isAwaitingService() {
        return state == CustomerState.WAITING || state == CustomerState.SEATED
                || state == CustomerState.ORDERED;
    }

    public void changeState(CustomerState s, int turn) {
        this.state = s;
        this.lastStateChangeTurn = turn;
    }

    public void recordFinalSatisfaction() {
        this.finalSatisfaction = satisfaction;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arrivalTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public int getFinalSatisfaction() {
        return finalSatisfaction;
    }

    public CustomerState getState() {
        return state;
    }

    public int getLastStateChangeTurn() {
        return lastStateChangeTurn;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "#" + id + " " + getTypeLabel() + " (satisfaction " + satisfaction + ")";
    }
}
