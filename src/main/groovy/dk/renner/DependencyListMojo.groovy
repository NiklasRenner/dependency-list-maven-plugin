package dk.renner

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

import static dk.renner.Utils.executeOnShell
import static dk.renner.Utils.getPattern
import static dk.renner.Utils.stringContainsWithList

@Mojo(defaultPhase = LifecyclePhase.TEST_COMPILE, name = "dependency-list", requiresDirectInvocation = false)
class DependencyListMojo extends AbstractMojo {

    @Component
    private MavenProject mavenProject;

    @Parameter
    private List<String> groupIdExcludes

    @Parameter
    private List<String> artifactIdExcludes

    @Parameter(defaultValue = "compile")
    private String scope

    void execute() {
        try {
            /* ${object} is used inside strings for string substitution, it gets replaced with object.toString() */
            def outputFile = new File("${mavenProject.build.directory}/dependencies.html")

            executeOnShell("mvn -q dependency:resolve", mavenProject.basedir)
            def commandOutput = executeOnShell("mvn -o dependency:list", mavenProject.basedir)

            def dependencyList = new ArrayList<Dependency>();
            def regex = getPattern(scope)
            commandOutput.split("\n").each { String line ->
                if (regex.matcher(line).find()) {
                    def dependencyString = line.replace("[INFO]    ", "")
                    def dependencyStringSplit = dependencyString.split(":")

                    def dependency = new Dependency(
                            groupId: dependencyStringSplit[0],
                            artifactId: dependencyStringSplit[1],
                            type: dependencyStringSplit[2],
                            version: dependencyStringSplit[3],
                            scope: dependencyStringSplit[4]
                    )

                    if (!dependencyList.contains(dependency)) {
                        dependencyList.add(dependency)
                    }
                }
            }

            def iter = dependencyList.iterator()
            while (iter.hasNext()) {
                def dependency = iter.next()

                if (stringContainsWithList(dependency.groupId, groupIdExcludes) || stringContainsWithList(dependency.artifactId, artifactIdExcludes)) {
                    iter.remove()
                }
            }

            /* <=> operator = compareTo() */
            //TODO: move sort impl to Dependency.compareTo() & use .sort() without closure
            dependencyList.sort { x, y ->
                if (x.groupId == y.groupId) {
                    /* 'return' keyword not needed */
                    x.artifactId <=> y.artifactId
                } else {
                    x.groupId <=> y.groupId
                }
            }

            def dependencyListElementTemplateString = this.getClass().getResource('/dependency.template').text
            def dependencyListElementTemplate = new Template(template: dependencyListElementTemplateString)
            def dependencyListElementsHtml = ""

            dependencyList.unique().each {
                /* empty (obj,obj) map initialization is done by using [:],
                objects put in have their types evaluated in runtime,
                so it can be used for all maps if you don't need typesafety */
                def valueMap = [:]
                valueMap.put("groupId", it.groupId)
                valueMap.put("artifactId", it.artifactId)
                valueMap.put("type", it.type)
                valueMap.put("version", it.version)
                valueMap.put("scope", it.scope)K

                dependencyListElementsHtml += dependencyListElementTemplate.merge(valueMap)
            }

            def dependencyListTemplateString = this.getClass().getResource('/dependency-list.template').text
            def dependencyListTemplate = new Template(template: dependencyListTemplateString)

            def valueMap = [:]
            valueMap.put("dependencyList", dependencyListElementsHtml);
            valueMap.put("projectName", mavenProject.build.finalName);

            outputFile.write(dependencyListTemplate.merge(valueMap))
        } catch (Exception ex) {
            getLog().error(ex)
        }
    }

}
