package dk.renner

class Dependency {

    String groupId
    String artifactId
    String type
    String version
    String scope

    @Override
    boolean equals(Object obj){
        def result = false

        if(obj != null && obj instanceof Dependency){
            result = this.id() == obj.id()
        }

        return result
    }

    private String id(){
        return groupId + artifactId + type + version + scope
    }
}
