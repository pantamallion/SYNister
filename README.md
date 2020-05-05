![SYNister Logo](/READMEphotos/SYNister.png)
# SYNister: Labsheet Generator
Description
## How to use
1. Download SYNister.jar file
2. Click to run, the following window will appear:  
![interface1](/READMEphotos/interface.png)    
3. Fill in full paths to inventory folder, folder of relevant construction files, and desired output location (as shown above)
4. Click "Generate Labsheets"
5. Repeat, editting relevant fields, as necessary
  
Example inventory folder, input folders, and outputs can be seen in /src/SYNister/Example  
### Inputs
#### Inventory
explain
![inv](/READMEphotos/inventory.png)  
Example of a valid inventory can be seen in /src/SYNister/Example/inventory
#### Construction Files
The construction file folder (the "Input" field) should be a folder containing only the .txt construction files which will be used to construct a single set of lab sheets. For example:  
![inputfolder1](/READMEphotos/inputfolder.png)   
   
Each construction file should be formatted as a list of steps. Lines starting with ">" or "//" will be ignored (and thus can be used for comments). Any text below a broken line ("---") will be ignored, and thus can be used to specify oligos for human reference.  
The following steps are supported with the given formats (replace bolded sections with specific items):  
 * pcr *primer1*,*primer2* on *template*		(*productLength*bp, *product*)
   * All unique pcr products must have unique names
   * If template is genomic, should end in “-gen”
   * If product already exists in the inventory, SYNister will not repeat the pcr
 * assemble *oligo1*,*oligo2*,*...*				(*enzyme*, *product*)
   * Assumes Golden Gate Assembly
   * If Gibson Assembly “enzyme” = “gibson”
 * digest *oligo* with *enzyme1*/*enzyme2*/*...*	(*product*)
 * ligate *digest*							(*product*)
 * transform *plasmid*						(*strain*, *antibiotic*)
 * pca *oligo1*,*oligo2*,*...* 					(*product*)    
Note: "acquire," "cleanup," and "gel" steps do not need to be specified and will be added where needed. If they are specified, they will be ignored.    
   
For example:   
![input1](/READMEphotos/input.png)  
 
    
More examples of valid input folders and constrcution files can be seen in /src/SYNister/Example/ConstructionFiles  
### Outputs
explain
More examples of outputs can be seen in /src/SYNister/Example/Output
## Troubleshooting
Error messages displayed under button  
![error](/READMEphotos/interface_error.png)  
Else check contruction file  
## Future plans
### ConstructionFileBuilder
![cfbuilders](/READMEphotos/ProcessConstructionFiles.png)   
explain
### Drag and drop GUI