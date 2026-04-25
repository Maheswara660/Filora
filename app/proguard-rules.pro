# Optimization flags
-dontoptimize
-repackageclasses ''
-allowaccessmodification
-mergeinterfacesaggressively

# Attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable,Signature,Annotation,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Project Specific
-keep class com.maheswara660.filora.** { *; }
-keep interface com.maheswara660.filora.** { *; }
-keep enum com.maheswara660.filora.** { *; }

# Kotlin & Coroutines
-dontwarn kotlin.**
-keep class kotlin.reflect.jvm.internal.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherLoader {}
-keepnames class kotlinx.coroutines.android.HandlerContext {}
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }
-keep class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keep class kotlinx.coroutines.internal.** { *; }
-dontwarn kotlinx.coroutines.internal.**

# kotlinx-serialization
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembernames class kotlinx.serialization.json.** {
    *** serializer(...);
}
-keepclassmembernames class * {
    *** companion(...);
}
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class * {
    *** $serializer;
}
-dontwarn kotlinx.serialization.**

# Jetpack Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
    @androidx.compose.runtime.ReadOnlyComposable *;
}
-dontwarn androidx.compose.**
-keep class androidx.compose.animation.core.AnimationVector** { *; }
-keep class androidx.compose.animation.core.TwoWayConverter** { *; }

# AndroidX Core & Libraries
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
-keep class androidx.datastore.** { *; }
-keep class androidx.palette.** { *; }
-keep class androidx.documentfile.provider.DocumentFile { *; }
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**
-keep class androidx.compose.material3.** { *; }
-dontwarn androidx.compose.material3.**

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**
-keep class androidx.media.** { *; }
-dontwarn androidx.media.**
-keep class android.support.v4.media.** { *; }
-dontwarn android.support.v4.media.**
-keep class * extends androidx.media3.session.MediaSessionService { *; }
-keep class androidx.media3.session.MediaStyleNotificationHelper { *; }

# Guava / ListenableFuture
-keep class com.google.common.util.concurrent.ListenableFuture { *; }
-keep class com.google.common.util.concurrent.Futures { *; }
-dontwarn com.google.common.**

# Coil (Image Loading)
-keep class coil3.** { *; }
-keep class io.coil_kt.coil3.** { *; }
-dontwarn coil3.**
-dontwarn io.coil_kt.coil3.**

# Okio & OkHttp
-keep class okio.** { *; }
-dontwarn okio.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Zip4j
-keep class net.lingala.zip4j.** { *; }

# Sora Editor
-keep class io.github.rosemoe.sora.** { *; }
-keep class org.eclipse.tm4e.** { *; }
-keep class org.joni.** { *; }

# Storage (Anggrayudi)
-keep class com.anggrayudi.storage.** { *; }
-dontwarn com.anggrayudi.storage.**

# APKEditor & apksig
-keep class com.reandroid.** { *; }
-dontwarn com.reandroid.**
-dontwarn java.lang.reflect.AnnotatedType
-keep class com.maheswara660.apkeditor.** { *; }
-keep class com.android.apksig.** { *; }
-dontwarn com.android.apksig.**

# Third-Party UI Libraries
-keep class com.google.accompanist.** { *; }
-keep class sh.calvin.reorderable.** { *; }
-keep class com.cheonjaeung.compose.grid.** { *; }
-keep class com.github.nanihadesuka.lazycolumnscrollbar.** { *; }
-keep class me.saket.cascade.** { *; }
-keep class io.github.kevinnzou.** { *; }
-keep class me.saket.telephoto.** { *; }
-keep class net.engawapg.lib.zoomable.** { *; }
-dontwarn net.engawapg.lib.zoomable.**
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Networking (Commons Net)
-keep class org.apache.commons.net.** { *; }

# General Android components
-keep class android.widget.RemoteViews { *; }
-keep class android.app.PendingIntent { *; }
-keep class android.app.Notification { *; }
-keep class android.app.NotificationManager { *; }

# Suppressions
-dontwarn com.google.j2objc.annotations.**
-dontwarn sun.misc.Unsafe
-dontwarn sun.reflect.**
-dontwarn java.lang.ClassValue
-dontwarn java.lang.ref.Cleaner
-dontwarn java.lang.invoke.*
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep names for interfaces
-keepnames interface * { *; }