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
import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import java.net.URL;
public class DicodermaJpg2Dcm extends Jpg2Dcm {
//    org.dcm4che3.tool.jpg2dcm.Jpg2Dcm main = new Jpg2Dcm();

    public void convertJpgToDcm(File srcFile, File destFile, String[] dicodermaMetadataAsArray)
            throws IOException, ParserConfigurationException, SAXException {

        Path srcFilePath = srcFile.toPath();
        Path destFilePath = destFile.toPath();
        setNoAPPn(false);
        setPhoto(false);
        Attributes staticMetadata = new Attributes();
        CLIUtils.addAttributes(staticMetadata, dicodermaMetadataAsArray);
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

    @Override
    protected enum ContentType {
        IMAGE_JPEG {
            @Override
            String getSampleMetadataFile(boolean photo) {
                ClassLoader classLoader = getClass().getClassLoader();
                URL resource = classLoader.getResource("secondaryCaptureImageMetadata.xml");
                return resource.getFile();
            }

            @Override
            String getSOPClassUID(boolean photo) {
                return photo
                        ? UID.VLPhotographicImageStorage
                        : UID.SecondaryCaptureImageStorage;
            }

            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new JPEGParser(channel);
            }
        },
        VIDEO_MPEG {
            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new MPEG2Parser(channel);
            }
        },
        VIDEO_MP4 {
            @Override
            XPEGParser newParser(SeekableByteChannel channel) throws IOException {
                return new MP4Parser(channel);
            }
        };

        static ContentType probe(Path path) throws IOException {
            String type = Files.probeContentType(path);
            if (type == null)
                throw new IllegalArgumentException(
                        MessageFormat.format(rb.getString("unsupported-file-ext"), path));
            switch (type.toLowerCase()) {
                case "image/jpeg":
                case "image/jp2":
                    return ContentType.IMAGE_JPEG;
                case "video/mpeg":
                    return ContentType.VIDEO_MPEG;
                case "video/mp4":
                case "video/quicktime":
                    return ContentType.VIDEO_MP4;
            }
            throw new IllegalArgumentException(
                    MessageFormat.format(rb.getString("unsupported-content-type"), type, path));
        }

        String getSampleMetadataFile(boolean photo) {
            return "resource:vlPhotographicImageMetadata.xml";
        }

        String getSOPClassUID(boolean photo) {
            return UID.VideoPhotographicImageStorage;
        }

        abstract XPEGParser newParser(SeekableByteChannel channel) throws IOException;
    }

}
