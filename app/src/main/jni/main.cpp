#include <jni.h>
#include "com_example_use_ndk_build_MainActivity.h"

#include <opencv2/opencv.hpp>

//#include "numpy/arrayobject.h"


using namespace cv;
using namespace std;

extern "C" {
JNIEXPORT void JNICALL Java_com_example_use_1ndk_1build_MainActivity_ConvertRGBtoGray
  (JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult1){

  Mat &matInput = *(Mat *)matAddrInput;
  Mat &matResult1 = *(Mat *)matAddrResult1;

  cvtColor(matInput, matResult1, COLOR_RGBA2GRAY);

  //Canny(matResult,matResult,150,155);
  //vector<Vec2f> lines;
  //HoughLines(matResult,lines,1,CV_PI/180,150);

  }

JNIEXPORT void JNICALL Java_com_example_use_1ndk_1build_MainActivity_ConvertGraytoCanny
        (JNIEnv *env, jobject instance, jlong matAddrResult1, jlong matAddrResult2, jint th1, jint th2){

    Mat &matResult1 = *(Mat *)matAddrResult1;
    Mat &matResult2 = *(Mat *)matAddrResult2;

    Canny(matResult1, matResult2, th1, th2);

}

JNIEXPORT void JNICALL Java_com_example_use_1ndk_1build_MainActivity_ConvertCannytoHoughp
        (JNIEnv *env, jobject instance, jlong matAddrResult2, jlong matAddrResult3, jint th3, jint th4, jint th5){

    Mat &matResult2 = *(Mat *)matAddrResult2;
    Mat &matResult3 = *(Mat *)matAddrResult3;
    vector<Vec4i> linesP;
    HoughLinesP(matResult2,linesP,1,(CV_PI/180),th5,th3,th4);

    Mat beforeResult3;
    threshold(matResult2,beforeResult3,150,255,THRESH_MASK);
    //matResult3=matResult2;
    for (size_t i = 0; i < linesP.size(); i++)
    {
        Vec4i l = linesP[i];
        line(beforeResult3, Point(l[0], l[1]), Point(l[2], l[3]), Scalar::all(255), 5, 8);
    }
    //matResult3=beforeResult3;
    Mat subd;
    subtract(matResult2,beforeResult3,subd);
    matResult3=subd;
}

}