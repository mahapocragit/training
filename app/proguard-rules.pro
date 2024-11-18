# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-dontskipnonpubliclibraryclasses
#-forceprocessing
#-optimizationpasses 5
#-overloadaggressively

## Removing logging code
# -assumenosideeffects class android.util.Log {
#public static *** d(...);
#public static *** v(...);
#public static *** i(...);
#public static *** w(...);
#public static *** e(...);
#}

## The -dontwarn option tells ProGuard not to complain about some artefacts in the Scala runtime
#
#-dontwarn android.support.**
#-dontwarn android.app.Notification
#-dontwarn org.apache.log4j.**
#-dontwarn com.google.common.**
## Annotations are represented by attributes that have no direct effect on the execution of the code.

-keepattributes *Annotation*
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

#-keepattributes InnerClasses
#-keep class **.R
#-keep class **.R$* {
#    <fields>;
#}

# These options let obfuscated applications or libraries produce stack traces that can still be deciphered later on
#-renamesourcefileattribute SourceFile
   #-keepattributes SourceFile,LineNumberTable
#
#
#-keep class com.worklight.androidgap.push.** { *; }
#-keep class com.worklight.wlclient.push.** { *; }
#-keep class com.worklight.common.security.AppAuthenticityToken { *; }
#
## Enable proguard with Google libs
#-keep class com.google.** { *;}
#-dontwarn com.google.common.**
#-dontwarn com.google.ads.**
#
## apache.http
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
#-optimizations !class/merging/vertical*,!class/merging/horizontal*,!code/simplification/arithmetic,!field/*,!code/allocation/variable
#
#-keep class net.sqlcipher.** { *; }
#-dontwarn net.sqlcipher.**
#
#-keep class org.codehaus.** { *; }
#-keepattributes *Annotation*,EnclosingMethod
#
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keepclassmembers class * implements javax.net.ssl.SSLSocketFactory {
#   private  javax.net.ssl.SSLSocketFactory delegate;
#}
#
## Remove debug logs in release build
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#}
#
## These classes contain references to external jars which are not included in the default MobileFirst project.
#-dontwarn com.worklight.common.internal.WLTrusteerInternal*
#-dontwarn com.worklight.jsonstore.**
#-dontwarn org.codehaus.jackson.map.ext.*
#-dontwarn com.worklight.androidgap.push.GCMIntentService
#-dontwarn com.worklight.androidgap.plugin.WLInitializationPlugin
#-dontwarn com.worklight.wlclient.push.GCMIntentService
#-dontwarn org.bouncycastle.**
#-dontwarn com.worklight.androidgap.jsonstore.security.SecurityManager
#
#-dontwarn com.worklight.wlclient.push.WLBroadcastReceiver
#-dontwarn com.worklight.wlclient.push.common.*
#-dontwarn com.worklight.wlclient.api.WLPush
#-dontwarn com.worklight.wlclient.api.SecurityUtils
#
-dontwarn android.support.v4.**
-dontwarn android.net.SSLCertificateSocketFactory
-dontwarn android.net.http.*
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.management.**
-dontwarn javax.xml.**
-dontwarn org.apache.**
-dontwarn org.slf4j.**

-keep class com.drew.** {*;}
-keep interface com.drew.** {*;}
-keep enum com.drew.** {*;}

-ignorewarnings
-keep class * {
    public private *;
}

