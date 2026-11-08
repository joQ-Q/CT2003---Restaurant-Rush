package staff;

import entity.GameState;
import entity.Order;
import entity.OrderStatus;

/**
 * The Chef advances one order by one preparation unit per turn.
 */
public class Chef {

    public Chef() {
        // no state to initialise
    }
    /** Calls method prepare(Order o, GameState state) 
     * First 'if' - checks if Order o is missing (null) to prevent NullPointerException 
     * OR (||) check if OrderStatus is anything other than IN_PREPARATION  
    */
    public ActionResult prepare(Order o, GameState state) {
        if (o == null || o.getStatus() != OrderStatus.IN_PREPARATION) {
            return ActionResult.fail("The Chef can only work on an order that is still in preparation.");
        }
        o.prepareOneUnit(state.getCurrentTurn());
        if (o.getStatus() == OrderStatus.READY) {
            return ActionResult.ok(o.getItem().getName() + " for customer #" + o.getCustomer().getId()
                    + " is READY (can be served next turn).");
        }
        return ActionResult.ok("Chef prepared unit " + o.getUnitsDone() + " of " + o.getItem().getPrepUnits()
                + " for customer #" + o.getCustomer().getId() + "'s " + o.getItem().getName() + ".");
    }
}
