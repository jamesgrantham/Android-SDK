### Quick Start

Put your developer key in ./Example/app/src/main/java/com/navisens/example/MotionDnaClient.java
```
private static final String DEVELOPER_KEY = "your developer key";
```
For version 0.1-beta, put your developer key in ./Example/app/src/main/java/com/navisens/example/MainActivity.java
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
    compile group: "com.navisens", name: "motiondnaapi", version: "0.2-SNAPSHOT", changing: true
    compile 'org.altbeacon:android-beacon-library:2.+'
    ...
}
```

for version 0.1-beta
```

dependencies {
    compile group: "com.navisens", name: "motiondnaapi", version: "0.1-SNAPSHOT", changing: true
    compile 'org.altbeacon:android-beacon-library:2.+'
    ...
}
```

### Change Log

####January 20, 2017
Changed:<br />
1.The MotionDnaApplication class' constructor has been changed to 
```
public MotionDnaApplication(MotionDnaInterface motionDna)
```
2.Added two methods in MotionDnaInterface
```
    public Context getAppContext();
    public PackageManager getPkgManager();
```
implementing the interface's two methods
```
@Override
    public Context getAppContext() {
        return getApplicationContext();
}

@Override
    public PackageManager getPkgManager() {
        return getPackageManager();
}
```






