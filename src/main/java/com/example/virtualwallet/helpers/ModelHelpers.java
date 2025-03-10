package com.example.virtualwallet.helpers;

public class ModelHelpers {

    public static String maskCreditCard(String cardNumber) {
        return "**" + cardNumber.substring(cardNumber.length() - 4);
    }
}