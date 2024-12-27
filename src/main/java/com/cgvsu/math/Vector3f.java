package com.cgvsu.math;

import java.util.Objects;

public class Vector3f {
    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3f sub(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    public final void mul(Vector3f var1, Vector3f var2) {
        this.x = var1.y * var2.z - var1.z * var2.y;
        this.y = -(var1.x * var2.z - var1.z * var2.x);
        this.z = var1.x * var2.y - var1.y * var2.x;
    }
    public Vector3f divide(float scalar) {
        if (scalar == 0) {
            throw new IllegalArgumentException("Мы не делим на 0");
        }
        return new Vector3f(this.x / scalar, this.y / scalar, this.z / scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        float length = length();
        if (length == 0) {
            throw new ArithmeticException("Ты точно хочешь нормализовать 0-вектор?");
        }
        return this.divide(length);
    }

    public float dotProduct(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3f crossProduct(Vector3f other) {
        float resultX = this.y * other.z - this.z * other.y;
        float resultY = this.z * other.x - this.x * other.z;
        float resultZ = this.x * other.y - this.y * other.x;
        return new Vector3f(resultX, resultY, resultZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector3f vector3f = (Vector3f) o;
        return Float.compare(x, vector3f.x) == 0 && Float.compare(y, vector3f.y) == 0 && Float.compare(z, vector3f.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public String coordsToStringSplitBySpace() {
        return String.format("%f %f %f", x, y, z);
    }
}