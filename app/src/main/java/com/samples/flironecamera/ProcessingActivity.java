package com.samples.flironecamera;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessingActivity extends AppCompatActivity {

    private static final String TAG = "ProcessingActivity";

    private ImageView image;

    private LinkedBlockingQueue<FrameDataHolder> framesBuffer = new LinkedBlockingQueue(21);

    //Handles network camera operations
    private CameraHandler cameraHandler; // to be received from initial activity through MyApplication singleton
    BarcodeScanner barcodeScanner;

    ScreenMode screenMode = new ScreenMode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing);

        image = findViewById(R.id.image);

        Button btnScan = findViewById(R.id.btnScan);


        MyApplication app = (MyApplication) getApplicationContext();
        cameraHandler = app.getCameraHandler();
        barcodeScanner = app.getBarcodeScanner();

        cameraHandler.startStream(streamDataListener);
        screenMode.setMode(Mode.SEARCH);
    }

    public void onButtonScanClick(View v) {
        if (screenMode.getMode() == Mode.FOUND) {
            screenMode.setMode(Mode.SEARCH);
            Log.d(TAG, "Mode changed to SEARCH");
        }
    }

    @Override
    protected void onDestroy() {
        cameraHandler.disconnect();
        super.onDestroy();
    }

    void showImages(Bitmap msxBitmap, Bitmap dcBitmap) {
        switch (screenMode.getMode()) {
            case SEARCH:
                image.setImageBitmap(dcBitmap);
                break;
            case FOUND:
                image.setImageBitmap(msxBitmap);
                break;
        }
    }

    private final CameraHandler.StreamDataListener streamDataListener = new CameraHandler.StreamDataListener() {

        @Override
        public void images(FrameDataHolder dataHolder) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showImages(dataHolder.msxBitmap, dataHolder.dcBitmap);
                }
            });
        }

        @Override
        public void images(Bitmap msxBitmap, Bitmap dcBitmap) {

            if (screenMode.getMode() == Mode.FOUND) return;
            SparseArray<Barcode> barcodes = barcodeScanner.scanBarcode(dcBitmap);
            if (barcodes.size() > 0) { // code(s) is(are) found
                Barcode barcode_0 = barcodes.valueAt(0);
                String code = barcode_0.rawValue;
//                dcBitmap = barcodeScanner.drawCode(dcBitmap, barcode_0, false);
                msxBitmap = barcodeScanner.drawCode(msxBitmap, barcode_0, true);
                screenMode.setMode(Mode.FOUND);
                Log.d(TAG, barcodes.size() + "code(s) found. Mode changed to FOUND");
            }

            try {
                framesBuffer.put(new FrameDataHolder(msxBitmap, dcBitmap));
            } catch (InterruptedException e) {
                //if interrupted while waiting for adding a new item in the queue
                Log.e(TAG, "images(), unable to add incoming images to frames buffer, exception:" + e);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "framebuffer size:" + framesBuffer.size());
                    FrameDataHolder poll = framesBuffer.poll();
                    showImages(poll.msxBitmap, poll.dcBitmap);
                }
            });

        }
    };

    public enum Mode {
        UNDEF,
        SEARCH,
        FOUND
    }

    public class ScreenMode {
        Mode currentMode = Mode.UNDEF;

        ScreenMode() {
        }

        void setMode(Mode mode) {
            switch (mode) {
                case UNDEF:
                    break;
                case SEARCH:
                    break;
                case FOUND:
                    break;
            }
            this.currentMode = mode;
        }

        public Mode getMode() {
            return this.currentMode;
        }
    }


}
