/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
#include <jni.h>
#include <string>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#include <nbla_utils/nnp.hpp>

#include "nnabla_interface.hpp"

#define OUTPUT_SIZE 1000
static NNablaInterface snn;

/**
 *
 */
extern "C" JNIEXPORT void
JNICALL
Java_net_seeeno_dl_imagerecodemo_NNablaImageNetDAO_nativeInitNeuralNetwork(
        JNIEnv *env,
        jobject mgr,
        jstring nnpPath,
        jstring network_name
        ) {

    const char *nnp_path = env->GetStringUTFChars(nnpPath, 0);
    const char *net_name = env->GetStringUTFChars(network_name, 0);
    snn.initialize(nnp_path, net_name);

}

/**
 *
 */
extern "C" JNIEXPORT const jfloatArray JNICALL
Java_net_seeeno_dl_imagerecodemo_NNablaImageNetDAO_nativePredict(
        JNIEnv *env,
        jobject obj,
        jintArray image_data
) {

    jfloatArray result;
    const int *rgb_data = env->GetIntArrayElements(image_data, 0);
    jsize len = env->GetArrayLength(image_data);
    result = env->NewFloatArray(OUTPUT_SIZE);

    const float *predict_array = snn.predict(rgb_data, len);
    env->SetFloatArrayRegion(result, 0, OUTPUT_SIZE, predict_array);

    return result;
}
