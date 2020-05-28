package in.co.dermatologist.dicoderma;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
@NoArgsConstructor
public class Dicoderma {

    protected BufferedImage bufferedImage;


    public void getDicodermaMetadata(BufferedImage bufferedImage) throws IOException, ImageReadException {
        byte[] imageBytes = bufferedImageToByteArray(bufferedImage);
        final ImageMetadata metadata = Imaging.getMetadata(imageBytes);
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
