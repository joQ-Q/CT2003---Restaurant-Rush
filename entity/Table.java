package entity;

/**
 * A restaurant table that seats one customer. A table stays occupied until
 * its customer pays or leaves.
 */
public class Table {

    private final int id;
    private Customer occupant;

    /**
     * Creates an empty table.
     *
     * @param id table number shown to the player
     */
    public Table(int id) {
        this.id = id;
    }

    /**
     * Returns the table number.
     *
     * @return the table number
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the seated customer, or {@code null} if the table is free.
     *
     * @return the seated customer, or {@code null} if the table is free
     */
    public Customer getOccupant() {
        return occupant;
    }

    /**
     * Returns {@code true} if nobody is sitting at this table.
     *
     * @return {@code true} if nobody is sitting at this table
     */
    public boolean isFree() {
        return occupant == null;
    }

    /**
     * Seats a customer at this table.
     *
     * @param c the customer to seat
     * @throws IllegalStateException if the table is already occupied
     */
    public void seat(Customer c) {
        if (!isFree()) {
            throw new IllegalStateException("Table " + id + " is already occupied");
        }
        occupant = c;
    }

    /** Frees the table after its customer pays or leaves. */
    public void release() {
        occupant = null;
    }
}