# Knowledge_Gradle
这个配置了一些Gradle的一些常用配置

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



## 参考资料：
[Gradle 完整指南（Android）](https://www.jianshu.com/p/9df3c3b6067a)
