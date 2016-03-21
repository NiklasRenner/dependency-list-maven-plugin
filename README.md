# dependency-list-maven-plugin
Maven plugin written in Groovy, for creating a .html file containing a list of project dependencies

##### why
* to try out Groovy
* to learn developing Maven plugins
* to get a formatted list of dependencies for a project

##### example use
```
<plugin>
  <groupId>dk.renner</groupId>
  <artifactId>dependency-list-maven-plugin</artifactId>
  <version>1.0.0</version>
  <executions>
      <execution>
          <id>generate-dependency-list</id>
          <goals>
              <goal>dependency-list</goal>
          </goals>
          <configuration>
          <outputFileName>file-name</outputFileName>
              <scopes>
                  <scope>compile</scope>
                  <scope>runtime</scope>
              </scopes>
              <groupIdExcludes>
                  <groupId>com.example1</groupId>
                  <groupId>com.example2</groupId>
              </groupIdExcludes>
              <artifactIdExcludes>
                  <artifactId>artifact-name1</artifactId>
                  <artifactId>artifact-name1</artifactId>
              </artifactIdExcludes>
          </configuration>
      </execution>
  </executions>
</plugin>
```
##### configuration properties

| key | value | description |
| ------------- | ------------- | ----- |
| groupIdExcludes | a list of groupId's, or parts of them, to exclude | see groupId |
| groupId | a groupId, or part of one, that should be excluded | excludes any groupId containing the value. doesn't exclude transative dependencies of matches. |
| artifactIdExcludes | a list of artifactId's, or parts of them, to exclude | see artifactId |
| artifactId | a artifactId, or part of one, that should be excluded | excludes any artifactId containing the value. doesn't exclude transative dependencies of matches. |
| scopes | a list of scopes to exclude | see scope. default: include all scopes |
| scope | 'compile', 'provided', 'runtime' or 'test' | scope determines which scope dependencies need to have to make the list |
| outputFileName | any valid filename | value 'file-name' results in file named 'file-name.html'. default value: 'dependencies' |

##### output
output of plugin is placed in '/target/dependencies.html' of the Maven project

##### jdk 1.6 & under
plugin will output error in maven log, concerning missing groovy dependency.

```
21-03-2016 00:35:17 org.codehaus.groovy.runtime.m12n.MetaInfExtensionModule newModule
WARNING: Module [groovy-all] - Unable to load extension class [org.codehaus.groovy.runtime.NioGroovyMethods]
```

this is not a fatal exception as the dependency is not used, and plugin should finish succesfully.

##### prerequisites to run
* Maven 3.x
* if plugin is run on a submodule, the parent project has to have been built previously

##### build
* clone repo with `git clone https://github.com/NiklasRenner/dependency-list-maven-plugin.git`
* navigate to folder & run `mvn clean install`
