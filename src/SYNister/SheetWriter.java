package SYNister;

import SYNister.model.SheetModels.AssemblySheet;
import SYNister.model.SheetModels.LabPacket;
import SYNister.model.SheetModels.LabSheet;

import java.io.File;
import java.io.PrintWriter;

/**
 * Takes a LabPacket and writes it to a file
 *
 * @author Arnav Raha
 */

public class SheetWriter {

    private PrintWriter writer;
    private File file;

    /**
     * Initiates SheetWriter, currently does nothing
     */
    public void initiate() throws Exception{
    }

    /**
     * Prints LapPacket into final lab sheets as a .doc
     * w/ formatting
     * @param lp LabPacket containing all necessary sheets in order
     * @param outputPath desired output file
     */
    public void run(LabPacket lp, String outputPath) throws Exception {

        StringBuilder text = new StringBuilder();

        //First page displays experiment name
        text.append("LABSHEETS FOR EXPERIMENT: ");
        text.append(lp.getTitle());
        text.append(Character.toString((char) 12)); //page break

        //creating output file and writer to said file
        file = new File(outputPath+"/"+lp.getTitle()+".doc");
        writer = new PrintWriter(outputPath+"/"+lp.getTitle()+".doc", "UTF-8");

        //loops through each labsheet in lp and adds text to string
        int index = 1;
        for (LabSheet ls : lp.getAllPages()) {

            //labels the step at top of every sheet
            text.append("Step ");
            text.append(index);
            text.append(" of ");
            text.append(lp.getAllPages().size());
            text.append(": ");
            text.append(ls.getTitle());
            text.append("\n\n");

            //if sheet has specified field, format and add it to string
            // fields in order: protocol, master mix, samples, sources, destination
            //                    program, DestTable (see below), note
            if (ls.getProtocol() != null) {
                text.append("protocol: ");
                text.append(ls.getProtocol());
                text.append("\n\n");

            }

            if (ls.getMasterMix() != null) {
                text.append(ls.getMasterMix());
            }

            text.append("samples:\n");
            for (String[] line : ls.getSampleTable()) {
                int i = 0;
                for (String word : line) {
                    if(word == null) {
                        word = "  ";
                    }

                    if (ls instanceof AssemblySheet) {
                        if (i==1) {
                            text.append(String.format("%-27s", word));
                        } else {
                            text.append(String.format("%-10s", word));
                        }
                        i++;

                    } else {
                        text.append(String.format("%-15s", word));
                    }
                }
                text.append("\n");
            }

            text.append("\nsource:\n");
            for (String[] line : ls.getSourceTable()) {

                for (String word : line) {
                    if(word == null) {
                        word = "  ";
                    }
                    text.append(String.format("%-17s", word));
                }
                text.append("\n");
            }

            if (ls.getDestination() != null) {
                text.append("\ndestination: ");
                text.append(ls.getDestination());
                text.append("\n");
            }

            if (ls.getProgram() != null) {
                text.append("program: ");
                text.append(ls.getProgram());
                text.append("\n");

            }

            //for pcr, destination for newly created diluted oligos
            //   as opposed to "actual" step destination: thermocycler
            if (ls.getDestTable() != null) {
                text.append("\ndestinations:\n");
                for (String[] line : ls.getDestTable()) {

                    for (String word : line) {
                        if (word == null) {
                            word = "  ";
                        }
                        text.append(String.format("%-20s", word));
                    }
                    text.append("\n");
                }
            }

            if(ls.getNote() != null) {
                text.append("\n");
                text.append(ls.getNote());
                text.append("\n");
            }


            index++;
            text.append(Character.toString((char) 12)); //page break

        }

        //Final page displays experiment name
        text.append("END OF LAB SHEETS FOR EXPERIMENT ");
        text.append(lp.getTitle());

        //writes string to file
        writer.write(text.toString());
        writer.close();
    }
}
