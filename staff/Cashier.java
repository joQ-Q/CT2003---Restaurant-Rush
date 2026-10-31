package staff;

import java.util.List;

import entity.Customer;
import entity.GameState;

/**
 * The Cashier automatically charges at most one READY_TO_PAY customer per turn, oldest first. A customer who is READY_TO_PAY this turn pays next turn.
 */

public class Cashier{
    public Cashier(){
        
    }

    public Customer collectPayment(GameState state){
        List<Customer> eligible = state.getCustomersAbleToPay();
        if (eligible.isEmpty()){
            return null;
        }
        Customer c = eligible.get(0);
        state.recordPayment(c);
        return c;
    }
}