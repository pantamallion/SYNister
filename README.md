![SYNister Logo](/READMEphotos/SYNister.png)
# SYNister: Labsheet Generator
SYNister converts a set of construction files for various DNA products into a single series of wetlab steps, outputted as a set of labsheets.
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
The inventory consists of a folder of TSV files where each file is a box in the inventory. The TSV should contain 3 copies of the same grid. In the first copy, the label of the item in a slot is specified. In the second, the substance is specified. In the third, the concentration is specified.    
![inv3](/READMEphotos/inventory.png)   
Example of a valid inventory can be seen in /src/SYNister/Example/inventory
#### Construction Files
The construction file folder (the "Input" field) should be a folder containing only the .txt construction files which will be used to construct a single set of lab sheets. For example:  
![inputfolder3s](/READMEphotos/inputfolder.png)   
   
Each construction file should be formatted as a list of steps. Lines starting with ">" or "//" will be ignored (and thus can be used for comments). Any text below a broken line ("---") will be ignored, and thus can be used to specify oligos for human reference.  
The following steps are supported with the given formats (replace bolded sections with specific items):  
 * pcr **primer1**,**primer2** on **template**		(**productLength**bp, **product**)
   * All unique pcr products must have unique names
   * If template is genomic, should end in “-gen”
   * If product already exists in the inventory, SYNister will not repeat the pcr
 * assemble **oligo1**,**oligo2**,**...**				(**enzyme**, **product**)
   * Assumes Golden Gate Assembly
   * If Gibson Assembly “enzyme” = “gibson”
 * digest **oligo** with **enzyme1**/**enzyme2**/**...**	(**product**)
 * ligate **digest**							(**product**)
 * transform **plasmid**						(**strain**, **antibiotic**)
 * pca **oligo1**,**oligo2**,**...** 					(**product**)   
 
Note: "acquire," "cleanup," and "gel" steps do not need to be specified and will be added where needed. If they are specified, they will be ignored.    
   
For example:   
![input3](/READMEphotos/input.png)  
 
    
More examples of valid input folders and construction files can be seen in /src/SYNister/Example/ConstructionFiles  
### Outputs
SYNister will output a single .doc of labsheets to be completed in order for the given folder of construction files. Some values (mainly labels and some locations) will be left blank for the user to fill in. **All** prefilled fields should be double-checked, but the fields of main concern are flagged with “[VERIFY].”  
  
Note: The inventory will *not* be updated automatically, and so should be manually editted.
  
Examples of outputs can be seen in /src/SYNister/Example/Output 
## Troubleshooting
Error messages are displayed under the button. For example:     
![error](/READMEphotos/interface_error.png)  
If error cannot be understood from error message, check inputs are in the formats specified above.  

Potential sources of error to keep in mind:
 * ensure steps in construction files sare formatted as specified, with all necessary information
 * ensure all unique DNAs have unique labels   
## Future plans
### Exception handling/more intensive tests
Currently run on a distinct set of unit tests and test inputs, which likely missed various possible scenarios. Greater testing would find these errors. Along with this, not all errors need to be fatal: SYNister should ideally be able to give a bit more leeway to the formatting of the construction files (leaving blanks where non-crucial information is missing, instead of failing entirely).
### ConstructionFileBuilder
Combine with ConstructionFileBuilder to go directly from list of specified oligos to labsheets. Currently SYNister takes .txt construction files as inputs. ConstructionFileBuilder could be modified to output these .txt files for SYNister read. Alternatively, as SYNister reads .txt construction files into ConstructionFile models and immediately extracts a List of steps, ConstructionFileBuilder can connect its ConstructionFile model outputs directly to this part of the code (at /src/SYNister/model/ProcessConstructionFile.java line 116, pictured below--"parser.run(sb.toString())" is a ConstructionFile).  
![cfbuilders2](/READMEphotos/ProcessConstructionFiles.png)   
### Drag and drop GUI
Typing (or even copying) entire filepaths can get tedious; a drag and drop functionality for inventory/input/destination folders might improve user experience.
### Formatting
The current .doc output has limitted formatting. Consider finding a way to output bolding/italics/different font sizes to improve readability.
### Adding Labels 
Currently reaction labels are left blank (as this information cannot be extracted from the construction file). Consider prompting user to provide a single-character code to derive labels from. If this is done, one should keep in mind how these labels might conflict with the inventory and/or parallel projects.