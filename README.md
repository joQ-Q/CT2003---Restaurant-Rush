# Restaurant Rush

A turn-based restaurant management game for the command line, written in Java for CT2003 (Object-Oriented Design and Programming). The player is the manager and decides what the Waiter and Chef do each turn.

**Repository:** https://github.com/joQ-Q/CT2003---Restaurant-Rush

| Stage | Adds | Git tag |
|---|---|---|
| 1 Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat | [`stage-1`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-1) |
| 2 Customer behaviour | VIP and Critic customers | [`stage-2`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-2) |
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
| `entity` | `GameState`, `Customer`, `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `Table`, `Order`, `Menu`, `MenuItem`, `Money`, `CustomerState`, `OrderStatus`, `WaiterTask` | Restaurant data |

Class diagrams: Stage 1 in `Stage-1-RestaurantRushClassDiagram.png`, Stage 2 in `Stage 2.drawio.png`.

## Documented rules

- **Base setting:** 18 turns, 2 tables, one Waiter, one Chef, one Cashier; one customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.
- **Menu:** Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units).
- **Food choice:** customer #n orders menu item (n − 1) mod 3, so customers 1–6 order Salad, Burger, Pasta, Salad, Burger, Pasta. This is deterministic, so every run is reproducible.
- **Customer types (Stage 2):**

  | Customer | Satisfaction loss per waiting turn | Bill | Special behaviour |
  |---|---|---|---|
  | Regular | 8 | Full price | — |
  | VIP | 5 | 10% off, rounded to the nearest cent | — |
  | Critic | 12 | Full price | Always orders the most expensive dish (Pasta, 2 units), so the Chef must give the critic's order priority |

  Each type overrides `getSatisfactionLoss()`, `calculateBill()` or `chooseMenuItem()` from `Customer`, so the turn controller never checks the customer type.
- **Arrivals (Stage 2):** turn 1 Regular, turn 4 VIP, turn 7 Regular, turn 10 Critic, turn 13 Regular, turn 16 Regular (six arrivals).
- **Money** is stored as integer cents and displayed with two decimals.
- **Turn order:** arrivals → start-of-turn display → Waiter → Chef → eating and payment → satisfaction loss and abandonment → end-of-turn summary.
- **Objective:** at the end of turn 18, at least 4 paid customers, revenue at least $45.00 and average satisfaction of paid customers at least 60.

## Test notes (base setting)

### Stage 2 (`stage-2`)

| Run | Input file | Saved output | Result |
|---|---|---|---|
| Victory | `test-inputs/victory.txt` | `test-outputs/victory-output-stage-2.txt` | 4 paid, revenue $58.50, average satisfaction 72.5, 0 unhappy departures → **VICTORY** |
| Defeat | `test-inputs/defeat.txt` | `test-outputs/defeat-output-stage-2.txt` | Waiter waits on turns 1 and 5. 4 paid, revenue $58.50, but the Critic (#4) pays at satisfaction 28, so the average is 59.8 → **DEFEAT** (missed by 0.2) |

Both runs include one VIP (#2, pays $13.50 for a $15.00 Burger) and one Critic (#4, orders Pasta).

**Defeat story:** the same strategy as the victory run, except the Waiter hesitates on turns 1 and 5. The delay pushes the backlog into the Critic's arrival on turn 10, and the Critic loses 12 satisfaction per waiting turn. Revenue and paid customers are still met, but the average misses by 0.2.

### Stage 1 (`stage-1`)

| Run | Input file | Saved output | Result |
|---|---|---|---|
| Victory | `test-inputs/victory.txt` | `test-outputs/victory-output-stage-1.txt` | 5 paid, revenue $66.00, average satisfaction 79.2 → **VICTORY** |
| Defeat | `test-inputs/defeat.txt` (at tag `stage-1`) | `test-outputs/defeat-output-stage-1.txt` | Waiter always waits: 0 paid, 2 unhappy departures → **DEFEAT** |

To replay a stage's runs, check out its tag (`git checkout stage-1`), then build and run with the input file.

**Victory strategy:** seat each customer on arrival, take the order the next turn while the Chef cooks, and serve the turn after the dish is READY. When the Waiter must choose between serving and seating a new arrival, serving first keeps the pipeline moving.

The customer who arrives on turn 16 cannot finish paying within 18 turns, so at most five customers can pay. This is intended by the brief.

## Stage comparisons

### Stage 1 → Stage 2
Compare: https://github.com/joQ-Q/CT2003---Restaurant-Rush/compare/stage-1...stage-2

| Change | File | Reason |
|---|---|---|
| New | `entity/VIPCustomer.java` | Subclass of `Customer`: 5-point satisfaction loss, overrides `calculateBill()` for 10% off |
| New | `entity/CriticCustomer.java` | Subclass of `Customer`: 12-point loss, overrides `chooseMenuItem()` to always order the most expensive dish |
| New | `Stage 2.drawio.png` | Stage 2 class diagram |
| New | `test-outputs/victory-output-stage-2.txt`, `test-outputs/defeat-output-stage-2.txt` | Saved transcripts of the Stage 2 runs |
| Changed | `control/ArrivalSchedule.java` | Small registration change: turn 4 creates a VIP and turn 10 a Critic instead of Regulars; still six arrivals |
| Changed | `boundary/ConsoleUI.java` | Welcome banner now says Stage 2 (display only) |
| Changed | `test-inputs/defeat.txt` | New near-win defeat run that involves the Critic |
| Changed | `.gitignore` | Ignore a local build folder |
| Changed | `README.md` | Stage 2 rules, test notes and this comparison |
| Renamed | `test-outputs/*-output.txt` → `*-output-stage-1.txt` | Keep Stage 1 transcripts beside the Stage 2 ones |
| Deleted | — | — |

`GameController`, `Customer` and the staff classes did not change: the new behaviour is dispatched through the customer objects (polymorphism), so adding customer types needed no edits to the turn logic.

### Stage 2 → Stage 3
_To be completed when `stage-3` is tagged._
Compare: https://github.com/joQ-Q/CT2003---Restaurant-Rush/compare/stage-2...stage-3

| Change | File | Reason |
|---|---|---|
| New | | |
| Changed | | |
| Deleted | | |
