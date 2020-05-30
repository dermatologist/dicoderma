package in.co.dermatologist.dicoderma;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Properties;

//import com.google.gson.Gson;
//import com.google.gson.JsonSyntaxException;

//import org.dcm4che3.data.Attributes;
//import org.dcm4che3.tool.common.CLIUtils;

@Getter
@Setter
@NoArgsConstructor
public class Dicoderma {

    protected BufferedImage bufferedImage;

    protected DicomSCModel model;

    //protected Gson gson = new Gson();
    protected ObjectMapper objectMapper = new ObjectMapper();

    public String getModelAsProperties(DicomSCModel model) throws IOException {
        //https://stackoverflow.com/questions/54274134/how-to-convert-a-json-into-properties-file-in-java
        JavaPropsMapper mapper = new JavaPropsMapper();

        // https://stackoverflow.com/questions/1579113/java-properties-object-to-string
        Properties props = mapper.writeValueAsProperties(model);
        StringWriter writer = new StringWriter();
        props.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

    public String[] getModelAsStringArray(DicomSCModel model) throws IOException {
        String props = getModelAsProperties(model);
        String[] filteredProps = new String[]{};
        String[] _props = props.replaceAll("\n", ",").split(",");
        for (String _prop : _props){
            if(Character.isUpperCase(_prop.charAt(0)) && // Starts with Capital Letter
                _prop.indexOf("=") > -1 && // Has =
                !_prop.endsWith("=")){ // But does not end with =, ie blank property
                    filteredProps = Arrays.copyOf(filteredProps, filteredProps.length + 1);
                    filteredProps[filteredProps.length - 1] = _prop; 
              
            }
        }
        return filteredProps;
    }

    public DicomSCModel getDicodermaMetadataFromFile(final File file) throws ImageReadException,
            IOException {
        // get all metadata stored in EXIF format (ie. from JPEG or TIFF).
        final ImageMetadata metadata = Imaging.getMetadata(file);
        return getModelFromMetadata(metadata);
    }

    private DicomSCModel getModelFromMetadata(ImageMetadata metadata){
        model = new DicomSCModel();
        // gson = new Gson();
        //String jsonInString = gson.toJson(model);
        if (metadata instanceof JpegImageMetadata) {
            try {
                final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT);
                String dicodermaMetadata = field.getValueDescription();
                System.out.print(dicodermaMetadata);
                // DicomSCModel model2 = gson.fromJson(dicodermaMetadata, DicomSCModel.class);
                DicomSCModel model2 = objectMapper.readValue(dicodermaMetadata.replace("'", ""), DicomSCModel.class);
                System.out.print(model2.toString());
                return model2;
                //jsonInString = gson.toJson(readModel);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    public DicomSCModel getDicodermaMetadata(BufferedImage bufferedImage) throws IOException, ImageReadException {
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
        return getModelFromMetadata(metadata);
    }

    public String getDicodermaMetadataAsString(DicomSCModel dicomSCModel) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dicomSCModel);
    }

    public BufferedImage putDicodermaMetadata(BufferedImage bufferedImage, DicomSCModel dicomSCModel) throws ImageWriteException, ImageReadException, IOException {
        return putDicodermaMetadataAsString(bufferedImage, getDicodermaMetadataAsString(dicomSCModel));
    }

    public BufferedImage putDicodermaMetadataAsString(BufferedImage bufferedImage, String model) throws IOException, ImageReadException, ImageWriteException {
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
        TiffOutputSet outputSet = getOutputSetFromMetadataAndModel(metadata, model);
        // https://stackoverflow.com/questions/10642864/bufferedinputstream-into-byte-to-be-send-over-a-socket-to-a-database
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] bytes = bytesOut.toByteArray();
        BufferedOutputStream os = new BufferedOutputStream(bytesOut);
        new ExifRewriter().updateExifMetadataLossless(imageBytes, os, outputSet);
        byte[] imageAsBytes = bytesOut.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageAsBytes);
        BufferedImage bImage = ImageIO.read(bis);
        return bImage;
    }

    private TiffOutputSet getOutputSetFromMetadataAndModel(ImageMetadata metadata, String model) throws ImageWriteException {
        TiffOutputSet outputSet = new TiffOutputSet();
        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            if(null != jpegMetadata){
                final TiffImageMetadata tiffImageMetadata = jpegMetadata.getExif();
                if(null != tiffImageMetadata)
                    // Copy it if present
                    outputSet = tiffImageMetadata.getOutputSet();
            }
            final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT );
            exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, model);
        }
        return outputSet;
    }

    public void putDicormMetadataToFile(final File jpegImageFile, final File dst, String model)
            throws IOException, ImageReadException, ImageWriteException {
        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {
            // note that metadata might be null if no metadata is found.
            final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
            TiffOutputSet outputSet = getOutputSetFromMetadataAndModel(metadata, model);
            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
                    outputSet);
        }

    }


    private byte[] bufferedImageToByteArray(BufferedImage bufferedImage) {
        // https://mkyong.com/java/how-to-convert-bufferedimage-to-byte-in-java/
        byte[] imageInByte = new byte[0];
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return imageInByte;
    }
}
