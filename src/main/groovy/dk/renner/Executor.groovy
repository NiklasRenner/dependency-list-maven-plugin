package dk.renner

class Executor {

    public static def EXECSHELL(String command, File workingDir, OSEnum os) {
        println command
        def process = new ProcessBuilder(addShellPrefix(command, os))
                .directory(workingDir)
                .redirectErrorStream(true)
                .start()
        process.inputStream.eachLine { println it }
        process.waitFor();
        return process.exitValue()
    }

    private static def addShellPrefix(String command, OSEnum os) {
        def commandArray = new String[3]
        if (os == OSEnum.WINDOWS) {
            commandArray[0] = "cmd"
        } else if (os == OSEnum.OTHER) {
            commandArray[0] = "sh"
        }
        commandArray[1] = "-c"
        commandArray[2] = command
        return commandArray
    }

}