import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.8.10"
val serializationVersion = "1.3.3"
val ktorVersion = "2.2.1"
val kotlin_css_version = "1.0.0-pre.473"
val logbackVersion = "1.2.11"
val kotlinWrappersVersion = "1.0.0-pre.354"
val kmongoVersion = "4.5.0"
val gebVersion = "7.0"


plugins {
    kotlin("multiplatform") version "1.8.10"
    application //to run JVM part
    kotlin("plugin.serialization") version "1.8.10"
//    groovy
}

group = "de.pacheco.soeren"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
    }

    js(IR) {
        browser {
            binaries.executable()
        }
    }

    sourceSets {
        @Suppress("UNUSED_VARIABLE") val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
            }
        }

        @Suppress("UNUSED_VARIABLE") val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        @Suppress("UNUSED_VARIABLE") val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-cors:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
//                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-server-tomcat:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
//                implementation("org.litote.kmongo:kmongo-coroutine-serialization:$kmongoVersion")

                implementation("org.jsoup:jsoup:1.15.3")
//                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
//                implementation("io.ktor:ktor-server-html-builder-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-css:$kotlin_css_version")
//                compile "org.gebish:geb-core:7.0"
//                compile "org.seleniumhq.selenium:selenium-firefox-driver:4.2.2"
//                compile "org.seleniumhq.selenium:selenium-support:4.2.2"
//                implementation("io.reactivex.rxjava2:rxkotlin:3.0.1")
            }
        }

        @Suppress("UNUSED_VARIABLE") val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:$kotlinWrappersVersion"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")

            }
        }
    }
}

application {
    mainClass.set("appLogic/ServerKt")
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}
