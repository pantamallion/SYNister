package SYNister.model;

import SYNister.model.StepModels.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Model of Construction File
 *
 * @author J. Christopher Anderson
 */
public class ConstructionFile {
    private final List<Step> steps;

    public ConstructionFile(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }
    
    public static void main(String[] args) {
        //Hard-code a ConstructionFile describing a CRISPR Experiment
        List<Step> steps = new ArrayList<>();
        
        //>Construction of pTarg-amilGFP1
        String pdtName = "pTarg-amilGFP";
        
        ////acquire oligo ca4238,ca4239
        steps.add(new Acquisition("ca4238"));
        steps.add(new Acquisition("ca4239"));
        steps.add(new Acquisition("pTargetF"));
        
        //pcr ca4238,ca4239 on pTargetF	(3927 bp, ipcr)
        steps.add(new PCR("ca4238", "ca4239", "pTargetF", "ipcr"));
        
        //cleanup ipcr	(pcr)
        steps.add(new Cleanup("ipcr", "pcr"));
        
        //digest pcr with SpeI,DpnI	(spedig)
        List<Enzyme> enzymes = new ArrayList<>();
        enzymes.add(Enzyme.SpeI);
        enzymes.add(Enzyme.DpnI);
        steps.add(new Digestion("pcr", enzymes, "spedig", 200));
        
        //cleanup spedig	(dig)
        steps.add(new Cleanup("spedig", "dig"));
        
        //ligate dig	(lig)
        List<String> digs = new ArrayList<>();
        digs.add("dig");
        steps.add(new Ligation(digs, "lig"));
        
        //transform lig	(DH10B, Spec)
        steps.add(new Transformation("lig", "DH10B", Antibiotic.Spec, pdtName));
        
        //Instantiate the Construction File
        ConstructionFile constf = new ConstructionFile(steps);
        
        //Print it out
        for(Step astep : constf.getSteps()) {
            System.out.println(astep.getOperation() + "   " + astep.getProduct() + "   " +  astep.getClass().toString());
        }
    }
}
