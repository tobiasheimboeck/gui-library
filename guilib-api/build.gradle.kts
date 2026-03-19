import java.util.Properties
import java.io.FileInputStream

plugins {
    java
    `maven-publish`
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()

if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
    localProperties.forEach { key, value ->
        project.extensions.extraProperties.set(key.toString(), value)
    }
}

dependencies {

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

task("sourcesJar", type = Jar::class) {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tobiasheimboeck/gui-library")
            credentials {
                username = localProperties.getProperty("gpr.user")
                password = localProperties.getProperty("gpr.key")
            }
        }
    }
    
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}