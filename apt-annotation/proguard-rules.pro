-keep @com.ghp.annotation.OptimusService class * {*;}
-keep class * {
    @com.ghp.annotation.OptimusService <fields>;
}
-keepclassmembers class * {
@com.ghp.annotation.OptimusService <methods>;
}