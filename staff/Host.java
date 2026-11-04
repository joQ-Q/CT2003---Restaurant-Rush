package restaurantrush.staff;

import restaurantrush.entity.Customer;
import restaurantrush.entity.GameState;

/**
 * The Host seats at most one queued customer per turn, in addition to the
 * Waiter's task. Seating follows the same rules as for the Waiter.
 */
public class Host {

    /**
     * Creates a Host.
     */
    public Host() {
        // no state to initialise
    }

    /**
     * Seats a queued customer at a free table.
     *
     * @param c     the customer to seat
     * @param state the game state
     * @return success, or the reason it failed
     */
    public ActionResult seat(Customer c, GameState state) {
        ActionResult result = state.seatCustomer(c);
        return result.isSuccess() ? ActionResult.ok("Host: " + result.getMessage()) : result;
    }
}
