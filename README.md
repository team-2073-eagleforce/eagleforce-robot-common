# Team 2073 Common

## Usage Instructions

### Eclipse

1. Clone this repository and import it as a Gradle project. (`File > Import... > Gradle > Existing Gradle Project`)
2. In the `Gradle Tasks` view, expand and run `eagleforce-robot-common > publishing > publishToMavenLocal`.
3. In the robot project's `build.gradle` file, add these lines in `repositories` and `dependencies`:

```groovy
repositories {
    // ... other repositories ...
    mavenLocal()
}
```

```groovy
dependencies {
    // ... other dependencies ...
    compile group: 'com.team2073.common', name: 'common', version: '1.0'
}
```

4. Refresh the robot project as a Gradle project. (`Right-click > Gradle > Refresh Gradle Project`)
