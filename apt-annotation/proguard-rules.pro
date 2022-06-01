-keep @com.ghp.annotation.OptimusAnnotation class * {*;}
-keep class * {
    @com.ghp.annotation.OptimusAnnotation <fields>;
}
-keepclassmembers class * {
@com.ghp.annotation.OptimusAnnotation <methods>;
}