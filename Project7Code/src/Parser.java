import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A parser will parse each vm commands into its lexical elements.
 * It will take in a file as input
 */

public class Parser {
    public String currentCommand;
    private BufferedReader br;

    public Parser(File file) {
        String filepath = file.getPath();
        try {
            this.br = new BufferedReader(new FileReader(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Check if the file has more commands. If yes, set currentCommand to the next command */
    public boolean hasMoreCommands() {
        try {
            String line = "";
            while (line.isEmpty()) {
                String originalContent = br.readLine();
                if (originalContent == null) {
                    return false;
                } else {
                    line = cleanString(originalContent);
                }
            }
            currentCommand = line;
            return true;
        }  catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Removes empty white spaces, remove any comments in the given command. */
    public String cleanString(String command) {
        command = command.trim();
        if (command.contains("//")) {
            int index = command.indexOf('/');
            command = command.substring(0, index);
        }
        return command;
    }

    /** Returns the type of the current vm command. */
    public String typeofCommand() {
        String command = currentCommand;
        if (command.contains("push")) {
            return "push";
        } else if (command.contains("pop")) {
            return "pop";
        } else if (command.contains("label")) {
            return "label";
        } else if (command.contains("goto")) {
            return "goto";
        } else if (command.contains("if")) {
            return "if";
        } else if (command.contains("function")) {
            return "function";
        } else if (command.contains("call")) {
            return "call";
        } else if (command.contains("return")) {
            return "return";
        } else {
            return "arithmetic";
        }
    }

    /** Returns the first parameter of the current vm command. */
    public String arg1() {
        if (!typeofCommand().equals("return")) {
            if (typeofCommand().equals("arithmetic")) {
                return currentCommand;
            } else {
                int firstIndex = currentCommand.indexOf(" ");
                String firstSub = currentCommand.substring(firstIndex + 1);
                int secondIndex = firstSub.indexOf(" ");
                return firstSub.substring(0, secondIndex);
            }
        } else {
            // If the vm command is a return command, it should not be applied to this method.
            return null;
        }
    }

    /** Returns the second parameter of the current vm command. */
    public String arg2() {
        if (typeofCommand().equals("push") || typeofCommand().equals("pop") ||
                typeofCommand().equals("function") || typeofCommand().equals("call")) {
            int firstIndex = currentCommand.indexOf(" ");
            String firstSub = currentCommand.substring(firstIndex + 1);
            int secondIndex = firstSub.indexOf(" ");
            return firstSub.substring(secondIndex + 1);
        } else {
            // If the type of vm command is not one of the above, it should not be applied to this method.
            return null;
        }
    }

    /** Close the BufferReader created. */
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
