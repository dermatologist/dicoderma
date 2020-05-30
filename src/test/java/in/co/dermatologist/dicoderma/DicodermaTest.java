package in.co.dermatologist.dicoderma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String s = dicoderma.getModelAsProperties(dicomSCModel);
        System.out.println(s);
    }

    @Test
    public void getDicodermMetadataFromFile() {
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