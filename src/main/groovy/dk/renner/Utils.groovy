package dk.renner

import java.util.regex.Pattern

class Utils {

    private static final COMPILE = "compile"
    private static final PROVIDED = "provided"
    private static final RUNTIME = "runtime"
    private static final TEST = "test"

    static Pattern getPattern(String scope) {
        if (scope != null) {
            if (scope == COMPILE || scope == PROVIDED || scope == RUNTIME || scope == TEST) {
                /* ~/regex/ is syntactic sugar to create a Pattern object that matches 'regex'  */
                return ~/.*:.*:.*:.*:${scope}/
            }
        }
        return ~/.*:.*:.*:.*:(compile|provided|runtime|test)/
    }

    static boolean stringContainsWithList(String s, List<String> strings) {
        /* can't return inside Closure! eg. it keeps iterating if you use return inside .each(){ } */
        def result = false;

        if (strings == null) return result

        strings.each {
            if (s.contains(it)) result = true
        }

        return result
    }

}