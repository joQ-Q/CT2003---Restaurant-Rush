package entity;

import java.util.List;

public abstract class Customer {

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

    public MenuOffering chooseOffering(Menu menu) {
        List<MenuOffering> offerings = menu.getOfferings();
        return offerings.get((id - 1) % offerings.size());
    }

    public void recoverSatisfaction(int points) {
        satisfaction = Math.min(MAX_SATISFACTION, satisfaction + points);
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
