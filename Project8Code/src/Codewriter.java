import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generating the assembling code from the parsed vm command.
 */

public class Codewriter {
    private File outFile;
    private BufferedWriter writer;
    private int i = 0;
    private int returnLabelNumber = 0;

    public Codewriter(String filepath) {
        this.outFile = new File(filepath);
        try {
            this.writer = new BufferedWriter(new FileWriter(outFile.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Helper method that pop a number and place it in RAM[Position]. */
    private void popAndPStore(int position) throws IOException {
        // Position can be chosen from the integers 13, 14, 15.
        writer.write("@SP\n" + "A=M-1\n" + "D=M\n" + "@R"+position+"\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
    }

    /** Helper method that push the calculated result stored in RAM[15] into stack. */
    private void pushArithmeticResult() throws IOException {
        writer.write("@R15\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
    }

    /** Helper method that push a constant into the stack.
     * constant is a string, either "0" or "-1".  */
    private void pushConstant(String constant) throws IOException {
        writer.write("@SP\n" + "A=M\n" + "M="+constant+"\n" + "@SP\n" + "M=M+1\n");
    }

    /** Writes to the output file the assembly code that implements
     * the given arithmetic command.
     */
    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            case "add":
                writer.write("// add\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x+y and place result in RAM[15].
                writer.write("@R14\n" + "D=M\n" + "@R13\n" + "D=D+M\n" + "@R15\n" + "M=D\n");
                pushArithmeticResult();
                break;
            case "sub":
                writer.write("// sub\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x-y and place result in RAM[15].
                writer.write("@R14\n" + "D=M\n" + "@R13\n" + "D=D-M\n" + "@R15\n" + "M=D\n");
                pushArithmeticResult();
                break;
            case "neg":
                writer.write("// neg\n");
                popAndPStore(13);
                // Calculate -y and place result in RAM[15].
                writer.write("@R13\n" + "D=M\n" + "@R15\n" + "M=-D\n");
                pushArithmeticResult();
                break;
            case "eq":
                writer.write("// eq\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x-y.
                writer.write("@R14\n" + "D=M\n" + "@R13\n" + "D=D-M\n");
                // Write out a C command with jumps to test if x-y=0 or not.
                writer.write("@EQ"+i+"\n" + "D;JEQ\n");
                pushConstant("0");
                writer.write("@ENDEQ"+i+"\n" + "0;JMP\n" + "(EQ"+i+")\n");
                pushConstant("-1");
                writer.write("(ENDEQ"+i+")\n");
                writer.write("\n");
                i++;
                break;
            case "gt":
                writer.write("// gt\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x-y.
                writer.write("@R14\n" + "D=M\n" + "@R13\n" + "D=D-M\n");
                // Write out a C command with jumps to test if x-y=0 or not.
                writer.write("@GT"+i+"\n" + "D;JGT\n");
                pushConstant("0");
                writer.write("@ENDGT"+i+"\n" + "0;JMP\n" + "(GT"+i+")\n");
                pushConstant("-1");
                writer.write("(ENDGT"+i+")\n");
                writer.write("\n");
                i++;
                break;
            case "lt":
                writer.write("// lt\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x-y.
                writer.write("@R14\n" + "D=M\n" + "@R13\n" + "D=D-M\n");
                // Write out a C command with jumps to test if x-y=0 or not.
                writer.write("@LT"+i+"\n" + "D;JLT\n");
                pushConstant("0");
                writer.write("@ENDLT"+i+"\n" + "0;JMP\n" + "(LT"+i+")\n");
                pushConstant("-1");
                writer.write("(ENDLT"+i+")\n");
                writer.write("\n");
                i++;
                break;
            case "and":
                writer.write("// and\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x AND y.
                writer.write("@14\n" + "D=M\n" + "@R13\n" + "D=D&M\n" + "@R15\n" + "M=D\n");
                pushArithmeticResult();
                break;
            case "or":
                writer.write("// or\n");
                popAndPStore(13);
                popAndPStore(14);
                // Calculate x OR y.
                writer.write("@14\n" + "D=M\n" + "@R13\n" + "D=D|M\n" + "@R15\n" + "M=D\n");
                pushArithmeticResult();
                break;
            case "not":
                writer.write("// not\n");
                popAndPStore(13);
                // Calculate NOT y.
                writer.write("@R13\n" + "D=M\n" + "@R15\n" + "M=!D\n");
                pushArithmeticResult();
                break;
            default:
                break;
        }
    }

    /** Writes to the output file the assembly code that implements the given command,
     * where the command is either a PUSH or POP command.
     * The command variable input is either push or pop. */
    public void writePushPop(String command, String segment, int index, File file) throws IOException {
        if (command.equals("push")) {
            switch (segment) {
                case "local":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    writer.write("@LCL\n" + "D=M\n" + "@" +index+"\n" + "A=D+A\n" + "D=M\n" +
                            "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "argument":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    writer.write("@ARG\n" + "D=M\n" + "@" +index+"\n" + "A=D+A\n" + "D=M\n" +
                            "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "this":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    writer.write("@THIS\n" + "D=M\n" + "@" +index+"\n" + "A=D+A\n" + "D=M\n" +
                            "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "that":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    writer.write("@THAT\n" + "D=M\n" + "@" +index+"\n" + "A=D+A\n" + "D=M\n" +
                            "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "static":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Pop an element from the stack.
                    String filename = file.getName();
                    int location = filename.indexOf(".");
                    filename = filename.substring(0, location);
                    String staticReference = filename + "." + index;
                    writer.write("@"+staticReference+"\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "temp":
                    writer.write("// vm command: " + command + segment + index + "\n") ;
                    writer.write("@5\n" + "D=A\n" + "@"+index+"\n" + "A=D+A\n" + "D=M\n" +
                            "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                case "pointer":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    if (index == 0) {
                        writer.write("@THIS\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    } else if (index == 1) {
                        writer.write("@THAT\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    }
                    break;
                case "constant":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    writer.write("@"+index+"\n" + "D=A\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
                    break;
                default:
                    break;
            }
        } else if (command.equals("pop")) {
            switch (segment) {
                case "local":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Get the address of segment + index and store it in RAM[13].
                    writer.write("@LCL\n" + "D=M\n" + "@"+index+"\n" + "D=D+A\n" + "@R13\n" + "M=D\n");
                    // Get the number stored in the address that SP-- is pointing at.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    // Store the number got in previous step in the address that segment + index is pointing at.
                    writer.write("@R13\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    break;
                case "argument" :
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Get the address of segment + index and store it in RAM[13].
                    writer.write("@ARG\n" + "D=M\n" + "@"+index+"\n" + "D=D+A\n" + "@R13\n" + "M=D\n");
                    // Get the number stored in the address that SP-- is pointing at.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    // Store the number got in previous step in the address that segment + index is pointing at.
                    writer.write("@R13\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    break;
                case "this":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Get the address of segment + index and store it in RAM[13].
                    writer.write("@THIS\n" + "D=M\n" + "@"+index+"\n" + "D=D+A\n" + "@R13\n" + "M=D\n");
                    // Get the number stored in the address that SP-- is pointing at.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    // Store the number got in previous step in the address that segment + index is pointing at.
                    writer.write("@R13\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    break;
                case "that":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Get the address of segment + index and store it in RAM[13].
                    writer.write("@THAT\n" + "D=M\n" + "@"+index+"\n" + "D=D+A\n" + "@R13\n" + "M=D\n");
                    // Get the number stored in the address that SP-- is pointing at.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    // Store the number got in previous step in the address that segment + index is pointing at.
                    writer.write("@R13\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    break;
                case "static":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Pop a number from the stack and store it in D register.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    String filename = file.getName();
                    int location = filename.indexOf(".");
                    filename = filename.substring(0, location);
                    String staticReference = filename + "." + index;
                    writer.write("@"+staticReference+"\n" + "M=D\n" +"@SP\n" + "M=M-1\n");
                    break;
                case "temp":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    // Get the address of 5 + index and store it in RAM[13].
                    writer.write("@5\n" + "D=A\n" + "@"+index+"\n" + "D=D+A\n" + "@R13\n" + "M=D\n");
                    // Get the number stored in the address that SP-- is pointing at.
                    writer.write("@SP\n" + "A=M-1\n" + "D=M\n");
                    // Store the number got in previous step in the address that segment + index is pointing at.
                    writer.write("@R13\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    break;
                case "pointer":
                    writer.write("// vm command: " + command + segment + index + "\n");
                    if (index == 0) {
                        writer.write("@SP\n" + "A=M-1\n" + "D=M\n" + "@THIS\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    } else if (index == 1) {
                        writer.write("@SP\n" + "A=M-1\n" + "D=M\n" + "@THAT\n" + "M=D\n" + "@SP\n" + "M=M-1\n");
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void writeInit() throws IOException {
        writer.write("@256\n" + "D=A\n" + "@SP\n" + "M=D\n");
    }

    /** Writes to the output file a Label command. */
    public void writeLabel(String label) throws IOException {
        writer.write("// vm command: label" + "\n");
        writer.write("("+label+")" + "\n");
    }

    /** Writes to the output file a Goto command. */
    public void writeGoto(String label) throws IOException{
        writer.write("// vm command: goto" + "\n");
        writer.write("@"+label+"\n");
        writer.write("0;JMP\n");
    }

    /** Writes to the output file a if-goto command.
     * Pop a number from stack and evaluate the command.*/
    public void writeIf(String label) throws IOException {
        writer.write("// vm command: if-goto" + "\n");
        writer.write("@SP\n" + "M=M-1\n" + "A=M\n" + "D=M\n");
        writer.write("@"+label+"\n" + "D;JNE\n");
    }

    /** Writes the assembly code for calling a function with
     * name of functionName and numArgs many arguments. */
    public void writeCall(String functionName, int numArgs) throws IOException {
        String returnLabel = "ReturnLabel" + returnLabelNumber;
        returnLabelNumber ++;
        writer.write("// vm command: call Function command" + "\n");
        writer.write("@"+returnLabel+"\n" + "D=A\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
        writer.write("@LCL\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
        writer.write("@ARG\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
        writer.write("@THIS\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
        writer.write("@THAT\n" + "D=M\n" + "@SP\n" + "A=M\n" + "M=D\n" + "@SP\n" + "M=M+1\n");
        writer.write("@SP\n" + "D=M\n" + "@5\n" + "D=D-A\n" +
                "@"+numArgs+"\n" + "D=D-A\n" + "@ARG\n" + "M=D\n");
        writer.write("@SP\n" + "D=M\n" + "@LCL\n" + "M=D\n");
        writeGoto(functionName);
        writeLabel(returnLabel);
    }

    /** Initialize the spaces allocated to the local variables. */
    public void writeFunction(String functionName, int numLocals) throws IOException {
        writer.write("// vm command: Function declaration command" + "\n");
        writeLabel(functionName);
        for (int j = 0; j < numLocals; j++) {
            pushConstant("0");
        }
    }

    public void writeSys() throws IOException {
        writer.write("// vm command: Initialization" + "\n");
        writeInit();
        writeCall("Sys.init", 0);
    }

    /** Writes assembly language that returns a function at the end of a function call. */
    public void returnFunction() throws IOException {
        writer.write("// vm command: function return command" + "\n");
        writer.write("@LCL\n" + "D=M\n" + "@R13\n" + "M=D\n");    // Store FRAME variable inside R13
        writer.write("@13\n" + "D=M\n" + "@5\n" + "A=D-A\n" + "D=M\n" + "@R14\n" + "M=D\n");    // Store RET variable inside R14
        writer.write("@SP\n" + "A=M-1\n" + "D=M\n" + "@SP\n" + "M=M-1\n" + "@ARG\n" + "A=M\n" + "M=D\n");
        writer.write("@ARG\n" + "D=M+1\n" + "@SP\n" + "M=D\n");
        writer.write("@R13\n" + "D=M\n" + "@1\n" + "A=D-A\n" + "D=M\n" + "@THAT\n" + "M=D\n");
        writer.write("@R13\n" + "D=M\n" + "@2\n" + "A=D-A\n" + "D=M\n" + "@THIS\n" + "M=D\n");
        writer.write("@R13\n" + "D=M\n" + "@3\n" + "A=D-A\n" + "D=M\n" + "@ARG\n" + "M=D\n");
        writer.write("@R13\n" + "D=M\n" + "@4\n" + "A=D-A\n" + "D=M\n" + "@LCL\n" + "M=D\n");
        writer.write("@R14\n" + "A=M\n" + "0;JMP\n");
    }

    /** Close the writer. */
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

