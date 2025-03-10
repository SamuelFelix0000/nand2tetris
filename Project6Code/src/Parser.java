import java.io.*;

/**
 *  A parser need to take in a file name and unpacks each instruction into its underlying fields.
 *  Need to read one line at a time and parse the command.
 *  Need to be able to skip whitespace and comments.
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

    public static void main(String[] args) {
        String filepath = "/Users/mbh/Desktop/Add.asm";
        File file = new File(filepath);
        Parser parser = new Parser(file);
        while (parser.hasMoreCommands()) {
            System.out.println(parser.currentCommand);
        }
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

    /** Returns the type of the current command. */
    public String typeOfCommand() {
        String command = currentCommand;
        if (command.contains("(")) {
            return "label";
        } else if (command.contains("@")) {
            return "A";
        } else if (command.isEmpty()) {
            return "empty";
        } else {
            return "C";
        }
    }

    /** Returns the address in A commands. */
    public String getACommand() {
        return currentCommand.substring(1);
    }

    /** Returns the label for a label command. */
    public String getLabel() {
        int start = currentCommand.indexOf("(");
        int end = currentCommand.indexOf(")");
        return currentCommand.substring(start + 1, end).trim();
    }

    /** Returns the Jump destination in a C command. */
    public String getJump() {
        if (currentCommand.contains(";")) {
            int index = currentCommand.indexOf(";");
            return currentCommand.substring(index + 1);
        }
        return "null";
    }

    /** Returns the string representing what to compute in a C command. */
    public String getComp() {
        if (currentCommand.contains(";") && currentCommand.contains("=")) {
            int start = currentCommand.indexOf("=");
            int end = currentCommand.indexOf(";");
            return currentCommand.substring(start + 1, end);
        } else if (currentCommand.contains(";") && !currentCommand.contains("=")) {
            int end = currentCommand.indexOf(";");
            return currentCommand.substring(0, end);
        } else if (!currentCommand.contains(";") && currentCommand.contains("=")) {
            int start = currentCommand.indexOf("=");
                return currentCommand.substring(start + 1);
        } else {
            return currentCommand;
        }
    }

    /** Returns the destination of where to store computed value for a C command. */
    public String getDest() {
        if (currentCommand.contains("=")) {
            int end = currentCommand.indexOf("=");
            return currentCommand.substring(0, end);
        }
        return "null";
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
