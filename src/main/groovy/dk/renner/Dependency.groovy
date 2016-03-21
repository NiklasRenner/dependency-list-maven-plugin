package dk.renner

class Dependency implements Comparable<Dependency> {

    String groupId
    String artifactId
    String type
    String version
    String scope

    @Override
    int compareTo(Dependency d) {
        if (scope != d.scope) {
            scope <=> d.scope
        } else if (groupId != d.groupId) {
            groupId <=> d.groupId
        } else if (artifactId != d.artifactId) {
            artifactId <=> d.artifactId
        } else {
            version <=> d.version
        }
    }

    @Override
    boolean equals(Object obj) {
        def result = false
        if (obj != null && obj instanceof Dependency) {
            result = this.id() == obj.id()
        }
        return result
    }

    private String id() {
        return groupId + artifactId + type + version + scope
    }
}
