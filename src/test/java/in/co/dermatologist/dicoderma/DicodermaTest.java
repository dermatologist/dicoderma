package in.co.dermatologist.dicoderma;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.util.Arrays;

import java.io.IOException;

public class DicodermaTest {

    DicomSCModel dicomSCModel;
    Dicoderma dicoderma;

    @BeforeEach
    public void setUp() {
        dicomSCModel = new DicomSCModel();
        dicoderma = new Dicoderma();
        dicomSCModel.PatientName = "Mickey Mouse";
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void getModelAsProperties() throws IOException {
        String s = dicoderma.getModelAsProperties(dicomSCModel);
        assertTrue(s.indexOf("=") > -1);
    }

    @Test
    public void getModelAsStringArray() throws IOException {
        // //String[] s = dicoderma.getModelAsCommaSeparatedString(dicomSCModel);
        // for (String prop : dicoderma.getModelAsStringArray(dicomSCModel))
        //     System.out.print(prop);
        assertFalse(Arrays.asList(dicoderma.getModelAsStringArray(dicomSCModel)).contains("PatientName=Mickey^Mouse"));
        assertTrue(Arrays.asList(dicoderma.getModelAsStringArray(dicomSCModel)).contains("Mickey^Mouse"));
    }

    @Test
    public void getDicodermaMetadataFromFile() throws IOException, ImageReadException{
        String testImage = "src/test/resources/test.jpg";
 
        File file = new File(testImage);
        //String absolutePath = file.getAbsolutePath();
        
        dicomSCModel = dicoderma.getDicodermaMetadataFromFile(file);
        assertTrue(dicoderma.getModelAsProperties(dicomSCModel).indexOf("=") > -1);
    }

    @Test
    public void getDicodermaMetadata() {
    }

    @Test
    public void getDicodermaMetadataAsString() throws JsonProcessingException{
        //System.out.print(dicoderma.getDicodermaMetadataAsString(dicomSCModel));
        assertTrue(dicoderma.getDicodermaMetadataAsString(dicomSCModel).indexOf("Mickey") > -1);
    }

    @Test
    public void putDicodermaMetadata() {
    }

    @Test
    public void putDicodermaMetadataAsString() {
    }

    @Test
    public void putDicomModelToFile() throws IOException, ImageReadException, ImageWriteException, JsonProcessingException{
        File filein = new File("src/test/resources/test.jpg");
        File fileout = new File("src/test/resources/test-out.jpg");
        fileout.delete();
        dicoderma.putDicomModelToFile(filein, fileout, dicomSCModel);
        assertTrue(fileout.exists());
    }
}