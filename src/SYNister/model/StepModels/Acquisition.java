package SYNister.model.StepModels;

/**
 * A Step that contains the information for an Acquisition
 * Currently no longer in use
 *
 * @author J. Christopher Anderson
 */
public class Acquisition implements Step {
    private final String dnaName;
    
    public Acquisition(String dnaName) {
        this.dnaName = dnaName;
    }

    @Override
    public Operation getOperation() {
        return Operation.acquire;
    }

    @Override
    public String getProduct() {
        return dnaName;
    }
}
