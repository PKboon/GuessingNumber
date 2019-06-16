/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gnum;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Guessing Number Game
 *
 * Create a random number between 1 and 1000. Display images to tell the user
 * that the number is lower, hight, or correct. Print history to the screen.
 * Write the random number, and all the guesses to a file. If the user doesn't
 * guess in 10 turns, display the number. Implement a Restart option - should
 * wipe out current game best score. Implement a Play again option - does not
 * wipe out current game best score. Implement Best Guess statistic - the less,
 * the better. Implement error handling for guess out of the range.
 *
 * @author Pikulkaew Boonpeng
 * @version 19/04/2019
 * @since 14/04/2019
 */
public class Gnum extends Application {

    // generate random number from 1-1000
    int theNumber = (int) (Math.random() * 1000 + 1);

    private int space = 10; // for easy setting space
    private VBox gameField = new VBox(space); // for easy center stuff

    // line1: tells what game it is
    private Label line1 = new Label("    Welcome to Guess the Number Game");

    // line2: announce stat
    private HBox line2 = new HBox();
    private int countGuess = 0;
    private int newBest = 11; // because it cannot be more than 10
    private Label bestGuessLbl = new Label("   Best Guess: ");
    private Label bestGuessValue = new Label();

    // line3: tells how many tries the player has left
    private HBox line3 = new HBox();
    private int tryLeft = 10;
    private Label tryLeftLbl = new Label("   Number of tries left: ");
    private Label tryLeftValue = new Label(Integer.toString(tryLeft));

    // line4: tells the range
    private Label line4 = new Label("Guess Number from 1 to 1000");

    // line5: textField
    private TextField inputTf = new TextField();
    int guess;

    // line6: all the button
    private HBox line6 = new HBox(space);
    private Button guessBtn = new Button("   Guess   ");
    private Button restartBtn = new Button("   Restart   ");
    private Button playAgnBtn = new Button("Play Again");

    // for gameEndText()
    private Label key = new Label(" ");

    // hintBox contends picture and history
    private HBox hintBox = new HBox(space * 10);
    // picture part
    private Label picture = new Label("");
    private ImageView picWon = new ImageView(new Image("https://www.nicepng.com/png/full/485-4854233_you-win-art.png"));
    private Image picArrow = new Image("http://pixelartmaker.com/art/0fc29bffe9ca6e5.png");
    private ImageView picLow = new ImageView(picArrow);
    private ImageView picHigh = new ImageView(picArrow);
    // history part
    private VBox histBox = new VBox(space / 2);
    Label histHead = new Label("\t  History: ");
    Label hist1 = new Label("");
    Label hist2 = new Label("");
    Label hist3 = new Label("");
    Label hist4 = new Label("");
    Label hist5 = new Label("");
    Label hist6 = new Label("");
    Label hist7 = new Label("");
    Label hist8 = new Label("");
    Label hist9 = new Label("");
    Label hist10 = new Label("");

    // For file stuff and history
    // java.io.File file = new java.io.File("record.txt");
    private final int ANS_MAX = 10;     // max amount of guess
    private final int GAME_MAX = 100;   // max amount of playAgnBtn
    private final int BOARD_MAX = 500; // max amount of restartBtn
    int ans;
    int game = 0;
    int board = 0;
    private int[][][] recordArr = new int[BOARD_MAX][GAME_MAX][ANS_MAX];

