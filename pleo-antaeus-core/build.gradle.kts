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
}