package com.cgvsu.model;

import java.util.*;
import java.util.List;

import com.cgvsu.objreader.exceptions.*;


import java.util.List;

public class Polygon {

    private List<Integer> vertexIndices; //абстракт класс
    private List<Integer> textureVertexIndices;
    private List<Integer> normalIndices;


    public Polygon() {
        vertexIndices = new ArrayList<Integer>(); //тут эррей тк у абстрактоного листа нельзя так
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
    }

    public void setVertexIndices(List<Integer> vertexIndices) {
        assert vertexIndices.size() >= 3;
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(List<Integer> textureVertexIndices) {
        assert textureVertexIndices.size() >= 3;
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(List<Integer> normalIndices) {
        assert normalIndices.size() >= 3;
        this.normalIndices = normalIndices;
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public List<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public List<Integer> getNormalIndices() {
        return normalIndices;
    }

    public void addVertexIndex(int index) {
        vertexIndices.add(index);
    }

    public void addTextureVertexIndex(int index) {
        textureVertexIndices.add(index);
    }

    public void addNormalIndex(int index) {
        normalIndices.add(index);
    }

    private int lineIndex;

    public boolean hasTexture() {
        return !textureVertexIndices.isEmpty();
    }

    public void checkIndices(int verticesSize, int textureVerticesSize, int normalsSize) {
        for (int i = 0; i < vertexIndices.size(); i++) {
            int vertexIndex = vertexIndices.get(i);
            if (vertexIndex >= verticesSize || vertexIndex < 0) {
                throw new FaceWordIndexException("vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < textureVertexIndices.size(); i++) {
            int textureVertexIndex = textureVertexIndices.get(i);
            if (textureVertexIndex >= textureVerticesSize || textureVertexIndex < 0) {
                throw new FaceWordIndexException("texture vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < normalIndices.size(); i++) {
            int normalIndex = normalIndices.get(i);
            if (normalIndex >= normalsSize || normalIndex < 0) {
                throw new FaceWordIndexException("normal", lineIndex, i + 1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
        return Objects.equals(vertexIndices, polygon.vertexIndices) &&
                Objects.equals(textureVertexIndices, polygon.textureVertexIndices) &&
                Objects.equals(normalIndices, polygon.normalIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexIndices, textureVertexIndices, normalIndices);
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }
}





//не меняла заготовка лектора полностью!