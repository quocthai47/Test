plugins {
    id("java")
    id ("application")
}

application {
    mainClass.set("com.td.server.ServerDispatcher")
}

group = "com.td"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:2.19.0")
    testImplementation("junit:junit:4.13.1")

}

tasks.test {
    useJUnitPlatform()
}

