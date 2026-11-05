# Restaurant Rush

A turn-based restaurant management game for the command line, written in Java for CT2003 (Object-Oriented Design and Programming). The player is the manager and decides what the Host, Waiter and Chef do each turn, and when to run Happy Hour.

**Repository:** https://github.com/joQ-Q/CT2003---Restaurant-Rush

| Stage | Adds | Git tag |
|---|---|---|
| 1 Basic operation | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat | [`stage-1`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-1) |
| 2 Customer behaviour | VIP and Critic customers | [`stage-2`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-2) |
| 3 New role and menu policy | Host, ComboMeal, Happy Hour | [`stage-3`](https://github.com/joQ-Q/CT2003---Restaurant-Rush/tree/stage-3) |

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
| `staff` | `Host`, `Waiter`, `Chef`, `Cashier`, `ActionResult` | Staff actions and rule checks |
| `entity` | `GameState`, `Customer`, `RegularCustomer`, `VIPCustomer`, `CriticCustomer`, `Table`, `Order`, `Menu`, `MenuOffering`, `MenuItem`, `ComboMeal`, `HappyHour`, `Money`, `CustomerState`, `OrderStatus`, `WaiterTask` | Restaurant data |

Class diagrams: Stage 1 in `Stage-1-RestaurantRushClassDiagram.png`, Stage 2 in `Stage 2.drawio.png`, Stage 3 in `Stage 3.drawio.png`.

## Documented rules

- **Base setting:** 18 turns, 2 tables, one Host (Stage 3), one Waiter, one Chef, one Cashier; one customer arrives at the start of turns 1, 4, 7, 10, 13 and 16.
- **Menu:** Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units).
- **Combos (Stage 3):** one `ComboMeal` for every pair of different dishes. A combo costs 90% of its two dishes' combined price, rounded to the nearest cent, and needs the sum of their preparation units:

  | Combo | Price | Units |
  |---|---|---|
  | Salad + Burger | $21.60 | 2 |
  | Salad + Pasta | $24.30 | 3 |
  | Burger + Pasta | $29.70 | 3 |

  `MenuItem` and `ComboMeal` both implement `MenuOffering`, so `Order`, the Chef and the Waiter treat a combo exactly like a single dish.
- **Food choice:** customer #n orders offering (n − 1) mod 6 from the list Salad, Burger, Pasta, Salad + Burger, Salad + Pasta, Burger + Pasta (Stage 1–2: dish (n − 1) mod 3). Critics override this (see below). The choice is deterministic, so every run is reproducible.
- **Customer types (Stage 2):**

  | Customer | Satisfaction loss per waiting turn | Bill | Special behaviour |
  |---|---|---|---|
  | Regular | 8 | Full price | — |
  | VIP | 5 | 10% off, rounded to the nearest cent | — |
  | Critic | 12 | Full price | Always orders the most expensive single dish (Pasta, 2 units), never a combo, so the Chef must give the critic's order priority |

  Each type overrides `getSatisfactionLoss()`, `calculateBill()` or `chooseOffering()` (called `chooseMenuItem()` before Stage 3) from `Customer`, so the turn controller never checks the customer type.
