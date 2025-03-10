import java.util.HashMap;

/**
 * A table with pre-defined symbols and corresponding binary code
 * as well as user-defined symbols, which will be recorded when compiling the code.
 */

public class SymbolTable {
    public HashMap<String, String> table = new HashMap<>();

    public SymbolTable() {
        table.put("SP", "0000000000000000");
        table.put("LCL", "0000000000000001");
        table.put("ARG", "0000000000000010");
        table.put("THIS", "0000000000000011");
        table.put("THAT", "0000000000000100");
        table.put("R0", "0000000000000000");
        table.put("R1", "0000000000000001");
        table.put("R2", "0000000000000010");
        table.put("R3", "0000000000000011");
        table.put("R4", "0000000000000100");
        table.put("R5", "0000000000000101");
        table.put("R6", "0000000000000110");
        table.put("R7", "0000000000000111");
        table.put("R8", "0000000000001000");
        table.put("R9", "0000000000001001");
        table.put("R10", "0000000000001010");
        table.put("R11", "0000000000001011");
        table.put("R12", "0000000000001100");
        table.put("R13", "0000000000001101");
        table.put("R14", "0000000000001110");
        table.put("R15", "0000000000001111");
        table.put("SCREEN", "0100000000000000");
        table.put("KBD", "0110000000000000");
    }

    public void addSymbol(String symbol, String binary) {
        table.put(symbol, binary);
    }

    public boolean checkContains(String symbol) {
        return table.containsKey(symbol);
    }

    public String getBinary(String symbol) {
        return table.get(symbol);
    }
}
