# Knowledge_Gradle

这个项目介绍了一些Gradle的常用配置

## Gradle的编译周期

在解析 Gradle 的编译过程之前我们需要理解在 Gradle 中非常重要的两个对象：**project**和**task**。  
* 每个项目的编译都至少有一个project，一个build.gradle就代表一个project；
* 每个project又包含多个task；
* 每个task又包含多个action；
* 每个action是一个代码块，里面包含需要被执行的代码。  

在编译过程中，Gradle 会根据 build 相关文件，聚合所有 project 和 task，执行 task 中的 action。  
因为 build.gradle 文件中的 task 非常多，先执行哪个后执行哪个需要一种逻辑来保证，这逻辑就是**依赖逻辑**。  
几乎所有 task 都要依赖其他 task 来执行，没有被依赖的 task 会首先被执行。  
所以到最后所有的 task 会构成一个**有向无环图**（DAG Directed Acyclic Graph）的数据结构。  

编译过程分为3个阶段：
1. **初始化阶段**：创建 project 对象，如果又多个 build.gradle，则会创建多个 project；
2. **配置阶段**：在这个阶段，会执行所有的编译脚本，同时还会创建 project 所有的 task，为最后一个阶段做准备；
3. **执行阶段**：在这个阶段，gradle 会根据传入的参数决定如何执行这些 task，真正 action 的执行代码就在这里。

## Gradle Files

下图是一个gradle项目最基础的文件配置：  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/gradle_all.png)  

* **setting.gradle**：  
这个 setting 文件定义了哪些module 应该被加入到编译过程，对于单个module 的项目可以不用需要这个文件，但是对于 multimodule 的项目我们就需要这个文件，否则gradle 不知道要加载哪些项目。这个文件的代码在初始化阶段就会被执行。  

* **顶层的build.gradle**：  
顶层的build.gradle文件的配置最终会被应用到所有项目中。它典型的配置如下：
```
//buildscript：定义了 Android 编译工具的类路径。
buildscript {
    
    repositories {
        google()
        //jcenter()是一个著名的 Maven 仓库。
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
    }
}

//allprojects：中定义的属性会被应用到所有 module 中，但是为了保证每个项目的独立性，我们一般不会在这里面操作太多共有的东西。
allprojects {
    repositories {
        google()
        jcenter()
    }
}
```

* **每个项目单独的 build.gradle**：  
针对每个module 的配置，如果这里的定义的选项和**顶层build.gradle**定义的相同，后者会被覆盖。典型的配置内容如下：  
```
//apply plugin：第一行代码应用了 Android 程序的 gradle 插件，作为 Android 的应用程序，这一步是必须的，因为 plugin 中提供了 Android 编译、测试、打包等等的所有task。
apply plugin: 'com.android.application'

//android：这是编译文件中最大的代码块，关于 android 的所有特殊配置都在这里，这就是由我们前面声明的plugin提供的。
android {
    compileSdkVersion 26

    /**
     * defaultConfig：就是程序的默认配置，注意，如果在AndroidMainfest.xml里面定义了与这里相同的属性，会以这里的为主。
     */
    defaultConfig {
        //applicationId：程序的唯一标识。（曾经定义的 AndroidMainfest.xml 中，那里定义的包名有两个用途：1.作为程序的唯一标识；2.作为我们R资源类的包名）
        applicationId "com.example.zhaodanyang.knowledge_gradle"
        minSdkVersion 15
        targetSdkVersion 26
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }

    /**
     * buildTypes：定义了编译类型，针对每个类型我们可以有不同的编译配置，不同的编译配置对应的有不同的编译命令（如：debug、release的类型）。
     */
    buildTypes {
        release {
            signingConfig signingConfigs.releaseConfig
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            buildConfigField("String", "API_URL", "\"www.google.com\"")
            buildConfigField("boolean", "LOG_SHOW", "false")
            resValue("string", "test", "test")
        }
    }
}

/**
 * dependencies:是属于 gradle 的依赖配置。它定义了当前项目需要依赖的其他库。
 * 我们在使用引入库的时候，每个库名包含三个元素：组名:库名称:版本号。
 */
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:26.1.0'
    implementation 'com.android.support.constraint:constraint-layout:1.0.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.1'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.1'
}
```
## Gradle Wrapper  

下图是一个 Android 项目所对应的 gradle wrapper 目录位置：  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/gradle_wrapper.png)  
Gradle 不断的在发展，新的版本难免会对以往的项目有一些向后兼容性的问题，这个时候，gradle wrapper 就应运而生了。  
gradlw wrapper 包含一些脚本文件和针对不同系统下面的运行文件。wrapper 有版本区分，但是并不需要你手动去下载，当你运行脚本的时候，如果本地没有会自动下载对应版本文件。  
如果我们更新了 gradle 的版本，并且让 Android Studio 自己下载更新 gradle 太过费时，可以根据以下步骤手动更新：  
1. 我们可以先从 gradle 官网上下载对应的版本；
2. 在你电脑的根目录，使用命令：open .gradle
3. 然后会自动弹出一个finder：  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/gradle_disk.png)  
4. 如果你之前 Android Studio 因为下载gradle的原因卡了很久都没有进来的话，对应的 gradle 版本目录会生成一串很长字母的文件夹，如下图所示：  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/gradle_disk_update.png)
5. 删除这个目录下面所有文件，将你下载的  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/gradle_zip.png)  
文件放到该文件夹下，不需要解压，然后重启 Android Studio 就行了。

## Gradle 的配置












## 参考资料：
[Gradle 完整指南（Android）](https://www.jianshu.com/p/9df3c3b6067a)
