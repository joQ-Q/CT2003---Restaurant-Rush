# Restaurant Rush

A turn-based restaurant management game for the command line, written in Java for CT2003 (Object-Oriented Design and Programming). The player is the manager and decides what the Waiter and Chef do each turn.

**Repository:** https://github.com/joQ-Q/CT2003---Restaurant-Rush

| Stage | Adds | Git tag |
|---|---|---|
| 1 Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat | [`stage-1`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-1) |
| 2 Customer behaviour | VIP and Critic customers | `stage-2` (not yet tagged) |
| 3 New role and menu policy | Host, ComboMeal, Happy Hour | `stage-3` (not yet tagged) |

## Build and run

Needs Java 11 or newer. Run these from the repository folder.

**Windows PowerShell**
```powershell
javac -d out Main.java boundary\*.java control\*.java entity\*.java staff\*.java
java -cp out Main
```

**macOS / Linux / Git Bash**
```bash
javac -d out Main.java boundary/*.java control/*.java entity/*.java staff/*.java
java -cp out Main
```

Play by typing a number and pressing Enter.

## Packages

| Package | Classes | Role |
|---|---|---|
| (default) | `Main` | Starts the game |
| `boundary` | `ConsoleUI` | Input and display only, no game rules |
| `control` | `GameController`, `ArrivalSchedule` | Turn sequence and arrivals |
| `staff` | `Waiter`, `Chef`, `Cashier`, `ActionResult` | Staff actions and rule checks |
| `entity` | `GameState`, `Customer`, `RegularCustomer`, `Table`, `Order`, `Menu`, `MenuItem`, `Money`, `CustomerState`, `OrderStatus`, `WaiterTask` | Restaurant data |

The Stage 1 class diagram is in `Stage-1-RestaurantRushClassDiagram.png`.

## Documented rules

- **Base setting:** 18 turns, 2 tables, one Waiter, one Chef, one Cashier; one customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.
- **Menu:** Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units).
- **Food choice:** customer #n orders menu item (n − 1) mod 3, so customers 1–6 order Salad, Burger, Pasta, Salad, Burger, Pasta. This is deterministic, so every run is reproducible.
- **Money** is stored as integer cents and displayed with two decimals.
- **Turn order:** arrivals → start-of-turn display → Waiter → Chef → eating and payment → satisfaction loss and abandonment → end-of-turn summary.
- **Objective:** at the end of turn 18, at least 4 paid customers, revenue at least $45.00 and average satisfaction of paid customers at least 60.

## Test notes (Stage 1, base setting)

| Run | Input file | Saved output | Result |
|---|---|---|---|
| Victory | `test-inputs/victory.txt` | `test-outputs/victory-output.txt` | 5 paid, revenue $66.00, average satisfaction 79.2, 0 unhappy departures → **VICTORY** |
| Defeat | `test-inputs/defeat.txt` | `test-outputs/defeat-output.txt` | Waiter always waits: 0 paid, revenue $0.00, average N/A, 2 unhappy departures → **DEFEAT** |

**Victory strategy:** seat each customer on arrival, take the order the next turn while the Chef cooks, and serve the turn after the dish is READY. On turns 10, 13 and 16 the Waiter must choose between serving the previous customer and seating the new arrival; serving first keeps the pipeline moving.

**Defeat strategy:** the Waiter chooses Wait every turn, so nobody is seated. Customer #1 reaches 0 satisfaction and leaves on turn 13, and customer #2 on turn 16.

The customer who arrives on turn 16 cannot finish paying within 18 turns, so at most five customers can pay. This is intended by the brief.

## Stage comparisons

### Stage 1 → Stage 2
_To be completed when `stage-2` is tagged._
Compare: https://github.com/joQ-Q/CT2003---Restaurant-Rush/compare/stage-1...stage-2

| Change | File | Reason |
|---|---|---|
| New | | |
| Changed | | |
| Deleted | | |

### Stage 2 → Stage 3
_To be completed when `stage-3` is tagged._
Compare: https://github.com/joQ-Q/CT2003---Restaurant-Rush/compare/stage-2...stage-3

| Change | File | Reason |
|---|---|---|
| New | | |
| Changed | | |
| Deleted | | |
