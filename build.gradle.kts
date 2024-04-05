import sun.tools.jar.resources.jar

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")
    repositories {
        mavenCentral()
    }
}
project(":server") {
    apply(plugin = "application")
    mainClass.set("server")
    dependencies {
        implementation(project(":common"))
        implementation("com.google.code.gson:gson:2.10.1")
    }
    jar {
        manifest {
            attributes 'Main-Class' : 'server.App'
        }
    }
}
project(":client") {
    apply(plugin = "application")
    mainClass.set("client.App")
    dependencies {
        implementation(project(":common"))
    }
    run {
        standardInput = System.in
    }
    jar {
        manifest {
            attributes 'Main-Class': 'client.App'
        }
    }
}
group = "ru.itmo.se"
version = "2.0"
description = "Lab6"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    mavenCentral()
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven"){
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>{
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}