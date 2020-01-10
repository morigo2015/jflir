
package com.samples.flironecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

class BarcodeScanner {

    private static final String TAG = "BarcodeScanner";

    private BarcodeDetector detector;

    public BarcodeScanner(Context context) {
        detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.EAN_13 | Barcode.QR_CODE)
                .build();

        if (!detector.isOperational()) {
            Log.e(TAG, "Detector initialisation failed");
        }
    }

    public final Bitmap scanBarcode(Bitmap bitmap){

        if (detector.isOperational() && bitmap != null) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            for (int index = 0; index < barcodes.size(); index++) {
                Barcode code = barcodes.valueAt(index);
                Log.d(TAG,"index = " + index + "\n");
                Log.d(TAG,"displayValue = " + code.displayValue + "\n");
                Log.d(TAG,"rawValue = " + code.rawValue + "\n");
                Log.d(TAG,"valueFormat = " + code.valueFormat + "\n");

                bitmap = drawText(bitmap, code.rawValue);
            }
            if (barcodes.size() == 0) {
                Log.d(TAG, "No barcode detected. Please try again.");
            }
        } else {
            Log.e(TAG,"Detector initialisation failed");
        }

        return bitmap;
    }


    private final Bitmap drawText(Bitmap bitmap, String text){
        bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Text Color
        paint.setTextSize(48); // Text Size
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
        // some more settings...

        canvas.drawBitmap(bitmap, 0, 0, paint);
        canvas.drawText(text, 50, 50, paint);

        return bitmap;
    }

}