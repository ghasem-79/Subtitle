import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    id("org.jetbrains.compose") version "0.3.0-build140"
}

group = "me.lenovo"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)

    // Theme finder
    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Unit test
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "com.jakode.subtitle.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Subtitle"
            description = "This is an app for co-naming subtitle"
            copyright = "© 2020 Jakode. All rights reserved."
            vendor = "Jakode2020"
            windows {
                iconFile.set(project.file("Subtitle.ico"))
            }
            linux {
                iconFile.set(project.file("Subtitle.png"))
            }
            macOS {
                iconFile.set(project.file("Subtitle.icns"))
            }
        }
    }
}