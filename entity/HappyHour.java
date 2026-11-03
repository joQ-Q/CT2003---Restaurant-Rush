package entity;

import staff.ActionResult;


public class HappyHour {

    private static final int PRICE_PERCENT = 80;
    private static final int RECOVERY_POINTS = 15;
    private static final int DURATION_TURNS = 2;

    private boolean used;
    private int turnsRemaining;

    /** Creates an unused Happy Hour. */
    public HappyHour() {
        // starts unused and inactive
    }

    /* Switches Happy Hour on for this turn and the next.*/
    public ActionResult activate() {
        if (used) {
            return ActionResult.fail("Happy Hour can only be activated once per game.");
        }
        used = true;
        turnsRemaining = DURATION_TURNS;
        return ActionResult.ok("Happy Hour is ON for this turn and the next: orders taken get 20% off, "
                + "and waiting customers recover " + RECOVERY_POINTS + " satisfaction at the start of each active turn.");
    }

    /** Returns whether Happy Hour is active this turn. */
    public boolean isActive() {
        return turnsRemaining > 0;
    }

    /** Returns whether Happy Hour has already been used this game */
    public boolean isUsed() {
        return used;
    }

    /** Applies the 20% Happy Hour discount if it is active, rounded to the nearest cent Call (VIP first, then Happy Hour).
     */
    public int applyDiscount(int priceCents) {
        if (!isActive()) {
            return priceCents;
        }
        return (priceCents * PRICE_PERCENT + 50) / 100;
    }

    /** Returns the satisfaction points waiting customers recover per active turn */
    public int getRecoveryPoints() {
        return RECOVERY_POINTS;
    }

    /** Returns how many turns of Happy Hour are left, counting the current turn */
    public int getTurnsRemaining() {
        return turnsRemaining;
    }
    
    /** Counts down at the end of each turn. */
    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }
}
