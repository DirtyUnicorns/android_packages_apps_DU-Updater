LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := DU-Updater
LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_SRC_FILES := $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res \
    frameworks/support/v7/appcompat/res \
    frameworks/support/v7/cardview/res \
    frameworks/support/design/res \
    frameworks/support/v7/recyclerview/res

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.appcompat
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.cardview
LOCAL_AAPT_FLAGS += --extra-packages android.support.design
LOCAL_AAPT_FLAGS += --extra-packages android.support.v7.recyclerview
LOCAL_AAPT_FLAGS += --extra-packages com.loopj.android.http
LOCAL_AAPT_FLAGS += --extra-packages cz.msebera.android.httpclient

LOCAL_STATIC_JAVA_LIBRARIES += android-support-v7-appcompat android-support-v7-cardview android-support-v13 android-support-design android-support-v7-recyclerview android-async-http httpclient

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := android-async-http:libs/android-async-http-1.4.9.jar \
                                        httpclient:libs/httpclient-4.4.1.2.jar

include $(BUILD_MULTI_PREBUILT)

