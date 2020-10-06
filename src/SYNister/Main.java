package SYNister;

import SYNister.model.ProcessConstructionFile;
import SYNister.model.SheetModels.LabPacket;

import java.io.File;


/**
 * User runs this file
 * @author Arnav Raha
 */

public class Main {

    private ProcessConstructionFile processor = new ProcessConstructionFile();
    private SheetWriter writer = new SheetWriter();
    private boolean initiated = false;

    /**
     * Initiates ProcessConstructionFile with given inventory,
     * and SheetWriter.
     * @param inventoryPath full path to inventory
     */
    public void initiate(String inventoryPath) throws Exception {
        processor.initiate(inventoryPath); //initiate construction file processor
        writer.initiate();//initiate file writer
        this.initiated = true;
    }

    /**
     * Returns whether or not Main.initiate() has been run
     * at least once.
     */
    public boolean isInit(){
        return this.initiated;
    }

    /**
     * Processes construction files and writes out labsheets
     * @param inputFilePath full path to set of construction files
     * @param outputFilePath fulll path to desired output location
     */
    public void run(String inputFilePath, String outputFilePath) throws Exception {
        LabPacket lp = processor.run(inputFilePath);
        writer.run(lp, outputFilePath);
    }

    /** Runs example of process */
    public static void main(String[] args) throws Exception{

        //change these to desired
        String inventory = "Example/inventory";
        String input = "Example/ConstructionFiles/";
        String output = "Example/Output/";
        inventory = new File(inventory).toURI().toString().substring(5); //converts local file path to fill path
        input = new File(input).toURI().toString().substring(5); //converts local file path to fill path
        output = new File(output).toURI().toString().substring(5); //converts local file path to fill path

        //initiation
        Main example = new Main();
        example.initiate(inventory);


        //running on all files in Examples/ConstructionFiles, change as desired
        String cfA = "A_construction_files";
        String cfK = "K_construction_files";
        String cfL = "L_construction_files";
        String cfO = "aspC";
        String cf1 = "cscR";
        String cf2 = "tyrB";
        String cf3 = "ilvE";
        String[] cfs = new String[] {cfA, cfK, cfL, cfO, cf1, cf2, cf3};
        for (String constfile : cfs) {
            System.out.println(constfile+" started");
            example.run(input+constfile,output);
            System.out.println("\t"+constfile+" ended");
        }
        String constfile = "Lycopene9";
        System.out.println(constfile);
        example.run(input+constfile,output);
        constfile = "Lycopene8_14UHLSOM";
        System.out.println(constfile);
        example.run(input+constfile,output);
        for (int i = 1; i < 11; i++) {
            if (i==8 || i==9) {
                continue;
            }
            System.out.println("Lyc"+i);
            example.run(input+"Lyc"+i,output);
        }
        System.out.println("pCSC1");
        example.run(input+"pCSC1",output);

    }
}
