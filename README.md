# maven-dependency-list-plugin
Maven plugin written in Groovy, for creating Maven a .html file containing a list of project dependencies

<br />
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
              <scope>compil</scope>
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

<br />
##### configuration properties

| key | value | description |
| ------------- | ------------- | ----- |
| groupIdExcludes | a list of groupId's, or parts of them, to exclude | see groupId |
| groupId | a groupId, or part of one, that should be excluded | example: value 'com.example' matches .*com.example.* |
| artifactIdExcludes | a list of artifactId's, or parts of them, to exclude | see artifactId |
| artifactId | a artifactId, or part of one, that should be excluded | example: value 'artifact' matches .*artifact.* |
| scope | 'compile', 'provided', 'runtime' or 'test' | scope determines which scope dependencies need to have to make the list |
