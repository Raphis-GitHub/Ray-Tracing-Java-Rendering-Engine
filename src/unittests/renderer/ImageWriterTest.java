package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static java.awt.Color.WHITE;

/**
 * Unit tests for ImageWriter class.
 */
public class ImageWriterTest {
    /**
     * Test method for writing an image with a grid pattern.
     */
    @Test
    public void testWriteImageWithGrid() {
        final int width = 801;
        final int height = 501;
        final ImageWriter imageWriter = new ImageWriter(width, height);

        final int step = 50; // Grid step size
        final Color background = new Color(WHITE);
        final Color grid = Color.BLACK;

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                // Every 50 pixels = grid line
                imageWriter.writePixel(x, y, (x % step == 0 || y % step == 0) ? grid : background);

        imageWriter.writeToImage("test");
    }
}
