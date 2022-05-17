plugins {
    java
    `java-library`
}

group = "cz.tefek"
version = "5.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "7.4.2"
}

sourceSets {
    java {
        val mainSet = sourceSets.main.get()

        create("commands") {
            java.srcDirs(
                "assets/commands/administrative/java",
                "assets/commands/general/java",
                "assets/commands/superuser/java")

            compileClasspath += mainSet.compileClasspath + mainSet.output
        }

        val data = create("data") {
            java.srcDirs("assets/data/java")

            compileClasspath += mainSet.compileClasspath
        }

        create("util") {
            java.srcDirs("assets/util/java")

            compileClasspath += data.compileClasspath + data.output
            runtimeClasspath += data.compileClasspath + data.output
        }

        main {
            compileClasspath += data.output
            runtimeClasspath += data.output
        }
    }
}

repositories {
    mavenCentral()

    maven {
        name = "BotdirilVega"
        url = uri("https://vega.botdiril.com")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

buildscript {
    repositories {
        maven {
            name = "BotdirilVega"
            url = uri("https://vega.botdiril.com")
        }
    }

    dependencies {
        classpath("com.botdiril:botdiril-schema-autocompile-plugin:0.1.1")
    }
}


tasks.withType<com.botdiril.framework.util.BuildSchemasTask> {
    addSourceSet(sourceSets["data"])
}

apply(plugin = "com.botdiril.botdiril-schema-autocompile-plugin")

dependencies {
    api("com.botdiril", "botdiril-sql-framework", "0.3.0")

    testImplementation(files("$buildDir/generated/botdiril-sql"))

    api("org.plutoengine", "plutolib", "22.3.0.0-alpha.0")
    api("org.plutoengine", "plutocomponent", "22.3.0.0-alpha.0")

    implementation("org.yaml", "snakeyaml", "1.28")

    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.12.3")
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", "2.12.3")

    implementation("org.slf4j", "slf4j-api", "1.8.0-beta4")
    implementation("org.slf4j", "slf4j-simple", "1.8.0-beta4")
    implementation("org.apache.logging.log4j", "log4j-core", "2.16.0")
    implementation("org.apache.logging.log4j", "log4j-api", "2.16.0")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.16.0")

    implementation("org.apache.commons", "commons-lang3", "3.11")
    implementation("org.apache.commons", "commons-text", "1.9")
    implementation("commons-io", "commons-io", "2.8.0")

    api("net.dv8tion", "JDA", "5.0.0-alpha.11") {
        exclude(module = "opus-java")
    }
}
