import java.util.Random;  // Importing Random class for generating random numbers
import javafx.application.Application;  // Import for JavaFX Application class
import javafx.scene.Scene;  // Import for JavaFX Scene class
import javafx.scene.control.Alert;  // Import for displaying Alerts
import javafx.scene.control.Button;  // Import for creating Buttons
import javafx.scene.control.Alert.AlertType;  // Import for specifying the type of alert
import javafx.scene.layout.BorderPane;  // Import for creating a BorderPane layout
import javafx.scene.layout.GridPane;  // Import for creating a GridPane layout
import javafx.stage.Stage;  // Import for handling window (stage) in JavaFX

public class Minesweeper extends Application {

    // Constants defining grid size and number of mines
    private static final int GRID_SIZE = 12;  // Size of the grid (12x12)
    private static final int NUM_MINES = 8;  // Number of mines to be placed in the grid
    private Button[][] buttons = new Button[GRID_SIZE][GRID_SIZE];  // Button array for each grid cell
    private boolean[][] mines = new boolean[GRID_SIZE][GRID_SIZE];  // Array to store mine positions
    private boolean[][] flagged = new boolean[GRID_SIZE][GRID_SIZE];  // Array to store flag statuses
    private int cellsRevealed = 0;  // Counter for cells revealed

    // Main entry point of the program, launches the application
    public static void main(String[] args) {
        launch(args);  // Launches the JavaFX application
    }

    // Override the start method to setup the main stage and user interface
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MINESWEEPER");  // Set the title of the game window
        BorderPane root = new BorderPane();  // Create a BorderPane layout
        GridPane grid = new GridPane();  // Create a GridPane layout for the game grid
        initializeGame(grid);  // Initialize the game by setting up the grid and mines

        // Button to restart the game
        Button restartButton = new Button("RESTART");
        restartButton.setOnAction(e -> initializeGame(grid));  // Restart the game on button click
        root.setBottom(restartButton);  // Place restart button at the bottom of the window
        root.setCenter(grid);  // Place the grid in the center of the window

