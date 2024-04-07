#include <jni.h>
#include <string>

jstring bu(JNIEnv *env) {
    std::string BU = "9.8!)v~u-8t%\";$08b8(c";
    std::string key = "QZL";

    std::string dec = BU;

    for (int i = 0; i < BU.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];


    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_utils_common_di_NetworkModule_bu(JNIEnv *env, jobject thiz) {
    return bu(env);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_imazh_data_ImazhRemoteDataSource_bu(JNIEnv *env, jobject thiz) {
    return bu(env);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_hamahang_data_HamahangRemoteDataSource_bu(JNIEnv *env, jobject thiz) {
    return bu(env);
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
    std::string AK = "a{5axb?x5i(7h|n:}4ax48t2;~5nx`jt`m\177c<t0;\177e:.eh(gi}f?.0;,ei{go)3:|0oub<t0kuda,cnuco,4n|5k}fk|fm|7:+gj}gkuni~do(dm\177oo(go{d;~d</anx";
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

    std::string AK = "jz0i/en)3<+nh/f<u4:~4=.0`xbkx2;,bj|28|0?{oata<y3jybn+e<ua?\177\060<t4nu4`,o?~4m,e;)nl+0jz4l\177\062=y3l,4;zf?{fmzem.0i~gk/fotbm)g?y5?)cl(ea,";
    std::string key = "YMV";
    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_ava_1negar_data_AvanegarRemoteDataSource_nak(JNIEnv *env, jobject thiz) {
    return ak(env);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_imazh_data_ImazhRemoteDataSource_iak(JNIEnv *env, jobject thiz) {
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

// region login
extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_login_data_LoginRemoteDataSource_lbu(JNIEnv *env, jobject thiz) {
    std::string AK = "\020\030\005\b\037KWC\026\031\030\024\017\r\bU\032BV\032\030\n\r_\031\034\030\b\r\003\fB\030\nC\002\035\036\a\021\017\024W\031\002\035\036<\031\002\020\037\t\003\070]^";
    std::string key = "xlq";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];

    return env->NewStringUTF(dec.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_login_data_LoginRemoteDataSource_lgs(JNIEnv *env, jobject thiz) {
    std::string AK = "\025\071\a\002";
    std::string key = "cPu";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];
    return env->NewStringUTF(dec.c_str());
}


extern "C"
JNIEXPORT jstring JNICALL
Java_ai_ivira_app_features_login_data_LoginRemoteDataSource_ls(JNIEnv *env, jobject thiz) {
    std::string AK = "\025\071\a\002";
    std::string key = "cPu";

    std::string dec = AK;

    for (int i = 0; i < AK.size(); i++)
        dec[i] = dec[i] ^ key[i % (key.size() / sizeof(char))];
    return env->NewStringUTF(dec.c_str());
}
// endregion login