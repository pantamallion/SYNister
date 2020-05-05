package SYNister.model.StepModels;

import java.util.List;

/**
 * A Step that contains the information for a PCA
 *
 * @author J. Christopher Anderson
 */
public class PCA implements Step {
    //Loose-coupled references by name
    private final List<String> oligoPool;
    private final String product;

    public PCA(List<String> oligoPool, String product) {
        this.oligoPool = oligoPool;
        this.product = product;
    }

    public List<String> getOligoPool() {
        return oligoPool;
    }


    @Override
    public String getProduct() {
        return product;
    }
    
    @Override
    public Operation getOperation() {
        return Operation.pca;
    }
}
