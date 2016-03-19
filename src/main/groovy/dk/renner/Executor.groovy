package dk.renner

import java.util.concurrent.TimeUnit

class Executor {

    public static getOS() {
        String osName = System.properties['os.name']
        if (osName.toLowerCase().contains('windows')) {
            return OSEnum.WINDOWS
        } else {
            return OSEnum.OTHER
        }
    }

    public static String executeOnShell(String command, File workingDir) {
        def process = new ProcessBuilder(addShellPrefix(command))
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()
        def result = new StringBuilder()
        process.inputStream.eachLine {
            result.append(it).append('\n')
        }
        process.waitFor(15, TimeUnit.SECONDS)
        process.destroy()
        return result
    }

    private static List<String> addShellPrefix(String command) {
        def commandArray
        if (getOS() == OSEnum.WINDOWS) {
            commandArray = new String[2]
            commandArray[0] = "powershell"
            commandArray[1] = command
        } else if (getOS() == OSEnum.OTHER) {
            commandArray = new String[3]
            commandArray[0] = "sh"
            commandArray[1] = "-c"
            commandArray[2] = command
        }
        return commandArray
    }

}