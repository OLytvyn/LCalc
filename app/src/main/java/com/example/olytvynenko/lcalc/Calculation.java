package com.example.olytvynenko.lcalc;

import java.math.BigDecimal;
import java.util.List;

public class Calculation {



    public String doCalc ( List<Double> numbers, List<String> actions ) {
        double result = 0;
        for ( int i  = 0; i < actions.size(); i++ ) {
            if (actions.get(i).equals("×") | actions.get(i).equals("÷")) {
                result = doAction(BigDecimal.valueOf(numbers.get(i)), BigDecimal.valueOf(numbers.get(i + 1)), actions.get(i));
                if (result==Double.POSITIVE_INFINITY) return "Infinity";
                numbers.set(i, 0.0d);
                numbers.set(i + 1, result);
                actions.set( i, i==0 ? "+" : actions.get(i - 1));// if 1st action is "×" OR "÷" -> i-1 = -1 <- index doesn't exist
            }
        }
        for ( int i = 0; i < actions.size(); i++ ) {
            result = doAction(BigDecimal.valueOf(numbers.get(i)), BigDecimal.valueOf(numbers.get(i + 1)), actions.get(i));
            if (result==Double.POSITIVE_INFINITY) return "Infinity";
            numbers.set(i + 1, result);
        }
        return String.valueOf(result);
    }

    private double doAction(BigDecimal numberOne, BigDecimal numberTwo, String theAction) {
        switch ( theAction ) {
            case "+" :
                return numberOne.add(numberTwo).doubleValue();
            case "-" :
                return numberOne.subtract(numberTwo).doubleValue();
            case "×" :
                return numberOne.multiply(numberTwo).doubleValue();
            case "÷" :
                return (numberTwo.doubleValue()==0d | numberTwo.doubleValue()==-0d) ? Double.POSITIVE_INFINITY :
                        numberOne.setScale(8, BigDecimal.ROUND_HALF_UP).divide(numberTwo, BigDecimal.ROUND_HALF_UP).doubleValue();
            default :
                return 0;
        }
    }
}
