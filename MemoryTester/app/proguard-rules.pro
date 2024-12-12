# Manter todas as classes de um pacote e seus subpacotes
-keep class br.inf.gr.memorytester.** { *; }
# Google Ads
-keep public class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keepclassmembers class * {
    public *;
}
-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

-keep public class com.google.android.gms.ads.MobileAds {
    public static void initialize(android.content.Context, com.google.android.gms.ads.initialization.OnInitializationCompleteListener);
}
# Manter nomes de métodos e campos usados por anotações de framework
-keepnames class * {
    @com.example.framework.annotation.* <fields>;
    @com.example.framework.annotation.* <methods>;
    @androidx.multidex.* <methods>;
    @androidx.appcompat.* <methods>;
    @androidx.vectordrawable.* <methods>;
    @androidx.browser.* <methods>;
    @androidx.legacy.* <methods>;
    @com.google.android.material.* <methods>;
    @androidx.constraintlayout.* <methods>;
    @com.google.android.gms.* <methods>;
    @com.google.android.gms.* <methods>;
    @androidx.cardview.* <methods>;
    @com.squareup.okhttp3.* <methods>;
    @com.squareup.okhttp3.* <methods>;
    @com.squareup.retrofit2.* <methods>;
    @com.squareup.retrofit2.* <methods>;
    @androidx.recyclerview.* <methods>;
    @androidx.recyclerview.* <methods>;
    @com.google.code.gson.* <methods>;
    @com.google.maps.android.* <methods>;
}



-keep class androidx.appcompat.** { *; }
-keep interface androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep interface com.google.android.material.** { *; }
-keep class androidx.activity.** { *; }
-keep interface androidx.activity.** { *; }
-keep class androidx.multidex.** { *; }
-keep interface androidx.multidex.** { *; }
-keep class androidx.constraintlayout.** { *; }
-keep interface androidx.constraintlayout.** { *; }
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }


# Desativar otimizações específicas para bibliotecas de terceiros
-dontoptimize
-dontpreverify

