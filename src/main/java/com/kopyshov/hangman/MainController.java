package com.kopyshov.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static com.kopyshov.hangman.WordsData.createWords;

public class MainController implements Initializable {
    @FXML
    TextArea terminal;
    @FXML
    TextField scannerLetter;
    @FXML
    public HBox secretWord;
    @FXML
    public Shape head;
    @FXML
    public Shape body;
    @FXML
    public Shape leftHand;
    @FXML
    public Shape rightHand;
    @FXML
    public Shape leftLeg;
    @FXML
    public Shape rightLeg;
    LinkedList<Shape> littleHuman = new LinkedList<>();
    int matches = 0;
    private static String randomWord;
    char[] splitRandomWord = new char[0];
    private static int countTry = 6;
    private final ArrayList<String> arrayWords = new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createWords();
        newGame();
        scannerLetterListener();
        setMessage();
    }
    public void newGame() {
        setMessage();
        countTry = 6;
        scannerLetter.setDisable(false);
        littleHuman.clear();
        littleHuman.add(head);
        littleHuman.add(body);
        littleHuman.add(leftHand);
        littleHuman.add(rightHand);
        littleHuman.add(leftLeg);
        littleHuman.add(rightLeg);
        head.setVisible(false);
        body.setVisible(false);
        leftHand.setVisible(false);
        rightHand.setVisible(false);
        leftLeg.setVisible(false);
        rightLeg.setVisible(false);

        randomWord = WordsData.wordsData.get((int) (Math.random() * WordsData.wordsData.size()));
        System.out.println(randomWord);
        splitRandomWord = randomWord.toCharArray();
        addRandomWordToSecretWord();
    }
    private void scannerLetterListener() {
        Pattern pattern = Pattern.compile("[а-яА-ЯёЁ]");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
            change.setText(change.getText().toUpperCase());
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });
        scannerLetter.setTextFormatter(formatter);
        scannerLetter.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                String enteredChar = scannerLetter.getText();
                terminal.appendText("Введена буква " + enteredChar + "\n");
                scannerLetter.setTextFormatter(null);
                scannerLetter.setText("");
                scannerLetter.setTextFormatter(formatter);
                compareCharWithRndWord(enteredChar);
            }
            if (keyEvent.getCode() == KeyCode.BACK_SPACE || keyEvent.getCode() == KeyCode.DELETE){
                scannerLetter.setTextFormatter(null);
                scannerLetter.setText("");
                scannerLetter.setTextFormatter(formatter);
            }
        });
    }
    private void addRandomWordToSecretWord() {
        secretWord.getChildren().clear();
        for (Character l : splitRandomWord) {
            Label rndWord = new Label(l.toString().toUpperCase());
            rndWord.setVisible(false);
            rndWord.setFont(new Font("Arial",20));
            rndWord.setStyle("-fx-font-weight: bold");
            StackPane rndStackPane = new StackPane();
            rndStackPane.setStyle("-fx-border-color: white; -fx-background-color: black;");
            rndStackPane.setPadding(new Insets(5));
            rndStackPane.setMinSize(35, 35);
            rndStackPane.getChildren().add(rndWord);
            secretWord.getChildren().add(rndStackPane);
        }
    }
    private void compareCharWithRndWord(String enteredChar) {
        System.out.println(enteredChar);
        if (randomWord.toUpperCase().contains(enteredChar)) {
            terminal.appendText("Угадал!\n");
            openCharacter(enteredChar);
            matches = 0;
            for(int i = 0; i < randomWord.length(); i++){
                Label labelI = (Label) ((StackPane) secretWord.getChildren().get(i)).getChildren().get(0);
                if (labelI.isVisible()) {
                    matches++;
                    if (countTry == 1 & countTry == matches) {
                        terminal.appendText("Человечек вспотел!");
                    }
                }
            }
            if(matches == randomWord.length()) {
                terminal.appendText("Выиграл! Ты молодец!!\n"
                                        + "Чтобы начать новую игру \n" +
                                        "нажми File -> New Game");
                terminal.end();
                scannerLetter.setDisable(true);
            }
        } else {
            littleHuman.getFirst().setVisible(true);
            littleHuman.removeFirst();
            --countTry;

            if(countTry != 0) {
                if (countTry == 1 & matches < randomWord.length() / 2) {
                    terminal.appendText("Ошибся. Количество оставшихся попыток: " + (countTry) +
                            ".\nМне кажется человечка уже не спасти.\n");
                    terminal.end();
                } else {
                    terminal.appendText("Ошибся. Количество оставшихся попыток: " + (countTry) + "\n");
                    terminal.end();
                }
            } else {
                scannerLetter.setDisable(true);
                for (int i = 0; i < randomWord.length(); i++) {
                    Label labelI = (Label) ((StackPane) secretWord.getChildren().get(i)).getChildren().get(0);
                    labelI.setVisible(true);
                    labelI.getParent().setStyle("-fx-border-color: black; -fx-background-color: white;");
                }
                terminal.appendText("Человечек повесился. Человечка жалко...\n" +
                    "Секретное слово: " + randomWord + "\n" +
                    "Чтобы начать новую игру нажмите\n" +
                    "File -> New Game\n");
                terminal.end();
            }
        }
    }

    private void openCharacter(String enteredChar) {
        for(int i = 0; i < randomWord.length(); i++){
            Label labelI = (Label) ((StackPane) secretWord.getChildren().get(i)).getChildren().get(0);
            if (labelI.getText().equals(enteredChar)) {
                labelI.setVisible(true);
                labelI.getParent().setStyle("-fx-border-color: black; -fx-background-color: white;");
            }
        }
    }
    private void setMessage() {
        terminal.setText("Здравствуйте! Введите букву и нажмите Enter.\n");

    }

    public void startNewGame(ActionEvent actionEvent) {
        newGame();
    }

    public void exitGame(ActionEvent actionEvent) {
        System.exit(0);
    }
}
