module com.kopyshov.hangman {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.kopyshov.hangman to javafx.fxml;
    exports com.kopyshov.hangman;
}