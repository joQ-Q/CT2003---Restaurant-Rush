package restaurantrush.entity;

//The stages a customer passes through, from arrival to leaving the restaurant.
public enum CustomerState {
    //In the waiting queue, not yet seated. 
    WAITING,
    // Seated at a table, order not yet taken. 
    SEATED,
    // Order taken; waiting for the dish to be cooked and served. 
    ORDERED,
    // Dish served; the customer is eating. 
    EATING,
    // Finished eating; waiting for the Cashier. 
    READY_TO_PAY,
    // Bill paid; the customer has left happily. 
    PAID,
    // Satisfaction reached 0; the customer left without paying. 
    LEFT
}
