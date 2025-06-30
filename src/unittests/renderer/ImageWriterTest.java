package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

/**
 * Unit tests for ImageWriter class.
 */
public class ImageWriterTest {
    /**
     * Test method for writing an image with a grid pattern.
     */
    @Test
    public void testWriteImageWithGrid() {
        int width = 800;
        int height = 500;
        ImageWriter imageWriter = new ImageWriter(width, height);

        Color background = new Color(255, 255, 255); // White
        Color grid = new Color(0, 0, 0);             // Black

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Every 50 pixels = grid line
                if (x % 50 == 0 || y % 50 == 0) {
                    imageWriter.writePixel(x, y, grid);
                } else {
                    imageWriter.writePixel(x, y, background);
                }
            }
        }

        imageWriter.writeToImage("test");
    }
}
