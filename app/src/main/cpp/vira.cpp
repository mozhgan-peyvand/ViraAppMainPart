#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_utils_common_di_NetworkModule_bu(JNIEnv *env, jobject thiz) {
    std::string BU = "9.8!)v~u-8t%\";$08b8(c";
    std::string key = "QZL";

    std::string dec = BU;

    for (int i = 0; i < BU.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_utils_data_HeaderInterceptor_ak(JNIEnv *env, jobject thiz) {
    std::string AK = "8)0?~oj.o?+c:xc`)b8{3:}e:{f=(f`xfh}f?|oiubl.4l|0nz3<+o=|ek+nn,0<+4?(ekyn;\177\062<tg;,7o/7<(5`/f;y7l~g:ybj}4`,5`~b;.5it3:}nl{f?,ojydny";
    std::string key = "YMV";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}
