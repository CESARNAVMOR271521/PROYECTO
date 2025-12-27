
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConverter {
    public static void main(String[] args) {
        try {
            File inputFile = new File("C:/Users/NAVAX519/.gemini/antigravity/brain/3a63aced-9af0-48d6-a0fc-ab236635d89b/uploaded_image_1766805020214.jpg");
            File outputFile = new File("src/resources/images/logo.png");
            
            // Create directory if it doesn't exist
            outputFile.getParentFile().mkdirs();

            BufferedImage image = ImageIO.read(inputFile);
            if (image == null) {
                System.out.println("Could not read input file.");
                return;
            }
            
            boolean result = ImageIO.write(image, "png", outputFile);
            if (result) {
                System.out.println("Image converted successfully to: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("Failed to write image.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
