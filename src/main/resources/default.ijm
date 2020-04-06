arguments = getArgument;
if (arguments=="") exit ("No argument!");
argumentArray = split(getArgument()," ");
if(lengthOf(argumentArray) != 3) exit ("Arguments: source dest!");

source = argumentArray[0];
dest = argumentArray[1];
type = argumentArray[2];

open(source);
setMinAndMax(6000,17500);
run("Apply LUT");
run("8-bit");
makeRectangle(50, 10, 412, 600);
run("Crop");
run("Size...", "width=512 height=512 depth=1 average interpolation=Bilinear");
saveAs(type, dest);
close();



