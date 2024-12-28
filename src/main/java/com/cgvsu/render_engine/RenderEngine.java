package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;

import javax.vecmath.*;

import com.cgvsu.model.Model;
import javafx.scene.paint.Color;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> models,
            final int width,
            final int height) {


        for (Model mesh : models) {
            if (mesh.viewMesh) renderModel(graphicsContext, mesh, camera, width, height);
        }
    }

    private static class PointVertexModel {

        public PointVertexModel(Point2f point, int vertexIndex, Model model) {
            this.point = new Point2f((float) Math.floor(point.x), (float) Math.floor(point.y));
            this.vertexIndex = vertexIndex;
            this.model = model;
        }

        public PointVertexModel(int vertexIndex) {
            this.point = new Point2f(0, 0);
            this.vertexIndex = vertexIndex;
            this.model = new Model();
        }

        public Point2f point;
        public int vertexIndex;

        public Model model;
    }

    private static class PolyModel {
        public Model model;
        public int poly = -2;

        public PolyModel(int poly, Model model) {
            this.poly = poly;
            this.model = model;
        }

        public PolyModel() {
            this.poly = -2;
            this.model = new Model();
        }
    }

    private static PointVertexModel selectedPVM = new PointVertexModel(-2);
    private static PolyModel selectedPoly = new PolyModel();

    public static void performDelete() {
        if (selectedPVM.vertexIndex > -2) {
            int nPoligons = selectedPVM.model.polygons.size();
            for (int i = 0; i < nPoligons; ++i) {
                Polygon poly = selectedPVM.model.polygons.get(i);
                if (poly.getVertexIndices().contains(selectedPVM.vertexIndex)) {
                    poly.getVertexIndices().remove((Integer) selectedPVM.vertexIndex);
                    selectedPVM.model.polygons.remove(i);
                    --i;
                    --nPoligons;
                }
            }
            selectedPVM = new PointVertexModel(-2);
        } else if (selectedPoly.poly > -2) {
            selectedPoly.model.polygons.remove(selectedPoly.poly);
            selectedPoly = new PolyModel();
        }
    }

    public static void findVertexIndexFromClick(Point2f mousePoint, Camera camera, List<Model> models, int width, int height) {
        for (Model mesh : models) {
            if (mesh.selected) {
                boolean vertexFound = false;

                Matrix4f modelMatrix = rotateScaleTranslate();
                Matrix4f viewMatrix = camera.getViewMatrix();
                Matrix4f projectionMatrix = camera.getProjectionMatrix();

                Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
                modelViewProjectionMatrix.mul(viewMatrix);
                modelViewProjectionMatrix.mul(projectionMatrix);

                final int nPolygons = mesh.polygons.size();
                for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
                    final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

                    for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                        Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                        javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

                        Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);

                        if (pointIsNearMouse(resultPoint, mousePoint)) {
                            selectedPoly = new PolyModel();
                            selectedPVM = new PointVertexModel(resultPoint, mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd), mesh);
                            vertexFound = true;
                        }
                    }
                }
                if (!vertexFound) {
                    for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
                        final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
                        List<Point2f> polyPoints = new ArrayList<>();
                        for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                            Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
                            javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);
                            Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);
                            polyPoints.add(resultPoint);
                        }
                        if (isPointInside(mousePoint, polyPoints)) {
                            selectedPVM = new PointVertexModel(-2);
                            selectedPoly = new PolyModel(polygonInd, mesh);
                            return;
                        }
                    }
                }
                return;
            }
        }
    }

    public static void deselect() {
        selectedPVM = new PointVertexModel(-2);
        selectedPoly = new PolyModel();
    }

    private static boolean pointIsNearMouse(Point2f point, Point2f mousePoint) {
        final int MAX_DELTA = 5;
        return Math.abs(point.x - mousePoint.x) < MAX_DELTA && Math.abs(point.y - mousePoint.y) < MAX_DELTA;
    }

    public static boolean isPointInside(Point2f testPoint, List<Point2f> vertices) {
        int numVertices = vertices.size();
        boolean inside = false;
        for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
            Point2f vertexI = vertices.get(i);
            Point2f vertexJ = vertices.get(j);

            if ((vertexI.y > testPoint.y) != (vertexJ.y > testPoint.y) &&
                    (testPoint.x < (vertexJ.x - vertexI.x) * (testPoint.y - vertexI.y) / (vertexJ.y - vertexI.y) + vertexI.x)) {
                inside = !inside;
            }
        }
        return inside;
    }

    private static void renderModel(
            final GraphicsContext graphicsContext,
            final Model mesh,
            Camera camera,
            final int width,
            final int height) {
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.mul(viewMatrix);
        modelViewProjectionMatrix.mul(projectionMatrix);

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            ArrayList<Point2f> resultPoints = new ArrayList<>();
            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                javax.vecmath.Vector3f vertexVecmath = new javax.vecmath.Vector3f(vertex.x, vertex.y, vertex.z);

                Point2f resultPoint = vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix, vertexVecmath), width, height);

                resultPoints.add(resultPoint);
            }

            graphicsContext.setStroke((mesh.selected) ? Color.BLACK : Color.GRAY);

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                if (mesh.selected) {
                    graphicsContext.setStroke(
                            (selectedPVM.vertexIndex > -2
                                    && (mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd) == selectedPVM.vertexIndex
                                    || mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd - 1) == selectedPVM.vertexIndex))
                                    ? Color.RED
                                    : graphicsContext.getStroke()
                    );
                    if (selectedPoly.poly > -2) {
                        graphicsContext.setStroke(
                                (polygonInd == selectedPoly.poly)
                                        ? Color.BLUE
                                        : graphicsContext.getStroke()
                        );
                        graphicsContext.setLineWidth((polygonInd == selectedPoly.poly)
                                ? 2
                                : 1);
                    }
                }
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);
        }
    }

    public void renderTexture(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> models,
            final int width,
            final int height
    ) {
        for (Model mesh : models) {
            if (mesh.viewTexture) {
                renderModelTexture(graphicsContext, mesh);
            }
        }
    }

    public void renderModelTexture(GraphicsContext graphicsContext, Model mesh) {
    }

}