package in.co.dermatologist.dicoderma;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class DicodermaJpg2DcmTest {

    DicomSCModel dicomSCModel;
    Dicoderma dicoderma;
    DicodermaJpg2Dcm dicodermaJpg2Dcm;

    @BeforeEach
    public void setUp() {
        dicomSCModel = new DicomSCModel();
        dicoderma = new Dicoderma();
        dicodermaJpg2Dcm = new DicodermaJpg2Dcm();
        dicomSCModel.PatientName = "Mickey Mouse";
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void convertJpgToDcm() throws IOException, ParserConfigurationException, SAXException {
        File filein = new File("src/test/resources/test-out.jpg");
        File fileout = new File("src/test/resources/test.dcm");
        String[] metadata = dicoderma.getModelAsStringArray(dicomSCModel);
        for(String m : metadata){
            System.out.println(m);
        }
        //dicodermaJpg2Dcm.convertJpgToDcm(filein, fileout, metadata);
    }
}