package model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Validator {
    public enum Modes {
        ID, WORD, EMAIL, PHONE, USER_STATUS
    }

    private Map<Modes, Function<String, Pattern>> functionalMap;
    private static final String NATURAL_REGEX_STRING = "[+]?(\\d*[1-9]\\d*)";
    private static final String EMAIL_REGEX_STRING = "([a-zA-Z0-9_]([.]?)([a-zA-Z0-9_]([.]?))*[a-zA-Z0-9])@([a-zA-Z]{3,6})[.]([a-z]{2,3})";

    public Validator() {
        functionalMap = new HashMap<>();
        functionalMap.put(Modes.ID, (x) -> Pattern.compile(NATURAL_REGEX_STRING));
        functionalMap.put(Modes.EMAIL, (x) -> Pattern.compile(EMAIL_REGEX_STRING));
    }

    public boolean isValid(String requestString, Modes mode) throws IllegalArgumentException {
        if (!functionalMap.containsKey(mode))
            throw new IllegalArgumentException("No such mode found");

        return functionalMap.get(mode).apply(requestString).matcher(requestString).matches();
    }
}