- **Arrivals (Stage 2):** turn 1 Regular, turn 4 VIP, turn 7 Regular, turn 10 Critic, turn 13 Regular, turn 16 Regular (six arrivals).
- **Host (Stage 3):** seats at most one waiting customer per turn, in addition to the Waiter's task, using the same seating rules. The Host is only asked when someone is waiting and a table is free; 0 skips.
- **Happy Hour (Stage 3):** offered at the start of every turn until used; can be activated once per game. It lasts that turn and the next. While active:
  - orders taken get 20% off, applied after the VIP discount and rounded to the nearest cent (a VIP's $15.00 Burger → $13.50 → $10.80);
  - at the start of each active turn, every customer still waiting for a table or for food recovers 15 satisfaction, up to 100.
- **Money** is stored as integer cents and displayed with two decimals.
- **Turn order:** arrivals → start-of-turn display → Happy Hour offer and recovery → Host → Waiter → Chef → eating and payment → satisfaction loss and abandonment → end-of-turn summary.
- **Objective:** at the end of turn 18, at least 4 paid customers, revenue at least $45.00 and average satisfaction of paid customers at least 60.

## Test notes (base setting)

### Stage 3 (`stage-3`)

| Run | Input file | Saved output | Result |
|---|---|---|---|
| Victory | `test-inputs/victory.txt` | `test-outputs/victory-output-stage-3.txt` | 5 paid, revenue $82.80, average satisfaction 84.6, 0 unhappy departures → **VICTORY** |
| Happy Hour + VIP | `test-inputs/happy-hour-vip.txt` | `test-outputs/happy-hour-vip-output-stage-3.txt` | Happy Hour activated on turn 4; VIP #2 pays $10.80 for a $15.00 Burger. 5 paid, revenue $80.10, average satisfaction 85.6 → **VICTORY** |
| Defeat | `test-inputs/defeat.txt` | `test-outputs/defeat-output-stage-3.txt` | Waiter waits on turns 3, 6, 8, 9 and 12–16. 3 paid, revenue $40.50 → **DEFEAT** (paid customers and revenue missed) |

All three runs use the Host every time a customer is waiting and a table is free, and include combo orders: customer #5 orders Salad + Pasta ($24.30, 3 units).

**Victory strategy:** the Host seats each arrival, so the Waiter spends its task taking orders and serving instead of seating. Happy Hour is not needed to win.

**Happy Hour story:** activating Happy Hour on turn 4, when the VIP arrives, stacks the two discounts (10% then 20%) on the VIP's order and keeps waiting customers at full satisfaction. Revenue drops by $2.70 compared with the victory run, but the average satisfaction rises from 84.6 to 85.6.

**Defeat story:** the Host still seats everyone, but the Waiter keeps hesitating. The Critic (#4) orders on turn 11 and the Pasta can be served from turn 13, but the Waiter does not serve it until turn 17, so the Critic is still eating when the game ends and never pays. Only three customers pay, which misses both the paid-customer and revenue targets. A Host cannot make up for an idle Waiter, because only the Waiter takes orders and serves.

The Stage 1 and Stage 2 input files no longer match the Stage 3 prompts (Happy Hour and Host add questions), so they were rewritten for Stage 3. To replay an earlier stage, check out its tag first.

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
Compare: https://github.com/joQ-Q/CT2003---Restaurant-Rush/compare/stage-2...stage-3

| Change | File | Reason |
|---|---|---|
| New | `staff/Host.java` | New staff role: seats one waiting customer per turn, in addition to the Waiter |
| New | `entity/MenuOffering.java` | Interface for anything that can be ordered (name, price, preparation units) |
| New | `entity/ComboMeal.java` | Implements `MenuOffering`; aggregates two `MenuItem`s, 90% of their price, sum of their units |
| New | `entity/HappyHour.java` | Once-per-game policy: 2 turns, 20% off orders taken, +15 satisfaction recovery while waiting |
| New | `Stage 3.drawio.png` | Stage 3 class diagram |
| New | `test-inputs/happy-hour-vip.txt` | Run that activates Happy Hour when the VIP arrives |
| New | `test-outputs/*-output-stage-3.txt` | Saved transcripts of the three Stage 3 runs |
| Changed | `entity/MenuItem.java` | Now implements `MenuOffering` |
| Changed | `entity/Menu.java` | Builds the three combos; `getOfferings()` returns dishes then combos |
| Changed | `entity/Order.java` | Holds a `MenuOffering` instead of a `MenuItem`, so a combo is cooked and served like a dish |
| Changed | `entity/Customer.java` | `chooseMenuItem()` → `chooseOffering()` over all six offerings; new `recoverSatisfaction()` for Happy Hour |
| Changed | `entity/CriticCustomer.java` | Overrides `chooseOffering()`; still picks the most expensive single dish |
| Changed | `entity/GameState.java` | Owns the game's `HappyHour` |
| Changed | `staff/Waiter.java` | Takes orders as `MenuOffering`s and applies the Happy Hour discount after the customer's own bill rule |
| Changed | `control/GameController.java` | Adds the Happy Hour offer and recovery and the Host step to the turn sequence |
| Changed | `boundary/ConsoleUI.java` | Stage 3 banner, combos in the menu, Happy Hour status and prompt, Host line in the staff summary |
| Changed | `test-inputs/victory.txt`, `test-inputs/defeat.txt` | Rewritten for the extra Happy Hour and Host prompts |
| Changed | `Main.java` | Comment no longer says Stage 1 |
| Changed | `README.md` | Stage 3 rules, test notes and this comparison |
| Deleted | — | — |

Unlike Stage 2, this stage had to change `GameController`, because the Host and Happy Hour are new steps in the turn. The combos needed no change to the Chef or the turn logic: they reach the kitchen through the `MenuOffering` interface.
