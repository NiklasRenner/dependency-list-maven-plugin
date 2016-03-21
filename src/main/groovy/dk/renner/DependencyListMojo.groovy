package dk.renner

import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.shared.dependency.tree.DependencyNode
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder

@Mojo(defaultPhase = LifecyclePhase.PACKAGE, name = "dependency-list", requiresDirectInvocation = false)
class DependencyListMojo extends AbstractMojo {

    /* escape dollarsign to avoid Groovy throwing a fit because of String substitution */
    @Parameter(defaultValue = "\${localRepository}", readonly = true)
    private ArtifactRepository localRepository;

    @Parameter(defaultValue = "\${project}", readonly = true)
    private MavenProject project;

    @Component
    private DependencyTreeBuilder dependencyTreeBuilder

    @Parameter(readonly = true)
    private List<String> groupIdExcludes

    @Parameter(readonly = true)
    private List<String> artifactIdExcludes

    @Parameter(readonly = true)
    private List<String> scopes

    void execute() {
        try {
            //path strings for I/O
            def buildDir = project.build.directory
            def outputFilePath = "${buildDir}/dependencies.html"

            //save all dependencies to dependencyList
            ArrayList<Dependency> dependencyList = [];
            dependencyTreeBuilder.buildDependencyTree(project, localRepository, null).children.each {
                saveDependencies(it, dependencyList)
            }

            //filter out dependencies with unwanted groupId's & artifactId's
            def iter = dependencyList.iterator()
            while (iter.hasNext()) {
                def dependency = iter.next()
                //TODO: prettify filter
                if (!scopeIncluded(dependency.scope, scopes) ||
                        idExcluded(dependency.groupId, groupIdExcludes) ||
                        idExcluded(dependency.artifactId, artifactIdExcludes)) {
                    iter.remove()
                }
            }

            //fill out html-template with Dependency data
            def dependencyTableElementTemplateString = this.getClass().getResource('/dependency-table-element.template').text
            def dependencyTableElementTemplate = new Template(template: dependencyTableElementTemplateString)
            def dependencyTableElementsString = ""

            dependencyList.sort().each {
                def valueMap = [:]
                valueMap.put("groupId", it.groupId)
                valueMap.put("artifactId", it.artifactId)
                valueMap.put("type", it.type)
                valueMap.put("version", it.version)
                valueMap.put("scope", it.scope)

                dependencyTableElementsString += dependencyTableElementTemplate.merge(valueMap)
            }

            def dependencyListTemplateString = this.getClass().getResource('/dependency-list.template').text
            def dependencyListTemplate = new Template(template: dependencyListTemplateString)

            def valueMap = [:]
            valueMap.put("dependencyList", dependencyTableElementsString);
            valueMap.put("projectName", project.build.finalName);

            //output finished dependency list to 'outputFilePath'
            new File(outputFilePath).write(dependencyListTemplate.merge(valueMap))
        } catch (all) {
            getLog().error(all)
            throw all
        }
    }

    private saveDependencies(DependencyNode node, List<Dependency> dependencyList) {
        node.children.each {
            def artifact = it.artifact
            def dependency = new Dependency(
                    groupId: artifact.groupId,
                    artifactId: artifact.artifactId,
                    type: artifact.type,
                    version: artifact.version,
                    scope: artifact.scope
            )

            if (!dependencyList.contains(dependency)) {
                dependencyList.add(dependency)
            }
            node.children.each {
                saveDependencies(it, dependencyList)
            }
        }
    }

    static boolean idExcluded(String s, List<String> strings) {
        def result = false;

        if (strings != null) {
            strings.each {
                if (s.contains(it)) result = true
            }
        }

        result
    }

    static boolean scopeIncluded(String scope, List<String> scopeIncludes) {
        if (scopeIncludes == null) return true

        def result = false
        scopeIncludes.each {
            if (scope.equals(it)) result = true
        }

        result
    }

}
