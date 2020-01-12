package com.samples.flironecamera;

import android.app.Application;

// singleton to transfer info between my activities

public class MyApplication extends Application {
    private CameraHandler cameraHandler;
    private BarcodeScanner barcodeScanner;

    public CameraHandler getCameraHandler() {return cameraHandler;}
    public void setCameraHandler(CameraHandler cameraHandler) {this.cameraHandler = cameraHandler;}

    public BarcodeScanner getBarcodeScanner() {return barcodeScanner;}
    public void setBarcodeScanner(BarcodeScanner barcodeScanner) {this.barcodeScanner = barcodeScanner;}
}
