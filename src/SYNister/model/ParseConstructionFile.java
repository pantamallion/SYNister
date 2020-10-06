package SYNister.model;

import SYNister.model.StepModels.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Parses construction file text file and returns
 * a ConstructionFile object
 *
 * @author J. Christopher Anderson
 */
public class ParseConstructionFile {

    public void initiate() throws Exception {
    }

    public ConstructionFile run(String rawText) throws Exception {
        //Replace common unnessary words
        String text = rawText.replace("to ", "");
        text = text.replace("from ", "");
        text = text.replace("on ", "");
        text = text.replace("with ", "");
        text = text.replace("the ", "");
        text = text.replace(" bp", "");
        text = text.replace("bp", "");
        text = text.replaceAll("\\(", "");
        text = text.replaceAll("\\)", "");

        //Break it into lines
        String[] lines = text.split("\\r|\\r?\\n");
        List<Step> steps = new ArrayList<>();

        HashSet<String> ignoreSteps = new HashSet<>();
        ignoreSteps.add("acquire");
        ignoreSteps.add("cleanup");
        ignoreSteps.add("zymo");
        ignoreSteps.add("gel");

        //Process each good line
        for (int i = 0; i < lines.length; i++) {
            String aline = lines[i];
            String[] spaces = aline.split("\\s+");

            //Ignore blank lines
            if (aline.trim().isEmpty()) {
                continue;
            }

            //Ignore commented-out lines and ignore headers
            if (aline.startsWith("//") || aline.startsWith(">")) {
                continue;
            }

            //Ignore acquire, cleanup, and gel steps

            if (ignoreSteps.contains(spaces[0].toLowerCase())) {
                continue;
            }

            //Quit before processing lines indicating FASTA sequences
            if (aline.startsWith("-")) {
                break;
            }


            //Try to parse the operation, if fails will throw Exception

            //Make operation name case-insensitive
            Operation op = Operation.valueOf(spaces[0].toLowerCase());

            //If past the gauntlet, keep the line
            Step parsedStep = parseLine(op, spaces);
            steps.add(parsedStep);
        }

        return new ConstructionFile(steps);
    }

    private Step parseLine(Operation op, String[] spaces) throws Exception {
        switch (op) {
            case pcr:
                if (spaces.length == 5) {
                    return createPCR(
                            spaces[1].split(",|/"),
                            spaces[2],
                            spaces[3].split(",|/")[0],
                            spaces[4]);
                } else {return createPCR(
                        spaces[1].split(",|/"),
                        spaces[2],
                        spaces[3].split(",|/")[0]);}
            case pca:
                return createPCA(
                        spaces[1].split(","),
                        spaces[3]);
            case cleanup:
                return createCleanup(
                        spaces[1],
                        spaces[2]);
            case digest:
                if (spaces.length != 5) {
                    throw new IllegalArgumentException("Digest step should be in the form: " +
                            "digest [substrate] ([Enz1/Enz2/..], [frag_length], [product])");
                }
                return createDigest(
                        spaces[1],
                        spaces[2].split(",|/"),
                        spaces[4],
                        Integer.parseInt(spaces[3].split("[+]")[0]));
            case ligate:
                return createLigation(
                        spaces[1].split(","),
                        spaces[2]);
            case transform:
                return createTransform(
                        spaces[1],
                        spaces[2].replace(",", ""),
                        spaces[3],
                        spaces[1]);
            case acquire:
                return createAcquire(
                        spaces[1].split(","));
            case assemble:

                if (spaces.length == 4) {
                    return createAssemble(
                            spaces[1].split(","),
                            spaces[2].replace(",", ""),
                            spaces[3]
                    );
                } else {
                    throw new IllegalArgumentException("Commas separating oligos should not be followed by a space");
                    /*String[] frags = spaces[1].split(",");
                    String frag1 = frags[0];
                    String frag2 = frags[1];
                    return createAssemble(
                            spaces[1].split(","),
                            spaces[2].replace(",", ""),
                            spaces[3]
                    );*/
                }
            default:
                throw new RuntimeException("Not implemented " + op);
        }

        
    }

    private Step createPCR(String[] oligos, String template, String size, String product) {
        return new PCR(oligos[0], oligos[1], template, product, Integer.parseInt(size));
    }

