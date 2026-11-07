package control;

import java.util.List;

import boundary.ConsoleUI;
import entity.Customer;
import entity.CustomerState;
import entity.GameState;
import entity.HappyHour;
import entity.Money;
import entity.Order;
import entity.WaiterTask;
import staff.ActionResult;
import staff.Cashier;
import staff.Chef;
import staff.Host;
import staff.Waiter;


/**
 * Runs the game (Stage 3 version). This is the only class that knows the
 * order of the six turn steps: arrivals, manager choices, kitchen and
 * serving, eating and payment, waiting and abandonment, and the final result.
 *
 * <p>It owns the game state, the arrival schedule and the staff (Waiter, Host,
 * Chef and Cashier). It only coordinates: it asks the {@link ConsoleUI} for
 * the manager's decisions and for displaying the state, while the game rules
 * live in the entity and staff classes.
 *
 * <p>Stage 3 adds the Host, who seats one queued customer in addition to the
 * Waiter's task, and Happy Hour, which the manager may switch on once per
 * game at the start of a turn.
 */
public class GameController {

    private final GameState state;
    private final ConsoleUI ui;
    private final ArrivalSchedule schedule;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private final Host host;

    /**
     * Creates a new game with the base setting: a fresh game state, the
     * arrival schedule, and the Waiter, Chef, Cashier and Host.
     *
     * @param ui the boundary object used to talk to the manager
     */
    public GameController(ConsoleUI ui) {
        this.ui = ui;
        this.state = new GameState();
        this.schedule = new ArrivalSchedule();
        this.waiter = new Waiter();
        this.chef = new Chef();
        this.cashier = new Cashier();
        this.host = new Host();
    }

    /**
     * Plays all 18 turns and shows the result. There is no early ending.
     */
    public void run() {
        ui.displayWelcome(state);
        while (!state.isGameOver()) {
            playTurn();
        }
        ui.displayResult(state);
    }

    /**
     * Plays one complete turn by running the turn steps in order. The Happy
     * Hour start-of-turn effects come first, the Host seats in addition to the
     * Waiter's task, and the Waiter acts before the Chef. Happy Hour's
     * remaining duration is reduced at the end of the turn.
     */
    private void playTurn() {
        state.advanceTurn();
        handleArrivals();                 // step 1
        ui.displayTurnStart(state);
        handleHappyHour();                // start of turn: optional activation
        applyHappyHourRecovery();         // start of turn: +15 while active
        handleHost();                     // step 2: Host seats, in addition to the Waiter
        handleWaiter();                   // step 2 + 3: Waiter acts before the Chef
        handleChef();
        handleEatingAndPayment();         // step 4
        handleWaitingLoss();              // step 5
        state.getHappyHour().endTurn();
        ui.displayTurnSummary(state);
    }

    /**
     * Step 1, arrivals: the customer scheduled for this turn, if any, joins
     * the waiting queue with satisfaction 100.
     */
    private void handleArrivals() {
        Customer arrival = schedule.getArrival(state.getCurrentTurn());
        if (arrival != null) {
            state.addArrival(arrival);
            ui.showMessage("Customer #" + arrival.getId() + " (" + arrival.getTypeLabel()
                    + ") arrives and joins the waiting queue.");
        }
    }

    /**
     * Start of turn, Happy Hour activation: if Happy Hour has not been used
     * yet, asks whether to switch it on now (it can be used once per game).
     * If it is active, shows how many turns it has left.
     */
    private void handleHappyHour() {
        HappyHour happyHour = state.getHappyHour();
        if (!happyHour.isUsed() && ui.promptHappyHour()) {
            ui.showMessage(happyHour.activate().getMessage());
        }
        if (happyHour.isActive()) {
            ui.displayHappyHour(happyHour.getTurnsRemaining());
        }
    }

    /**
     * Start of each active Happy Hour turn: customers still waiting for a
     * table or food recover satisfaction, up to the maximum of 100. Customers
     * who are eating or ready to pay are not affected. Nothing happens when
     * Happy Hour is not active.
     */
    private void applyHappyHourRecovery() {
        HappyHour happyHour = state.getHappyHour();
        if (!happyHour.isActive()) {
            return;
        }
        for (Customer c : state.getCustomersInRestaurant()) {
            if (c.isAwaitingService()) {
                int before = c.getSatisfaction();
                c.recoverSatisfaction(happyHour.getRecoveryPoints());
                ui.showMessage("Happy Hour: customer #" + c.getId() + " recovers satisfaction " + before + " -> "
                        + c.getSatisfaction() + ".");
            }
        }
    }

