dir1 = "D:/Projekte/test/21052/";
dir2 = "D:/Projekte/test/test/";
list = getFileList(dir1);
setBatchMode(true);
for (i=0; i<list.length; i++) {
 open(dir1+list[i]);
 setMinAndMay(6000,17500);
 run("Apply LUT");
 run("8-bit");
 makeRectangle(50, 10, 412, 600);
 run("Crop");
 run("Size...", "width=512 height=512 depth=1 average interpolation=Bilinear");
 saveAs("png", dir2+list[i]);
 close();
}




