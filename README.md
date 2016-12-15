### Quick Start

Put your developer key in ./Example/app/src/main/java/com/navisens/example/MainActivity.java
```
private static final String DEVELOPER_KEY = "your developer key";
```
Sync project with gradle and run it

### Documentation

http://docs.navisens.com

### aar download

https://oss.sonatype.org/content/groups/public/com/navisens/motiondnaapi/

### Usage
1.Configure the project build.gradle to add two maven URLs
```
allprojects {
    repositories {
        ...
        maven {
            url 'https://oss.sonatype.org/content/groups/public'
        }
        maven { 
            url 'https://maven.fabric.io/public'
        }
    }
}
```
2.Configure the module build.gradle to add two dependencies 
```
dependencies {
    compile 'com.navisens:motiondnaapi:0.1-SNAPSHOT'
    compile 'org.altbeacon:android-beacon-library:2.+'
    ...
}
```
