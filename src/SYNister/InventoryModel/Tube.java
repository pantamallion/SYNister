package SYNister.InventoryModel;

/**
 * Model of Tube
 *
 * @author J. Christopher Anderson
 */
public class Tube {

//    private final Map<String, Double> wellContent;  TODO in future:  encode content of each well
    private Integer volume;
    private String label;
    private String comp;
    private String concetration;

    public Tube(Integer vol, String l, String c, String conc) {
        volume = vol;
        label = l;
        comp = c;
        concetration = conc;
    }
    
    public int getVolume() {
        return volume;
    }
    public String getLabel() {
        return label;
    }
    public String getComp() {
        return comp;
    }
    public String getConcetration() {
        return concetration;
    }
    
    public void addVolume(int amount) throws Exception {
        volume += amount;
    }
    
    public void removeVolume(int amount) throws Exception {
        volume -= amount;
    }


}
