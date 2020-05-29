package in.co.dermatologist.dicoderma;

//import org.dcm4che3.tool.jpg2dcm.Jpg2Dcm;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.imageio.codec.XPEGParser;
import org.dcm4che3.imageio.codec.jpeg.JPEG;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.io.SAXReader;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.StreamUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class DicodermaJpg2Dcm extends Jpg2Dcm {
//    org.dcm4che3.tool.jpg2dcm.Jpg2Dcm main = new Jpg2Dcm();

    public void convertJpgToDcm(Path srcFilePath, Path destFilePath, String commaSeperatedDicodermaMetadata)
            throws IOException, ParserConfigurationException, SAXException {
        //Jpg2Dcm main = new Jpg2Dcm();
        setNoAPPn(false);
        setPhoto(false);
        Attributes staticMetadata = new Attributes();
        String[] dicodermaMetadataWithSlash = commaSeperatedDicodermaMetadata.split(",");
        CLIUtils.addAttributes(staticMetadata, dicodermaMetadataWithSlash);
        supplementMissingUIDs(staticMetadata);
        supplementMissingValue(staticMetadata, Tag.SeriesNumber, "999");
        supplementMissingValue(staticMetadata, Tag.InstanceNumber, "1");
        supplementType2Tags(staticMetadata);


        ContentType fileType = ContentType.probe(srcFilePath);
        Attributes fileMetadata = SAXReader.parse(StreamUtils.openFileOrURL(fileType.getSampleMetadataFile(photo)));
        fileMetadata.addAll(staticMetadata);
        supplementMissingValue(fileMetadata, Tag.SOPClassUID, fileType.getSOPClassUID(photo));
        try (SeekableByteChannel channel = Files.newByteChannel(srcFilePath);
             DicomOutputStream dos = new DicomOutputStream(destFilePath.toFile())) {
            XPEGParser parser = fileType.newParser(channel);
            parser.getAttributes(fileMetadata);
            dos.writeDataset(fileMetadata.createFileMetaInformation(parser.getTransferSyntaxUID()), fileMetadata);
            dos.writeHeader(Tag.PixelData, VR.OB, -1);
            dos.writeHeader(Tag.Item, null, 0);
            if (noAPPn && parser.getPositionAfterAPPSegments() > 0) {
                copyPixelData(channel, parser.getPositionAfterAPPSegments(), dos,
                        (byte) 0xFF, (byte) JPEG.SOI);
            } else {
                copyPixelData(channel, parser.getCodeStreamPosition(), dos);
            }
            dos.writeHeader(Tag.SequenceDelimitationItem, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(MessageFormat.format(rb.getString("converted"), srcFilePath, destFilePath));
    }
}
