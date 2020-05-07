package com.example.nokiaphonerecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import android.util.AttributeSet;
import android.util.Size;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;


import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    ImageButton flashButton;
    ImageButton pictureButton;
    ImageButton settingsButton;

    TextureView textureView;

    CameraDevice cameraDevice;

    String cameraId;

    Size imageDimensions;

    CaptureRequest.Builder captureRequestBuilder;

    CameraCaptureSession cameraSession;

    Handler backgroundHandler;
    HandlerThread handlerThread;

    int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashButton = (ImageButton)findViewById(R.id.flash_button);
        settingsButton = (ImageButton)findViewById(R.id.settings_button);
        textureView = (TextureView)findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
    }

    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try{
                openCamera();
            } catch (CameraAccessException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void openCamera() throws CameraAccessException{
        CameraManager cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);

        cameraId = cameraManager.getCameraIdList()[0];

        CameraCharacteristics cc = cameraManager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap map = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestCameraPermission();
            return;
        }
        cameraManager.openCamera(cameraId, stateCallback, null);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else{

        }
    }

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                cameraDevice = camera;
                startCameraPreview();
            } catch (CameraAccessException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private void startCameraPreview() throws CameraAccessException{
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());

        Surface surface = new Surface(texture);

        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {

                try {
                    if (cameraDevice == null) {
                        return;
                    }
                    cameraSession = session;
                    updatePreview();
                } catch (CameraAccessException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {

            }
        } ,null);


    }

    private void updatePreview() throws CameraAccessException{
        if (cameraDevice == null){
            return;
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();

        if (textureView.isAvailable()){
            try{
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else{
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    private void startBackgroundThread(){
        handlerThread = new HandlerThread("Camera Background");
        handlerThread.start();

        backgroundHandler = new Handler(handlerThread.getLooper());

    }

    @Override
    protected void onPause() {
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    private void stopBackgroundThread() throws InterruptedException{
        handlerThread.quitSafely();
        handlerThread.join();

        backgroundHandler = null;
        handlerThread = null;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
    }

    public void showPopupflash(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.flash_settings, popup.getMenu());
        popup.show();
    }



}