    /**
     * Step 2, Host: seat at most one queued customer, or do nothing. Only
     * asked when someone is waiting and a table is free. An invalid choice
     * explains why and asks again.
     */
    private void handleHost() {
        while (!state.getWaitingCustomers().isEmpty() && state.findFreeTable() != null) {
            ui.showMessage("Host: choose a customer to seat, or 0 to skip.");
            Customer c = ui.promptCustomer(state.getWaitingCustomers());
            if (c == null) {
                ui.showMessage("The Host does not seat anyone this turn.");
                return;
            }
            ActionResult result = host.seat(c, state);
            ui.showMessage(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    /**
     * Step 2 and 3, Waiter: at most one task. An invalid choice explains why
     * and asks again without using up the task.
     */
    private void handleWaiter() {
        while (true) {
            WaiterTask task = ui.promptWaiterTask();
            ActionResult result;
            switch (task) {
                case SEAT:
                    result = trySeat();
                    break;
                case TAKE_ORDER:
                    result = tryTakeOrder();
                    break;
                case SERVE:
                    result = tryServe();
                    break;
                case WAIT:
                default:
                    ui.showMessage("The Waiter waits this turn.");
                    return;
            }
            if (result == null) {
                continue;                  // manager went back to the task menu
            }
            ui.showMessage(result.getMessage());
            if (result.isSuccess()) {
                return;
            }
        }
    }

    /**
     * Tries the Waiter's seat task: checks that someone is queued and a table
     * is free, lets the manager pick a queued customer, and seats them.
     *
     * @return the result of seating, a failed result if no customer is queued
     *         or no table is free, or {@code null} if the manager went back
     *         to the task menu
     */
    private ActionResult trySeat() {
        List<Customer> waiting = state.getWaitingCustomers();
        if (waiting.isEmpty()) {
            return ActionResult.fail("No customer is waiting in the queue.");
        }
        if (state.findFreeTable() == null) {
            return ActionResult.fail("Both tables are occupied. A table frees up only when its customer pays or leaves.");
        }
        Customer c = ui.promptCustomer(waiting);
        return c == null ? null : waiter.seat(c, state);
    }

    /**
     * Tries the Waiter's take-order task: lets the manager pick a seated
     * customer who has not ordered yet, and takes that customer's order.
     *
     * @return the result of taking the order, a failed result if nobody is
     *         waiting to order, or {@code null} if the manager went back to
     *         the task menu
     */
    private ActionResult tryTakeOrder() {
        List<Customer> seated = state.getCustomersToOrder();
        if (seated.isEmpty()) {
            return ActionResult.fail("No seated customer is waiting to order.");
        }
        Customer c = ui.promptCustomer(seated);
        return c == null ? null : waiter.takeOrder(c, state);
    }

    /**
     * Tries the Waiter's serve task: lets the manager pick a servable READY
     * order and serves it.
     *
     * @return the result of serving, a failed result if no dish is READY, or
     *         {@code null} if the manager went back to the task menu
     */
    private ActionResult tryServe() {
        List<Order> ready = state.getServableOrders();
        if (ready.isEmpty()) {
            return ActionResult.fail("No dish is READY to serve. A dish finished this turn can be served next turn at the earliest.");
        }
        Order o = ui.promptOrder(ready);
        return o == null ? null : waiter.serve(o, state);
    }

    /**
     * Step 2 and 3, Chef: advance at most one order by one preparation unit,
     * or wait. The Chef may work on an order the Waiter took earlier this
     * turn.
     */
    private void handleChef() {
        List<Order> cooking = state.getOrdersInPreparation();
        if (cooking.isEmpty()) {
            ui.showMessage("The Chef has nothing to prepare this turn.");
            return;
        }
        ui.showMessage("Chef: choose an order to prepare, or 0 to wait.");
        Order o = ui.promptOrder(cooking);
        if (o == null) {
            ui.showMessage("The Chef waits this turn.");
            return;
        }
        ui.showMessage(chef.prepare(o, state).getMessage());
    }

    /**
     * Step 4: customers served on an earlier turn finish eating; then the
     * Cashier charges the oldest customer who was already READY_TO_PAY.
     */
    private void handleEatingAndPayment() {
        int turn = state.getCurrentTurn();
        for (Customer c : state.customersIn(CustomerState.EATING)) {
            if (c.getLastStateChangeTurn() < turn) {
                c.changeState(CustomerState.READY_TO_PAY, turn);
                ui.showMessage("Customer #" + c.getId() + " finished eating and is ready to pay (pays next turn at the earliest).");
            }
        }
        Customer paid = cashier.collectPayment(state);
        if (paid == null) {
            ui.showMessage("Cashier (automatic): nobody is ready to pay this turn.");
        } else {
            ui.showMessage("Cashier (automatic) collected " + Money.format(paid.getOrder().getBillCents()) + " from customer #"
                    + paid.getId() + " (satisfaction " + paid.getFinalSatisfaction() + " recorded). Table freed.");
        }
    }

    /**
     * Step 5, waiting and abandonment: unserved customers lose satisfaction,
     * and anyone who reaches 0 leaves without paying. The departure is
     * recorded. Customers who are served, eating or ready to pay lose nothing.
     */
    private void handleWaitingLoss() {
        for (Customer c : state.getCustomersInRestaurant()) {
            if (c.applyWaitingLoss()) {
                state.recordDeparture(c);
                ui.showMessage("Customer #" + c.getId() + " ran out of patience and left without paying.");
            }
        }
    }
}
