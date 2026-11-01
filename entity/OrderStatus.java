package entity;

/**
 * The life cycle of an order in the kitchen and at the table.
 */
public enum OrderStatus {
    /** Taken by the Waiter; the Chef still has preparation units to do. */
    IN_PREPARATION,
    /** All preparation units are done; waiting for the Waiter to serve it. */
    READY,
    /** Served to the customer. */
    SERVED,
    /** The customer has paid for the order. */
    PAID,
    /** Cancelled because the customer left before being served. */
    CANCELLED
}