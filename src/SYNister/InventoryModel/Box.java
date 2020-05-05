package SYNister.InventoryModel;
import SYNister.utils.FileUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Model of Box in Inventory
 *
 * @author Vikram Shivakumar
 * @author Rishi Patel
c
 */
public class Box {
    private String name;
    private final String filename;
    private Tube[][] slots;
    private String description;
    private String location;
    private String plate_type;
    private int temperature;
    private boolean isFull;
    private HashMap<String, String> nameToLoc;
    private HashMap<String, String> nameToConc;
    private HashSet<String> emptySlots;
    private Inventory parentInvetory;

    public Box(String filename, HashMap nameToLoc, HashMap nameToConc, Inventory parentInvetory) throws Exception {
        this.filename = filename;
        this.nameToLoc = nameToLoc;
        this.emptySlots = new HashSet<>();
        this.nameToConc = nameToConc;
        this.isFull = false;
        initializeMetaData();
        parseInventory();
    }

    // Gets a tube object located at a specific row and specific column
    public Tube getTube(int row, int col) {
        return slots[row][col];
    }

    // Gets the name of the box
    public String getName() {
        return name;
    }

    // Stores meta data for box as attributes of box class
    // Gets dimensions for 2d array of tubes for box from tsv file
    public void initializeMetaData() throws Exception {
        String buff = FileUtils.readResourceFile(filename);
        String[] lines = buff.split("\\r|\\r?\\n");
        String name = lines[0].split("\t")[1];
        String desc = lines[1].split("\t")[1];
        String loc = lines[2].split("\t")[1];
      //  String pt = lines[3].split("\t")[1];
        Integer temp =  Integer.parseInt(lines[4].split("\t")[1]);

        this.name = name;
        this.description = desc;
        this.location = loc;
     //   this.plate_type = pt;
        this.temperature = temp;

        int x = 0;
        String cur = lines[x];
        while (!cur.startsWith(">>")) {
            x += 1;
            cur = lines[x];
        }
        int y = x;
        x += 1;
        cur = lines[x];
        while (!cur.startsWith(">>")) {
            x += 1;
            cur = lines[x];
        }
        int numRows = x - y - 2;
        int numCols = cur.split("\t").length - 1;
        slots = new Tube[numRows][numCols];
    }

    // Parses inventory tsv file and fills 2d array of tubes that corresponds to box object
    public void parseInventory() throws Exception {
        // Reads in tsv file and splits it by line
        String buff = FileUtils.readResourceFile(filename);
        String[] lines = buff.split("\\r|\\r?\\n");

        // Looks for line that begins with ">> label" to indicate label data
        // findStart method returns 120 if it cannot find the given parameters
        Integer nameStart = findStart(lines, ">>label") + 1;
        // If file does not contains label data, then look for name data
        if (nameStart == 121) {
            nameStart = findStart(lines, ">>name") + 1;
        }
        // Look for composition, concentration, and note data respectively in tsv file
        Integer compositonStart = findStart(lines, ">>composition") + 1;
        Integer concentrationStart = findStart(lines, ">>concentration") + 1;
        Integer noteStart = findStart(lines, ">>note") + 1;

        // Iterate over name, composition, concentration, and note data in tsv row by row and form
        // tube object using the data for parameters, then add data to 2-dimnensional array of tubes
        // corresponding to the box
        for (int x = 0; x < slots.length; x++) {
            // Select the xth row of the name, composition, concentration, and note data if available
            String[] nameLine = checkFileForAttribute(lines, nameStart + x);
            String[] compositionLine = checkFileForAttribute(lines, compositonStart + x);
            String[] concentrationLine = checkFileForAttribute(lines, concentrationStart + x);
            String[] noteLine = checkFileForAttribute(lines, noteStart);
            boolean oligos = filename.contains("oligos");
            for (int y = 0; y < slots[0].length; y++) {
                // Select the yth column within the xth row and extract the following tube attributes
                String tubeLabel = (String) findTubeAttribute(nameLine, y + 1);
                String tubeComp = (String) findTubeAttribute(compositionLine, y + 1);
                String tubeConc = (String) findTubeAttribute(concentrationLine, y + 1);
                // Create new instance of tube class using data from file
                // Add new instance of tube class to 2-dimensional array of tubes
                slots[x][y] = new Tube(0, tubeLabel, tubeComp, tubeConc);
                // Call processSlot function to add tube to HashMaps nameToLoc and nameToConc
                // which allows for easy lookup of tube later OR add empty slots to set of empty slots which
                // allows easy lookup of empty slots when you need to add a tube later
                processSlot(tubeLabel, tubeComp, tubeConc, x, y, oligos);
            }
        }

    }
    // When you add a newly instantiated tube to a specific slot within the box call this method to add the tube to
    // HashMaps nameToLoc and nameToConc which allows for easy lookup of tube later OR add empty slots to set of
    // empty slots which allows easy lookup of empty slots when you need to add a tube later
    private void processSlot(String tubeLabel, String tubeComp, String tubeConc, int row, int col, boolean oligos) {

        // Turn tube location into a single human readable string that can be stored in HashMap and written to
        // lab sheets
        String tubeLocation = name + "/" + getLetterForRow(row) + (col+1);


        // Turn tube location into length 2 array that can be stored in a set of empty slots in the box
        int[] currentSlot = new int[2];
        currentSlot[0] = row;
        currentSlot[1] = col;
        // Test if both the the tubeLabel and tubeComp are null, if both are the slot will be added to emptySlots Set
        boolean empty = tubeLabel == null && tubeComp == null;
        if (empty) {
            emptySlots.add(tubeLocation);
        }
        // If only the tubeLabel is null then the key for the HashMaps
        else if (tubeLabel == null) {
            addTubeToHashMaps(tubeComp, tubeConc, tubeLocation);
        }
        // If tubeLabel is not null it is used as the key for the HashMaps
        else {
            addTubeToHashMaps(tubeLabel, tubeConc, tubeLocation);
        }
    }

