# TomeyReader
 
Converts Tomey Casia .exam files into pgns

## Installation
 
Maven build
 
## Usage

java -Xmx2048m -jar tomeyreader-2.0.jar -s $source_dir -t $target_dir -d=false

* -sourceFolder \{path\} or -s \{path\} 
    * Path pointing to .exam files
* -fileExtension \{string\}
    * Extension of exam files
    *  default .exam
* -targetFolder \{path\} or -t \{path\} 
    * Target folder for extracted images
    * default export
* -d \{bool\}
    * Set to true if a new subdirectory within the target folder should be created for every exam file
    * default true
* -x \{int\}
    * Width of extracted images within the exam file
    * default -1, autodetect
* -y \{int\}
    * Height of extracted images within the exam file
    * default -1, autodetect
* -imageCount \{int\}
    * Total number of images within the exam file
    * default -1, autodetect
* -z \{int\}
    * Pixel depth of an image
    * default -1, autodetect
* -offset \{int\}
    * Starting position of the first image within the exam file
    * default -1, autodetect
* -macro \{path\}
    * Imagej macro for postprocessing of extracted images
    * default empty
* -postPlugins \{path\}
    * Plugins for imagej postprocessing
    * default empty
* -postDir \{path\}
    * Target folder for post processed images
    * default \{path\}
* -postCreatDir \{bool\}
    * Set to true if a new subdirectory within the post processing folder should be created for every exam file
    * default true
* -mode \{int\}
    * Mode: 3 = extract VAA-Images, Eye-Images, Patient-Infos, 2 = extract VAA-Images, 1 = extract Eye-Images, 0 = extract Patient-Infos
    * default  3
## License
 
The MIT License (MIT)

Copyright (c) 2021 Andreas Glatz

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
