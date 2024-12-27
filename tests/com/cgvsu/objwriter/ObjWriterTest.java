package objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class ObjWriterTests {
    private final ObjWriterClass objWriter = new ObjWriterClass();

    // Тест вершин
    @ParameterizedTest
    @CsvSource({ "0.0f, 2.7f, 1", "0.3, 0, 0", "77.341f, 0.00052f, -17.3551f", "-1.2f, -15.0, 5.3" })
    public void vertexToStringTest(float x, float y, float z) {
        String result = objWriter.vertexToString(new Vector3f(x, y, z));
        String[] array = result.split(" ");
        Assertions.assertEquals("v", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }

    // Тест текстурных координат
    @ParameterizedTest
    @CsvSource({ "0.0f, 2.7f", "0.3, 0", "77.341f, 0.00052f", "-1.2f, -15.0" })
    public void textureVertexToStringTest(float x, float y) {
        String result = objWriter.textureVertexToString(new Vector2f(x, y));
        String[] array = result.split(" ");
        Assertions.assertEquals("vt", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
    }

    // Тест нормалей
    @ParameterizedTest
    @CsvSource({ "0.0f, 2.7f, 1", "0.3, 0, 0", "77.341f, 0.00052f, -17.3551f", "-1.2f, -15.0, 5.3" })
    public void normalToStringTest(float x, float y, float z) {
        String result = objWriter.normalToString(new Vector3f(x, y, z));
        String[] array = result.split(" ");
        Assertions.assertEquals("vn", array[0]);
        Assertions.assertEquals(x, Float.parseFloat(array[1]));
        Assertions.assertEquals(y, Float.parseFloat(array[2]));
        Assertions.assertEquals(z, Float.parseFloat(array[3]));
    }

    // Тест полигона с вершинами (без текстурных координат и нормалей)
    @Test
    public void polygonToStringTestWithOnlyVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1 2 3", result);
    }

    // Тест полигона с вершинами и текстурными координатами (без нормалей)
    @Test
    public void polygonToStringTestWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 4)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(2, 5, 6, 1)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/3 2/6 3/7 5/2", result);
    }

    // Тест полигона с вершинами и нормалями (без текстурных координат)
    @Test
    public void polygonToStringTestWithNormalIndicesAndWithoutTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 4)));
        polygon.setNormalIndices(new ArrayList<>(List.of(2, 5, 6, 1)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1//3 2//6 3//7 5//2", result);
    }

    // Тест полигона с вершиной, текстурными координатами и нормалями
    @Test
    public void polygonToStringTestWithNormalIndicesAndWithTextureVertexIndices() {
        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(List.of(0, 1, 2, 5)));
        polygon.setTextureVertexIndices(new ArrayList<>(List.of(6, 4, 3, 1)));
        polygon.setNormalIndices(new ArrayList<>(List.of(3, 5, 4, 2)));
        String result = objWriter.polygonToString(polygon);
        Assertions.assertEquals("f 1/7/4 2/5/6 3/4/5 6/2/3", result);
    }

    // Тест записи координат в файлик
    @Test
    public void testWrite() throws IOException {
        Model model = new Model();
        model.vertices = new ArrayList<>(List.of(
                new Vector3f(1.0f, 2.0f, 3.0f),
                new Vector3f(4.0f, 5.0f, 6.0f),
                new Vector3f(7.0f, 8.0f, 9.0f)
        ));
        model.textureVertices = new ArrayList<>(List.of(
                new Vector2f(0.1f, 0.2f),
                new Vector2f(0.3f, 0.4f),
                new Vector2f(0.5f, 0.6f)
        ));
        model.normals = new ArrayList<>(List.of(
                new Vector3f(-1.0f, -2.0f, -3.0f),
                new Vector3f(-4.0f, -5.0f, -6.0f),
                new Vector3f(-7.0f, -8.0f, -9.0f)
        ));
        model.polygons = new ArrayList<>(List.of(
                new Polygon() {{
                    setVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
                    setTextureVertexIndices(new ArrayList<>(List.of(0, 1, 2)));
                    setNormalIndices(new ArrayList<>(List.of(0, 1, 2)));
                }}
        ));

        String filename = "test.obj";
        ObjWriterClass objWriter = new ObjWriterClass();
        objWriter.write(model, filename);

        List<String> fileLines = Files.readAllLines(Paths.get(filename));
        List<String> expectedLines = List.of(
                "v 1.0 2.0 3.0",
                "v 4.0 5.0 6.0",
                "v 7.0 8.0 9.0",
                "vt 0.1 0.2",
                "vt 0.3 0.4",
                "vt 0.5 0.6",
                "vn -1.0 -2.0 -3.0",
                "vn -4.0 -5.0 -6.0",
                "vn -7.0 -8.0 -9.0",
                "f 1/1/1 2/2/2 3/3/3"
        );

        Assertions.assertEquals(expectedLines, fileLines);
        File file = new File(filename);
        file.delete();
    }
}
