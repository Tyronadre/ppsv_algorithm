plugins {
    id("java")
}

group = "de.henrik"
version = "1.0-SNAPSHOT"

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