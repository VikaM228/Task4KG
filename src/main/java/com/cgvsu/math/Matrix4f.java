package com.cgvsu.math;

public class Matrix4f {
    public float[][] elements;

    public Matrix4f(float[][] elements) {
        if (elements.length != 4 || elements[0].length != 4 || elements[1].length != 4 || elements[2].length != 4 || elements[3].length != 4) {
            throw new IllegalArgumentException("Тебе сложно написать матрицу 4x4?");
        }
        this.elements = elements;
    }

    public float[][] getElements() {
        return elements;
    }

    public Matrix4f plus(Matrix4f other) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.elements[i][j] + other.elements[i][j];
            }
        }
        return new Matrix4f(result);
    }

    public Matrix4f minus(Matrix4f other) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.elements[i][j] - other.elements[i][j];
            }
        }
        return new Matrix4f(result);
    }

    public Matrix4f multiply(Matrix4f other) {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i][j] += this.elements[i][k] * other.elements[k][j];
                }
            }
        }
        return new Matrix4f(result);
    }

    public Vector4f multiplyByVector(Vector4f vector) {
        float resultX = elements[0][0] * vector.getX() + elements[0][1] * vector.getY() + elements[0][2] * vector.getZ() + elements[0][3] * vector.getW();
        float resultY = elements[1][0] * vector.getX() + elements[1][1] * vector.getY() + elements[1][2] * vector.getZ() + elements[1][3] * vector.getW();
        float resultZ = elements[2][0] * vector.getX() + elements[2][1] * vector.getY() + elements[2][2] * vector.getZ() + elements[2][3] * vector.getW();
        float resultW = elements[3][0] * vector.getX() + elements[3][1] * vector.getY() + elements[3][2] * vector.getZ() + elements[3][3] * vector.getW();
        return new Vector4f(resultX, resultY, resultZ, resultW);
    }

    public Matrix4f transpose() {
        float[][] result = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result[i][j] = this.elements[j][i];
            }
        }
        return new Matrix4f(result);
    }

    public static Matrix4f identity() {
        float[][] identityMatrix = {{1, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        return new Matrix4f(identityMatrix);
    }

    public static Matrix4f zeroMatrix() {
        float[][] zeroMatrix = {{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        return new Matrix4f(zeroMatrix);
    }

    public float determinant() {
        return elements[0][0] * (elements[1][1] * (elements[2][2] * elements[3][3] - elements[2][3] * elements[3][2])
                - elements[1][2] * (elements[2][1] * elements[3][3] - elements[2][3] * elements[3][1])
                + elements[1][3] * (elements[2][1] * elements[3][2] - elements[2][2] * elements[3][1]))
                - elements[0][1] * (elements[1][0] * (elements[2][2] * elements[3][3] - elements[2][3] * elements[3][2])
                - elements[1][2] * (elements[2][0] * elements[3][3] - elements[2][3] * elements[3][0])
                + elements[1][3] * (elements[2][0] * elements[3][2] - elements[2][2] * elements[3][0]))
                + elements[0][2] * (elements[1][0] * (elements[2][1] * elements[3][3] - elements[2][3] * elements[3][1])
                - elements[1][1] * (elements[2][0] * elements[3][3] - elements[2][3] * elements[3][0])
                + elements[1][3] * (elements[2][0] * elements[3][1] - elements[2][1] * elements[3][0]))
                - elements[0][3] * (elements[1][0] * (elements[2][1] * elements[3][2] - elements[2][2] * elements[3][1])
                - elements[1][1] * (elements[2][0] * elements[3][2] - elements[2][2] * elements[3][0])
                + elements[1][2] * (elements[2][0] * elements[3][1] - elements[2][1] * elements[3][0]));
    }
}
