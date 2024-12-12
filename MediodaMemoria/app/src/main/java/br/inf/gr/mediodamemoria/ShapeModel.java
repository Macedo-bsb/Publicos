package br.inf.gr.mediodamemoria;

import java.util.Objects;

class Shape {
    private final int shape;
    private final int color;

    public Shape(int shape, int color) {
        this.shape = shape;
        this.color = color;
    }

    public int getShape() {
        return shape;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shape shape1 = (Shape) o;
        return shape == shape1.shape && color == shape1.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, color);
    }

    @Override
    public String toString() {
        return "Shape{" +
                "shape=" + shape +
                ", color=" + color +
                '}';
    }
}
