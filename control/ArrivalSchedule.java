package control;

import entity.CriticCustomer;
import entity.VIPCustomer;
import entity.Customer;
import entity.RegularCustomer;

//Decides who arrives and when. In the base setting one customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.

public class ArrivalSchedule {

    private static final int[] ARRIVAL_TURNS = {1, 4, 7, 10, 13, 16};
    private static final int VIP_TURN = 4;
    private static final int CRITIC_TURN = 10;

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
 /* encapulsation and information hiding, fixed turn will create fixed type
 of customers without external source being able to change it */
    private Customer createCustomer(int turn, int id) {
        if (turn == VIP_TURN){
            return new VIPCustomer(id, turn);
        }
        if (turn == CRITIC_TURN){
            return new CriticCustomer(id, turn);
        }
        return new RegularCustomer(id, turn);
    }
}
