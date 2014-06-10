generic-gradle-model
====================

A generic General purpose Gradle model that exposes more of your project info than what you get from the shipped tooling api.


### Usage

#### Add it to your tooling api project
```groovy
repositories {
  jcenter()
  maven {
    url "http://dl.bintray.com/rundis/maven"
  }
}

dependencies {
  compile 'no.rundis.gradle:generic-gradle-model:0.0.2'
}

```

#### Apply plugin to candidate project
```groovy

buildScript {
  repositories {
    jcenter()
    maven {
      url "http://dl.bintray.com/rundis/maven"
    }
  }
  dependencies {
    classpath 'no.rundis.gradle:generic-gradle-model:0.0.2'
  }
}


apply plugin: 'generic-gradle-model'
```

#### OR apply plugin through init script
```groovy
initscript {
  repositories {
    maven {
      url 'http://dl.bintray.com/rundis/maven'
    }
  }
  dependencies { classpath "no.rundis.gradle:generic-gradle-model:0.0.1" }
}

allprojects {
    apply plugin: org.gradle.tooling.model.generic.GenericGradleModelPlugin
}

```










### Dependencies
The api supports retrieving dependencies for both a root project and (if any) sub projects.


For a subproject dependencies the following example illustrates the datatructure

```groovy
[
    [name:sub01, group:003, version:unspecified, configurations:[
        compile:
            [nodes:[
                [name:groovy-all, group:org.codehaus.groovy, version:2.2.2, type:dependency],
                [name:groovy-stream, group:com.bloidonia, version:0.8.1, type:dependency]],
             edges:[
                [a:compile, b:groovy-all:org.codehaus.groovy:2.2.2],
                [a:compile, b:groovy-stream:com.bloidonia:0.8.1],
                [a:groovy-stream:com.bloidonia:0.8.1, b:groovy-all:org.codehaus.groovy:2.2.2]]],
        runtime:
            [nodes:[
                [name:groovy-all, group:org.codehaus.groovy, version:2.2.2, type:dependency],
                [name:groovy-stream, group:com.bloidonia, version:0.8.1, type:dependency]],
            edges:[
                [a:runtime, b:groovy-all:org.codehaus.groovy:2.2.2],
                [a:runtime, b:groovy-stream:com.bloidonia:0.8.1],
                [a:groovy-stream:com.bloidonia:0.8.1, b:groovy-all:org.codehaus.groovy:2.2.2]]]],
     [name:sub02, group:003, version:unspecified, configurations:[
        compile:[nodes:[
            [name:sub01, group:003, version:unspecified, type:project],
            [name:groovy-all, group:org.codehaus.groovy, version:2.2.2, type:dependency],
            ...
```

* Shows a list of sub projects
* For each sub project
    * Contains a map of all defined configurations for the given sub project
    * For each configurations
        * A list of dependency nodes (with std maven coordinates + type to indicate interproject dependency or third party)
        * A list of edge definitions (i.e arrows between dependencies)


The root project dependencies follows the same structure as each sub project outlined above



### Runtime classpath
Returns an ordered list of files/directories representing the runtime classpath for all the defined sourceSets for your project.

_It only works at top level, so no multiproject support here unfortunately._
