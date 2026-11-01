package restaurantrush.control;

import java.util.List;

import restaurantrush.boundary.ConsoleUI;
import restaurantrush.entity.Customer;
import restaurantrush.entity.CustomerState;
import restaurantrush.entity.GameState;
import restaurantrush.entity.Money;
import restaurantrush.entity.Order;
import restaurantrush.entity.WaiterTask;
import restaurantrush.staff.ActionResult;
import restaurantrush.staff.Cashier;
import restaurantrush.staff.Chef;
import restaurantrush.staff.Waiter;


public class GameController {

    private final GameState state;
    private final ConsoleUI ui;
    private final ArrivalSchedule schedule;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;


    public GameController(ConsoleUI ui) {
        this.ui = ui;
        this.state = new GameState();
        this.schedule = new ArrivalSchedule();
        this.waiter = new Waiter();
        this.chef = new Chef();
        this.cashier = new Cashier();
    }


    public void run() {
        ui.displayWelcome(state);
        while (!state.isGameOver()) {
            playTurn();
        }
        ui.displayResult(state);
    }

    private void playTurn() {
        state.advanceTurn();
        handleArrivals();                 // step 1
        ui.displayTurnStart(state);
        handleWaiter();                   // step 2 + 3: Waiter acts before the Chef
        handleChef();
        handleEatingAndPayment();         // step 4
        handleWaitingLoss();              // step 5
        ui.displayTurnSummary(state);
    }

    // Step 1: the scheduled customer joins the queue with satisfaction 100. 
    private void handleArrivals() {
        Customer arrival = schedule.getArrival(state.getCurrentTurn());
        if (arrival != null) {
            state.addArrival(arrival);
            ui.showMessage("Customer #" + arrival.getId() + " (" + arrival.getTypeLabel()
                    + ") arrives and joins the waiting queue.");
        }
    }

    // Step 2 Waiter: at most one task. An invalid choice explains why and asks again without using up the task.
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

    private ActionResult tryTakeOrder() {
        List<Customer> seated = state.getCustomersToOrder();
        if (seated.isEmpty()) {
            return ActionResult.fail("No seated customer is waiting to order.");
        }
        Customer c = ui.promptCustomer(seated);
        return c == null ? null : waiter.takeOrder(c, state);
    }

    private ActionResult tryServe() {
        List<Order> ready = state.getServableOrders();
        if (ready.isEmpty()) {
            return ActionResult.fail("No dish is READY to serve. A dish finished this turn can be served next turn at the earliest.");
        }
        Order o = ui.promptOrder(ready);
        return o == null ? null : waiter.serve(o, state);
    }

    //Step 3, Chef: advance at most one order by one unit, or wait. 
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

    // Step 4: customers served on an earlier turn finish eating; then the Cashier charges the oldest customer who was already READY_TO_PAY.
     
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

    // Step 5: unserved customers lose satisfaction; anyone at 0 leaves without paying. 
    private void handleWaitingLoss() {
        for (Customer c : state.getCustomersInRestaurant()) {
            if (c.applyWaitingLoss()) {
                state.recordDeparture(c);
                ui.showMessage("Customer #" + c.getId() + " ran out of patience and left without paying.");
            }
        }
    }
}
