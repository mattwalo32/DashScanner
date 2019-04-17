package com.la_a.dashscanner.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.la_a.dashscanner.R;
import com.la_a.dashscanner.fragment.FragmentCaptureBarcode;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int MIN_NUMBER_SCANS = 1;
    private static final int NUMBER_OF_CHARS = 12;

    private boolean isScanning = false;

    private ArrayList<String> rawList = new ArrayList<>();
    private ArrayList<String> barcodes = new ArrayList<>();
    private MediaPlayer mp;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        isScanning = false;
        mp = MediaPlayer.create(this, R.raw.beep);
    }

    public void onObjectDetected(Barcode data) {
        if(isScanning)
        {

            if(data.displayValue.length() != NUMBER_OF_CHARS)
                return;

            int count = 0;

            rawList.add(data.displayValue);

            for(int i = 0; i < rawList.size(); i++)
            {
                if(rawList.get(i).equals(data.displayValue))
                {
                    count++;

                    if(count >= MIN_NUMBER_SCANS)
                        break;
                }
            }

            if(!barcodes.contains(data.displayValue) && count >= MIN_NUMBER_SCANS)
            {
                barcodes.add(data.displayValue);
            }

            mp.start();
        }
    }

    public void toggleScanningMode(View v)
    {
        isScanning = !isScanning;

        String text = isScanning ? "Stop Scanning" : "Start Scanning";

        if(v instanceof TextView)
            ((TextView) v).setText(text);

        if(!isScanning)
            showScanningResults();
    }

    public void showScanningResults()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(barcodes.size() + " Items Scanned");

        StringBuilder message = new StringBuilder();
        for(String barcode : barcodes)
        {
            message.append(barcode);
            message.append("\n");
        }

        builder.setMessage(message.toString());
        builder.show();

        barcodes.clear();
    }

}
