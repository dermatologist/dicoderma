# dicoderma - A JAVA library that marries dermatology to DICOM

## Why dicoderma

Dermatology never really embraced DICOM for image management. Dicoderma is an attempt to narrow the rift by freeing dermatologists from the overhead of DICOM compliant infrastructure but gives some of the advantages like storing patient metadata with images. Dicoderma facilitates this by mapping DICOM tags to EXIF USER_CONTENT. You can also convert JPEG to DCM.

* uses Java8 and maven
* Install locally by ``` mvn clean install ```


## Usage

### Write metadata to a file

```
        dicomSCModel = new DicomSCModel();
        dicoderma = new Dicoderma();
        dicomSCModel.PatientName = "Mickey Mouse";
        File filein = new File("src/test/resources/test.jpg");
        File fileout = new File("src/test/resources/test-out.jpg");
        fileout.delete();
        dicoderma.putDicomModelToFile(filein, fileout, dicomSCModel);

```

### Read metadata from file

```
        dicomSCModel = new DicomSCModel();
        dicoderma = new Dicoderma();
        dicomSCModel = dicoderma.getDicodermaMetadataFromFile(file);
```

### Write dcm file

```
        File filein = new File("src/test/resources/test-out.jpg");
        File fileout = new File("src/test/resources/test.dcm");
        String[] metadata = dicoderma.getModelAsStringArray(dicomSCModel);
        fileout.delete();
        dicodermaJpg2Dcm.convertJpgToDcm(filein, fileout, metadata);

```

### See [dit4ij - the dermatology image tagger for ImageJ/Fiji](https://github.com/dermatologist/dit4ij)


## Author

* [Bell Eapen](https://nuchange.ca)
