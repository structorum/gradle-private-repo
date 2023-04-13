## Gradle Private Repo Plugin

This project helps with connecting to private Maven repositories in Gradle builds.

It can read repository settings from *.properties* files or from Java *system properties*,
meaning users don't have to put sensitive or team-specific repository credentials in their *.gradle(.kts)* scripts or in version control.


### Usage

Usage is simple.
This plugin provides a number of DSL methods which act inside a Gradle `RepositoryHandler`.
In other words, inside gradle `repositories` blocks.
All public methods are named `mavenRepo` and provide variations on accepted arguments.

Include the plugin:
```kotlin
plugins {
    id("io.github.structorum.gradle.private-repo") version "1.0"
}
```

For example, this configuration assumes the default file,
`local.properties` (in the project root),
and defines an unnamed maven repository:
```kotlin
repositories {
    mavenRepo()
    mavenCentral()
}
```

The repository can be named,
e.g. `mavenRepo("github")`,
or the *.properties* file path can be specified (as a `java.io.File`),
e.g. `mavenRepo(File("maven.properties"))`,
or both options may be specified.

The unnamed repo configuration properties are:
- `maven.repo.url`
- `maven.repo.username`
- `maven.repo.password`

Named repo properties include the repo name,
e.g. for `mavenRepo("github")`:
- `maven.repo.github.url`
- `maven.repo.github.username`
- `maven.repo.github.password`

System properties override file properties,
which may be useful for CD systems or for users who prefer to specify passwords on the command line.
Username and password are not required to fetch dependencies from public repos.
It's also possible to specify all via *system properties*,
in which case no *.properties* file need exist.

Publishing works the same way,
just put the `mavenRepo()` call inside the appropriate block,
like so:
```kotlin
publishing {
    repositories {
        mavenRepo("internal")
    }
}
```

*Copyright 2023 Structorum.*
*Licensed under the [Apache License, version 2.0](LICENSE.txt).*
