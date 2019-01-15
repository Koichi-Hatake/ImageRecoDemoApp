/*
 * Copyright (C) 2018 Koichi Hatakeyama
 * All rights reserved.
 */
#ifndef DEEPLEARNING_NNABLA_INTERFACE_H
#define DEEPLEARNING_NNABLA_INTERFACE_H

#include <nbla_utils/nnp.hpp>
#include <android/log.h>

#include <cassert>
#include <fstream>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>

class NNablaInterface {

private:
    nbla::utils::nnp::Nnp *mNnp = NULL;
    std::shared_ptr<nbla::utils::nnp::Executor> mExecutor;
    nbla::Context mContext{{"cpu:float"}, "CpuCachedArray", "0"};
    const char *TAG = "NativeCode";

public:
    bool initialize(std::string nnp_file, std::string network_name) {
        if (mNnp != NULL) {
            delete mNnp;
        }
        mNnp = new nbla::utils::nnp::Nnp(mContext);
        mNnp->add(nnp_file);
        mExecutor = mNnp->get_executor(network_name);
        mExecutor->set_batch_size(1);

        return true;
    }

    const float *predict(const int *rgb_data, const int len) {

        nbla::CgVariablePtr x = mExecutor->get_data_variables().at(0).variable;
        //uint8_t *data = x->variable()->cast_data_and_get_pointer<uint8_t>(mContext);
        uint32_t *data = x->variable()->cast_data_and_get_pointer<uint32_t>(mContext);
        __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Input Length: %d", len);
        __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Buffer Size: %d", x->variable()->size());

        // Copy RGB data array to input buffer.
        std::memcpy(data, rgb_data, sizeof(uint32_t)*len);

        mExecutor->execute();
        nbla::CgVariablePtr y = mExecutor->get_output_variables().at(0).variable;
        const float *y_data = y->variable()->get_data_pointer<float>(mContext);

        return y_data;
    }
};

#endif //DEEPLEARNING_NNABLA_INTERFACE_H
