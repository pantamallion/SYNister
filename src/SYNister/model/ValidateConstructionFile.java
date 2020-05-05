package SYNister.model;

import java.util.HashSet;
import java.util.Set;
import SYNister.model.StepModels.*;

/**
 *(!!!!!!!!!  not functional (!!!!!!!!!  )
 * 
 * @author J. Christopher Anderson
 */
public class ValidateConstructionFile {
    public void initiate() throws Exception {}
    
    /**
     * Test whether all variables described refer to reachable things
     * (not completed)
     * 
     * @param constf
     * @return
     * @throws Exception 
     */
    public boolean run(ConstructionFile constf) throws Exception {
        //Gather up all tokens that are arguments
        Set<String> dependencies = new HashSet<>();
        for(Step astep : constf.getSteps()) {
            switch(astep.getOperation()) {
                case acquire:
                    Acquisition acq = (Acquisition) astep;
                    dependencies.add(acq.getProduct());
                    break;
                case pcr:
                    PCR pcr = (PCR) astep;
                    dependencies.add(pcr.getOligo1());
                    dependencies.add(pcr.getOligo2());
                    dependencies.add(pcr.getTemplate());
                    break;
                case pca:
                    PCA pca = (PCA) astep;
                    dependencies.addAll(pca.getOligoPool());
                    break;
                case digest:
                    Digestion dig = (Digestion) astep;
                    dependencies.add(dig.getSubstrate());
                    break;
                case ligate:
                    Ligation lig = (Ligation) astep;
                    dependencies.addAll(lig.getFragments());
                    break;
                case transform:
                    Transformation trans = (Transformation) astep;
                    break;
                case cleanup:
                    Cleanup cleanup = (Cleanup) astep;
                    break;
                case assemble:
                    Assembly assem = (Assembly) astep;
                    break;
            }
        }

        return true;
    }
    
    public static void main(String[] args) throws Exception {
        //Initializze the Function
        ValidateConstructionFile serilaizer = new ValidateConstructionFile();
        serilaizer.initiate();
        CrisprConstructionFactory factory = new CrisprConstructionFactory();
        factory.initiate();
        
        //Run the factory
        ConstructionFile constf = factory.run("fb21", "fb22", "pTarg-amilGFP");
    
        //Validate it
        boolean result = serilaizer.run(constf);
        System.out.println(result);
    }
}
