apply from: "${rootDir.path}/gradle/config.gradle"
apply from: "${rootDir.path}/gradle/version.gradle"

buildscript {
    ext.kotlin_version = "1.4.21"
    ext.kotlin_coroutines_version = "1.3.2"
    repositories {
        maven { url "https://maven.aliyun.com/repository/google" }
        maven { url "https://maven.aliyun.com/repository/jcenter" }
        google()
        jcenter()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.1.1"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath 'com.tencent.bugly:symtabfileuploader:2.2.1'
    }
}

allprojects {
    repositories {
        maven { url "https://maven.aliyun.com/repository/google" }
        maven { url "https://maven.aliyun.com/repository/jcenter" }
        maven { url "https://jitpack.io" }
        //maven { url "https://raw.githubusercontent.com/goweii/maven-repository/master/releases/" }
        maven { url "https://gitee.com/goweii/maven-repository/raw/master/releases/" }
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}