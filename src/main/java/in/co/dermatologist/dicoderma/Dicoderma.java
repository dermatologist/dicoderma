package in.co.dermatologist.dicoderma;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;

import com.google.gson.Gson;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;

@Getter
@Setter
@NoArgsConstructor
public class Dicoderma {

    protected BufferedImage bufferedImage;

    protected DicomSCModel model;

    protected Gson gson = new Gson();

    public DicomSCModel getDicodermaMetadata(BufferedImage bufferedImage) throws IOException, ImageReadException {
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
        model = new DicomSCModel();
        gson = new Gson();
        //String jsonInString = gson.toJson(model);
        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT);
            String dicodermaMetadata = field.getValueDescription();
            DicomSCModel readModel = gson.fromJson(dicodermaMetadata, DicomSCModel.class);
            //jsonInString = gson.toJson(readModel);
            return readModel;
        }
        return model;
    }

    public String getDicodermaMetadataAsString(DicomSCModel dicomSCModel){
        return gson.toJson(dicomSCModel);
    }

    public byte[] putDicodermaMetadataAsString(BufferedImage bufferedImage, String model){
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
        TiffOutputSet outputSet = new TiffOutputSet();

        // https://stackoverflow.com/questions/10642864/bufferedinputstream-into-byte-to-be-send-over-a-socket-to-a-database
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        byte[] bytes = bytesOut.toByteArray();
        BufferedOutputStream os = new BufferedOutputStream(bytesOut);

        //OutputStream os = new BufferedOutputStream(fos);
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
            new ExifRewriter().updateExifMetadataLossless(imageBytes, os, outputSet);
        }
        return bytesOut.toByteArray();
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
