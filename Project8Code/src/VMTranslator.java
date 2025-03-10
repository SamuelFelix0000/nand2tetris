import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The VM translator which takes in a vm file with VM codes
 * and translate it into assembly code.
 * This is where the main method exists.
 */

public class VMTranslator {
    public static void main(String[] args) throws IOException {
        String inputFileName = args[0];
        File file = new File(inputFileName);
        Codewriter writer;
        if (file.isFile() && !inputFileName.endsWith(".vm")) {
            System.out.println("Error: Input must be a directory or a file with a .vm extension");
        } else if (file.isFile() && inputFileName.endsWith(".vm")) {
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".vm")) + ".asm";
            System.out.println(outputFileName);
            writer = new Codewriter(outputFileName);
            translatorHelper(file, writer);
            writer.close();
        } else {
            ArrayList<File> fileArray = new ArrayList<>();
            File[] files = file.listFiles();
            String outputFileName;
            if (inputFileName.contains("/")) {
                outputFileName = inputFileName.substring(inputFileName.lastIndexOf("/"));
            } else {
                outputFileName = inputFileName;
            }
            outputFileName = inputFileName + "/" + outputFileName + ".asm";
            writer = new Codewriter(outputFileName);
            assert files != null;
            for (File target : files) {
                if (target.getName().endsWith(".vm")) {
                    fileArray.add(target);
                }
            }
            writer.writeSys();
            for (File i : fileArray) {
                if (!i.getName().equals("Sys.vm")) {
                    translatorHelper(i, writer);
                }
            }
            File Sysfile = new File(inputFileName + "/" + "Sys.vm");
            translatorHelper(Sysfile, writer);
            writer.close();
        }
    }

    private static void translatorHelper(File file, Codewriter codewriter) throws IOException {
        Parser parser = new Parser(file);
        while (parser.hasMoreCommands()) {
            System.out.println(parser.currentCommand);
            if (parser.typeofCommand().equals("arithmetic")) {
                codewriter.writeArithmetic(parser.arg1());
            } else if (parser.typeofCommand().equals("push")) {
                codewriter.writePushPop("push", parser.arg1(), Integer.parseInt(parser.arg2()), file);
            } else if (parser.typeofCommand().equals("pop")) {
                codewriter.writePushPop("pop", parser.arg1(), Integer.parseInt(parser.arg2()), file);
            } else if (parser.typeofCommand().equals("label")) {
                codewriter.writeLabel(parser.arg1());
            } else if (parser.typeofCommand().equals("goto")) {
                codewriter.writeGoto(parser.arg1());
            } else if (parser.typeofCommand().equals("if")) {
                codewriter.writeIf(parser.arg1());
            } else if (parser.typeofCommand().equals("function")) {
                codewriter.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
            } else if (parser.typeofCommand().equals("call")) {
                codewriter.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
            } else if (parser.typeofCommand().equals("return")) {
                codewriter.returnFunction();
            }
        }
        parser.close();
    }
}
