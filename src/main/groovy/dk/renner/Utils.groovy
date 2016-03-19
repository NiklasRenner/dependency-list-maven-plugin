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
                return ~/.*:.*:.*:.*:${scope}/
            }
        }
        return ~/.*:.*:.*:.*:(compile|provided|runtime|test)/
    }

}