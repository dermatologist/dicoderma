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
        dicomSCModel.PatientName = "Mickey Mouse";
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void getModelAsProperties() throws IOException {
        String s = dicoderma.getModelAsProperties(dicomSCModel);
        System.out.print(s);
    }

    @Test
    public void getModelAsStringArray() throws IOException {
        //String[] s = dicoderma.getModelAsCommaSeparatedString(dicomSCModel);
        for (String prop : dicoderma.getModelAsStringArray(dicomSCModel))
            System.out.print(prop);
    }

    @Test
    public void getDicodermMetadataFromFile() throws IOException, ImageReadException{
        String testImage = "src/test/resources/test.jpg";
 
        File file = new File(testImage);
        String absolutePath = file.getAbsolutePath();
        
        dicomSCModel = dicoderma.getDicodermMetadataFromFile(file);
        System.out.print(dicoderma.getModelAsProperties(dicomSCModel));
        
        assertTrue(absolutePath.endsWith("src/test/resources/test.jpg"));
    }

    @Test
    public void getDicodermaMetadata() {
    }

    @Test
    public void getDicodermaMetadataAsString() {
        System.out.print(dicoderma.getDicodermaMetadataAsString(dicomSCModel));
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