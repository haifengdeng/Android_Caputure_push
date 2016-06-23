# Android.mk for FFmpeg
#
# Lei Xiaohua À×Ïöæè
# leixiaohua1020@126.com
# http://blog.csdn.net/leixiaohua1020
# 

LOCAL_PATH := $(call my-dir)

# FFmpeg library
include $(CLEAR_VARS)
LOCAL_MODULE := rtmp 
LOCAL_SRC_FILES := librtmp/librtmp.so
include $(PREBUILT_SHARED_LIBRARY)

# Program
include $(CLEAR_VARS)
LOCAL_MODULE := rtmp-jni 
LOCAL_SRC_FILES := librtmp-jni.c rtmpmuxer.c  flvmuxer/xiecc_rtmp.c
LOCAL_C_INCLUDES += $(LOCAL_PATH)/librtmp $(LOCAL_PATH) $(LOCAL_PATH)/flvmuxer
LOCAL_LDLIBS := -llog -lz
LOCAL_SHARED_LIBRARIES := rtmp
include $(BUILD_SHARED_LIBRARY)

