package SYNister.model.SheetModels;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

import SYNister.InventoryModel.Inventory;
import SYNister.model.StepModels.*;
import java.util.List;


/**
 * Packet of LabSheets,
 * informataion here is directly used to print lab sheet docs
 *
 * @author niharikadesaraju
 * @author Arnav Raha
 */
public class LabPacket {
    private String title;
    private List<LabSheet> pages;
    private List<String> labels;
    
    public LabPacket(String title) {
        this.title = title;
        pages = new ArrayList<>();
    }

    public void addPage(LabSheet sheet) {
        pages.add(sheet);
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getLabels() {
        return labels;
    }

    public String getTitle() {
        return this.title;
    }
    
    public List<LabSheet> getAllPages() {
        return this.pages;
    }
    
    public LabSheet getPage(int pageNumber) {
        return this.pages.get(pageNumber);
    }

    public void fillLocations(){
        //step1 recheck inventory
        //step2 check previous steps
    }

    //prints list of sheets in packet
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Labsheet ").append(title).append(": ");
        for (LabSheet s : pages) {
            sb.append(s.getClass().getSimpleName());
            if (pages.lastIndexOf(s) < pages.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }


    //example
    public static void main(String[] args) throws Exception {
        Inventory inv = new Inventory();
        //creating PCRSheet
        PCR pcr1 = new PCR("cscB1", "pTargRev", "pTargetF", "pTarget-cscB1/ipcr");
        PCR pcr2 = new PCR("cscB2", "pTargRev", "pTargetF", "pTarget-cscB2/ipcr");
        PCR[] p = {pcr1, pcr2};
        PCRSheet psheet = new PCRSheet(p,  inv, " ");
        
        //creating Digestion Sheet
        Enzyme aari = Enzyme.AarI;
        Enzyme bbsi = Enzyme.BbsI;
        List<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(aari);
        enzymes.add(bbsi);
        Digestion d1 = new Digestion("A1p", enzymes, "pTarget-cscB1/spedig", "A1", "boxA/A1", 200);
        Digestion d2 = new Digestion("A2p", enzymes, "pTarget-cscB1/spedig", "A2", "boxA/A2", 200);
        Digestion d3 = new Digestion("A3p", enzymes, "pTarget-cscB1/spedig", "A3", "boxA/A3", 200);
        Digestion[] d = {d1, d2, d3};       
        DigestSheet dsheet = new DigestSheet(d, " ","A",inv);
        
        //creating ligation sheet
        List<String> fragments = new ArrayList<>();
        fragments.add("idk");
        fragments.add("idk2");
        Ligation l1 = new Ligation(fragments,"pTarget-cscB1/lig", "A1", "boxA/B1");
        Ligation l2 = new Ligation(fragments,"pTarget-cscB2/lig", "A2", "boxA/B2");
        Ligation l3 = new Ligation(fragments,"pTarget-cscB3/lig", "A3", "boxA/B3");
        Ligation[] l = {l1, l2, l3};       
        LigateSheet lsheet = new LigateSheet(l, "A", new Inventory());
        
        //creating tranformation sheet
        Antibiotic spec = Antibiotic.Spec;
        Transformation t1 = new Transformation("dna", "Mach1", spec, "pTarget-cscB1", "A1");
        Transformation t2 = new Transformation("dna", "Mach1", spec, "pTarget-cscB2", "A2");
        Transformation t3 = new Transformation("dna", "Mach1", spec, "pTarget-cscB3", "A3");
        Transformation[] t = {t1, t2, t3};       
        TransformSheet tsheet = new TransformSheet(t,  "A", inv);
        
        LabPacket packet = new LabPacket("cscB CRISPR Knockouts");
        packet.addPage(psheet);
        packet.addPage(dsheet);
        packet.addPage(lsheet);
        packet.addPage(tsheet);
        
        System.out.println("title: " + packet.getTitle());
        System.out.println("sheets: " + packet.getAllPages());
    }
}
