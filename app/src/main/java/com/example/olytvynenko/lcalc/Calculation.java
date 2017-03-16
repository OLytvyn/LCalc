package com.example.olytvynenko.lcalc;

import java.util.ArrayList;
import java.util.List;

public class Calculation {

    // strSource = "123.8998-(-125.98)-131*546/798";
    private String strSource;
    private String strResult;

    public void setStrSource(String sSource) {
        strSource = sSource;
    }

    public String getStrResult() {
        return strResult;
    }

    public void calc() {
        // find index of all signs ( + - * / )
        RegexMatches rm = new RegexMatches();
        rm.setInput(strSource);
        rm.findIndexes();
        int[] iSigns = rm.getISigns();
        int[] iSubStart = rm.getISubStart();
        int[] iSubEnd = rm.getISubEnd();

        int iSignsLength = iSigns.length;
        double[] dDouble = new double[iSignsLength + 1];
        String[] sSign = new String[iSignsLength];
        int[] iPriority = new int[iSignsLength];

        for (int i = 0; i <= iSignsLength; i++ ) {
            String sDouble;
            sDouble = strSource.substring(iSubStart[i], iSubEnd[i]);
            if ( Character.toString(sDouble.charAt(0)).equals("(") ) { //delete parentheses near the negative number
                String sTmp = sDouble;
                sDouble = sTmp.substring(1, sTmp.length()-1); //Log.d("myLogs", "this is a negative number");
            }
            dDouble[i] = Double.parseDouble(sDouble);
            if ( i < iSignsLength ) {
                sSign[i] = Character.toString(strSource.charAt(iSigns[i]));
                if ( sSign[i].equals("×") | sSign[i].equals("÷") ) {
                    iPriority[i] = 2;
                } else if ( sSign[i].equals("+") | sSign[i].equals("-") ) {
                    iPriority[i] = 3;
                } else {
                    iPriority[i] = 4;// throw an exception...
                }
            }
        }

        double result = 0;
        for ( int i  = 0; i < iSignsLength; i++ ) {
            if ( iPriority[i] == 2 ) {
                switch ( sSign[i] ) {
                    case "×" :
                        result = dDouble[i] * dDouble[i + 1];
                        dDouble[i + 1] = result;
                        dDouble[i] = 0;
                        if ( i == 0 ) {
                            sSign[i] = "+";
                        } else {
                            sSign[i] = sSign[i - 1];
                        }
                        break;
                    case "÷" :
                        result = dDouble[i] / dDouble[i + 1];
                        dDouble[i + 1] = result;
                        dDouble[i] = 0;
                        if ( i == 0 ) {
                            sSign[i] = "+";
                        } else {
                            sSign[i] = sSign[i - 1];
                        }
                        break;
                }
            }
        }
        for ( int i = 0; i < iSignsLength; i++ ) {
            switch ( sSign[i] ) {
                case "+" :
                    result = dDouble[i] + dDouble[i + 1];
                    dDouble[i + 1] = result;
                    break;
                case "-" :
                    result = dDouble[i] - dDouble[i + 1];
                    dDouble[i + 1] = result;
                    break;
            }
        }
        strResult = String.valueOf(result);
    }

    public String doCalc ( List<Double> numbers, List<String> actions ) {
        double result = 0;
        for ( int i  = 0; i < actions.size(); i++ ) {
            if (actions.get(i).equals("×") | actions.get(i).equals("÷")) {
                result = doAction(numbers.get(i), numbers.get(i + 1), actions.get(i));
                numbers.set(i, 0.0d);
                numbers.set(i + 1, result);
                actions.set( i, i==0 ? "+" : actions.get(i - 1));// if 1st action is "×" OR "÷" -> i-1 = -1 <- index doesn't exist
            }
        }
        for ( int i = 0; i < actions.size(); i++ ) {
            result = doAction(numbers.get(i), numbers.get(i + 1), actions.get(i));
            numbers.set(i + 1, result);
        }
        return strResult = String.valueOf(result);
    }

    private double doAction( double numberOne, double numberTwo, String theAction) {
        double result = 0;
        switch ( theAction ) {
            case "+" :
                result = numberOne + numberTwo;
                break;
            case "-" :
                result = numberOne - numberTwo;
                break;
            case "×" :
                result = numberOne * numberTwo;
                break;
            case "÷" :
                result = numberOne / numberTwo;
                break;
        }
        return result;
    }
}
