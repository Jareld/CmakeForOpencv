#include <jni.h>
#include <string>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/log.h>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <opencv2/imgproc/imgproc_c.h>
#include <opencv2/core/matx.hpp>
#include <opencv2/core/mat.hpp>

#define  LOG_TAG    "videoplay"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)



extern "C" {
jstring
Java_com_example_jareld_cmakeforopencv_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    cv::Mat src;

    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT jbyteArray JNICALL
Java_com_example_jareld_cmakeforopencv_MainActivity_getNewPixel(JNIEnv *env, jobject instance,
                                                                jstring path_) {
         const char *path = env->GetStringUTFChars(path_, 0);
         cv::Mat src = cv::imread(path );
         LOGD("aaa3 = %s" , path);

         LOGD("src.channels = %d" , src.channels());
         cv::Mat dst;

         int width = src.cols;
         int height = src.rows;
         LOGD("width = %d , height = %d" , width , height);
         cv::resize(src, dst, cv::Size(3840, 1700),  0, 0, cv::INTER_AREA);
         width = dst.cols;

           height = dst.rows;
    int channle = dst.channels();
    int total= dst.total();

    LOGD("width = %d , height = %d channle = %d  , total = %d" , width , height , channle , total);

    jbyteArray arr =   env->NewByteArray(width * height *4  );
    jbyte *pics = env->GetByteArrayElements(arr, NULL);
//    for(int i = 0 ; i < height ; i++)
//    {
//        for(int j = 0 ; j < width ; j++)
//        {
//            int index = i*height + j;
//            int pixelIntensity = inputImageData[index];// 这样就遍历了每个像素
//            pics[index] = pixelIntensity;
//        }
//    }
    long index = 0;
    //遍历所有像素，并设置像素值(取反)
//    for( int i = 0; i < height; i++) {
//
//        for( int j = 0; j < width; j++ ) {
//            cv::Vec4b val = dst.at<cv::Vec4b>(i,j);
//            char blue = val[0];
//            char green = val[1];
//            char red = val[2];
//            char alpha = val[3];
//            index = i *width *4 + j *4;
//            pics[index + 0 ] = red;
//            pics[index + 1 ] = green;
//            pics[index + 2 ] = blue;
//            pics[index + 3 ] = alpha;
//
//
//        }
//    }
   int num = 0;
      for( int i = 0; i < height; i++) {
          //获取第 i 行首像素指针
          cv::Vec3b *p = dst.ptr<cv::Vec3b>(i);
          for( int j = 0; j < width; j++ ) {
//              index = i *width *4 + j * 4;
//                pics[index + 0 ]  =    p[j][2] ;
//                 pics[index + 1 ]  =    p[j][1] ;
//                pics[index + 2 ]  =    p[j][0] ;
//                 pics[index + 3 ] = 255;
              pics[index] = p[j][2];
              index ++;
              pics[index] = p[j][1];
              index ++;
              pics[index] = p[j][0];
              index ++;
              pics[index] = 255;
              index ++;

          }
      }
    LOGD("index = %d ");
    jbyteArray it = (*env).NewByteArray(width * height   *4 );
    (*env).SetByteArrayRegion(it, 0, width * height*4 , pics);
    env->ReleaseStringUTFChars(path_, path);
    env->ReleaseByteArrayElements(arr, pics, 0);

    return it;





}

JNIEXPORT void JNICALL
Java_com_example_jareld_cmakeforopencv_MainActivity_setSurfaceView(JNIEnv *env, jclass type,
                                                                   jobject surface,
                                                                   jbyteArray rgba_, jint width,
                                                                   jint height) {
    jbyte *rgba = env->GetByteArrayElements(rgba_, NULL);
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);

    ANativeWindow_setBuffersGeometry(nativeWindow, 3840, 1700,
                                     WINDOW_FORMAT_RGBA_8888);


    ANativeWindow_Buffer windowBuffer;
    ANativeWindow_lock(nativeWindow, &windowBuffer, 0);
//    int32_t  format = windowBuffer.format;
//    int32_t width1 = windowBuffer.width;
//    int32_t height1 = windowBuffer.height;
//    int32_t stride1 = windowBuffer.stride;
//    LOGD("formataaa %d" , format );
//    LOGD("width1aaa %d" , width1 );
//    LOGD("height1aaa %d" , height1 );
//    LOGD("stride1aaa %d" , stride1 );

    uint8_t *dst = (uint8_t *) windowBuffer.bits;
    int dstStride = windowBuffer.stride * 4;
    uint8_t *src = (uint8_t *) rgba;
    int srcStride =width *4 ;
    LOGD("dstStride = %d ----srcStride = %d " , dstStride , srcStride );

//    // 由于window的stride和帧的stride不同,因此需要逐行复制
    int h;
    for (h = 0; h < height; h++) {
        //  LOGD("进行了 == %d" , h);
        memcpy(dst + h * dstStride, src + h * srcStride, srcStride);
    }
    LOGD("h=%d" , h);
//
    ANativeWindow_unlockAndPost(nativeWindow);
    env->ReleaseByteArrayElements(rgba_, rgba, 0);
}
JNIEXPORT void JNICALL
Java_com_example_jareld_cmakeforopencv_MainActivity_setPathAndSurfaceView(JNIEnv *env,
                                                                          jobject instance,
                                                                          jstring path_,
                                                                          jobject surface,
                                                                          jint width, jint height , jint type) {
    const char *path = env->GetStringUTFChars(path_, 0);

    jbyteArray arr =   env->NewByteArray(width * height *4  );
    jbyte *rgba = env->GetByteArrayElements(arr, NULL);

    cv::Mat src = cv::imread(path );
    cv::Mat dst;

 switch (type){
     case cv::INTER_NEAREST :
         cv::resize(src, dst, cv::Size(width, height),  0, 0, cv::INTER_NEAREST);
         break;
     case cv::INTER_LINEAR :
         cv::resize(src, dst, cv::Size(width, height),  0, 0, cv::INTER_LINEAR);
         break;
     case cv::INTER_CUBIC :
         cv::resize(src, dst, cv::Size(width, height),  0, 0, cv::INTER_CUBIC);
         break;
     case cv::INTER_AREA :
         cv::resize(src, dst, cv::Size(width, height),  0, 0, cv::INTER_AREA);
         break;
     case cv::INTER_LANCZOS4 :
         cv::resize(src, dst, cv::Size(width, height),  0, 0, cv::INTER_LANCZOS4);
         break;
 }
 long index = 0;
 for( int i = 0; i < height; i++) {
     //获取第 i 行首像素指针
     cv::Vec3b *p = dst.ptr<cv::Vec3b>(i);
     //因为mat中只有3个通道的，  要是 Vec4b  就错了
     for( int j = 0; j < width; j++ ) {
         // 这里 为什么赋值是210   因为opencv中的顺序是BGR
         //                         而我们需要的顺序是RGBA，所以自己最后还加了个Alpha
       //  index = i *width *4 + j * 4;
//            rgba[index + 0 ]  =    p[j][2] ;
//            rgba[index + 1 ]  =    p[j][1] ;
//            rgba[index + 2 ]  =    p[j][0] ;
//            rgba[index + 3 ] = 255;
         rgba[index] = p[j][2];
         index ++;
         rgba[index] = p[j][1];
         index ++;
         rgba[index] = p[j][0];
         index ++;
         rgba[index] = 255;
         index ++;
     }
 }

 ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);

 ANativeWindow_setBuffersGeometry(nativeWindow, width, height,
                                  WINDOW_FORMAT_RGBA_8888);
 ANativeWindow_Buffer windowBuffer;
 ANativeWindow_lock(nativeWindow, &windowBuffer, 0);
 uint8_t *dst_data = (uint8_t *) windowBuffer.bits;
 int dstStride = windowBuffer.stride * 4;
 uint8_t *src_data = (uint8_t *) rgba;
 int srcStride =width *4 ;
 int h;
 for (h = 0; h < height; h++) {
     memcpy(dst_data + h * dstStride, src_data + h * srcStride, srcStride);
 }
 ANativeWindow_unlockAndPost(nativeWindow);
 env->ReleaseByteArrayElements(arr, rgba, 0);
 env->ReleaseStringUTFChars(path_, path);

}
}