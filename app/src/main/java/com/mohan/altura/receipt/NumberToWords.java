package com.mohan.altura.receipt;

public class NumberToWords {

    private static final String[] ones = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public static String convert(long number) {
        if (number == 0) return "Zero";
        if (number < 0) return "Minus " + convert(-number);

        String result = "";

        if (number >= 10000000) {
            result += convert(number / 10000000) + " Crore ";
            number %= 10000000;
        }
        if (number >= 100000) {
            result += convert(number / 100000) + " Lakh ";
            number %= 100000;
        }
        if (number >= 1000) {
            result += convert(number / 1000) + " Thousand ";
            number %= 1000;
        }
        if (number >= 100) {
            result += ones[(int)(number / 100)] + " Hundred ";
            number %= 100;
        }
        if (number >= 20) {
            result += tens[(int)(number / 10)] + " ";
            number %= 10;
        }
        if (number > 0) {
            result += ones[(int) number] + " ";
        }

        return result.trim();
    }

    public static String convertAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            long rupees = (long) amount;
            long paise = Math.round((amount - rupees) * 100);

            String result = convert(rupees) + " Rupees";
            if (paise > 0) {
                result += " and " + convert(paise) + " Paise";
            }
            return result + " Only";
        } catch (NumberFormatException e) {
            return "";
        }
    }
}
