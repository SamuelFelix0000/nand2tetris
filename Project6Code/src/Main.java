import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Objects;

/**
 * Code that performs the job of the Hack Assembler.
 * Uses the other classes, takes in a file and return the binary translation line by line.
 */

public class Main {
    public static String toBinary(int decimal) {
        return String.format("%16s", Integer.toBinaryString(decimal)).replace(' ', '0');
    }

    public static void main(String[] args) {
        int memoryLocation = 16;
        int programLine = -1;
        SymbolTable symbolTable = new SymbolTable();
        String filepath = "/Users/mbh/Desktop/Rect.asm";
        File file = new File(filepath);
        File output = new File("/Users/mbh/Desktop/Rect.hack");
        try {
            Files.createFile(Path.of(output.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(output.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // The first pass is used to add the labels in the code to the SymbolTable.
        Parser parser1 = new Parser(file);
        while (parser1.hasMoreCommands()) {
            if (Objects.equals(parser1.typeOfCommand(), "label")) {
                String label = parser1.getLabel();
                symbolTable.addSymbol(label, toBinary(programLine + 1));
            }
            if (Objects.equals(parser1.typeOfCommand(), "A") || Objects.equals(parser1.typeOfCommand(), "C")) {
                programLine += 1;
            }
        }
        parser1.close();
        // The second pass will translate the code into binary machine code.
        Parser parser2 = new Parser(file);
        Code mainCoder = new Code(file);
        while (parser2.hasMoreCommands()) {
            System.out.println(parser2.currentCommand);
            if (Objects.equals(parser2.typeOfCommand(), "A")) {
                try {
                    assert writer != null;
                    String command = parser2.getACommand();
                    if (Code.isInteger(command)) {
                        writer.write(toBinary(Integer.parseInt(command)));
                        System.out.println(toBinary(Integer.parseInt(command)));
                    }
                    else if (symbolTable.checkContains(command)) {
                        writer.write(symbolTable.getBinary(command));
                        System.out.println(symbolTable.getBinary(command));
                    } else {
                        symbolTable.addSymbol(command, toBinary(memoryLocation));
                        System.out.println(symbolTable.getBinary(command));
                        writer.write(symbolTable.getBinary(command));
                        memoryLocation += 1;
                    }
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (Objects.equals(parser2.typeOfCommand(), "C")) {
                try {
                    assert writer != null;
                    String dest = parser2.getDest();
                    String comp = parser2.getComp();
                    String jump = parser2.getJump();
                    writer.write("111" +  mainCoder.codeComp(comp) + mainCoder.destCode(dest) + mainCoder.jumpCode(jump));
                    System.out.println("111" +  mainCoder.codeComp(comp) + mainCoder.destCode(dest) + mainCoder.jumpCode(jump));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        parser2.close();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
