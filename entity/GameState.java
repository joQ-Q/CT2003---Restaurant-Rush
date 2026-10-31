package entity;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.OptionalDouble;

/** Game rules: 18 max turns, 2 tables, at least 4 paid customers, revenue at least $45, average satisfaction 60 */
public class GameState {
    public static final int MAX_TURNS = 18;
    public static final int TABLE_COUNT = 2;
    public static final int TARGET_PAID = 4;
    public static final int TARGET_REVENUE_CENTS = 4500;
    public static final int TARGET_AVG_SATISFACTION = 60;

    private int currentTurn;
    private int revenueCents;
    private int unhappyDepartures;
    private int nextOrderId = 1;
    private final List<Table> tables = new Arraylist<>();
    private final Deque<Customer> waitingQueue = new ArrayDeque<>();
    private final Menu menu = new Menu();
    
    /* new game */
    public GameState(){
        for (int i = 1; i <= TABLE_COUNT; i++){
            tables.add(new Table(i));
        }
    }
    /* next turn */
    public void advanceTurn(){
        currentTurn ++;
    }

    public boolean isGameOver(){
        return currentTurn >= MAX_TURNS;
    }

    /* add customer */
    public void addArrival(Customer c){
        customers.add(c);
        waitingQueue.addLast(c);
    }

    /* seat customer */
    public ActionResult seatCustomer(Customer c){
        if (c==null || !waitingQueue.contains(c)){
            return ActionResult.fail("That customer is not in the waiting queue.");
        }
        Table table = findFreeTable();
        if (table == null){
            return ActionResult.fail("Both tables are occupied. A table frees up only when its customer pays or leaves.");
        }
        waitingQueue.remove(c);
        table.seat(c);
        c.setTable(table);
        c.changeState(CustomerState.SEATED, currentTurn);
        return ActionResult.ok("Customer #" + c.getId() + " is seated at table " + table.getId() + ".");
    }

    /* free up first table */
    public Table findFreeTable(){
        for (Table t: tables){
            if (t.isFree()){
                return t;
            }
        }
        return null;
    }

    /* return next order number */
    public int nextOrderId(){
        return nextOrderId++;
    }
    /**
     * Collects a customer's bill: add to revenue, records the final satisfaction, marks customer and order PAID and frees the table */
    public void recordPayment(Customer c) {
        Order o = c.getOrder();
        revenueCents += o.getBillCents();
        o.markPaid();
        c.recordFinalSatisfaction();
        c.changeState(CustomerState.PAID, currentTurn);
        releaseTable(c);
    }

    /** Removes a customer who left unhappy: cancels any unfinished order and frees the table. Revenue is not affected */
    public void recordDeparture(Customer c) {
        waitingQueue.remove(c);
        if (c.getOrder() != null) {
            c.getOrder().cancel();
        }
        c.changeState(CustomerState.LEFT, currentTurn);
        releaseTable(c);
        unhappyDepartures++;
    }

    private void releaseTable(Customer c) {
        if (c.getTable() != null) {
            c.getTable().release();
            c.setTable(null);
        }
    }

    /** Returns customers in the waiting queue, oldest first */
    public List<Customer> getWaitingCustomers() {
        return new ArrayList<>(waitingQueue);
    }

    /** Returns seated customers whose order has not been taken */
    public List<Customer> getCustomersToOrder() {
        return customersIn(CustomerState.SEATED);
    }

    /** Returns orders the Waiter may serve this turn */
    public List<Order> getServableOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : getAllOrders()) {
            if (o.isServable(currentTurn)) {
                result.add(o);
            }
        }
        return result;
    }

    /** Returns orders the Chef can still work on */
    public List<Order> getOrdersInPreparation() {
        List<Order> result = new ArrayList<>();
        for (Order o : getAllOrders()) {
            if (o.getStatus() == OrderStatus.IN_PREPARATION) {
                result.add(o);
            }
        }
        return result;
    }

    /** Customers the Cashier may charge this turn: READY_TO_PAY since an earlier turn, oldest first 
     */
    public List<Customer> getCustomersAbleToPay() {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customersIn(CustomerState.READY_TO_PAY)) {
            if (c.getLastStateChangeTurn() < currentTurn) {
                result.add(c);
            }
        }
        result.sort(Comparator.comparingInt(Customer::getLastStateChangeTurn).thenComparingInt(Customer::getId));
        return result;
    }

    /** Customers still inside the restaurant (queued or at a table) */
    public List<Customer> getCustomersInRestaurant() {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getState() != CustomerState.PAID && c.getState() != CustomerState.LEFT) {
                result.add(c);
            }
        }
        return result;
    }

    /** Returns all customers currently in the given state */
    public List<Customer> customersIn(CustomerState state) {
        List<Customer> result = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getState() == state) {
                result.add(c);
            }
        }
        return result;
    }

    private List<Order> getAllOrders() {
        List<Order> result = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getOrder() != null) {
                result.add(c.getOrder());
            }
        }
        return result;
    }

    /** Number of customers who have paid */
    public int getPaidCount() {
        return customersIn(CustomerState.PAID).size();
    }

    /** Returns number of customers who have been served (eating, ready to pay or paid) */
    public int getServedCount() {
        return customersIn(CustomerState.EATING).size() + customersIn(CustomerState.READY_TO_PAY).size()
                + getPaidCount();
    }

    /** Average satisfaction recorded at payment time */
    public OptionalDouble getAverageSatisfaction() {
        return customersIn(CustomerState.PAID).stream().mapToInt(Customer::getFinalSatisfaction).average();
    }

    /** Checks all three victory targets */
    public boolean isVictory() {
        return getPaidCount() >= TARGET_PAID && revenueCents >= TARGET_REVENUE_CENTS
                && getAverageSatisfaction().orElse(0) >= TARGET_AVG_SATISFACTION;
    }

    /** Returns the current turn number */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /** Returns total revenue in cents */
    public int getRevenueCents() {
        return revenueCents;
    }

    /** Returns number of customers who left unhappy */
    public int getUnhappyDepartures() {
        return unhappyDepartures;
    }

    /** Returns the tables */
    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    /** Returns the menu */
    public Menu getMenu() {
        return menu;
    }
}


