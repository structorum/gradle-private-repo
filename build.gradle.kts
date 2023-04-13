//import io.github.structorum.gradle.mavenRepo

/*
 * Copyright 2023 Structorum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `kotlin-dsl`
    `maven-publish`
//    id("com.gradle.plugin-publish") version "1.2.0"
//    id("io.github.structorum.gradle.private-repo") version "1.0"
}

allprojects {
    group = "io.github.structorum.gradle"
    version = "1.1-SNAPSHOT"
}

publishing {
    repositories {
        mavenLocal()
//        mavenRepo()
    }
}

gradlePlugin {
    website.set("https://github.com/structorum/gradle-private-repo")
    vcsUrl.set("https://github.com/structorum/gradle-private-repo")
    plugins {
        create("private-repo") {
            id = "$group.$name"
            implementationClass = "$group.PrivateRepoPlugin"
            displayName = "Gradle Private Repo Plugin"
            description = "Define private Maven repositories in Gradle."
            tags.set(listOf("maven", "repo", "repository"))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
}
