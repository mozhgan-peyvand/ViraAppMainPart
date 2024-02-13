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
Java_ai_ivira_app_features_imazh_data_ImazhRemoteDataSource_bi(JNIEnv *env, jobject thiz) {
    std::string bi = "http://192.168.33.21:3002";
    return env->NewStringUTF(bi.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_avasho_data_AvashoRepository_bu(JNIEnv *env, jobject thiz) {
    std::string BU = "9.8!)v~u-8t%\";$08b8(c\";$08c6;84--(";
    std::string key = "QZL";

    std::string dec = BU;

    for (int i = 0; i < BU.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

jstring ak(JNIEnv *env) {
    std::string AK = "8)0?~oj.o?+c:xc`)b8{3:}e:{f=(f`xfh}f?|oiubl.4l|0nz3<+o=|ek+nn,0<+4?(ekyn;\177\062<tg;,7o/7<(5`/f;y7l~g:ybj}4`,5`~b;.5it3:}nl{f?,ojydny";
    std::string key = "YMV";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cgw(JNIEnv *env, jobject thiz) {
    std::string AK = ".,2(5biw!92=19?u0kh961694,h14w5=4./;#w!92=19?\030uw27-=(";
    std::string key = "FX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cup(JNIEnv *env, jobject thiz) {
    std::string AK = ".,2(5biw$942'.'6\"u0kh./*'v'(/('*2v/*i+#*01%=i:'*,909(<\006ki<','w0149iihhhh";
    std::string key = "FX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cgws(JNIEnv *env, jobject thiz) {
    std::string AK = "3;>;;:04(";
    std::string key = "QZL";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cgwu(JNIEnv *env, jobject thiz) {
    std::string AK = "'3>0";
    std::string key = "QZL";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cgwd(JNIEnv *env, jobject thiz) {
    std::string AK = ".,2(5biw!92=19?u0kk<#.h961694,h14w5=4./;#w!92=19?\030uw27-=(";
    std::string key = "FX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cupd(JNIEnv *env, jobject thiz) {
    std::string AK = ".,2(5biw$942'.'6\"u0kk<#.h('*2<6v/*i+#*01%=i:'*,909(<\006ki<','g5;.=+9\b9+={./*'~5;.=+9\020=4+/7(ewvvvv";
    std::string key = "FX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_home_data_VersionDataHelper_cgwpd(JNIEnv *env, jobject thiz) {
    std::string AK = ".*\nn%\b/\002u+3o\004>\r>\026\064\002\025";
    std::string key = "FX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_utils_ui_initializers_SentryInitializer_dsn(JNIEnv *env, jobject thiz) {
    std::string AK = "?,>'+pxw|`krdk+g=.ck~cm\177\065<s4:|nhz4kx1j}c\030\071\062\066>%!d'98#<:y18xi|";
    std::string key = "WXJ";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_avasho_data_AvashoRemoteDataSource_sak(JNIEnv *env, jobject thiz) {
    return ak(env);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_imazh_data_ImazhRemoteDataSource_sai(JNIEnv *env, jobject thiz) {
    std::string AK = "2f632ed518a52e2b972a4a81f01f6f58fcd38f9ae324769ec2a5f1e9642ac7accac10f894eb2b7fd807a86934b8b4443f42c30f5decf54745d3d4e0716dacc5a";
    return env->NewStringUTF(AK.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_ava_1negar_data_AvanegarRemoteDataSource_nak(JNIEnv *env, jobject thiz) {
    return ak(env);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_config_data_ConfigDataHelper_cud(JNIEnv *env, jobject thiz) {
    std::string AK = ";\021\002,";
    std::string key = "Mxp";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_config_data_ConfigDataHelper_cpd(JNIEnv *env, jobject thiz) {
    std::string AK = "\021\v5O\032)\020#J\n\fN;\037\062\037)\025=4";
    std::string key = "y";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_config_data_ConfigDataHelper_cad(JNIEnv *env, jobject thiz) {
    std::string AK = "\021.\r*\n`Vu\033;\v0\030,\030\064\035w\017iT>\034,W*\030(\r>\tt\020(";
    std::string key = "yZ";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_config_data_ConfigDataHelper_cgp(JNIEnv *env, jobject thiz) {
    std::string AK = "\025\071\a\002";
    std::string key = "cPu";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_config_data_ConfigDataHelper_cap(JNIEnv *env, jobject thiz) {
    std::string AK = "\035,\001(\006bZw\027\071\a2\024.\024\066\021u\003k[.\034*\024v\024(\034(\024*\001v\034*";
    std::string key = "uX";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}