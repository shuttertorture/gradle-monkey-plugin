# gradle-monkey-plugin [![Build Status](https://travis-ci.org/AutoScout24/gradle-monkey-plugin.png)](https://travis-ci.org/AutoScout24/gradle-monkey-plugin) [![Maven Central](https://img.shields.io/maven-central/v/com.autoscout24.gradle/gradle-monkey-plugin.svg)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.autoscout24.gradle%22%20AND%20a%3A%22gradle-monkey-plugin%22) [![License MIT](http://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/AutoScout24/gradle-monkey-plugin/blob/master/LICENSE)
A Gradle plugin for running Android monkey tests.

## Basic usage

Add to your build.gradle

```gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
		    classpath group: 'com.autoscout24.gradle', name: 'gradle-monkey-plugin', version: '2.1.1'
    }
}

apply plugin: 'com.autoscout24.gradle.monkey'
```

## Advanced usage

Add to your build.gradle

```gradle
monkey {
    teamCityLog = true
    eventCount = 1000
    seed = 246
    delay = 100
    failOnFailure = false
    install = true
}
```

* `teamCityLog`: Add features for [TeamCity](http://www.jetbrains.com/teamcity/)
* `eventCount`: Number of monkey events
* `seed`: Seed value for pseudo-random number generator
* `delay`: Delay between events in milliseconds
* `failOnFailure`: Deactivate exit code on failure
* `install`: Reinstalls the APK first

## License

gradle-monkey-plugin is available under the MIT license. See the LICENSE file for more info.
