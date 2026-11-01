package restaurantrush.control;

import restaurantrush.entity.Customer;
import restaurantrush.entity.RegularCustomer;

//Decides who arrives and when. In the base setting one customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.

public class ArrivalSchedule {

    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};

    private int nextCustomerId = 1;

    //Creates the base-setting arrival schedule.
    public ArrivalSchedule() {
        // no state to initialise
    }

    public Customer getArrival(int turn) {
        for (int t : ARRIVAL_TURNS) {
            if (t == turn) {
                return createCustomer(turn, nextCustomerId++);
            }
        }
        return null;
    }

    private Customer createCustomer(int turn, int id) {
        return new RegularCustomer(id, turn);
    }
}
