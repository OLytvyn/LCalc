package com.example.olytvynenko.lcalc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        }
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setText("0");

        findViewById(R.id.btnCancel).setOnClickListener(this);
        findViewById(R.id.btnPercent).setOnClickListener(this);
        findViewById(R.id.btnBackspace).setOnClickListener(this);
        findViewById(R.id.btnDivide).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);
        findViewById(R.id.btnMulti).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btnMinus).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btnPlus).setOnClickListener(this);
        findViewById(R.id.btnDecimalSeparator).setOnClickListener(this);
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btnSignCh).setOnClickListener(this);
        findViewById(R.id.btnEquals).setOnClickListener(this);
    }

    private String strRes = "";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                strRes = doString('0', strRes);
                break;
            case R.id.btn1:
                strRes = doString('1', strRes);
                break;
            case R.id.btn2:
                strRes = doString('2', strRes);
                break;
            case R.id.btn3:
                strRes = doString('3', strRes);
                break;
            case R.id.btn4:
                strRes = doString('4', strRes);
                break;
            case R.id.btn5:
                strRes = doString('5', strRes);
                break;
            case R.id.btn6:
                strRes = doString('6', strRes);
                break;
            case R.id.btn7:
                strRes = doString('7', strRes);
                break;
            case R.id.btn8:
                strRes = doString('8', strRes);
                break;
            case R.id.btn9:
                strRes = doString('9', strRes);
                break;
            case R.id.btnDecimalSeparator:
                strRes = doString('.', strRes);
                break;
            case R.id.btnPlus:
                strRes = doString('+', strRes);
                break;
            case R.id.btnMinus:
                strRes = doString('-', strRes);
                break;
            case R.id.btnMulti:
                strRes = doString('×', strRes);
                break;
            case R.id.btnDivide:
                strRes = doString('÷', strRes);
                break;
            case R.id.btnEquals:
                if ( !strRes.isEmpty() && Character.isDigit(strRes.charAt(strRes.length()-1))
                        | strRes.endsWith(")") || strRes.endsWith(".") ) {
                    if ( !number.isEmpty() ) numbers.add( doNumber(number) );
                    Calculation calculation = new Calculation();
                    strRes = calculation.doCalc(numbers, actions); // numbers.size() should be equals (actions.size()+1)
                    listsClear();
                    strRes = doSimpleAndOrNegative(strRes);
                    if ( strRes.matches(".*nfinit.*") || strRes.matches("NaN") ) { // division by zero exception Infinity | NaN
                        strRes = "";
                        Toast.makeText(this, getString(R.string.div_zero), Toast.LENGTH_SHORT).show();
                    }
                    tvResult.setText(strRes.isEmpty() ? "0" : strRes);
                    clearStrResAndNumber();
                    return;
                }
                break;
            case R.id.btnCancel:
                clearStrResAndNumber();
                break;
            case R.id.btnBackspace:
                if ( !strRes.isEmpty() ) strRes = deleteCharacter( strRes );
                break;
            case R.id.btnSignCh:
                if ( !number.isEmpty() ) strRes = doString( '\u00B1' , strRes);
                break;
            case R.id.btnPercent: // number% from numbers.get(numbers.size()-1)
                if ( !numbers.isEmpty() & !number.isEmpty() & !number.endsWith(")") ) {
                    strRes = strRes.substring(0, strRes.length()-number.length());
                    number = doPercent(number);
                    if ( number.startsWith("-") ) number = "(" + number + ")";
                    if ( "×".equals( actions.get(actions.size()-1) ) ) { // if "×" -> do just (only) percents from previous number
                        int strResCoeff = numbers.get(numbers.size()-1) > 0 ? 1 : -1;//if numbers.get(numbers.size()-1) is positive "1" : "-1"
                        strRes = strRes.substring(0, (strRes.length() - numbers.get(numbers.size()-1).toString().length() + strResCoeff));
                        numbers.remove(numbers.size()-1);
                        actions.remove(actions.size()-1);
                        strRes = strRes + number;
                    } else { // previous number(or string) "+" or "-" do percents from previous number - "tips" or "discounts"
                        strRes = strRes + number;
                    }
                }
                break;
        }
        tvResult.setText(strRes.isEmpty() ? "0" : strRes);
    }

    private static final String REGEXD = "^[(-]?[0-9]*\\)?\\.?[0-9]*\\)?([eE][+-]?[0-9]*)?$";
    private List<Double> numbers = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private String number = "";
    private String doString(char c, String s) {
        if ( strRes.isEmpty() ) listsClear();
        // condition for the doing number -- method makeNumber
        if ( Character.isDigit(c) | c == '.' ) {
            if ( (number + Character.toString(c)).matches(REGEXD) ) {
                if ( number.isEmpty() & c == '.' ) {
                    number =  "0.";
                    s = s + "0.";
                } else if ( number.equals("0") & Character.isDigit(c)) {// prevent "00" or "02" condition
                    return s;
                } else {
                    number = number + Character.toString(c);
                    s = s + Character.toString(c);
                }
            }
        }
        // add action -- method addActionAndNumber
        if ( !number.isEmpty() & !s.isEmpty() &
                !s.endsWith("+") & !s.endsWith("-") & !s.endsWith("×") & !s.endsWith("÷") &
                (c == '+' || c == '-' || c == '×' || c == '÷') ) {
            actions.add(Character.toString(c));
            numbers.add(doNumber(number));
            number = "";
            s = s + Character.toString(c);
        }
        // sign change -- method changeSign
        if ( c == '\u00B1' & !number.isEmpty() ) { // debug this if statement ( (-3)+(-5)+100, <- 100, +, chSign of (-5)
            if ( !number.endsWith(")") ) {
                s = s.substring(0, s.length() - number.length());
                number = "(-" + number + ")";
            } else if ( number.endsWith(")") ) {
                s = s.substring(0, s.length() - number.length());
                number = number.substring(2, number.length()-1);
            } else if ( s.equals(number) ) { // if there is only ONE number in the string s
                s = "";
                number = number.substring( 2, (number.length() - 1) );
            } else {
                s = s.substring(0, s.length()-number.length()+2 );
                number = number.substring( 2, (number.length() - 1) );
            }
            s = s + number;
        }
        return s;
    }

    private void listsClear() {
        numbers.clear();
        actions.clear();
    }

    private double doNumber ( String s ) { //String -> double; delete parentheses near the negative number
        double d;
        d = Double.parseDouble( s.startsWith("(-") ? s.substring(1, s.length()-1) : s );
        return d;
    }

    private String doPercent ( String s ) {
        double d;
        d = numbers.get(numbers.size() - 1) * doNumber(s) / 100;
        return String.valueOf(d);
    }

    private String deleteCharacter( String s ) {
        if ( s.length() > 1 & ( s.endsWith("-") || s.endsWith("+") || s.endsWith("×") || s.endsWith("÷") ) ) { // apply count of parentheses in the future
            actions.remove(actions.size() - 1);
            if ( number.isEmpty() & numbers.size() > 0 ) { // number.isEmpty()
                number = numbers.get(numbers.size() - 1).toString(); // Double -12.0 -> String "-12.0"
                number = doSimpleAndOrNegative(number); // -> String "(-12.0)"
                numbers.remove(numbers.size() - 1);
            }
        } else {  // s ends with number OR point OR parentheses
            if (number.length() > 1 & !number.endsWith(")") ){ // number has at least 2 chars -> delete last char
                number = number.substring(0, number.length() - 1);
            } else if ( number.length() == 1 ) { // delete whole number -> number=""
                number = "";
            } else if ( number.isEmpty() & numbers.size() > 0 ) { // number.isEmpty()
                number = numbers.get(numbers.size() - 1).toString(); // Double -12.0 -> String "-12.0"
                number = doSimpleAndOrNegative(number); // -> String "(-12.0)"
                numbers.remove(numbers.size() - 1);
            }
        }
        if ( !number.equals(s) & !s.isEmpty() ) s = s.substring(0, (strRes.length() - 1));
        return s;
    }

    private String doSimpleAndOrNegative( String s ) { // number W/O ".0" in the end and/or negative
        if ( s.endsWith(".0") ) s = s.substring(0, s.length() - 2); // number without ".0" in the end
        if ( s.startsWith("-") ) s = "(" + s + ")"; // negative number is always inside parentheses
        return s;
    }

    private void clearStrResAndNumber() {
        strRes = "";
        number = "";
    }
}
