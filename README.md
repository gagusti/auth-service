## Gradle Wrapper Configuration
We introduce gradle wrapper for build the components, the gradle version we are
targeting with this project is 7.4, then you must execute the following code 
to download the zip

`gradle wrapper --gradle-version 7.4 --distribution-type all`

## Lombok Library and Dependencies
For Lombok library we introduce these dependencies

```
compileOnly 'org.projectlombok:lombok:1.18.36'
annotationProcessor 'org.projectlombok:lombok:1.18.36'

testCompileOnly 'org.projectlombok:lombok:1.18.36'
testAnnotationProcessor 'org.projectlombok:lombok:1.18.36'```
