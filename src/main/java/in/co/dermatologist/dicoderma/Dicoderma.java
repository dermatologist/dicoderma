package in.co.dermatologist.dicoderma;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import com.google.gson.Gson;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class Dicoderma {

    protected BufferedImage bufferedImage;

    protected DicomSCModel model;

    public String getDicodermaMetadata(BufferedImage bufferedImage) throws IOException, ImageReadException {
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
        model = new DicomSCModel();
        Gson gson = new Gson();
        String jsonInString = gson.toJson(model);
        if (metadata instanceof JpegImageMetadata) {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
            final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_USER_COMMENT);
            String dicodermaMetadata = field.getValueDescription();
            DicomSCModel readModel = gson.fromJson(dicodermaMetadata, DicomSCModel.class);
            jsonInString = gson.toJson(readModel);
        }
        return jsonInString;
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
