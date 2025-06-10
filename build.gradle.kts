plugins {
    kotlin("jvm") version "2.1.20"
}

group = "me.snipz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.minebench.de/")
}

dependencies {
    compileOnly("org.purpurmc.purpur", "purpur-api", "1.21.4-R0.1-SNAPSHOT")
    compileOnly("de.themoep:minedown-adventure:1.7.4-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:6.3.0")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}