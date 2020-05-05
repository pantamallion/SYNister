package SYNister.model;

import SYNister.InventoryModel.Inventory;
import SYNister.model.SheetModels.*;
import SYNister.model.StepModels.*;
import SYNister.utils.FileUtils;

import java.io.File;
import java.util.*;

/**
 * Program to take in raw construction file data, process it, and
 * output a LabPacket Model (a collection of LabSheets for the steps
 * encoded in a set of construction files.
 *
 * @author Stephen Lin
 * @author Arnav Raha
 */

public class ProcessConstructionFile {

    private ParseConstructionFile parser;
    private Inventory inventory;
    private String title;

    /**
     *
     * Set the target for the inventory stock -
     * this folder will be searched for samples and their associated
     * locations.
     */
    public void initiate(String inventoryPath) throws Exception {
        parser = new ParseConstructionFile();
        parser.initiate();
        inventory = new Inventory();
        File folder = new File(inventoryPath);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()) {
                String name = file.getName();
                if (name.charAt(0) != '.') {
                    inventory.addBox(inventoryPath+name);
                }
            }
        }
    }

    /**
     * Given a pathName, process each construction file (in the form
     * of raw text) and return a LabPacket data structure containing
     * the information and ready to be outputted in a .doc file or similar.
     * @param pathName folder name of construction files
     * @return labPacket containing LabSheets
     */
    public LabPacket run(String pathName) throws Exception {

        File folder = new File(pathName);
        File[] constructionFilesList = folder.listFiles();
        //Ensure that the folder name is valid
        if (constructionFilesList == null) {
            throw new IllegalArgumentException(pathName + " is not a valid directory");
        }
        //Get the list of construction files to be processed
        List<String> constructionFilesNames = new ArrayList<>();
        //Store necessary labels
        for (File file : constructionFilesList) {
            if (file.isFile()) {
                String name = file.getName();
                if (name.charAt(0) != '.') {
                    constructionFilesNames.add(name);
                }
            }
        }

        //Contain the ordering of steps for each construction file
        List<List<Step>> allSteps = new ArrayList<>();

        //if construction file lists these steps, ignore them
        HashSet<String> ignoreSteps = new HashSet<>();
        ignoreSteps.add("acquire");
        ignoreSteps.add("cleanup");
        ignoreSteps.add("zymo");
        ignoreSteps.add("gel");

        //Parse each construction file in lexicographic order
        java.util.Collections.sort(constructionFilesNames);
        for (String fileName : constructionFilesNames) {
            //System.out.println(fileName);

            String constructionFileData = FileUtils.readFile(pathName + '/' + fileName);
            StringBuilder sb = new StringBuilder();
            String[] lines = constructionFileData.split("\\r|\\r?\\n");
            int stepCount = 0;
            for (String line: lines) {

                //Terminate before reading FASTA sequences
                if (line.startsWith("-")) {
                    break;
                }
                //Only consider lines that could be valid steps
                if (!line.startsWith(">") && !line.startsWith("//")
                        && (line.length()>0)
                        && ignoreSteps.contains(line.split("\\s+")[0].toLowerCase())) {
                    continue;
                }
                sb.append(line).append('\n');
                stepCount++;
            }

            if (allSteps.size() < stepCount) {
                for (int i = 0; i < stepCount; i++) {
                    allSteps.add(new ArrayList<>());
                }
            }

            //Creates List of Steps
            List<Step> cfSteps = parser.run(sb.toString()).getSteps();
            int emptyStep = 0;
            outer:
            for (Step step : cfSteps) {
                for (int i = 0; i < emptyStep; i++) {
                    if (step.getOperation() == allSteps.get(i).get(0).getOperation()) {
                        allSteps.get(i).add(step);
                        continue outer;
                    }
                }
                allSteps.get(emptyStep).add(step);
                emptyStep++;
            }
        }

        //Default labsheet title is first String of folderName (the construction files folder)
        this.title = pathName.split("/")[pathName.split("/").length-1];

        LabPacket labPacket = generateLabPacket(allSteps, this.title);

        return labPacket;
    }


    /**
     * Given a List of List of Steps allSteps, generate a new LabPacket named
     * title containing the Steps (possibly multiple if multiple construction
     * files are being processed) in each List of allSteps.
     * @param allSteps List of List of Steps, or the construction file(s) data
     * @param title Title of the lab packet to be generated
     * @return labPacket containing LabSheets
     */
    @SuppressWarnings("unchecked")
    private LabPacket generateLabPacket(List<List<Step>> allSteps, String title) {
        LabPacket labPacket = new LabPacket(title);

        uniqueProductNames(allSteps); //validates unique Steps have unique product names to avoid colllisions
        processPCRSteps(allSteps); //sorts PCRs by protocol and adds Gel and Zymo steps
        processDigestSteps(allSteps); //adds Zymo steps

        //Creating sheet for each List of steps
        String sourceZymoGel = ""; //source of Zymo & Gel should be inferred from ordering of sheets
        for (List<Step> stepList : allSteps) {
            LabSheet sheet;
            Step s = stepList.get(0);
            if (s instanceof Acquisition) {
                continue;
            }
            if (s instanceof PCR) {
                List<PCR> PCRList = (List<PCR>)(List<?>) stepList;
                sheet = generatePCRSheet(PCRList);
                sourceZymoGel = sheet.getDestination(); //next Zymo & Gel source is PCR destination
            } else if (s instanceof Cleanup) {
                List<Cleanup> cleanupList = (List<Cleanup>)(List<?>) stepList;
                sheet = generateCleanupSheet(cleanupList, sourceZymoGel);
                sourceZymoGel = ""; //clear Zymo & Gel source location
            } else if (s instanceof Digestion) {
                List<Digestion> digestList = (List<Digestion>)(List<?>) stepList;
                sheet = generateDigestSheet(digestList);
                sourceZymoGel = sheet.getDestination(); //next Zymo source is Digestion destination
            } else if (s instanceof Ligation) {
                List<Ligation> ligateList = (List<Ligation>)(List<?>) stepList;
                sheet = generateLigateSheet(ligateList);
                sourceZymoGel = sheet.getDestination(); //next Zymo source is Ligation destination
            } else if (s instanceof Transformation) {
                List<Transformation> transformList = (List<Transformation>)(List<?>) stepList;
                sheet = generateTransformSheet(transformList);
            } else if (s instanceof Assembly) {
                List<Assembly> assembleList = (List<Assembly>)(List<?>) stepList;
                validateAssembly(assembleList);
                sheet = generateAssemblySheet(assembleList);
            } else if (s instanceof Gel) {
                List<Gel> gelList = (List<Gel>)(List<?>) stepList;
                sheet = generateGelSheet(gelList, sourceZymoGel);
            } else {
                //This version of code only supports the above Steps
                throw new IllegalArgumentException("allSteps contains unimplemented Step type. Cannot create labsheet.");
            }
            labPacket.addPage(sheet);
        }

        return labPacket;
    }

    /**
     * Given a List of PCR Steps, return a corresponding PCRSheet
     * @param PCRList List of PCR Steps
     * @return sheet - a PCRSheet
     */
    private PCRSheet generatePCRSheet(List<PCR> PCRList) {
        PCR[] pcrTable = PCRList.toArray(new PCR[0]);
        PCRSheet sheet = new PCRSheet(pcrTable, inventory, this.title);
        return sheet;
    }

    /**
     * Given a List of Cleanup Steps, return a corresponding CleanupSheet
     * @param cleanupSteps List of Cleanup Steps
     * @return a CleanupSheet
     */
    private CleanupSheet generateCleanupSheet(List<Cleanup> cleanupSteps, String source) {
        Cleanup[] cleanupTable = cleanupSteps.toArray(new Cleanup[0]);
        return new CleanupSheet(cleanupTable, source, this.title);
    }

    /**
     * Given a List of Digestion Steps, return a corresponding DigestSheet
     * @param digestList List of Digestion Steps
     * @return a DigestSheet
     */
    private DigestSheet generateDigestSheet(List<Digestion> digestList) {
        Digestion[] digestTable = digestList.toArray(new Digestion[0]);
        return new DigestSheet(digestTable, " ", this.title, inventory);
    }

    /**
     * Given a List of Ligation Steps, return a corresponding LigateSheet
     * @param ligationSteps List of Ligation Steps
     * @return a LigateSheet
     */
    private LigateSheet generateLigateSheet(List<Ligation> ligationSteps) {
        Ligation[] ligateTable = ligationSteps.toArray(new Ligation[0]);
        //Default program is main/LIGATE
        return new LigateSheet(ligateTable,  this.title, inventory);
    }

    /**
     * Given a List of Transform Steps, return a corresponding TransformSheet
     * @param transformSteps List of Transform Steps
     * @return a TransformSheet
     */
    private TransformSheet generateTransformSheet(List<Transformation> transformSteps) {
        Transformation[] transformTable = transformSteps.toArray(new Transformation[0]);
        //Default source is thermocycler
        return new TransformSheet(transformTable, this.title, this.inventory);
    }

    /**
     * Given a List of Assembly Steps, return a corresponding AssemblySheet
     * @param assemblySteps List of Transform Steps
     * @return an AssemblySheet
     */
    private AssemblySheet generateAssemblySheet(List<Assembly> assemblySteps) {
        Assembly[] assembleTable = assemblySteps.toArray(new Assembly[0]);
        //Default destination is thermocycler
        return new AssemblySheet(assembleTable, this.title, inventory);
    }

    /**
     * Given a List of Gel Steps, return a corresponding GelSheet
     * @param gelSteps List of Transform Steps
     * @return an AssemblySheet
     */
    private GelSheet generateGelSheet(List<Gel> gelSteps, String source) {
        Gel[] gelTable = gelSteps.toArray(new Gel[0]);
        return new GelSheet(gelTable, source, this.title);
    }

    /**
     * Given a List of Assembly Steps, validates all steps use the same enzyme
     * @param assemblySteps List of Transform Steps
     * @return null
     */
    private void validateAssembly(List<Assembly> assemblySteps) {
        Enzyme e = assemblySteps.get(0).getEnzyme();
        for (Assembly a : assemblySteps) {
            if (e != a.getEnzyme()) {
                throw new IllegalArgumentException("All assembly steps should use the same enzymes");
            }
        }

    }

    /**
     * Given all steps, sorts PCR by protocol and adds gel and zymo steps
     * Removes pcr step if product already in inventory
     * @param allSteps List of Lists of Steps
     * @return null
     */
    private void processPCRSteps(List<List<Step>> allSteps) {

        ArrayList<Step>[] pcrs = (ArrayList<Step>[])new ArrayList[7];
        ArrayList<Step>[] zymos = (ArrayList<Step>[])new ArrayList[7];
        ArrayList<Step>[] gels = (ArrayList<Step>[])new ArrayList[7];
        for (int i = 0; i < 7; i++){
            pcrs[i] = new ArrayList<>();
            zymos[i] = new ArrayList<>();
            gels[i] = new ArrayList<>();

        }

        List<Step> unnecessaryPCR = new ArrayList<>();
        for (List<Step> stepList : allSteps) {
            for (Step s : stepList) {
                if (s instanceof PCR) {
                    if (inventory.findTube(s.getProduct()) != null) {
                        unnecessaryPCR.add(s);
                    }
                }
            }
            for (Step r : unnecessaryPCR) {
                stepList.remove(r);
            }
        }

        ArrayList<List<Step>> remove = new ArrayList<List<Step>>();
        for (List<Step> stepList : allSteps) {
            if (stepList.size() == 0) {
                remove.add(stepList);
            }
        }
        for (List<Step> r : remove) {
            allSteps.remove(r);
        }
        remove.clear();

        //Sort by protocol
        for (List<Step> stepList : allSteps) {
            for (Step s : stepList) {
                if(s instanceof PCR){
                    PCR p = (PCR) s;
                    boolean repeat = false;
                    for (ArrayList<Step> cat : pcrs) {
                        for (Step item : cat) {
                            if (p.getProduct().equals(item.getProduct())) {
                                repeat = true;
                            }
                        }
                    }

                    if (repeat) {
                        continue;
                    }

                    switch ( p.getType() ) {
                        case NGSMALL: pcrs[0].add(p); break;
                        case NGMED: pcrs[1].add(p); break;
                        case NGBIG: pcrs[2].add(p); break;
                        case GSMALL: pcrs[3].add(p); break;
                        case GMED: pcrs[4].add(p); break;
                        case GBIG: pcrs[5].add(p); break;
                        default: pcrs[6].add(p); break;
                    }
                    remove.add(stepList);
                }
            }
        }

        //create Zymo steps
        for(int i = 0; i < 7; i++) {
            if(pcrs[i].size() > 0){
                for(Step p : pcrs[i]) {
                    zymos[i].add(new Cleanup(p.getProduct(), p.getProduct()));
                }
            }
        }

        //create gel steps
        for(int i = 0; i < 7; i++) {
            if(pcrs[i].size() > 0){
                for(Step p : pcrs[i]) {
                    gels[i].add(new Gel(p.getProduct(), p.getProduct(), ((PCR)p).getProductLength()));
                }
            }
        }


        //remove steps where needed
        int lastIndex = 0;
        for (int i = 1; i < allSteps.size(); i++) {
            if (allSteps.get(i).get(0).getClass().equals(allSteps.get(lastIndex).get(0).getClass())) {
                allSteps.get(lastIndex).addAll(allSteps.get(i));
                remove.add(allSteps.get(i));
            } else {
                lastIndex=i;
            }

        }
        for (List<Step> r : remove) {
            allSteps.remove(r);
        }

        //add pcrs, zymos, gels to allSteps
        for (int i = 6; i >= 0; i--) {
            if (zymos[i].size() > 0) {
                allSteps.add(0, zymos[i]);
            }
        }
        for (int i = 6; i >= 0; i--) {
            if (gels[i].size() > 0) {
                allSteps.add(0, gels[i]);
            }
        }
        for (int i = 6; i >= 0; i--) {
            if (pcrs[i].size() > 0) {
                allSteps.add(0, pcrs[i]);
            }
        }
    }

    /**
     * Adds zymos after Digest steps
     * @param allSteps List of Lists of Steps
     * @return null
     */
    private void processDigestSteps(List<List<Step>> allSteps) {
        List<List<Step>> temp = new ArrayList();
        temp.addAll(allSteps);
        allSteps.clear();

        for (List<Step> steps : temp){
            allSteps.add(steps);
                List<Step> zymoStep = new ArrayList<>();
                if (steps.get(0) instanceof Digestion) {
                    for (Step s : steps) {
                        zymoStep.add(new Cleanup(s.getProduct(), s.getProduct()));
                    }
                    allSteps.add(zymoStep);
                }
            }
    }

    //checks that product names are unique, else throw error
    private void uniqueProductNames(List<List<Step>> allSteps) {
        for (List<Step> stepList : allSteps) {
            HashSet<String> products = new HashSet<>();
            for (Step s : stepList) {
                products.add(s.getProduct());
            }
            if (products.size() != stepList.size()) {
                if (stepList.get(0).getOperation() == Operation.pcr) {
                    if (!uniquePCR(stepList)) {return;}
                }
                String errorMessage ="All product names must be unique. Check: "+stepList.get(0).getOperation().toString();
                throw new IllegalArgumentException(errorMessage);
            }
        }

    }

    //checks if all PCR steps in a list are unique
    // (i.e. that the sets of oligos, template, and product are all different)
    private boolean uniquePCR(List<Step> pcrs) {
        for (int i = 0; i < pcrs.size()-1; i++) {
            PCR a = (PCR) pcrs.get(i);
            for (int j = i+1; j < pcrs.size(); j++) {
                PCR b = (PCR) pcrs.get(j);
                boolean sameOligos = a.getOligo1().equals(b.getOligo1()) && a.getOligo2().equals(b.getOligo2())
                        || a.getOligo2().equals(b.getOligo1()) && a.getOligo1().equals(b.getOligo2());
                boolean sameTemplate = a.getTemplate().equals(b.getTemplate());
                boolean sameProducts = a.getProduct().equals(b.getProduct());

                if (sameOligos && sameProducts && sameTemplate) {
                    return false;
                }
            }
        }
        return true;
    }

}
