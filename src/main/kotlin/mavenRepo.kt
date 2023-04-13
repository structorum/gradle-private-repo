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

package io.github.structorum.gradle

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.io.File
import java.net.URI
import java.util.*

private object MavenProperty {
    fun url(name: String?): String =
        if (name != null) "maven.repo.$name.url" else "maven.repo.url"

    fun username(name: String?): String =
        if (name != null) "maven.repo.$name.username" else "maven.repo.username"

    fun password(name: String?): String =
        if (name != null) "maven.repo.$name.password" else "maven.repo.password"
}

fun RepositoryHandler.mavenRepo(): MavenArtifactRepository {
    return MavenRepoConfig.load(null, null).apply(this)
}

fun RepositoryHandler.mavenRepo(
    action: Action<in MavenArtifactRepository>
): MavenArtifactRepository {
    return mavenRepo().apply { action.execute(this) }
}

fun RepositoryHandler.mavenRepo(propertyFile: File): MavenArtifactRepository {
    return MavenRepoConfig.load(null, propertyFile).apply(this)
}

fun RepositoryHandler.mavenRepo(
    propertyFile: File,
    action: Action<in MavenArtifactRepository>
): MavenArtifactRepository {
    return mavenRepo(propertyFile).apply { action.execute(this) }
}

fun RepositoryHandler.mavenRepo(name: String): MavenArtifactRepository {
    return MavenRepoConfig.load(name, null).apply(this)
}

fun RepositoryHandler.mavenRepo(
    name: String,
    action: Action<in MavenArtifactRepository>
): MavenArtifactRepository {
    return mavenRepo(name).apply { action.execute(this) }
}

fun RepositoryHandler.mavenRepo(name: String, propertyFile: File): MavenArtifactRepository {
    return MavenRepoConfig.load(name, propertyFile).apply(this)
}

fun RepositoryHandler.mavenRepo(
    name: String,
    propertyFile: File,
    action: Action<in MavenArtifactRepository>
): MavenArtifactRepository {
    return mavenRepo(name, propertyFile).apply { action.execute(this) }
}

private data class MavenRepoConfig(
    val name: String?,
    val url: String?,
    val username: String?,
    val password: String?
) {
    companion object {
        fun load(name: String?, propertyFile: File?): MavenRepoConfig {
            return loadMavenRepoConfig(name, propertyFile)
        }
    }

    fun apply(handler: RepositoryHandler): MavenArtifactRepository {
        return handler.applyMavenRepoConfig(this)
    }
}

private fun RepositoryHandler.applyMavenRepoConfig(
    config: MavenRepoConfig
): MavenArtifactRepository = Setup.project.run {
    maven {
        if (config.name != null) {
            name = config.name
        }
        if (config.url == null) {
            logger.error("Maven URL is required")
        } else {
            url = URI.create(config.url)
        }
        credentials {
            if (config.username == null || config.password == null) {
                if (config.username != null || config.password != null) {
                    logger.error("If either Maven username or password is set, both must be set")
                }
                logger.info("Using unauthenticated Maven configuration")
            }
            if (config.username != null) {
                username = config.username
            }
            if (config.password != null) {
                password = config.password
            }
        }
    }
}

private fun loadMavenRepoConfig(
    name: String?,
    propertyFile: File?
): MavenRepoConfig = Setup.project.run {
    var url: String? = null
    var username: String? = null
    var password: String? = null

    val file = when (propertyFile) {
        null -> Setup.defaultPropertyFile
        else -> rootProject.file(propertyFile)
    }

    val urlProperty = MavenProperty.url(name)
    val usernameProperty = MavenProperty.username(name)
    val passwordProperty = MavenProperty.password(name)

    if (file.exists()) {
        logger.info("Configuring Maven repo from property file: {}", file)
        val localProperties = Properties().apply {
            load(file.inputStream())
        }

        fun getProperty(name: String): String? {
            val prop = localProperties[name]
            if (prop == null) {
                logger.warn("Maven repo property unset in property file: {}", name)
                return null
            }
            return prop.toString()
        }

        url = getProperty(urlProperty)
        username = getProperty(usernameProperty)
        password = getProperty(passwordProperty)
    } else {
        logger.warn("Maven property file not found: {}", file)
    }

    fun getSystemPropertyOrDefault(name: String, default: String?): String? {
        val prop = System.getProperty(name)
        if (prop != null) {
            logger.warn("Overriding Maven property from file with system property: {}", name)
            return prop
        }
        return default
    }
    url = getSystemPropertyOrDefault(urlProperty, url)
    username = getSystemPropertyOrDefault(usernameProperty, username)
    password = getSystemPropertyOrDefault(passwordProperty, password)

    MavenRepoConfig(name, url, username, password)
}
