package com.ProjektInzynierski.BackEnd.util;

import com.ProjektInzynierski.BackEnd.enums.LoginMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for creating result map with provided message
 */
public class ResultMap {

    private static final String EMAIL = "email";
    private static final String RESULT = "result";
    private static final String ERROR = "error";

    public static Map<String, String> createErrorMap(String error) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(ERROR, error);
        return resultMap;
    }

    public static Map<String, String> createSuccessMap(String result) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(RESULT, result);
        return resultMap;
    }

    public static Map<String, String> createRegistrationSuccessMap(String result, String email) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(RESULT, result);
        resultMap.put(EMAIL, email);
        return resultMap;
    }

    public static Map<String, String> createNullBodyErrorMap() {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put(ERROR, LoginMsg.WRONG_EMAIL_OR_PASSWORD.getErrorMsg());
        return resultMap;
    }

}