        Scene scene = new Scene(root, 600.0, 650.0);  // Creating the scene with the layout
        primaryStage.setScene(scene);  // Set the scene for the primary stage
        primaryStage.show();  // Show the game window
    }

    // Initialize the game by setting up the grid and resetting variables
    private void initializeGame(GridPane grid) {
        grid.getChildren().clear();  // Clear any existing children (buttons)
        mines = new boolean[GRID_SIZE][GRID_SIZE];  // Reset the mine positions
        flagged = new boolean[GRID_SIZE][GRID_SIZE];  // Reset the flagged positions
        buttons = new Button[GRID_SIZE][GRID_SIZE];  // Reset the button array
        cellsRevealed = 0;  // Reset the revealed cells counter
        initializeMines();  // Call the method to randomly place mines
        initializeButtons(grid);  // Call the method to initialize buttons for each grid cell
    }

    // Randomly place mines on the grid
    private void initializeMines() {
        Random random = new Random();  // Create a Random object for generating random numbers
        int placedMines = 0;  // Counter for placed mines

        // Loop until all mines are placed on the grid
        while (placedMines < NUM_MINES) {
            int row = random.nextInt(GRID_SIZE);  // Random row position
            int col = random.nextInt(GRID_SIZE);  // Random column position
            if (!mines[row][col]) {  // If the cell does not already contain a mine
                mines[row][col] = true;  // Place a mine in the current cell
                placedMines++;  // Increment the placed mines counter
            }
        }
    }

    // Initialize the buttons for each cell in the grid
    private void initializeButtons(GridPane grid) {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                Button button = new Button();  // Create a new button for the current cell
                button.setPrefSize(50.0, 50.0);  // Set the preferred size for the button
                int r = row;  // Capture row and column for the lambda expression
                int c = col;

                // Action on button click: reveal the cell
                button.setOnAction(e -> handleClick(r, c));

                // Action on right click: toggle the flag on the cell
                button.setOnMousePressed(e -> {
                    if (e.isSecondaryButtonDown()) {
                        toggleFlag(r, c);  // Toggle the flag on right click
                    }
                });

                buttons[row][col] = button;  // Store the button in the button array
                grid.add(button, col, row);  // Add the button to the grid at the appropriate position
            }
        }
    }

    // Handle the click on a cell
    private void handleClick(int row, int col) {
        if (!flagged[row][col]) {  // Only reveal cell if it's not flagged
            if (mines[row][col]) {  // If the clicked cell contains a mine
                revealAllMines();  // Reveal all mines
                showAlert("GAME OVER", "BOOOM! YOU CLICKED ON A MINE.\nRestart the game by clicking the 'Restart' button below!");
            } else {
                revealCell(row, col);  // Otherwise, reveal the cell and check its surroundings
            }
        }
    }

    // Reveal the content of a cell and update the UI
    private void revealCell(int row, int col) {
        int surroundingMines = countSurroundingMines(row, col);  // Counting mines around the cell
        buttons[row][col].setText(surroundingMines == 0 ? "" : String.valueOf(surroundingMines));  // Set the text of the button based on the surrounding mines
        buttons[row][col].setDisable(true);  // Disable the button once clicked
        cellsRevealed++;  // Increment the cells revealed counter

        // Check if all non-mine cells are revealed
        int totalCellsWithoutMines = GRID_SIZE * GRID_SIZE - NUM_MINES;
        if (cellsRevealed == totalCellsWithoutMines) {
            showAlert("CONGRATULATIONS!", "YOU WON!\nRestart the game to play again!");
        }

        if (surroundingMines == 0) {
            revealAdjacentCells(row, col);  // If no surrounding mines, reveal adjacent cells
        }
    }

    // Count how many mines are surrounding a given cell
    private int countSurroundingMines(int row, int col) {
        int count = 0;  // Counter for surrounding mines

        // Loop through the surrounding cells (including diagonals)
        for (int r = row - 1; r <= row + 1; ++r) {
            for (int c = col - 1; c <= col + 1; ++c) {
                if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE && mines[r][c]) {
                    count++;  // Increment count if a mine is found
                }
            }
        }
        return count;
    }

    // Reveal all the mines on the grid (used when the player loses)
    private void revealAllMines() {
        for (int row = 0; row < GRID_SIZE; ++row) {
            for (int col = 0; col < GRID_SIZE; ++col) {
                if (mines[row][col]) {
                    buttons[row][col].setText("\ud83d\udca3");  // Set a mine emoji for visual representation
                    buttons[row][col].setDisable(true);  // Disable the button
                }
            }
        }
    }

    // Recursively reveal adjacent cells if there are no surrounding mines
    private void revealAdjacentCells(int row, int col) {
        for (int r = row - 1; r <= row + 1; ++r) {
            for (int c = col - 1; c <= col + 1; ++c) {
                if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE && !buttons[r][c].isDisabled() && !flagged[r][c]) {
                    revealCell(r, c);  // Reveal the adjacent cell
                }
            }
        }
    }

    // Toggle the flag on a cell when right-clicked
    private void toggleFlag(int row, int col) {
        if (!buttons[row][col].isDisabled()) {  // Only allow flagging if the cell is not revealed
            flagged[row][col] = !flagged[row][col];  // Toggle the flagged state
            buttons[row][col].setText(flagged[row][col] ? "\ud83d\udea9" : "");  // Set flag emoji if flagged
        }
    }

    // Show a pop-up alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);  // Create an information alert
        alert.setTitle(title);  // Set the title of the alert
        alert.setHeaderText(null);  // No header text
        alert.setContentText(message);  // Set the content of the alert
        alert.showAndWait();  // Show the alert and wait for the user to close it
    }
}

/*
 A simple JavaFX game based Minesweeper where players click cells on a 12x12 grid,
 avoiding mines. It features a restart button, mine placement,
 flagging with right click, and reveals surrounding cells with no adjacent mines.
 If a mine is clicked, the game ends. Players win by uncovering all safe cells.
  */