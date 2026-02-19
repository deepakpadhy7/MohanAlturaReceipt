# Mohan Altura Receipt Generator - Android App

## Overview
An Android application to generate Maintenance Payment Receipts for Mohan Altura Association, matching the official receipt format.

## Project Structure
```
ReceiptApp/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/mohan/altura/receipt/
│       │   ├── MainActivity.java          ← Form to fill receipt details
│       │   ├── ReceiptPreviewActivity.java ← Preview + Save/Share
│       │   ├── ReceiptData.java           ← Data model
│       │   └── NumberToWords.java         ← Converts amount to words
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml      ← Input form UI
│           │   └── activity_receipt_preview.xml ← Receipt template UI
│           ├── values/
│           │   ├── strings.xml
│           │   ├── colors.xml
│           │   └── themes.xml
│           └── drawable/                  ← Custom drawables
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## How to Build & Run

### Prerequisites
- Android Studio (Flamingo or newer)
- Android SDK 34
- JDK 8+

### Steps
1. Open Android Studio
2. Select **File → Open** and choose the `ReceiptApp` folder
3. Wait for Gradle sync to complete
4. Connect an Android device (API 24+) or start an emulator
5. Click **Run ▶** (Shift+F10)

### Or Build APK
```bash
cd ReceiptApp
./gradlew assembleDebug
# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Features

### Input Form (MainActivity)
- **Receipt Number** – optional sequential number
- **Date** – date picker (defaults to today)
- **Resident Name** – full name
- **Flat No** – apartment number
- **Wing** – dropdown (A1-C4)
- **Month & Year** – month dropdown + year field
- **Payment Mode** – Cash / UPI / Net Banking
- **Amount** – numeric input with **automatic conversion to words**
- **Received By** – name of collecting person

### Receipt Preview (ReceiptPreviewActivity)
- Matches the official Mohan Altura receipt template exactly:
  - Logo with circular border (top-left)
  - Date and Association address (top-right)
  - All fields with underline borders
  - Payment mode checkboxes (Cash ☑, UPI ☐, Net Banking ☐)
  - Amount in a bordered box
  - Payer and Authorized signature lines
- **Save as PNG** – saves to device storage
- **Share** – share via WhatsApp, Email, etc.

## Customization

### Default Maintenance Amount
In `MainActivity.java`, change:
```java
etAmount.setText("4000");
```

### Adding More Wings
In `strings.xml`, add to the `wings` array:
```xml
<item>D1</item>
```

### Changing Association Details
Edit `activity_receipt_preview.xml` – look for the address TextViews.

## Receipt Template Match
The app replicates:
- ✅ Mohan Altura logo (circular)
- ✅ Date field (top right)
- ✅ Association name & address
- ✅ "MAINTENANCE PAYMENT RECEIPT" title
- ✅ "RECEIVED WITH THANKS FROM" header
- ✅ Name, Flat No, Wing fields with underlines
- ✅ "FOR THE MONTH OF" field
- ✅ Mode of Payment checkboxes (Cash/UPI/Net Banking)
- ✅ Amount in words
- ✅ Received By with amount box
- ✅ Payer Signature and Authorized Signature lines
- ✅ Receipt number
