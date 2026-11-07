package staff;

/**
 * The outcome of a staff action, such as seating a customer or serving a
 * dish. It is immutable and carries two things: whether the action succeeded,
 * and a message for the manager.
 *
 * <p>A failed result is how a rule violation is reported. Its message explains
 * why the choice was invalid, and no game state changes, so the manager can
 * choose again without using up the staff task. Instances are created only
 * through {@link #ok(String)} and {@link #fail(String)}.
 */
public final class ActionResult {

    private final boolean success;
    private final String message;

    /**
     * Creates a result. It is private so that results are only created
     * through the {@link #ok(String)} and {@link #fail(String)} factories.
     *
     * @param success whether the action succeeded
     * @param message the message to show the manager
     */
    private ActionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful result.
     *
     * @param message a description of what happened
     * @return a result for which {@link #isSuccess()} is {@code true}
     */
    public static ActionResult ok(String message) {
        return new ActionResult(true, message);
    }

    /**
     * Creates a failed result.
     *
     * @param reason why the action was not allowed
     * @return a result for which {@link #isSuccess()} is {@code false}
     */
    public static ActionResult fail(String reason) {
        return new ActionResult(false, reason);
    }

    /**
     * Tells whether the action succeeded.
     *
     * @return {@code true} if the action was carried out, {@code false} if it
     *         was rejected
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the message for the manager.
     *
     * @return the success description or the reason for the failure
     */
    public String getMessage() {
        return message;
    }
}
 
