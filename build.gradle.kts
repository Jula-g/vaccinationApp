buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.30")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.dokka") version "1.4.30"
}
apply(plugin = "org.jetbrains.dokka")