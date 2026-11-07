package staff;

import entity.Customer;
import entity.CustomerState;
import entity.GameState;
import entity.MenuOffering;
import entity.Money;
import entity.Order;

/**
 * The Waiter staff member (Stage 3 version). It performs the three Waiter
 * tasks: seating a queued customer, taking a seated customer's order, and
 * serving a READY order.
 *
 * <p>The Waiter holds no state of its own. The rule that the Waiter completes
 * at most one task per turn is enforced by the {@code GameController}, which
 * only ends the Waiter's step after a successful task. Each method returns an
 * {@link ActionResult}: a failed result explains why the choice was invalid,
 * and nothing in the game state changes, so the manager can choose again
 * without using up the task.
 *
 * <p>Orders are made from a {@link MenuOffering}, so single menu items and
 * combo meals are handled by the same code. The bill is locked in when the
 * order is taken, after the customer's own discount and then the Happy Hour
 * discount.
 */
public class Waiter {

    /**
     * Creates a Waiter. The Waiter has no state to initialise.
     */
    public Waiter() {
        // no state to initialise
    }

    /**
     * Seats one queued customer at a free table. The seating rules are
     * delegated to {@link GameState#seatCustomer(Customer)}, so that the same
     * logic is shared with the Host.
     *
     * @param c     the queued customer to seat
     * @param state the current game state
     * @return a successful result if the customer was seated, otherwise a
     *         failed result explaining why not
     */
    public ActionResult seat(Customer c, GameState state) {
        return state.seatCustomer(c);
    }

    /**
     * Takes the order of a seated customer. The customer, not the manager,
     * chooses the offering, which may be a single item or a combo meal. The
     * bill is calculated and locked in at this moment: first the customer's
     * own discount through {@link Customer#calculateBill(int)}, then the
     * Happy Hour discount if it is active. Orders already placed are never
     * changed afterwards. The customer then moves to the ORDERED state.
     *
     * @param c     the seated customer who has not ordered yet
     * @param state the current game state
     * @return a successful result describing the offering, bill (noting Happy
     *         Hour when applied) and preparation units, or a failed result if
     *         the customer is {@code null} or is not in the SEATED state
     */
    public ActionResult takeOrder(Customer c, GameState state) {
        if (c == null || c.getState() != CustomerState.SEATED) {
            return ActionResult.fail("Only a seated customer who has not ordered yet can place an order.");
        }
        MenuOffering item = c.chooseOffering(state.getMenu());
        int bill = state.getHappyHour().applyDiscount(c.calculateBill(item.getPriceCents()));
        Order order = new Order(state.nextOrderId(), item, c, bill);
        c.setOrder(order);
        c.changeState(CustomerState.ORDERED, state.getCurrentTurn());
        return ActionResult.ok("Customer #" + c.getId() + " ordered " + item.getName() + " (bill "
                + Money.format(bill) + (state.getHappyHour().isActive() ? " with Happy Hour 20% off" : "")
                + ", " + item.getPrepUnits() + " preparation unit" + (item.getPrepUnits() > 1 ? "s" : "") + ").");
    }

    /**
     * Serves a READY order to its customer. A dish can only be served if it
     * was already READY before the current turn, so a dish finished this turn
     * is served next turn at the earliest. On success the order becomes SERVED
     * and the customer starts eating.
     *
     * @param o     the order to serve
     * @param state the current game state
     * @return a successful result naming the dish and customer, or a failed
     *         result if the order is {@code null} or not yet servable
     */
    public ActionResult serve(Order o, GameState state) {
        if (o == null || !o.isServable(state.getCurrentTurn())) {
            return ActionResult.fail("That dish is not READY yet. A dish finished this turn can be served next turn at the earliest.");
        }
        o.markServed();
        o.getCustomer().changeState(CustomerState.EATING, state.getCurrentTurn());
        return ActionResult.ok("Served " + o.getItem().getName() + " to customer #" + o.getCustomer().getId() + ".");
    }
}
