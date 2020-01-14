
package com.samples.flironecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

class BarcodeScanner {

    private static final String TAG = "BarcodeScanner";

    private BarcodeDetector detector;

    private Paint rectPaint;
    private Paint barcodePaint;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 4.0f;

    BarcodeScanner(Context context) {
        detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.EAN_13 | Barcode.QR_CODE)
                .build();

        if (!detector.isOperational()) {
            Log.e(TAG, "Detector initialisation failed");
        }

        rectPaint = new Paint();
        rectPaint.setColor(TEXT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        barcodePaint = new Paint();
        barcodePaint.setColor(TEXT_COLOR);
        barcodePaint.setTextSize(TEXT_SIZE);
    }

    public final SparseArray<Barcode> scanBarcode(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        for (int index = 0; index < barcodes.size(); index++) {
            Barcode code = barcodes.valueAt(index);
            Log.d(TAG, "index = " + index + "\n");
            Log.d(TAG, "displayValue = " + code.displayValue + "\n");
            Log.d(TAG, "rawValue = " + code.rawValue + "\n");
            Log.d(TAG, "valueFormat = " + code.valueFormat + "\n");
//            bitmap = drawCode(bitmap, code);

//            bitmap = drawText(bitmap, code.rawValue);
        }
        if (barcodes.size() == 0) {
            Log.d(TAG, "No barcode detected. Please try again.");
        }
        return barcodes;
    }

    public final Bitmap drawCode(Bitmap bitmap, Barcode barcode, boolean need_scale) {

        float IR_TO_VISUAL_RATIO = (float) (640. / 1440.);

        bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);

        Point[] points = barcode.cornerPoints;
        if (need_scale) {
            for (Point point : points) {
                point.x = (int) (point.x * IR_TO_VISUAL_RATIO);
                point.y = (int) (point.y * IR_TO_VISUAL_RATIO);
            }
        }
        Path path = new Path();
        path.reset();
        path.moveTo(points[0].x, points[0].y);
        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i].x, points[i].y);
        }
        path.lineTo(points[0].x, points[0].y);

        // Draws the bounding box around the BarcodeBlock.
        canvas.drawPath(path, rectPaint);

        // Renders the barcode at the bottom of the box.
        RectF rect = new RectF(barcode.getBoundingBox());
        canvas.drawText(barcode.rawValue, rect.left, rect.bottom, barcodePaint);

        return bitmap;
    }

//    public final Bitmap drawText(Bitmap bitmap, String text) {
//        bitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
//        Canvas canvas = new Canvas(bitmap);
//
//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE); // Text Color
//        paint.setTextSize(48); // Text Size
////        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)); // Text Overlapping Pattern
//        // some more settings...
//
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        canvas.drawText(text, 50, 50, paint);
//
//        return bitmap;
//    }

}