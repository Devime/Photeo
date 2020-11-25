package com.example.projet;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Newapn extends AppCompatActivity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private Button btnCapture;
    private TextureView textureView;

    //gesture
    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY = 90;
    //gesture grp view
    private float x1, x2, y1, y2;
    private ImageView grp1img;
    private TextView grp1txfix1;
    private TextView grp1txfix2;
    private TextView grp1txfix3;
    private TextView grp1tx1;
    private TextView grp1tx2;
    private TextView grp1tx3;
    private TextView grp2txfix1;
    private TextView grp2txfix2;
    private TextView grp2txfix3;
    private TextView grp2tx1;
    private TextView grp2tx2;
    private TextView grp2tx3;
    private int state = 0;
    private GestureDetector gestureDetector;

    //gyro
    private SensorManager sensorManager;
    private Sensor gyrosensor;
    private SensorEventListener gyroeventListener;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    //geopositionnement



    //Save to FILE
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apnview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);




        grp1img = findViewById(R.id.angle);
        grp1txfix1 = findViewById(R.id.X);
        grp1txfix2 = findViewById(R.id.Y);
        grp1txfix3 = findViewById(R.id.Z);
        grp1tx1 = findViewById(R.id.Xt);
        grp1tx2 = findViewById(R.id.Yt);
        grp1tx3 = findViewById(R.id.Zt);

        grp2txfix1 = findViewById(R.id.latitude);
        grp2txfix2 = findViewById(R.id.longitude);
        grp2txfix3 = findViewById(R.id.altitude);
        grp2tx1 = findViewById(R.id.latitudet);
        grp2tx2 = findViewById(R.id.longitudet);
        grp2tx3 = findViewById(R.id.altitudet);
        this.gestureDetector = new GestureDetector(this, this);

        gogyro();
        gogeo();

        textureView = (TextureView) findViewById(R.id.apnarea);

        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        btnCapture = (Button) findViewById(R.id.btakepic);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void gogeo() {

        String locationContext = Context.LOCATION_SERVICE;
        LocationManager locationManager =
                (LocationManager) getSystemService(locationContext);
        String provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},101);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("-------------------------------------------<----------------------------------<-------------------->"+location.getAltitude());
            String latitude =""+location.getLatitude();
            String longitude =""+location.getLongitude();
            String altitude =""+location.getAltitude();
            grp2tx1.setText(latitude);
            grp2tx2.setText(longitude);
            grp2tx3.setText(altitude);
        }




    }

    private void gogyro() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyrosensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if(gyrosensor == null){
            Toast.makeText(this,"gyro indisponible",Toast.LENGTH_LONG);
        }
        gyroeventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                int rotx= (int) (((float) sensorEvent.values[1])*100);
                int roty=(int) (((float) sensorEvent.values[0])*100);
                int rotz=(int) (((float) sensorEvent.values[2])*100);
                grp1tx1.setText(String.valueOf(rotx));
                grp1tx2.setText(String.valueOf(roty));
                grp1tx3.setText(String.valueOf(rotz));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    private void takePicture() {
        if(null == cameraDevice) {

            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final File file = new File(Environment.getExternalStorageDirectory()+"/"+ts+".jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(Newapn.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() {
        try{
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert  texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(),imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    if(cameraDevice == null)
                        return;
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Newapn.this, "Changed", Toast.LENGTH_SHORT).show();
                }
            },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(cameraDevice == null)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE,CaptureRequest.CONTROL_MODE_AUTO);
        try{
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(),null,mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    private void openCamera() {
        CameraManager manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            //Check realtime permission if run higher API 23
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId,stateCallback,null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        if(textureView.isAvailable())
            openCamera();
        else
            textureView.setSurfaceTextureListener(textureListener);
        sensorManager.registerListener(gyroeventListener,gyrosensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        sensorManager.unregisterListener(gyroeventListener);
        super.onPause();
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try{
            mBackgroundThread.join();
            mBackgroundThread= null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

        if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE &&
                Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
            // Left swipe...
            System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            switch (state) {
                case 0:
                    //nothing
                    break;
                case 1:
                    swipeperformer(0);
                    state=0;
                    break;
                case 2:
                    swipeperformer(1);
                    state=1;
                    break;
                case 3:
                    swipeperformer(2);
                    state=2;
                    break;
                default:
                    break;
            }
        }
        else if ((event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE &&
                Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)){
            // Right swipe...
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            switch (state) {
                case 0:
                    swipeperformer(1);
                    state=1;
                    break;
                case 1:
                    swipeperformer(2);
                    state=2;
                    break;
                case 2:
                    swipeperformer(3);
                    state=3;
                    break;
                case 3:
                    //nothing
                    break;
                default:
                    break;
            }
        }
        else if ((event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE &&
                Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)){
            // Swipe up...
        }
        else if ((event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE &&
                Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)) {
            // Swipe down...
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void swipeperformer(int etat){
        switch (etat) {
            case 0:
                // all
                grp1img.setVisibility(View.VISIBLE);
                grp1txfix1.setVisibility(View.VISIBLE);
                grp1txfix2.setVisibility(View.VISIBLE);
                grp1txfix3.setVisibility(View.VISIBLE);
                grp1tx1.setVisibility(View.VISIBLE);
                grp1tx2.setVisibility(View.VISIBLE);
                grp1tx3.setVisibility(View.VISIBLE);

                grp2txfix1.setVisibility(View.VISIBLE);
                grp2txfix2.setVisibility(View.VISIBLE);
                grp2txfix3.setVisibility(View.VISIBLE);
                grp2tx1.setVisibility(View.VISIBLE);
                grp2tx2.setVisibility(View.VISIBLE);
                grp2tx3.setVisibility(View.VISIBLE);
                break;
            case 1:
                //angle only
                grp1img.setVisibility(View.VISIBLE);
                grp1txfix1.setVisibility(View.VISIBLE);
                grp1txfix2.setVisibility(View.VISIBLE);
                grp1txfix3.setVisibility(View.VISIBLE);
                grp1tx1.setVisibility(View.VISIBLE);
                grp1tx2.setVisibility(View.VISIBLE);
                grp1tx3.setVisibility(View.VISIBLE);

                grp2txfix1.setVisibility(View.INVISIBLE);
                grp2txfix2.setVisibility(View.INVISIBLE);
                grp2txfix3.setVisibility(View.INVISIBLE);
                grp2tx1.setVisibility(View.INVISIBLE);
                grp2tx2.setVisibility(View.INVISIBLE);
                grp2tx3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                //only pos
                grp1img.setVisibility(View.INVISIBLE);
                grp1txfix1.setVisibility(View.INVISIBLE);
                grp1txfix2.setVisibility(View.INVISIBLE);
                grp1txfix3.setVisibility(View.INVISIBLE);
                grp1tx1.setVisibility(View.INVISIBLE);
                grp1tx2.setVisibility(View.INVISIBLE);
                grp1tx3.setVisibility(View.INVISIBLE);

                grp2txfix1.setVisibility(View.VISIBLE);
                grp2txfix2.setVisibility(View.VISIBLE);
                grp2txfix3.setVisibility(View.VISIBLE);
                grp2tx1.setVisibility(View.VISIBLE);
                grp2tx2.setVisibility(View.VISIBLE);
                grp2tx3.setVisibility(View.VISIBLE);

                break;
            case 3:
                //rien
                grp1img.setVisibility(View.INVISIBLE);
                grp1txfix1.setVisibility(View.INVISIBLE);
                grp1txfix2.setVisibility(View.INVISIBLE);
                grp1txfix3.setVisibility(View.INVISIBLE);
                grp1tx1.setVisibility(View.INVISIBLE);
                grp1tx2.setVisibility(View.INVISIBLE);
                grp1tx3.setVisibility(View.INVISIBLE);

                grp2txfix1.setVisibility(View.INVISIBLE);
                grp2txfix2.setVisibility(View.INVISIBLE);
                grp2txfix3.setVisibility(View.INVISIBLE);
                grp2tx1.setVisibility(View.INVISIBLE);
                grp2tx2.setVisibility(View.INVISIBLE);
                grp2tx3.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


}
