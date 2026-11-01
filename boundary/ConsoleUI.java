package restaurantrush.boundary;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;
import java.util.Scanner;

import restaurantrush.entity.Customer;
import restaurantrush.entity.GameState;
import restaurantrush.entity.MenuItem;
import restaurantrush.entity.Money;
import restaurantrush.entity.Order;
import restaurantrush.entity.Table;
import restaurantrush.entity.WaiterTask;


public class ConsoleUI {

    private static final String LINE = "============================================================";

    private final Scanner scanner;
    private boolean inputEnded;

    public ConsoleUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayWelcome(GameState state) {
        System.out.println(LINE);
        System.out.println("  RESTAURANT RUSH  -  Stage 1: Basic operation");
        System.out.println(LINE);
        System.out.println("You are the manager. Staff: 1 Waiter, 1 Chef, 1 Cashier. Tables: " + GameState.TABLE_COUNT + ".");
        System.out.println("Customers arrive on turns 1, 4, 7, 10, 13 and 16.");
        System.out.println("Menu:");
        for (MenuItem item : state.getMenu().getItems()) {
            System.out.println("  - " + item);
        }
        System.out.println("Goal after turn " + GameState.MAX_TURNS + ": at least " + GameState.TARGET_PAID
                + " paid customers, revenue at least " + Money.format(GameState.TARGET_REVENUE_CENTS)
                + ", average satisfaction of paid customers at least " + GameState.TARGET_AVG_SATISFACTION + ".");
    }

    public void displayTurnStart(GameState state) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  TURN " + state.getCurrentTurn() + " / " + GameState.MAX_TURNS);
        System.out.println(LINE);
        System.out.println("Revenue: " + Money.format(state.getRevenueCents()) + "   Avg satisfaction (paid): "
                + formatAverage(state.getAverageSatisfaction()) + "   Paid: " + state.getPaidCount());

        List<Customer> queue = state.getWaitingCustomers();
        System.out.print("Waiting queue: ");
        if (queue.isEmpty()) {
            System.out.println("(empty)");
        } else {
            System.out.println();
            for (Customer c : queue) {
                System.out.println("  " + c);
            }
        }

        System.out.println("Tables:");
        for (Table t : state.getTables()) {
            if (t.isFree()) {
                System.out.println("  Table " + t.getId() + ": free");
            } else {
                Customer c = t.getOccupant();
                String order = c.getOrder() == null ? "no order yet"
                        : c.getOrder().getItem().getName() + " " + c.getOrder().getStatus() + " ("
                                + c.getOrder().getUnitsDone() + "/" + c.getOrder().getItem().getPrepUnits() + " units)";
                System.out.println("  Table " + t.getId() + ": " + c + " | " + c.getState() + " | " + order);
            }
        }

        System.out.println("Staff tasks available:");
        System.out.println("  Waiter : seat " + queue.size() + " waiting (" + (state.findFreeTable() == null ? "no" : "a")
                + " free table) | take order " + state.getCustomersToOrder().size() + " | serve "
                + state.getServableOrders().size() + " ready dish(es)");
        System.out.println("  Chef   : " + state.getOrdersInPreparation().size() + " order(s) in preparation");
        List<Customer> payers = state.getCustomersAbleToPay();
        if (payers.isEmpty()) {
            System.out.println("  Cashier: automatic, nobody can pay this turn");
        } else {
            Customer next = payers.get(0);
            System.out.println("  Cashier: automatic, will charge #" + next.getId() + " "
                    + Money.format(next.getOrder().getBillCents()) + " after the Waiter and Chef act");
        }
    }

    public WaiterTask promptWaiterTask() {
        System.out.println();
        System.out.println("Waiter task:  1) Seat a customer   2) Take an order   3) Serve a READY dish   0) Wait");
        int choice = readInt("Choose 0-3: ", 0, 3);
        switch (choice) {
            case 1:
                return WaiterTask.SEAT;
            case 2:
                return WaiterTask.TAKE_ORDER;
            case 3:
                return WaiterTask.SERVE;
            default:
                return WaiterTask.WAIT;
        }
    }

    public Customer promptCustomer(List<Customer> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + options.get(i));
        }
        System.out.println("  0) Back");
        int choice = readInt("Choose a customer: ", 0, options.size());
        return choice == 0 ? null : options.get(choice - 1);
    }


    public Order promptOrder(List<Order> options) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + options.get(i));
        }
        System.out.println("  0) Back / Wait");
        int choice = readInt("Choose an order: ", 0, options.size());
        return choice == 0 ? null : options.get(choice - 1);
    }


    public void showMessage(String message) {
        System.out.println("> " + message);
    }

    public void displayTurnSummary(GameState state) {
        System.out.println("--- End of turn " + state.getCurrentTurn() + ": served " + state.getServedCount()
                + " | unhappy departures " + state.getUnhappyDepartures() + " | revenue "
                + Money.format(state.getRevenueCents()) + " | avg satisfaction "
                + formatAverage(state.getAverageSatisfaction()) + " | turns used " + state.getCurrentTurn() + "/"
                + GameState.MAX_TURNS + " ---");
    }

    public void displayResult(GameState state) {
        OptionalDouble avg = state.getAverageSatisfaction();
        System.out.println();
        System.out.println(LINE);
        System.out.println(state.isVictory() ? "  VICTORY! The restaurant hit every target."
                : "  DEFEAT. The restaurant missed at least one target.");
        System.out.println(LINE);
        System.out.println(mark(state.getPaidCount() >= GameState.TARGET_PAID) + " Paid customers: "
                + state.getPaidCount() + " (target " + GameState.TARGET_PAID + ")");
        System.out.println(mark(state.getRevenueCents() >= GameState.TARGET_REVENUE_CENTS) + " Revenue: "
                + Money.format(state.getRevenueCents()) + " (target " + Money.format(GameState.TARGET_REVENUE_CENTS) + ")");
        System.out.println(mark(avg.orElse(0) >= GameState.TARGET_AVG_SATISFACTION) + " Average satisfaction: "
                + formatAverage(avg) + " (target " + GameState.TARGET_AVG_SATISFACTION + ")");
        System.out.println("    Unhappy departures: " + state.getUnhappyDepartures());
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (inputEnded) {
                System.out.println("0 (no more input)");
                return 0;
            }
            String line;
            try {
                line = scanner.nextLine().trim();
            } catch (NoSuchElementException e) {
                inputEnded = true;
                System.out.println("0 (no more input)");
                return 0;
            }
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("'" + line + "' is not a number. Please enter a number from " + min + " to " + max + ".");
            }
        }
    }

    private static String formatAverage(OptionalDouble avg) {
        return avg.isPresent() ? String.format("%.1f", avg.getAsDouble()) : "N/A";
    }

    private static String mark(boolean ok) {
        return ok ? "[OK]  " : "[MISS]";
    }
}