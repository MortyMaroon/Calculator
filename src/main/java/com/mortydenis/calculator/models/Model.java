package com.mortydenis.calculator.models;

import java.math.BigDecimal;

public class Model {

    public Double calculation(Double first, Double second, String operator) {
        BigDecimal firstOperand = BigDecimal.valueOf(first);
        BigDecimal secondOperand = BigDecimal.valueOf(second);
        switch (operator) {
            case "+":
                return firstOperand.add(secondOperand).doubleValue();
            case "-":
                return firstOperand.subtract(secondOperand).doubleValue();
            case "x":
                return firstOperand.multiply(secondOperand).doubleValue();
            case "/":
                return secondOperand.equals(0) ? 0. : firstOperand.divide(secondOperand).doubleValue();
            default:
                return 0.;
        }
    }
}
