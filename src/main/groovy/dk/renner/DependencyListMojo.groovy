package dk.renner

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

import static dk.renner.Utils.*

@Mojo(defaultPhase = LifecyclePhase.TEST_COMPILE, name = "dependency-list", requiresDirectInvocation = false)
class DependencyListMojo extends AbstractMojo {

    @Component
    private MavenProject mavenProject;

    @Parameter
    private String[] groupIdExcludes

    @Parameter
    private String[] artifactIdExcludes

    @Parameter(defaultValue = "compile")
    private String scope

    void execute() {
        try {
            def dependencyList = new ArrayList<Dependency>();
            def outputFile = new File("${mavenProject.build.directory}/dependencies.html")

            def list
            if (System.properties['os.name'].toLowerCase().contains('windows')) {
                list = "cmd /c mvn -o dependency:list".execute().text
            } else {
                getLog().debug(EXECSHELL("mvn -o dependency:list", mavenProject.basedir, OSEnum.OTHER) + "")
                getLog().debug("STUFF: " + ["sh", "-c", "pwd"].execute().text)
                list = ["sh", "-c", "mvn", "-f", mavenProject.basedir, "-o", "dependency:list"].execute().text
                getLog().error(list)
            }

            def regex = getPattern(scope)

            list.split("\n").each {
                if (regex.matcher(it).find()) {
                    def dependencyString = it.replace("[INFO]    ", "")
                    def dependencyStringList = dependencyString.split(":")

                    def dependency = new Dependency(
                            groupId: dependencyStringList[0],
                            artifactId: dependencyStringList[1],
                            type: dependencyStringList[2],
                            version: dependencyStringList[3],
                            scope: dependencyStringList[4]
                    )

                    if (!dependencyList.contains(dependency)) {
                        dependencyList.add(dependency)
                    }
                }
            }

            if (groupIdExcludes != null) {
                groupIdExcludes.each {
                    def iterator = dependencyList.iterator()

                    while (iterator.hasNext()) {
                        if (iterator.next().groupId.contains(it)) {
                            iterator.remove()
                        }
                    }
                }
            }

            if (artifactIdExcludes != null) {
                artifactIdExcludes.each {
                    def iterator = dependencyList.iterator()

                    while (iterator.hasNext()) {
                        if (iterator.next().artifactId.contains(it)) {
                            iterator.remove()
                        }
                    }
                }
            }

            dependencyList.sort { x, y ->
                if (x.groupId == y.groupId) {
                    x.artifactId <=> y.artifactId
                } else {
                    x.groupId <=> y.groupId
                }
            }

            def templateDependencyString = this.getClass().getResource('/dependency.template').text
            def dependencyListElementTemplate = new Template(template: templateDependencyString)

            def dependencyListHtml = ""

            dependencyList.unique().each {
                def map = [:]

                map.put("groupId", it.groupId)
                map.put("artifactId", it.artifactId)
                map.put("type", it.type)
                map.put("version", it.version)
                map.put("scope", it.scope)

                dependencyListHtml += dependencyListElementTemplate.merge(map)
            }

            def templateString = this.getClass().getResource('/dependency-list.template').text
            def htmlTemplate = new Template(template: templateString)

            def map = [:]

            map.put("dependencyList", dependencyListHtml);
            map.put("projectName", mavenProject.build.finalName);

            outputFile.write(htmlTemplate.merge(map))

        } catch (Exception ex) {
            getLog().error(ex)
        }
    }

}
