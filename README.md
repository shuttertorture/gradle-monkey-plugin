# gradle-monkey-plugin [![Build Status](https://travis-ci.org/AutoScout24/gradle-monkey-plugin.png)](https://travis-ci.org/AutoScout24/gradle-monkey-plugin)
A Gradle plugin for running Android monkey tests.

## Basic usage

Add to your build.gradle

```gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
		    classpath group: 'com.autoscout24.gradle', name: 'gradle-monkey-plugin', version: '0.1+'
    }
}

apply plugin: 'monkey'
```

## Advanced usage

Add to your build.gradle

```gradle
monkey {
    teamCityLog = true
    eventCount = 1000
    failOnFailure = false
    install = true
}
```

* `teamCityLog`: Add features for [TeamCity](http://www.jetbrains.com/teamcity/)
* `eventCount`: Number of monkey events
* `failOnFailure`: Deactivate exit code on failure
* `install`: Reinstalls the APK first

## License

gradle-monkey-plugin is available under the MIT license. See the LICENSE file for more info.
