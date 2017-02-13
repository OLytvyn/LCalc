package com.example.olytvynenko.lcalc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
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
        findViewById(R.id.btnParentheses).setOnClickListener(this);
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
                if ( Character.isDigit(strRes.charAt(strRes.length()-1)) | strRes.endsWith(")") || strRes.endsWith(".") ) {
                    if ( !number.isEmpty() ) numbers.add( doNumber(number) );
                    Calculation calculation = new Calculation();
                    strRes = calculation.doCalc(numbers, actions); // numbers.size() should be equals (actions.size()+1)
                    listsClear();
                    if ( strRes.startsWith("-") ) strRes = "(" + strRes + ")"; // negative number is always inside parentheses
                    number = strRes;
                }
                break;
            case R.id.btnCancel:
                strRes = "";
                number = "";
                break;
            case R.id.btnBackspace:
                if ( !strRes.isEmpty() ) {
                    strRes = deleteCharacter( strRes );
                } else {
                    listsClear();
                }
                break;
            case R.id.btnSignCh:
                if ( !number.isEmpty() ) {
                    strRes = doString( '\u00B1' , strRes);
                }
                break;
        }
        if ( strRes.isEmpty() ) {
            tvResult.setText("0");
        } else {
            tvResult.setText(strRes);
        }
    }

    private List<Double> numbers = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private String number = "";
    private String doString(char c, String s) {
        if ( strRes.isEmpty() ) listsClear();
        if ( Character.isDigit(c) | c == '.' ) { // condition for the doing number
            RegexMatches rm = new RegexMatches();
            if ( rm.isDouble(number + Character.toString(c)) ) {
                if ( number.isEmpty() & c == '.' ) {
                    number =  "0.";
                    s = s + "0.";
                } else {
                    number = number + Character.toString(c);
                    s = s + Character.toString(c);
                }
            }
        }
        if ( !s.isEmpty() & !s.endsWith("+") & !s.endsWith("-") & !s.endsWith("×") & !s.endsWith("÷") & //debug "-" in the start of the string
                (c == '+' || c == '-' || c == '×' || c == '÷') ) {
            actions.add(Character.toString(c));
            if ( !number.isEmpty() ) {
                numbers.add(doNumber(number));
                number = "";
            }
            s = s + Character.toString(c);
        }
        if ( c == '\u00B1' & !number.isEmpty() ) { // debug this if statement ( (-3)+(-5)+100, <- 100, +, chSign of (-5) - OK!!!
            if ( !number.endsWith(")") ) {
                s = s.substring(0, s.length()-number.length() );
                number = "(-" + number + ")";
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

    private double doNumber ( String s ) {
        double d;
        if ( Character.toString(s.charAt(0)).equals("(") && Character.toString(s.charAt(1)).equals("-") ) { //delete parentheses near the negative number
            d = Double.parseDouble(s.substring(1, s.length()-1));
        } else {
            d = Double.parseDouble(s);
        }
        return d;
    }

    private String deleteCharacter( String s ) { // DEBUG - deleting one character number after deleting previous operation sign ( (1-)+5-
        if ( s.length() > 1 & s.endsWith("-") || s.endsWith("+") || s.endsWith("×") || s.endsWith("÷") ) { // apply count of parentheses in the future
            actions.remove(actions.size() - 1);
            if ( number.isEmpty() & numbers.size() > 0 ) { // number.isEmpty()
                number = numbers.get(numbers.size() - 1).toString(); // Double 12.0 -> String "12.0"
                if (number.startsWith("-"))
                    number = "(" + number + ")"; // negative number is always inside parentheses
                if (number.endsWith("0") & number.charAt(number.length() - 2) == '.') {
                    number = number.substring(0, number.length() - 2);
                }
                numbers.remove(numbers.size() - 1);
            }
        } else {  // s ends with number OR point OR parentheses
            if (number.length() > 1 & !number.endsWith(")") ){ // number has at least 2 chars -> delete last char
                number = number.substring(0, number.length() - 1);
            } else if ( number.length() == 1 ) { // delete whole number -> number=""
                number = "";
            } else if ( number.isEmpty() & numbers.size() > 0 ) { // number.isEmpty()
                number = numbers.get(numbers.size() - 1).toString(); // Double 12.0 -> String "12.0"
                if ( number.startsWith("-") ) number = "(" + number + ")"; // negative number is always inside parentheses
                if (number.endsWith("0") & number.charAt(number.length() - 2) == '.') {
                    number = number.substring(0, number.length() - 2);
                }
                numbers.remove(numbers.size() - 1);
            }
        }
        if ( number.matches(".*nfinit.*") ) {
            number = "";
            s = "";
        }
        if ( !number.endsWith(")") & !s.isEmpty() ) s = s.substring(0, (strRes.length() - 1));
        return s;
    }
}
