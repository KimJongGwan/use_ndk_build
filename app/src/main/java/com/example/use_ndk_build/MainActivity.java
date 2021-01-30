package com.example.use_ndk_build;
//1 개, 2 개 또는 3 개의 버튼을 표시 할 수있는 Dialog의 하위 클래스입니다.

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.CAMERA;

//구형 Android 기기에서 일부 최신 플랫폼 기능을 사용하려는 활동의 기본 클래스입니다.
//Interface that defines a dialog-type class that can be shown, dismissed, or canceled, and may have buttons that can be clicked.
//A mapping from String keys to various Parcelable values.
//Indicates that Lint should treat this type as targeting a given API level, no matter what the project target is.
//Class for retrieving various kinds of information related to the application packages that are currently installed on the device.
//Information about the current build, extracted from system properties.
//API for sending log output.
//Provides a dedicated drawing surface embedded inside of a view hierarchy.
//The interface that apps use to talk to the window manager.
//추가한 주석ㅘㅏㅚㅗㅓㅏㅣㅗㅓㅣ


public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult1;
    private Mat matResult2;
    private Mat matResult3;
    private Mat matResult4;
    public int threshold1=50;
    public int threshold2=50;
    public int threshold3=50;
    public int threshold4=50;
    public int threshold5=50;

    private CameraBridgeViewBase mOpenCvCameraView;

// native는 자바가 아닌 언어(보통 C나 C++)로 구현한 후 자바에서 사용하려고 할 때 이용하는 키워드이다.
// 자바로 구현하기 까다로운 것을 다른 언어로 구현해서, 자바에서 사용하기 위한 방법이다. 구현할때 JNI(Java Native Interface)를 사용한다
    //ConvertRGBtoGray 매소드를 C++과 연결함.
    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult1);
    public native void ConvertGraytoCanny(long matAddrResult1,long matAddrResult2, int th1, int th2);
    public native void ConvertCannytoHoughp(long matAddrResult2,long matAddrResult3, int th1, int th2 ,int th3);
    public native void ConvertHoughptoMorph();

    // OpenCV 네이티브 라이브러리와 C++코드로 빌드된 라이브러리를 읽음(CMakeList.txt add_library and target 참조)
    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }


    //status에 따라서 LoaderCallbackInterface가 SUCCESS이면 카메라뷰를 enable, 아니면 다시 반복하는 함수인데 정확한 역활은 아직 모르겠음
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    //화면이 세로에서 가로 모드 전환 시, back을 눌러 activity를 종료한 경우, 등등의 상황에서 onCreate 함수가 다시 호출됨, init 같은것.
    //savedInstanceState로 activity가 종료될 때 데이터를 저장함.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //전체화면으로 만들기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //화면이 계속 켜지는것을 유지하게 함
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //()안의 내용을 파싱하여 뷰를 생성하는 메소드
        setContentView(R.layout.activity_main);

        //threshole값 변경해는부분
        TextView tv1 = (TextView)findViewById(R.id.textView_threshold1);
        SeekBar sb1  = (SeekBar) findViewById(R.id.seekBar_threshold1);
        TextView tv2 = (TextView)findViewById(R.id.textView_threshold2);
        SeekBar sb2  = (SeekBar) findViewById(R.id.seekBar_threshold2);
        TextView tv3 = (TextView)findViewById(R.id.textView_threshold3);
        SeekBar sb3  = (SeekBar) findViewById(R.id.seekBar_threshold3);
        TextView tv4 = (TextView)findViewById(R.id.textView_threshold4);
        SeekBar sb4  = (SeekBar) findViewById(R.id.seekBar_threshold4);
        TextView tv5 = (TextView)findViewById(R.id.textView_threshold5);
        SeekBar sb5  = (SeekBar) findViewById(R.id.seekBar_threshold5);

        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold1=seekBar.getProgress();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                threshold1=seekBar.getProgress();
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threshold1=seekBar.getProgress();
                tv1.setText(new StringBuilder().append("th1:"+threshold1));
            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold2=seekBar.getProgress();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                threshold2=seekBar.getProgress();
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threshold2=seekBar.getProgress();
                tv2.setText(new StringBuilder().append("th2:"+threshold2));
            }
        });
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold3=seekBar.getProgress();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                threshold3=seekBar.getProgress();
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threshold3=seekBar.getProgress();
                tv3.setText(new StringBuilder().append("th3:"+threshold3));
            }
        });
        sb4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold4=seekBar.getProgress();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                threshold4=seekBar.getProgress();
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threshold4=seekBar.getProgress();
                tv4.setText(new StringBuilder().append("th4:"+threshold4));
            }
        });
        sb5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold5=seekBar.getProgress();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                threshold5=seekBar.getProgress();
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                threshold5=seekBar.getProgress();
                tv5.setText(new StringBuilder().append("th5:"+threshold5));
            }
        });


        //카메라 보이는 뷰를 넣음
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
    }
//https://developer.android.com/guide/components/activities/activity-lifecycle?hl=ko
    //onPause : 시스템은 사용자가 활동을 떠나는 것을 나타내는 첫 번째 신호로 이 메서드를 호출합니다
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    //onResume : 활동이 재개됨 상태에 들어가면 포그라운드에 표시되고 시스템이 onResume() 콜백을 호출합니다. 이 상태에 들어갔을 때 앱이 사용자와 상호작용합니다
    @Override
    public void onResume()
    {
        super.onResume();
        //Android Project에 OpenCV 환경 구성이 되었는지 확인할 때는 OpenCVLoader.initDebug() 메소를 호출하여 확인할 수 있습니다.
        //https://faith-developer.tistory.com/184
        if (!OpenCVLoader.initDebug()) {//환경 구성이 안되었을 때
            Log.d(TAG, "onResume :: Internal OpenCV library not found."); //디버그할때 msg날림
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {//환경구성이 되었을 때
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //활동이 소멸됨 상태로 전환하면 이 활동의 수명 주기와 연결된 모든 수명 주기 인식 구성요소는 ON_DESTROY 이벤트를 수신합니다.
    // 여기서 수명 주기 구성요소는 활동이 소멸되기 전에 필요한 것을 정리할 수 있습니다.
    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    //이미지를 읽어오는 부분

    //
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //rgb 값 읽어옴
        matInput = inputFrame.rgba();

        if ( matResult1 == null || matResult2 == null || matResult3 == null){
            //type의 종류는 링크 참조  //https://nextus.tistory.com/14
            matResult1 = new Mat(matInput.rows(), matInput.cols(), matInput.type());
            matResult2 = new Mat(matInput.rows(), matInput.cols(), matInput.type());
            matResult3 = new Mat(matInput.rows(), matInput.cols(), matInput.type());
        }
        //getNativeObjAddr은 Mat형식 인자들 가져오는거 https://dreamaz.tistory.com/456
        ConvertRGBtoGray(matInput.getNativeObjAddr(), matResult1.getNativeObjAddr());
        ConvertGraytoCanny(matResult1.getNativeObjAddr(),matResult2.getNativeObjAddr(),threshold1,threshold2);
        ConvertCannytoHoughp(matResult2.getNativeObjAddr(),matResult3.getNativeObjAddr(),threshold3,threshold4,threshold5);

        return matResult3;
    }

    //<? extends 클래스> 매개변수의 자료형을 특정 클래스를 상속받은 클래스로만 제한함
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


    //여기서부턴 퍼미션 관련 메소드
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;


    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }


}