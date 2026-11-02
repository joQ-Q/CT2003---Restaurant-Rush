import java.util.Scanner;

import restaurantrush.boundary.ConsoleUI;
import restaurantrush.control.GameController;

/**
 * Entry point for Restaurant Rush (Stage 1).
 */
public class Main {

    private Main() {
        // not instantiated; use main()
    }

    /**
     * Starts a new game in the console.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameController game = new GameController(new ConsoleUI(scanner));
        game.run();
        scanner.close();
    }
}
