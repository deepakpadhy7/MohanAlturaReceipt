package com.mohan.altura.receipt;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etReceiptNumber, etDate, etName, etFlatNo, etYear, etAmount, etReceivedBy;
    private AutoCompleteTextView actvWing, actvMonth, actvPaymentMode;
    private android.widget.TextView tvAmountWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupDropdowns();
        setupDatePicker();
        setupAmountWatcher();
        setDefaultValues();

        findViewById(R.id.btnGenerateReceipt).setOnClickListener(v -> generateReceipt());
    }

    private void initViews() {
        etReceiptNumber = findViewById(R.id.etReceiptNumber);
        etDate = findViewById(R.id.etDate);
        etName = findViewById(R.id.etName);
        etFlatNo = findViewById(R.id.etFlatNo);
        etYear = findViewById(R.id.etYear);
        etAmount = findViewById(R.id.etAmount);
        etReceivedBy = findViewById(R.id.etReceivedBy);
        actvWing = findViewById(R.id.actvWing);
        actvMonth = findViewById(R.id.actvMonth);
        actvPaymentMode = findViewById(R.id.actvPaymentMode);
        tvAmountWords = findViewById(R.id.tvAmountWords);
    }

    private void setupDropdowns() {
        String[] wings = getResources().getStringArray(R.array.wings);
        ArrayAdapter<String> wingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, wings);
        actvWing.setAdapter(wingAdapter);

        String[] months = getResources().getStringArray(R.array.months);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, months);
        actvMonth.setAdapter(monthAdapter);

        String[] paymentModes = getResources().getStringArray(R.array.payment_modes);
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, paymentModes);
        actvPaymentMode.setAdapter(paymentAdapter);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateStr = String.format("%02d/%02d/%04d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        etDate.setText(dateStr);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupAmountWatcher() {
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String amountStr = s.toString().trim();
                if (!amountStr.isEmpty()) {
                    String words = NumberToWords.convertAmount(amountStr);
                    tvAmountWords.setText("→ " + words);
                } else {
                    tvAmountWords.setText("");
                }
            }
        });
    }

    private void setDefaultValues() {
        // Set current date
        Calendar calendar = Calendar.getInstance();
        String today = String.format("%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));
        etDate.setText(today);

        // Set current year
        etYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        // Set current month
        String[] months = getResources().getStringArray(R.array.months);
        actvMonth.setText(months[calendar.get(Calendar.MONTH)], false);

        // Set default payment mode
        actvPaymentMode.setText("Cash", false);

        // Default maintenance amount
        etAmount.setText("4000");
    }

    private void generateReceipt() {
        // Validate
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String flatNo = etFlatNo.getText() != null ? etFlatNo.getText().toString().trim() : "";
        String wing = actvWing.getText().toString().trim();
        String month = actvMonth.getText().toString().trim();
        String year = etYear.getText() != null ? etYear.getText().toString().trim() : "";
        String paymentMode = actvPaymentMode.getText().toString().trim();
        String amount = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String receivedBy = etReceivedBy.getText() != null ? etReceivedBy.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";
        String receiptNum = etReceiptNumber.getText() != null ? etReceiptNumber.getText().toString().trim() : "";

        if (name.isEmpty() || flatNo.isEmpty() || wing.isEmpty() || month.isEmpty() ||
                year.isEmpty() || paymentMode.isEmpty() || amount.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        ReceiptData data = new ReceiptData();
        data.receiptNumber = receiptNum.isEmpty() ? "" : receiptNum;
        data.date = date;
        data.name = name;
        data.flatNo = flatNo;
        data.wing = wing;
        data.month = month + "-" + year.substring(year.length() - 2);
        data.year = year;
        data.paymentMode = paymentMode;
        data.amount = amount;
        data.amountInWords = NumberToWords.convertAmount(amount);
        data.receivedBy = receivedBy;

        Intent intent = new Intent(this, ReceiptPreviewActivity.class);
        intent.putExtra("receipt_data", data);
        startActivity(intent);
    }
}
