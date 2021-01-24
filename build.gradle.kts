plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.0-RC" // mirai-console version
}

//application {
//    mainClassName="BotMain"
//}

mirai {
    coreVersion = "2.0-RC" // mirai-core version
}

group = "top.colter"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
//    implementation("net.mamoe:mirai-console-terminal:2.0-RC") // 自行替换版本
//    implementation("net.mamoe:mirai-core:2.0-RC")
//    implementation("net.mamoe:mirai-console:2.0-RC")
//    runtimeOnly("net.mamoe:mirai-login-solver-selenium:1.0-dev-15")
    implementation("com.alibaba:fastjson:1.2.74")
}

//val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
