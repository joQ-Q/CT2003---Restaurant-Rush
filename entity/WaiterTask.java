package restaurantrush.entity;


public enum WaiterTask {
    //Seat one queued customer at a free table.
    SEAT,
    // Take one seated customer's order. 
    TAKE_ORDER,
    // Serve one READY order.
    SERVE,
    // Do nothing this turn. 
    WAIT
}
