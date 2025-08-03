import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.7.20"
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("net.dv8tion:JDA:4.4.0_352")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}