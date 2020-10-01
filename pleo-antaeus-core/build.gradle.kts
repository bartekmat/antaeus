plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-data"))
    api(project(":pleo-antaeus-models"))
    // https://mvnrepository.com/artifact/org.quartz-scheduler/quartz
    compile  ("org.quartz-scheduler:quartz:2.3.2")
    compile ("io.github.microutils:kotlin-logging:1.12.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.google.code.gson:gson:2.8.6")
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    compile ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")



}