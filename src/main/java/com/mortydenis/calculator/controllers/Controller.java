package com.mortydenis.calculator.controllers;

import com.mortydenis.calculator.models.Model;
import com.mortydenis.calculator.services.JDBCPostgreSQL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private final int MAX_LENGTH_NUMBER = 14;
    private boolean historyPanIsActive;
    private JDBCPostgreSQL database;
    private Model model = new Model();
    private boolean startCalculating;
    private boolean expectedNumber;
    private Double firstOperand;
    private Double secondOperand;
    private Double result;
    private String operator;

    @FXML
    private Pane historyPane;

    @FXML
    private ListView<String> operationList;

    @FXML
    private Label numberLine;

    @FXML
    private Label fullCalculationLine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preparationForWork();
    }

    private void preparationForWork() {
        this.database = JDBCPostgreSQL.getInstance();
        this.model = new Model();
        this.startCalculating = true;
        this.expectedNumber = true;
        this.firstOperand = 0.;
        this.secondOperand = 0.;
        this.result = 0.;
        this.operator = null;
        numberLine.setText("0");
        fullCalculationLine.setText("");
    }

    @FXML
    public void numberListener(ActionEvent actionEvent) {
        if (numberLine.getText().length() <= MAX_LENGTH_NUMBER) {
            if (startCalculating) {
                cleanAllLinesListener();
                startCalculating = false;
            }
            if (expectedNumber) {
                numberLine.setText("");
                expectedNumber = false;
            }
            String number = ((Button)actionEvent.getSource()).getText();
            numberLine.setText(numberLine.getText() + number);
        }
    }

    @FXML
    public void numberModifierListener(ActionEvent actionEvent) {
        if (!numberLine.getText().isEmpty()) {
            String modifier = ((Button)actionEvent.getSource()).getText();
            switch (modifier) {
                case "+/-":
                    double number = Double.parseDouble(numberLine.getText());
                    numberLine.setText(String.valueOf(number * -1));
                    break;
                case ".":
                    if (!numberLine.getText().contains(".")) {
                        numberLine.setText(numberLine.getText() + modifier);
                    }
                    break;
            }
        }
    }

    @FXML
    public void commandListener(ActionEvent actionEvent) {
        String command = ((Button)actionEvent.getSource()).getText();
        if (operator == null) {
            firstOperand = Double.parseDouble(numberLine.getText());
        } else {
            if (expectedNumber){
                return;
            }
            calculate();
            firstOperand = result;
        }
        operator = command;
        preparationNumberAndCalculationLine(command);
    }

    public void resultListener() {
        if (operator != null && !startCalculating) {
            calculate();
            preparationNumberAndCalculationLine("=");
            database.saveOperation(fullCalculationLine.getText() + result);
            startCalculating = true;
        }
    }

    private void calculate(){
        secondOperand = Double.parseDouble(numberLine.getText());
        result = model.calculation(firstOperand, secondOperand, operator);
        numberLine.setText(String.valueOf(result));
    }

    private void preparationNumberAndCalculationLine(String command) {
        fullCalculationLine.setText(fullCalculationLine.getText() + (secondOperand == 0. ? firstOperand : secondOperand) + command);
        expectedNumber = true;
    }

    public void backSpaceNumberLineListener() {
        String number = numberLine.getText();
        if (number.length() > 0) {
            numberLine.setText(number.substring(0, number.length() - 1));
        }
    }

    public void cleanNumberLineListener() {
        numberLine.setText("0");
        expectedNumber = true;
    }

    public void cleanAllLinesListener() {
        preparationForWork();
    }

    public void showHistory() {
        if (historyPanIsActive) {
            backToMainPane();
        } else {
            prepareHistoryPane();
        }
    }

    private void backToMainPane(){
        historyPane.toBack();
        historyPane.setVisible(false);
        historyPane.setDisable(true);
        historyPanIsActive = false;
    }

    private void prepareHistoryPane() {
        historyPane.toFront();
        historyPane.setVisible(true);
        historyPane.setDisable(false);
        ObservableList<String> operation = FXCollections.observableArrayList(database.getHistoryOperations());
        operationList.setItems(operation);
        historyPanIsActive = true;
    }
}