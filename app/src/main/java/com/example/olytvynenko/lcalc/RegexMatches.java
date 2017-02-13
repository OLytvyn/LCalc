package com.example.olytvynenko.lcalc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches {

    public void setInput(String input) {
        this.input = input;
    }

    public int[] getISigns() {
        return iSigns;
    }

    public int[] getISubStart() {
        return iSubStart;
    }

    public int[] getISubEnd() {
        return iSubEnd;
    }

    private static final String REGEX = "\\+|\\d-|\\d-\\(|\\)-\\d|\\)-\\(|/|\\*";
    private String input; // = "123.8998-(-125.98)-131*546/798";//"123.98+(-125)-131*546/798";
    private int count = 0;
    private void findCountOfSigns() {
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(input);   // getting matcher object
        while ( m.find() ) {
            count++;
        }
    }

    private int[] iSigns;
    private int[] iSubStart;
    private int[] iSubEnd;
    public void findIndexes() {
        findCountOfSigns();
        iSigns = new int[count];
        iSubStart = new int[count + 1];
        iSubEnd = new int[count + 1];
        Pattern pa = Pattern.compile(REGEX);
        Matcher ma = pa.matcher(input);   // getting matcher object
        int i = 0;
        while( ma.find() ) {
            int st = ma.start();
            int en = ma.end();
            if ( st != en-1 ) {
                st++;
            }
            iSigns[i] = st;
            iSubEnd[i] = iSigns[i];
            i++;
        }
        iSubEnd[count] = input.length();

        iSubStart[0] = 0;
        for ( i = 1; i < (count + 1); i++ ) {
            iSubStart[i] = iSigns[i - 1] + 1;
        }
    }

    private static final String REGEXD = "^[(-]?[0-9]*\\)?\\.?[0-9]*\\)?([eE][+-]?[0-9]*)?$";
    public boolean isDouble( String s ) {
        Pattern p = Pattern.compile(REGEXD);
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