    /**
     * For game started
     *
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        settings(); // call setting to the screen
        Pane boardPane = new Pane();    // pane for game
        boardPane.getChildren().add(gameField); // put gameField to this pane

        // loop the actions while less than 100 games
        if (board < BOARD_MAX) {
            // set guessBtnAction to guessBtn
            guessBtn.setOnAction(e -> {
                try {
                    guessBtnAction();
                } catch (Exception ex) {
                    Logger.getLogger(Gnum.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            // set restartBtnAction to restartBtn
            restartBtn.setOnAction(e -> {
                restartBtnAction();
            });
            // set playAgnAction to playAgnBtn
            playAgnBtn.setOnAction(e -> playAgnBtnAction());
        }

        clearFile(); // clear file when game is open again

        Scene scene = new Scene(boardPane, 600, 640);
        primaryStage.setResizable(false); // cannot resize the window
        primaryStage.setTitle("Guess the Number Game by PK");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Actions for guess button
     *
     * @throws Exception
     */
    public void guessBtnAction() {

        if (inputTf.getText().isEmpty() || inputTf.getText() instanceof String) {
            // when player enters either nothing or String
            line4.setTextFill(Color.
                    color(Math.random(), Math.random(), Math.random()));
        } else {
            // get number from player
            guess = Integer.parseInt(inputTf.getText());

            if (isValidNumber(guess)) { // check if the number is valid
                ans++;  // for recordArr
                recordArr[board][game][ans - 1] = guess; // put guess in recordArr
                toFile();   // put guess to file
                toHistScreen(board, game);  // show history

                // reset range line color when player get the number in [1,1000] 
                line4.setTextFill(Color.BLUEVIOLET);
                countGuess++;   // guess counter for newBest stat
                if (countGuess > 10) {
                    countGuess = 10;
                }

                // when tryLeft is more than 0
                if (tryLeft > 0) {
                    if (isMatch()) {
                        bestGuess(); // record best guess
                        gameEndText();   // show the number
                        hintBox.getChildren().set(1, picWon); // show picture                   
                        guessBtn.setDisable(true);  // disable guessBtn
                    } else if (guess < theNumber) {
                        lostTry();   // decrease tryLeft
                        if (tryLeft == 0) {  // when tryLeft is 0
                            bestGuess(); // record best guess
                            gameEndText();   // show the number
                            guessBtn.setDisable(true);  // disable guessBtn
                        }
                        hintBox.getChildren().set(1, picHigh); // show picture
                    } else if (guess > theNumber) {
                        lostTry();   // decrease tryLeft
                        if (tryLeft == 0) {  // when tryLeft is 0
                            bestGuess(); // record best guess
                            gameEndText();   // show the number
                            guessBtn.setDisable(true);  // disable guessBtn
                        }
                        hintBox.getChildren().set(1, picLow); // show picture
                    }
                }
            } else {
                // when player enters the number that is not in [1,1000]
                line4.setTextFill(Color.
                        color(Math.random(), Math.random(), Math.random()));
            }
        }
    }

    /**
     * Actions for BOTH restart and play again buttons
     *
     */
    public void RestartAndPlayAgnActions() {
        guessBtn.setDisable(false);     // enable guessBtn
        theNumber = (int) (Math.random() * 1000 + 1); // generate new number
        tryLeft = 10;       // set tryLeft to the max
        tryLeftValue.setText(Integer.toString(tryLeft));
        countGuess = 0;     // set guess counter to the beginning
        inputTf.setText("");// empty text field
        key.setText("  ");  // empty answer line         
        clearHistScreen();    // clear history
        hintBox.getChildren().set(1, new Label(""));    // set no pictures
        ans = 0;    // reset to the first ans
    }

    /**
     * Actions for restart button
     */
    public void restartBtnAction() {
        RestartAndPlayAgnActions();
        bestGuessValue.setText("");
        newBest = 11;       // reset stat 
        game = 0;   // reset to the first game
        board++;    // change the board, focus on another board's stat
    }

    /**
     * Actions for play again button
     */
    public void playAgnBtnAction() {
        RestartAndPlayAgnActions();
        game++;     // change to a new game, still have the current game's stat
    }

    /**
     * Shows the number when the game ended
     */
    public void gameEndText() {
        // tells if player could make it and display theNumber
        if (isMatch()) {
            key.setText("Congrats! The number was " + theNumber);
        } else {
            key.setText("Game Over. The number was " + theNumber);
        }
    }

