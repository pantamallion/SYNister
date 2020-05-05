package SYNister.InventoryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Model of Inventory
 *
 * @author Rishi Patel
 */

public class Inventory {
    private ArrayList<Box> allBoxes;
    private HashMap<String, Integer> boxPosition;
    private HashMap<String, String> nameToLoc;
    private HashMap<String, String> nameToConc;
    private HashSet<Box> boxesWithOpenSlots;

    public Inventory() {
        allBoxes = new ArrayList<>();
        boxPosition = new HashMap<>();
        nameToLoc = new HashMap<>();
        nameToConc = new HashMap<>();
        boxesWithOpenSlots = new HashSet<>();
    }

    public void addBox(String filename) throws Exception {
        Box newBox = new Box(filename, nameToLoc, nameToConc, this);
        boxPosition.put(newBox.getName(), allBoxes.size());
        allBoxes.add(newBox);
        if (newBox.findEmptySlot() != null) {
            boxesWithOpenSlots.add(newBox);
        }
    }

    public Box getBox(String boxName) {
        int pos = boxPosition.get(boxName).intValue();
        return allBoxes.get(pos);
    }

    public String findTube (String label) {
        return nameToLoc.get(label);
    }

    public String findConc (String label) {
        return nameToConc.get(label);
    }

    public String fillEmptySlot(Tube t) throws Exception{
        Iterator iter = boxesWithOpenSlots.iterator();
        Box openBox = null;
        String emptySlot = null;
        if (iter.hasNext()) {
            openBox = (Box) iter.next();
            emptySlot = openBox.findEmptySlot();
        }
        int slashPos = emptySlot.indexOf("/");
        int row = Integer.valueOf(getNumberForLetter(emptySlot.charAt(slashPos + 1)));
        int col = Integer.valueOf(emptySlot.charAt(slashPos + 2));

        openBox.addTube(t, row, col);
        return emptySlot;
    }

    private int getNumberForLetter(char col) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int count = 0;
        while (count < 0) {
            if (alphabet[count] == col) {
                return count;
            }
        }
        throw new IllegalArgumentException("Argument must be a capital letter from A-Z");

    }

    public HashSet<Box> getBoxesWithOpenSlots() {
        return boxesWithOpenSlots;
    }
}
