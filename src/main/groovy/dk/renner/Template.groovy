package dk.renner

class Template {

    String template

    String merge(Map<String,String> properties){
        def result = template

        properties.entrySet().each {
            result = result.replace(wrap(it.key), it.value)
        }

        result
    }

    private static String wrap(String property){
        "{{${property}}}"
    }
}