    /**
     * Check if guess matches theNumber
     *
     * @return true if matches
     */
    public boolean isMatch() {
        if (guess == theNumber) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Decreases tryLeft every missed guess
     */
    public void lostTry() {
        tryLeft--;
        tryLeftValue.setText(Integer.toString(tryLeft));
    }

    /**
     * Find best score
     */
    public void bestGuess() {
        if (newBest > countGuess) { // if counter less than newBest
            newBest = countGuess;   // assign counter to newBest
            if (newBest == 10) {
                if (!isMatch()) {
                    bestGuessValue.setText("");
                }
            } else {
                bestGuessValue.setText(Integer.toString(newBest));
            }
        } else if (newBest <= countGuess) {
            // show newBest if counter is more than newBest
            bestGuessValue.setText(Integer.toString(newBest));
        }
    }

    /**
     * Checks if the number enter is in the range of [1,1000]
     *
     * @param number The guess that the user input
     * @return true if the number is in the range
     */
    public boolean isValidNumber(int number) {
        // check if guess is out of [1,1000]
        if (number < 1 || number > 1000) {
            return false;
        }
        return true;
    }

    /**
     * Writes the number and all the guesses of each game of each board to the
     * file
     */
    public void toFile() {
        // create the fileWriter here instead of the File - use ", true" to append
        try (FileWriter fw = new FileWriter("records.txt", true);
                // apparently FileWriter prefers to be buffered
                BufferedWriter bw = new BufferedWriter(fw);
                // now create the PrintWriter.
                PrintWriter output = new PrintWriter(bw);) {
            // changed to println so it comes out nicer
            if (ans - 1 == 0) {
                output.println("\nBoard" + (board + 1) + " Game" + (game + 1) + ": The number was " + theNumber);
            }
            output.println("Guess" + ans + ": " + guess);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    /**
     * Clear records when the program runs again
     *
     * @throws IOException
     */
    public void clearFile() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("records.txt"));
        writer.close();
    }

    /**
     * Shows all the guesses in the current game
     *
     * @param board Set the number of board /restartBtn
     * @param game Set the number of game /playAgnBtn
     */
    public void toHistScreen(int board, int game) {
        if (recordArr[board][game][0] > 0) {
            hist1.setText("\t   # 1: " + String.valueOf(recordArr[board][game][0]));
        }
        if (recordArr[board][game][1] > 0) {
            hist2.setText("\t   # 2: " + String.valueOf(recordArr[board][game][1]));
        }
        if (recordArr[board][game][2] > 0) {
            hist3.setText("\t   # 3: " + String.valueOf(recordArr[board][game][2]));
        }
        if (recordArr[board][game][3] > 0) {
            hist4.setText("\t   # 4: " + String.valueOf(recordArr[board][game][3]));
        }
        if (recordArr[board][game][4] > 0) {
            hist5.setText("\t   # 5: " + String.valueOf(recordArr[board][game][4]));
        }
        if (recordArr[board][game][5] > 0) {
            hist6.setText("\t   # 6: " + String.valueOf(recordArr[board][game][5]));
        }
        if (recordArr[board][game][6] > 0) {
            hist7.setText("\t   # 7: " + String.valueOf(recordArr[board][game][6]));
        }
        if (recordArr[board][game][7] > 0) {
            hist8.setText("\t   # 8: " + String.valueOf(recordArr[board][game][7]));
        }
        if (recordArr[board][game][8] > 0) {
            hist9.setText("\t   # 9: " + String.valueOf(recordArr[board][game][8]));
        }
        if (recordArr[board][game][9] > 0) {
            hist10.setText("\t   #10: " + String.valueOf(recordArr[board][game][9]));
        }
    }

    /**
     * Clears history on the screen
     */
    public void clearHistScreen() {
        hist1.setText("");
        hist2.setText("");
        hist3.setText("");
        hist4.setText("");
        hist5.setText("");
        hist6.setText("");
        hist7.setText("");
        hist8.setText("");
        hist9.setText("");
        hist10.setText("");
    }

    /**
     * Interface settings
     */
    private void settings() { // layout settings

        // put everty thing in gameField and set them to center
        gameField.setAlignment(Pos.CENTER);
        gameField.getChildren().addAll(line1, line2, line3,
                line4, inputTf, line6, key, hintBox);

        // set history box
        hintBox.getChildren().addAll(histBox, picture);
        histBox.getChildren().addAll(histHead, hist1, hist2, hist3, hist4,
                hist5, hist6, hist7, hist8, hist9, hist10);
        histHead.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        hist1.setFont(Font.font("Arial", 18));
        hist2.setFont(Font.font("Arial", 18));
        hist3.setFont(Font.font("Arial", 18));
        hist4.setFont(Font.font("Arial", 18));
        hist5.setFont(Font.font("Arial", 18));
        hist6.setFont(Font.font("Arial", 18));
        hist7.setFont(Font.font("Arial", 18));
        hist8.setFont(Font.font("Arial", 18));
        hist9.setFont(Font.font("Arial", 18));
        hist10.setFont(Font.font("Arial", 18));

        picWon.setTranslateY(space * 4);
        picWon.setFitWidth(260);
        picWon.setFitHeight(216);
        picLow.setTranslateY(space * 4);
        picLow.setFitWidth(238);
        picLow.setFitHeight(216);
        picLow.setRotate(180);
        picHigh.setTranslateY(space * 4);
        picHigh.setFitWidth(238);
        picHigh.setFitHeight(216);
        line1.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        line1.setTextFill(Color.BLUEVIOLET);
        line2.getChildren().addAll(bestGuessLbl, bestGuessValue);
        bestGuessLbl.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        bestGuessValue.setFont(Font.font("Arial", 26));
        line3.getChildren().addAll(tryLeftLbl, tryLeftValue);
        tryLeftLbl.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        tryLeftValue.setFont(Font.font("Arial", 26));
        line4.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        line4.setTextFill(Color.BLUEVIOLET);
        inputTf.setFont(Font.font(26));
        inputTf.setMaxWidth(150);
        inputTf.setAlignment(Pos.CENTER);
        key.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        key.setTextFill(Color.DEEPSKYBLUE);
        line6.getChildren().addAll(restartBtn, guessBtn, playAgnBtn);
        line6.setAlignment(Pos.CENTER);
        guessBtn.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        guessBtn.setTextFill(Color.DARKRED);
        restartBtn.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        playAgnBtn.setFont(Font.font("Arial", FontWeight.BOLD, 26));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
