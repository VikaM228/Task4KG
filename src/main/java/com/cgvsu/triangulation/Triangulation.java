package com.cgvsu.triangulation;

import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Triangulation {
    public static List<Polygon> triangulate(ArrayList<Polygon> polygons) {
        List<Polygon> triangulatedPolygons = new ArrayList<>(); // Новый список для хранения триангулированных полигонов

        for (Polygon polygon : polygons) { // Проходим по всем полигонам модели
            List<Integer> vertexIndices = polygon.getVertexIndices(); // Получаем индексы вершин полигона
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices(); // Получаем индексы текстурных координат
            List<Integer> normalIndices = polygon.getNormalIndices(); // Получаем индексы нормалей

            if (vertexIndices.size() <= 3) { // Если полигон уже треугольник, добавляем его без изменений
                triangulatedPolygons.add(polygon); //тут важно не добавлять существующий а создавать новый полигон чтобы потом не было ошибок ИСПРАВЬ!
            } else {
                // Выполняем триангуляцию с помощью "веерного" метода
                for (int i = 1; i < vertexIndices.size() - 1; i++) {
                    Polygon triangle = new Polygon(); // Создаём новый треугольник

                    // Добавляем индексы вершин треугольника
                    triangle.addVertexIndex(vertexIndices.get(0)); // Первая вершина веера
                    triangle.addVertexIndex(vertexIndices.get(i)); // Текущая вершина
                    triangle.addVertexIndex(vertexIndices.get(i + 1)); // Следующая вершина

                    // Если есть текстурные координаты, добавляем их индексы
                    if (!textureVertexIndices.isEmpty()) {
                        triangle.addTextureVertexIndex(textureVertexIndices.get(0)); // Первая текстурная координата веера
                        triangle.addTextureVertexIndex(textureVertexIndices.get(i)); // Текущая текстурная координата
                        triangle.addTextureVertexIndex(textureVertexIndices.get(i + 1)); // Следующая текстурная координата
                    }

                    // Если есть нормали, добавляем их индексы
                    if (!normalIndices.isEmpty()) {
                        triangle.addNormalIndex(normalIndices.get(0)); // Первая нормаль веера
                        triangle.addNormalIndex(normalIndices.get(i)); // Текущая нормаль
                        triangle.addNormalIndex(normalIndices.get(i + 1)); // Следующая нормаль
                    }

                    triangulatedPolygons.add(triangle); // Добавляем треугольник в список триангулированных полигонов
                }
            }
        }

        // Заменяем исходные полигоны на триангулированные
        return polygons = (ArrayList<Polygon>) triangulatedPolygons;
    }
}
