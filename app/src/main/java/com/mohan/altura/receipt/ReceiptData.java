package com.mohan.altura.receipt;

import java.io.Serializable;

public class ReceiptData implements Serializable {
    public String receiptNumber;
    public String date;
    public String name;
    public String flatNo;
    public String wing;
    public String month;
    public String year;
    public String paymentMode;
    public String amount;
    public String amountInWords;
    public String receivedBy;

    public ReceiptData() {}
}
