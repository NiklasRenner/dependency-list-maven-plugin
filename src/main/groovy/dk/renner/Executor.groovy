package dk.renner

class Executor {

    static OSEnum getOS() {
        /* map.get(key) = map[key] */
        String osName = System.properties['os.name']
        if (osName.toLowerCase().contains('windows')) {
            return OSEnum.WINDOWS
        } else {
            return OSEnum.OTHER
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

        process.waitFor()
        return result
    }

    static List<String> addShellPrefix(String command) {
        def commandArray
        if (getOS() == OSEnum.WINDOWS) {
            commandArray = new String[2]
            commandArray[0] = "powershell"
            commandArray[1] = command
        } else {
            commandArray = new String[3]
            commandArray[0] = "sh"
            commandArray[1] = "-c"
            commandArray[2] = command
        }
        return commandArray
    }

}