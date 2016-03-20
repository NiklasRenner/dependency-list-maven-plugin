package dk.renner

import java.util.regex.Pattern

final class Utils {

    //TODO: find better way to to this, catch catches both NullPointerException if String is null & IllegalArgumentException if no Enum with specified name exists
    static Pattern getPattern(String scopeString) {
        try {
            /* 'as' keyword can be used to attempt to coerce to string to enum */
            def scope = scopeString.toUpperCase() as Scope
            return ~/.*:.*:.*:.*:${scope}/
        } catch (all) {
            return ~/.*:.*:.*:.*:(compile|provided|runtime|test)/
        }
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

    static OperatingSystem getOS() {
        /* map.get(key) = map[key] */
        String osName = System.properties['os.name']
        if (osName.toLowerCase().contains('windows')) {
            return OperatingSystem.WINDOWS
        } else {
            return OperatingSystem.OTHER
        }
    }

    static String executeOnShell(String command, File workingDir) {
        def process = new ProcessBuilder(addShellPrefix(command))
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()

        def result = new StringBuilder()
        process.inputStream.eachLine {
            result.append(it).append('\n')
        }
        process.waitFor()  //TODO: figure out if needed

        return result.toString()
    }

    private static List<String> addShellPrefix(String command) {
        def commandArray = []
        if (getOS() == OperatingSystem.WINDOWS) {
            //powershell used because 'cmd -c' wasn't working(enviroment not complete in cmd?)
            commandArray.add("powershell")
        } else {
            commandArray.add("sh")
            commandArray.add("-c")
        }
        commandArray.add(command)

        return commandArray
    }

}