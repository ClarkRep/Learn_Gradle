# Knowledge_Gradle

这个项目介绍了一些Gradle的常用配置

## Gradle的编译周期

在解析 Gradle 的编译过程之前我们需要理解在 Gradle 中非常重要的两个对象：**project**和**task**。  
Gradle 中，每一个待编译的工程都叫一个 project 。每一个 project 在构建的时候都包含一系列的 task。比如一个 Android APK 的编译可能包含：Java源码编译 task、资源编译 task、JNI 编译 task、lint检查 task、打包生成 APK 的 task、签名 task 等。插件本身就是包含了若干 task 的。  
* 每个项目的编译都至少有一个 project，一个 build.gradle 就代表一个 project；
* 每个 project 又包含多个 task；
* 每个 task 又包含多个 action；
* 每个 action 是一个代码块，里面包含需要被执行的代码。  

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

### 1.BuildConfig

这个类相信大家都不会陌生，我们最常用的用法就是通过 BuildConfig.DEBUG 来判断当前的版本是否是 debug 版本，如果是就会输出一些只有在 debug 环境下才会执行的操作。  
这个类就是由gradle 根据 配置文件生成的。为什么gradle 可以直接生成一个Java 字节码类，这就得益于我们的 gradle 的编写语言是Groovy, Groovy 是一种 JVM 语言，JVM 语言的特征就是，虽然编写的语法不一样，但是他们最终都会编程 JVM 字节码文件。同是JVM 语言的还有 Scala,Kotlin 等等。  
以下是 buildConfigField()和resValue()方法的基本使用：
```
debug {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            //配置debug环境下API_URL所对应的String
            buildConfigField("String", "API_URL", "\"www.baidu.com\"")
            //配置debug环境下LOG_SHOW所对应的boolean
            buildConfigField("boolean", "LOG_SHOW", "true")
            //配置debug环境下test字符串资源文件（第二个参数不能在strings.xml中存在，否则编译会失败）
            resValue("string", "test", "debug")
        }
```

### 2.Repositories
Repositories 就是代码仓库，这个相信大家都知道，我们平时的添加的一些 dependency 就是从这里下载的。  
Gradle 支持三种类型的仓库：Maven,Ivy和一些静态文件或者文件夹。  
在编译的执行阶段，gradle 将会从仓库中取出对应需要的依赖文件，当然，gradle 本地也会有自己的缓存，不会每次都去取这些依赖。  
gradle 支持多种 Maven 仓库，一般我们就是用共有的jCenter就可以了。
* 有一些项目，可能是一些公司私有的仓库中的，这时候我们需要手动加入仓库连接：  
```
    repositories {  
        maven{
            url "http://www.github.com"
        }
    }
```  
* 如果仓库有密码，也可以同时传入用户名和密码：  
```
    repositories {
        maven{
            url "http://repo.acmecorp.com.maven2"
            credentials{
                username 'user'
                password 'password'
            }
        }
    }
```  
* 我们也可以使用相对路径配置本地仓库，我们可以通过配置项目中存在的静态文件夹作为本地仓库：  
```
    repositories {
        flatDir{
            dirs 'aars'
        }
    }
```  

### 3.Dependencies  
* 我们在引用网络库的时候，每个库名称包含三个元素：组名:库名称:版本号,如下：
```
dependencies {
    implementation 'com.android.support:appcompat-v7:26.1.0'
}
```

* 引用本地文件、jar包：  
```
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
}
```  

* 配置本地so库
```
android{
    sourceSets.main{
        jniLibs.srcDir 'src/main/jnilibs'
    }
}
```  

### 4.仓库项目 - Library Projects
如果我们要写一个library项目让其他的项目引用，我们的bubild.gradle的plugin 就不能是andrid plugin了，需要引用如下plugin：  
```
apply plugin: 'com.android.library'
```
引用的时候在setting文件中include即可。  

## 多样化编译 - Build Variants
在开发过程中经产会遇到这样的需求：  
* 我们需要在 debug 和 release 两种情况下配置不同的服务器地址；
* 当打市场渠道包的时候，我们可能需要打免费版、收费版，或者内部版、外部版的程序；
* 为了让市场版和 debug 版同时存在与一个手机，我们需要编译的时候自动给 debug 版本不一样的包名。

### 1.Build Type
通常开发程序都会有 debug 和 release 两种 Build Type，如果为了这两种 Build Type 在手机里可以共存，我们可以修改其中一个 Build Type 的ApplicationId 来实现，如下：  
```
buildTypes {
        debug {
            //给applicationId添加后缀：.debug。
            applicationIdSuffix ".debug"
            //给版本名称添加后缀：-debug。
            versionNameSuffix "-debug"
        }
    }
```

### 2.Product flavors  
前面我们都是针对同一份源码编译同一个程序的不同类型，如果我们要针对同一份源码编译不同的程序（包名也不同，比如免费版和收费版），就需要用到 productFlavors 了。  
productFlavors 和 buildTypes 是不一样的，而且他们的属性也不一样。  
所有的 productFlavors 版本和 defaultConfig 共享所有属性。  
productFlavors 字面意思就是：产品多样性，通常我们都会用它来进行多渠打包操作。  
看下面这个 demo 展示了productFlavors的组合使用方式：  

```
android{

    defaultConfig {
        versionNameSuffix "DefaultConfig"
    }

    //这里定义了两个维度：price、level。
    //这两个维度又分别定义了两种类型：price - free、paid；size - big、small。
    //这两种维度可以两两组合成你所想要的生成的打包对象。
    flavorDimensions("price", "size")
    productFlavors {
        free {
            dimension("price")
            versionNameSuffix "Free"
        }
        paid {
            dimension("price")
            versionNameSuffix "Paid"
        }
        big {
            dimension("size")
            versionNameSuffix "Big"
        }
        small {
            dimension("size")
            versionNameSuffix "Small"
        }
    }
    
    buildTypes {
        debug {
            versionNameSuffix "-debug"
        }
    }
}
```
配置完 build 一下，这时我们可以看到 Build Variant 下面生成了多种组合方式：  
![](https://raw.githubusercontent.com/zdy793410600/Knowledge_Gradle/master/img/productFlavors.png)
上面的代码中，我们在 defaultConfig、productFlavors、buildTypes 中都使用 versionNameSuffix 为 versionName 添加了后缀名，然后选中 paidBigDebug 这一 Build Variant ，安装到测试机上，通过log打印 **BuildConfig.VERSION_NAME**，可以看到打印结果是：  
```
1.0DefaultConfigPaidBig-debug
```
通过log打印可以看出，编译的顺序是： defaultConfig -> productFlavors -> buildTypes。  

### 3.Source Sets  
每当创建一个新的 buildType 的时候，gradle 默认都会创建一个新的 source set。我们可以建立与 main 文件夹同级的文件夹，根据编译类型的不同我们可以选择对某些源码直接进行替换。  
其实，对应 productFlavors ，我们也可以创建对应的 source set。  
要注意的是：创建的 source set 必须要和对应的 buildType 或 productFlavors 名称对应上。  
下图我们创建了对应 debug、release 这两种 buildType 以及对应 free、paid 这两种 productFlavors 的 source set：  




## 参考资料：
[Gradle 完整指南（Android）](https://www.jianshu.com/p/9df3c3b6067a)