    // Adds tube to HashMaps
    public void addTubeToHashMaps(String tubeKey, String tubeConc, String tubeLocation) {
        int currConc = StringToConcentration(tubeConc);
        // Checks if nameToLoc HashMap already contains a tube with the same label or composition
        if (nameToLoc.containsKey(tubeKey)) {
            int oldConc = StringToConcentration(nameToLoc.get(tubeKey));
            // Checks if concentration of current tube is higher than that of current tube representing that label or
            // composition in the HashMap and if it is higher it replaces it
            if (oldConc < currConc) {
                nameToLoc.put(tubeKey, tubeLocation);
                nameToConc.replace(tubeKey, tubeConc);
            }
        }
        // If nameToLoc HashMap does not contain a tube with the same label or composition, add the tube to both
        // HashMaps
        else {
            nameToLoc.put(tubeKey, tubeLocation);
            nameToConc.put(tubeKey, tubeConc);
        }

    }

    // Turns string representation of concentration into an int representation
    // If string is null, concentration is assumed to be 0
    // If string is not an explicit concentration in uM, then it is assumed to be 100 um
    public int StringToConcentration(String tubeConcentration) {
        if (tubeConcentration == null || tubeConcentration.isEmpty()) {
            return 0;
        }
        int spaceIndex = tubeConcentration.indexOf(" ");
        if (spaceIndex == -1) {
            return 0;
        }
        //If the concentration in the box is not an explicit concentration in uM, assume a standard stock of 100uM
        try {
            return Integer.valueOf(tubeConcentration.substring(0, spaceIndex));
        } catch (NumberFormatException e) {
            return 100;
        }
    }

    // Returns tube attribute for a specified position in a single row
    public Object findTubeAttribute(Object [] line, int position) {
        if (position >= line.length) {
            return null;
        }
        return line[position];
    }

    // Finds data by looking for specified header
    public Integer findStart(String[] lines, String keyword) {
        for (int x = 1; x < lines.length; x ++) {
            if (lines[x].startsWith(keyword)) {
                return x;
            }
        }
        return 120;
    }

    public String  findTube(String label) {
        return nameToLoc.get(label);
    }

    public void removeTube(int row, int col) throws Exception {
        if (slots[row][col] == null) {
            throw new Exception("Slot is already empty");
        }
        slots[row][col] = null;
    }

    public void addTube(Tube t, int row, int col) throws Exception {
        if (slots[row][col] != null) {
            throw new Exception("Slot is already filled with another tube");
        }
        slots[row][col] = t;
        processSlot(t.getLabel(), t.getComp(), t.getConcetration(), row, col, this.name.contains("oligos"));
        boolean lastSlotFilled = lastSlotFilled();
    }

    public boolean lastSlotFilled() {
        if (findEmptySlot() == null) {
            parentInvetory.getBoxesWithOpenSlots().remove(this);
            return true;
        }
        return false;
    }
    private String[] checkFileForAttribute(String[] lines, int position) {
        String[] output = new String[1];
        output[0] = null;
        if (position >= 121) {
            return output;
        }
        return lines[position].split("\t");
    }

    public String findEmptySlot() {
        Iterator iter = emptySlots.iterator();
        while (iter.hasNext()) {
            return (String) iter.next();
        }
        return null;
    }
    private String getLetterForRow(int row) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        if (row > 25) {
            return null;
        }
        return Character.toString(alphabet[row]);
    }


    public static void testBox(Tube [][] boxSlots, int num, char letter) {

        HashMap<Character, Integer> charToInt = new HashMap();
        charToInt.put('A', 0);
        charToInt.put('B', 1);
        charToInt.put('C', 2);
        charToInt.put('D', 3);
        charToInt.put('E', 4);
        charToInt.put('F', 5);
        charToInt.put('G', 6);
        charToInt.put('H', 7);
        charToInt.put('I', 8);

        int[] testCoordinates = new int[2];
        testCoordinates[0] = num - 1;
        testCoordinates[1] = charToInt.get(letter);


        Tube curr = boxSlots[testCoordinates[0]][testCoordinates[1]];

        System.out.println("Testing Tube at the coordinates: " + num + letter);
        System.out.println("The tube's label is " + curr.getLabel());
        System.out.println("The tube's composition is " + curr.getComp());
        System.out.println("The tube's concentration is " + curr.getConcetration());
    }


    //run example
    public static void main(String[] args) throws Exception{
        Inventory parent = new Inventory();
        HashMap nL = new HashMap();
        Box a = new Box("inventory/BoxA.txt", nL,nL, parent);
        testBox(a.slots, 1 ,'C');

    }
}
