package com.mohan.altura.receipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptPreviewActivity extends AppCompatActivity {

    private View receiptContainer;
    private ReceiptData receiptData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_preview);

        receiptData = (ReceiptData) getIntent().getSerializableExtra("receipt_data");
        if (receiptData == null) {
            finish();
            return;
        }

        receiptContainer = findViewById(R.id.receiptContainer);
        populateReceipt();
        setupButtons();
    }

    private void populateReceipt() {
        // Date
        android.widget.TextView tvDate = findViewById(R.id.tvReceiptDate);
        tvDate.setText(receiptData.date.isEmpty() ? "__/__/____" : receiptData.date);

        // Name
        android.widget.TextView tvName = findViewById(R.id.tvName);
        tvName.setText(receiptData.name);

        // Flat No
        android.widget.TextView tvFlatNo = findViewById(R.id.tvFlatNo);
        tvFlatNo.setText(receiptData.flatNo);

        // Wing
        android.widget.TextView tvWing = findViewById(R.id.tvWing);
        tvWing.setText(receiptData.wing);

        // Month
        android.widget.TextView tvMonth = findViewById(R.id.tvMonth);
        tvMonth.setText(receiptData.month);

        // Payment Mode checkboxes
        View checkCash = findViewById(R.id.checkCash);
        View checkUPI = findViewById(R.id.checkUPI);
        View checkNetBanking = findViewById(R.id.checkNetBanking);

        checkCash.setBackgroundResource(R.drawable.checkbox_unchecked);
        checkUPI.setBackgroundResource(R.drawable.checkbox_unchecked);
        checkNetBanking.setBackgroundResource(R.drawable.checkbox_unchecked);

        switch (receiptData.paymentMode) {
            case "Cash":
                checkCash.setBackgroundResource(R.drawable.checkbox_checked);
                break;
            case "UPI":
                checkUPI.setBackgroundResource(R.drawable.checkbox_checked);
                break;
            case "Net Banking":
                checkNetBanking.setBackgroundResource(R.drawable.checkbox_checked);
                break;
        }

        // Amount in words
        android.widget.TextView tvAmountWords = findViewById(R.id.tvAmountWords);
        tvAmountWords.setText(receiptData.amountInWords);

        // Received By
        android.widget.TextView tvReceivedBy = findViewById(R.id.tvReceivedBy);
        tvReceivedBy.setText(receiptData.receivedBy.isEmpty() ? "___________" : receiptData.receivedBy);

        // Amount
        android.widget.TextView tvAmount = findViewById(R.id.tvAmount);
        try {
            double amt = Double.parseDouble(receiptData.amount);
            if (amt == (long) amt) {
                tvAmount.setText(String.format("%,.0f/-", amt));
            } else {
                tvAmount.setText(String.format("%,.2f/-", amt));
            }
        } catch (Exception e) {
            tvAmount.setText(receiptData.amount + "/-");
        }

        // Auth signature (received by person)
        android.widget.TextView tvAuthSig = findViewById(R.id.tvAuthSignature);
        tvAuthSig.setText(receiptData.receivedBy);

        // Receipt number
        android.widget.TextView tvReceiptNo = findViewById(R.id.tvReceiptNo);
        if (!receiptData.receiptNumber.isEmpty()) {
            tvReceiptNo.setText("Receipt No: " + receiptData.receiptNumber);
        }
    }

    private void setupButtons() {
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnSavePdf).setOnClickListener(v -> saveAsPdf());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveAsPdf());

        findViewById(R.id.btnShareReceipt).setOnClickListener(v -> shareReceipt());
        findViewById(R.id.btnShare).setOnClickListener(v -> shareReceipt());
    }

    private Bitmap getReceiptBitmap() {
        receiptContainer.setDrawingCacheEnabled(true);
        receiptContainer.buildDrawingCache();

        // Create a bitmap from the view
        Bitmap bitmap = Bitmap.createBitmap(
                receiptContainer.getWidth(),
                receiptContainer.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        receiptContainer.draw(canvas);
        return bitmap;
    }

    private void saveAsPdf() {
        // Save as image (PNG) since PDF requires API 19+ PrintManager approach
        // We'll save as a high-quality PNG which can be shared/printed
        try {
            // Wait for view to be laid out
            receiptContainer.post(() -> {
                Bitmap bitmap = getReceiptBitmap();
                saveBitmapToFile(bitmap, true);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, boolean showToast) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Receipt_" + receiptData.flatNo + "_" + receiptData.wing + "_" + timestamp + ".png";

        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir == null) {
            dir = new File(getCacheDir(), "Receipts");
        }
        dir.mkdirs();

        File file = new File(dir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (showToast) {
                Toast.makeText(this, "Receipt saved!\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File saveTemp(Bitmap bitmap) {
        File cacheDir = new File(getCacheDir(), "receipts");
        cacheDir.mkdirs();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File file = new File(cacheDir, "Receipt_" + timestamp + ".png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            return null;
        }
        return file;
    }

    private void shareReceipt() {
        receiptContainer.post(() -> {
            Bitmap bitmap = getReceiptBitmap();
            File file = saveTemp(bitmap);
            if (file == null) {
                Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Maintenance Receipt - " +
                    receiptData.name + " Flat " + receiptData.flatNo + receiptData.wing);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
        });
    }
}