    private Step createPCR(String[] oligos, String template, String product) {
        return new PCR(oligos[0], oligos[1], template, product);
    }
    
    private Step createPCA(String[] oligos, String product) {
        List<String> frags = new ArrayList<>();
        for (String frag : oligos) {
            frags.add(frag);
        }
        return new PCA(frags, product);
    }
    
    private Step createCleanup(String substrate, String product) {
        return new Cleanup(substrate, product);
    }

    private Step createDigest(String substrate, String[] enzymes, String product, int length) {
        List<Enzyme> enzList = new ArrayList<>();
        for (String enz : enzymes) {
            Enzyme enzyme = Enzyme.valueOf(enz);
            enzList.add(enzyme);
        }
        return new Digestion(substrate, enzList, product, length);
    }

    private Step createLigation(String[] fragments, String product) {
        List<String> frags = new ArrayList<>();
        for (String frag : fragments) {
            frags.add(frag);
        }
        return new Ligation(frags, product);
    }

    private Step createTransform(String substrate, String strain, String antibiotic, String product) {
        Antibiotic ab = Antibiotic.valueOf(antibiotic);
        return new Transformation(substrate, strain, ab, product);
    }

    private Step createAcquire(String[] dnas) {
        return new Acquisition(dnas[0]);
    }

    /**
     * @author Stephen Lin
     */
    private Step createAssemble(String[] frags, String enzymeString, String product) {
        if (enzymeString.toLowerCase().equals("gibson")) {
            enzymeString = "gibson";
        }
        Enzyme enzyme = Enzyme.valueOf(enzymeString);
        List<String> fragments = Arrays.asList(frags);
        return new Assembly(fragments, enzyme, product);
    }

    public static void main(String[] args) throws Exception {
        //CRISPRExample();
        PCAExample();
    }

    public static void CRISPRExample() throws Exception {
        //Initialize the Function
        ParseConstructionFile parser = new ParseConstructionFile();
        parser.initiate();
        SerializeConstructionFile serilaizer = new SerializeConstructionFile();
        serilaizer.initiate();

        String data = ">Construction of pTarg-amilGFP1\n"
                + "acquire ca4238\n"
                + "acquire ca4239\n"
                + "acquire pTargetF\n"
                + "pcr ca4238,ca4239 on pTargetF	(3927 bp, ipcr)\n"
                + "cleanup ipcr	(pcr)\n"
                + "digest pcr with SpeI,DpnI	(spedig)\n"
                + "cleanup spedig	(dig)\n"
                + "ligate dig	(lig)\n"
                + "transform lig	(DH10B, Spec)";

        //Serialize it back
        ConstructionFile constf = parser.run(data);
        String serial = serilaizer.run(constf);
        System.out.println(serial);
    }

    public static void PCAExample() throws Exception {
        //Initializze the Function
        ParseConstructionFile parser = new ParseConstructionFile();
        parser.initiate();
        SerializeConstructionFile serilaizer = new SerializeConstructionFile();
        serilaizer.initiate();

        String data = ">Construction of synthon1\n"
                + "acquire ca4240\n"
                + "acquire ca4241\n"
                + "acquire ca4263\n"
                + "acquire ca4264\n"
                + "acquire pBca9145\n"
                + "\n"
                + "//Synthesize the gene and cut\n"
                + "pca ca4240,ca4241	(1423 bp, pca)\n"
                + "pcr ca4240,ca4241 on pca	(1445 bp, ipcr)\n"
                + "cleanup ipcr	(ipcrc)\n"
                + "digest ipcrc with EcoRI,BamHI	(iDig)\n"
                + "cleanup iDig	(ins)\n"
                + "\n"
                + "//Amplify the plasmid backbone and cut\n"
                + "pcr ca4263,ca4264 on pBca9145	(2532 bp, vpcr)\n"
                + "cleanup vpcr	(vpcrc)\n"
                + "digest vpcrc with EcoRI,BamHI,DpnI	(vDig)\n"
                + "cleanup vDig	(vec)\n"
                + "\n"
                + "//Ligate and transform\n"
                + "ligate ins,vec	(lig)\n"
                + "transform lig	(DH10B, Spec)";

        //Serialize it back
        ConstructionFile constf = parser.run(data);
        String serial = serilaizer.run(constf);
        System.out.println(serial);
    }

}
