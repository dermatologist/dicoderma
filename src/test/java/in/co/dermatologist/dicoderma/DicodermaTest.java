package in.co.dermatologist.dicoderma;

import org.apache.commons.imaging.ImageReadException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.*;

import java.io.IOException;

public class DicodermaTest {

    DicomSCModel dicomSCModel;
    Dicoderma dicoderma;

    @BeforeEach
    public void setUp() {
        dicomSCModel = new DicomSCModel();
        dicoderma = new Dicoderma();
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void getModelAsProperties() throws IOException {
        dicomSCModel.PatientName = "Mickey Mouse";
        String s = dicoderma.getModelAsProperties(dicomSCModel);
        System.out.println(s);
    }

    @Test
    public void getDicodermMetadataFromFile() throws IOException, ImageReadException{
        String testImage = "src/test/resources/test.jpg";
 
        File file = new File(testImage);
        String absolutePath = file.getAbsolutePath();
        
        dicomSCModel = dicoderma.getDicodermMetadataFromFile(file);
        System.out.println(dicoderma.getModelAsProperties(dicomSCModel));
        
        assertTrue(absolutePath.endsWith("src/test/resources/test.jpg"));
    }

    @Test
    public void getDicodermaMetadata() {
    }

    @Test
    public void getDicodermaMetadataAsString() {
    }

    @Test
    public void putDicodermaMetadata() {
    }

    @Test
    public void putDicodermaMetadataAsString() {
    }

    @Test
    public void putDicormMetadataToFile() {
    }
}