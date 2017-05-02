package com.example.olytvynenko.lcalc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String RESTRING = "^[(-]?[0-9]*\\)?(\\.|,)?[0-9]*\\)?([eE][+-]?[0-9]*)?$";
    private static final String REBIGDECIMAL = "^[(-]?[0-9]*\\)?\\.?[0-9]*\\)?([eE][+-]?[0-9]*)?$";
    private TextView tvResult;
    private String strRes = "";
    private String number = "";
    private String action = "";
    private String ds;
    private List<BigDecimal> numbers = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private MathContext mc = new MathContext(15, RoundingMode.HALF_UP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE & getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ds = String.valueOf(DecimalFormatSymbols.getInstance().getDecimalSeparator());
        if (!".".equals(ds) & !",".equals(ds)) ds = ".";//because of RESTRING
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setText(strRes.isEmpty() ? "0" : strRes);

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

    @Override
    public void onClick(View v) {

        int btnId = v.getId();
        String btnString = ((Button) v).getText().toString();
        boolean btnIsDigit = Character.isDigit(btnString.charAt(0));

        if (btnIsDigit || btnId==R.id.btnDecimalSeparator) {
            if (number.length()+1 == 18) {

                Toast.makeText(this, getString(R.string.maxNumberDigits), Toast.LENGTH_SHORT).show();

            } else {

                strRes = addDecDigit(strRes, btnIsDigit, btnString, ds, number);
            }
        } else if (btnId==R.id.btnPlus || btnId==R.id.btnMinus || btnId==R.id.btnMulti || btnId==R.id.btnDivide) {

            strRes = addDecActionAndDecNumber(strRes, action, btnString, number);

        } else if (btnId==R.id.btnSignCh) {

            strRes = changeNumberSign(strRes, number);

        } else if (btnId==R.id.btnCancel) {

            strRes = doCancel();

        } else if (btnId==R.id.btnBackspace) {

            if (!strRes.isEmpty()) strRes = delChar(strRes);

        } else if (btnId==R.id.btnPercent) {

            strRes = doPercent(strRes);

        } else if (btnId==R.id.btnEquals) {

            strRes = doEquals(strRes);
            if ("Infinity".equals(strRes)) {
                strRes = doCancel();
                Toast.makeText(this, getString(R.string.div_zero), Toast.LENGTH_SHORT).show();
            }

        }
        tvResult.setText(strRes.isEmpty() ? "0" : strRes);
    }

    private String addDecDigit(String strRes, boolean btnIsDigit, String btnString, String decimalSeparator, String number) {
        strRes = strRes.substring(0, strRes.length() - number.length());
        number = doNumber(btnIsDigit, btnString, decimalSeparator, number);
        this.number = number;
        this.action = "";
        strRes = strRes.concat(number);
        return strRes;
    }

    private String doNumber(boolean btnIsDigit, String digit, String decimalSeparator, String number) {
        boolean numberIsNegative = false;
        if ("0".equals(number) & btnIsDigit) {
            number = digit;
        } else if (btnIsDigit | digit.equals(decimalSeparator)) {
            if (number.startsWith("(-")) {
                numberIsNegative = true;
                number = changeSign(number);
            }
            if ((number.concat(digit)).matches(RESTRING)) {
                if (number.isEmpty() & digit.equals(decimalSeparator)) {
                    number = "0".concat(decimalSeparator);
                } else {
                    number = number.concat(digit);
                }
            }
            if (numberIsNegative) number = changeSign(number);
        }
        return number;
    }

    private String addDecActionAndDecNumber(String strRes, String prevAction, String currAction, String currNumber){
        strRes = addActionAndNumber(strRes, prevAction, currAction, currNumber);
        this.action = currAction;
        this.number = "";
        return strRes;
    }

    private String addActionAndNumber (String strRes, String prevAction, String currAction, String currNumber) {
        if (prevAction.isEmpty()) {
            this.actions.add(currAction);
        } else {
            strRes = strRes.substring(0, strRes.length() - prevAction.length());
            this.actions.set(this.actions.size() - 1, currAction);
        }
        if (!currNumber.isEmpty()) this.numbers.add(doBigDecimal(currNumber));
        strRes = strRes.concat(currAction);
        return strRes;
    }

    private BigDecimal doBigDecimal(String number) { //String -> BigDecimal; delete parentheses near the negative number
        number = number.replace(this.ds,".");
        number = number.startsWith("(-") ? number.substring(1, number.length()-1) : number;
        return (number.matches(REBIGDECIMAL)) ? new BigDecimal(number) : BigDecimal.ZERO;
    }

    private String changeNumberSign(String strRes, String currNumber) {
        if (currNumber.isEmpty() & !this.numbers.isEmpty()) {
            currNumber = String.valueOf(this.numbers.get(this.numbers.size()-1));
            currNumber = doNegativeORPositiveString(currNumber);
            strRes = strRes.substring(0, strRes.length() - currNumber.length());
            currNumber = changeSign(currNumber);
            this.numbers.set(this.numbers.size()-1, doBigDecimal(currNumber));
            strRes = strRes.concat(currNumber);
            this.number = "";
        } else if (!currNumber.isEmpty()) {
            strRes = strRes.substring(0, strRes.length() - currNumber.length());
            currNumber = changeSign(currNumber);
            strRes = strRes.concat(currNumber);
            this.number = currNumber;
        }
        return strRes;
    }

    private String doNegativeORPositiveString(String number) { // negative number is always inside parentheses
        number = number.replace(".", this.ds);
        return (number.startsWith("-")) ? "(".concat(number).concat(")") : number;
    }

    private String changeSign (String number) {
        if (!number.isEmpty()) {
            number = number.startsWith("(-") ? number.substring(2, number.length()-1) : "(-".concat(number).concat(")");
        }
        return number;
    }

    private String doCancel() {
        this.strRes = "";
        this.number = "";
        this.action = "";
        listsClear();
        return strRes;
    }

    private void listsClear() {
        this.numbers.clear();
        this.actions.clear();
    }

    private String doPercent(String strRes) {
        String lastAction = "";
        if (!this.actions.isEmpty()) lastAction = this.actions.get(this.actions.size()-1);
        if (!this.numbers.isEmpty() & !this.number.isEmpty() & ("+".equals(lastAction) ||
                "-".equals(lastAction) || "×".equals(lastAction) || "÷".equals(lastAction))) {
            strRes = strRes.substring(0, strRes.length()-this.number.length());
            this.number = calcPercent(this.actions.get(this.actions.size()-1), this.number);
            this.number = doNegativeORPositiveString(this.number);
            if ("×".equals(lastAction) | "÷".equals(lastAction)) { // if "×" | "÷" -> do just (only) percents from previous number
                int strResCoeff = this.numbers.get(this.numbers.size()-1).signum()==1 ? 1 : 3;//if numbers.get(numbers.size()-1) is positive - "1" : is negative - "3"
                strRes = strRes.substring(0, (strRes.length() - this.numbers.get(this.numbers.size()-1).toString().length() - strResCoeff));
                this.numbers.remove(this.numbers.size()-1);
                this.actions.remove(this.actions.size()-1);
                strRes = strRes.concat(this.number);
            } else { // previous number(or string) "+" or "-" do percents from previous number - "tips" or "discounts"
                strRes = strRes.concat(this.number);
            }
        }
        return strRes;
    }

    private String calcPercent(String action, String number) { // number!=BigDecimal.ZERO!!!
        switch (action) {
            case "÷" :
                return String.valueOf(this.numbers.get(this.numbers.size()-1).divide(doBigDecimal(number), this.mc).multiply(new BigDecimal("100"), this.mc).stripTrailingZeros());
            default :
                return String.valueOf(this.numbers.get(this.numbers.size()-1).multiply(doBigDecimal(number), this.mc).divide(new BigDecimal("100"), this.mc).stripTrailingZeros());

        }
    }

    private String delChar(String s) { // s=s+n+a+number -- whole string. Add delete ch from negative number!!!
        boolean numberIsNegative = false; // add if number.isEmpty() and !numbers.isEmpty() -> sWONumber = s - (230, 231, 232)
        String sWONumber = s.substring(0, s.length()-this.number.length());
        if (s.length() > 1 & (s.endsWith("-") || s.endsWith("+") || s.endsWith("×") || s.endsWith("÷"))) { // apply count of parentheses in the future
            this.actions.remove(this.actions.size() - 1);
            if (this.number.isEmpty() & !this.numbers.isEmpty()) { // number.isEmpty()
                this.number = this.numbers.get(numbers.size() - 1).toString(); // BigDecimal "-12.0" -> String "-12.0"
                this.number = doNegativeORPositiveString(this.number); // -> String "(-12)"
                this.numbers.remove(this.numbers.size() - 1);
            }
            if (!this.number.equals(s) & !s.isEmpty()) s = s.substring(0, (this.strRes.length() - 1));
        } else {  // s ends with number OR point OR parentheses
            if (this.number.startsWith("(-")) {
                this.number = changeSign(this.number); // negative number -> positive number
                numberIsNegative = true;
            }
            if (this.number.length() > 1 & !this.number.startsWith("(-") ){ // number has at least 2 chars -> delete last char
                this.number = this.number.substring(0, this.number.length() - 1);
            } else if (this.number.length() == 1) { // delete whole number -> number=""
                this.number = "";
            } else if (this.number.isEmpty() & this.numbers.size() > 0) { // number.isEmpty()
                this.number = this.numbers.get(this.numbers.size() - 1).toString(); // BigDecimal "-12.0" -> String "-12.0"
                this.number = doNegativeORPositiveString(this.number); // -> String "(-12)"
                this.numbers.remove(this.numbers.size() - 1);
            }
            if (numberIsNegative & !this.number.isEmpty()) this.number = changeSign(this.number); // positive number -> negative number
            if (!this.number.equals(s) & !s.isEmpty()) s = sWONumber.concat(this.number);
        }
        return s;
    }

    private String doEquals(String strRes) {
        if (!strRes.isEmpty() | strRes.endsWith(")") || strRes.endsWith(this.ds)) {
            if (!this.number.isEmpty()) {
                this.numbers.add(doBigDecimal(this.number));
            } else {
                if (!this.actions.isEmpty()) this.actions.set(this.actions.size()-1, "+");
                this.numbers.add(BigDecimal.ZERO);
            }
            strRes = doCalc(this.numbers, this.actions); // numbers.size() should be equals (actions.size()+1)
            listsClear();
            strRes = "0".equals(strRes) ? "" : doNegativeORPositiveString(strRes);
            if (!"Infinity".equals(strRes) & !strRes.isEmpty()) this.numbers.add(doBigDecimal(strRes));
            this.number = "";
            this.action = "";
        }
        return strRes;
    }

    private String doCalc (List<BigDecimal> numbers, List<String> actions) {
        BigDecimal result = BigDecimal.ZERO;
        for (int i  = 0; i < actions.size(); i++) {
            if (actions.get(i).equals("×") | actions.get(i).equals("÷")) {
                result = doAction(numbers.get(i), numbers.get(i + 1), actions.get(i));
                if (result==null) return "Infinity";
                numbers.set(i, BigDecimal.ZERO);
                numbers.set(i + 1, result);
                actions.set(i, i==0 ? "+" : actions.get(i - 1));// if 1st action is "×" OR "÷" -> i-1 = -1 <- index doesn't exist
            }
        }
        for (int i = 0; i < actions.size(); i++) {
            result = doAction(numbers.get(i), numbers.get(i + 1), actions.get(i));
            if (result==null) return "Infinity";
            numbers.set(i + 1, result);
        }
        return String.valueOf((result.compareTo(BigDecimal.ZERO)==0) ? BigDecimal.ZERO : result.stripTrailingZeros().toPlainString());
    }

    private BigDecimal doAction(BigDecimal numberOne, BigDecimal numberTwo, String theAction) {
        switch (theAction) {
            case "+" :
                return numberOne.add(numberTwo, this.mc);
            case "-" :
                return numberOne.subtract(numberTwo, this.mc);
            case "×" :
                return numberOne.multiply(numberTwo, this.mc);
            case "÷" :
                return (numberTwo.equals(BigDecimal.ZERO)) ? null :
                        numberOne.divide(numberTwo, this.mc);
            default :
                return BigDecimal.ZERO;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("strRes", this.strRes);
        outState.putString("number", this.number);
        outState.putString("action", this.action);
        outState.putString("ds", this.ds);
        String[] numbers = new String[this.numbers.size()];
        String[] actions = new String[this.actions.size()];
        for (int i = 0; i < this.numbers.size(); i++) {
            numbers[i] = this.numbers.get(i).toString();
            if (i < this.actions.size()) actions[i] = this.actions.get(i);
        }
        outState.putStringArray("numbers", numbers);
        outState.putStringArray("actions", actions);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.strRes = savedInstanceState.getString("strRes");
        if (this.strRes != null) tvResult.setText(this.strRes.isEmpty() ? "0" : this.strRes);
        this.number = savedInstanceState.getString("number");
        this.action = savedInstanceState.getString("action");
        this.ds = savedInstanceState.getString("ds");
        String[] numbers = savedInstanceState.getStringArray("numbers");
        String[] actions = savedInstanceState.getStringArray("actions");
        if (numbers != null & actions != null) {
            for (int i = 0; i < numbers.length; i++) {
                this.numbers.add(new BigDecimal(numbers[i]));
                if (i < actions.length) this.actions.add(actions[i]);
            }
        }
    }
}
