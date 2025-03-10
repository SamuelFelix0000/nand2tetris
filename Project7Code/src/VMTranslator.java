import java.io.File;
import java.io.IOException;

/**
 * The VM translator which takes in a vm file with VM codes
 * and translate it into assembly code.
 * This is where the main method exists.
 */

public class VMTranslator {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator.vm");
            return;
        }
        String inputFileName = args[0];
        if (!inputFileName.endsWith(".vm")) {
            System.out.println("Error: Input file must have a .vm extension");
            return;
        }
        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf(".vm")) + ".asm";
        File file = new File(inputFileName);
        Parser parser = new Parser(file);
        Codewriter codewriter = new Codewriter(outputFileName);
        while (parser.hasMoreCommands()) {
            System.out.println(parser.currentCommand);
            if (parser.typeofCommand().equals("arithmetic")) {
                codewriter.writeArithmetic(parser.arg1());
            } else if (parser.typeofCommand().equals("push")) {
                codewriter.writePushPop("push", parser.arg1(), Integer.parseInt(parser.arg2()));
            } else if (parser.typeofCommand().equals("pop")) {
                codewriter.writePushPop("pop", parser.arg1(), Integer.parseInt(parser.arg2()));
            }
        }
        parser.close();
        codewriter.close();
    }
}
