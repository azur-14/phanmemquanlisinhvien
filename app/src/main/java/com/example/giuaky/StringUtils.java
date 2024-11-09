package com.example.giuaky;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern NON_ASCII = Pattern.compile("[^\\p{ASCII}]");

    public static String removeAccents(String s) {
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return NON_ASCII.matcher(normalized).replaceAll("");
    }
}