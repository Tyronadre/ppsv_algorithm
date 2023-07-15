plugins {
    `java-library`
    id("org.springframework.boot") version "3.1.1"
}

apply(plugin = "io.spring.dependency-management")

group = "de.henrik"
version = "0.1.13"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.graphstream:gs-ui-swing:2.0")
    implementation("org.graphstream:gs-core:2.0")
    implementation("org.graphstream:gs-ui:1.3")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "de.henrik.Main"
    }
    from(sourceSets.main.get().output)
}
