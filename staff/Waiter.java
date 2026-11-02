package staff;

import entity.Customer;
import entity.CustomerState;
import entity.GameState;
import entity.MenuItem;
import entity.Money;
import entity.Order;

public class Waiter {

    
     //Creates a Waiter.

    public Waiter() {
        // no state to initialise
    }

    public ActionResult seat(Customer c, GameState state) {
        return state.seatCustomer(c);
    }
    public ActionResult takeOrder(Customer c, GameState state) {
        if (c == null || c.getState() != CustomerState.SEATED) {
            return ActionResult.fail("Only a seated customer who has not ordered yet can place an order.");
        }
        MenuItem item = c.chooseMenuItem(state.getMenu());
        int bill = c.calculateBill(item.getPriceCents());
        Order order = new Order(state.nextOrderId(), item, c, bill);
        c.setOrder(order);
        c.changeState(CustomerState.ORDERED, state.getCurrentTurn());
        return ActionResult.ok("Customer #" + c.getId() + " ordered " + item.getName() + " (bill "
                + Money.format(bill) + ", " + item.getPrepUnits() + " preparation unit"
                + (item.getPrepUnits() > 1 ? "s" : "") + ").");
    }

    public ActionResult serve(Order o, GameState state) {
        if (o == null || !o.isServable(state.getCurrentTurn())) {
            return ActionResult.fail("That dish is not READY yet. A dish finished this turn can be served next turn at the earliest.");
        }
        o.markServed();
        o.getCustomer().changeState(CustomerState.EATING, state.getCurrentTurn());
        return ActionResult.ok("Served " + o.getItem().getName() + " to customer #" + o.getCustomer().getId() + ".");
    }
}
