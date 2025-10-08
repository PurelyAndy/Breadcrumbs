/*
 * The MIT License
 *
 * Copyright (c) 2016-2024 JOML
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

//? if <=1.19.2 {
/*package org.joml;

/^*
 * Interface to a read-only view of a 4x4 matrix of single-precision floats.
 * 
 * @author Kai Burjack
 ^/
public interface Matrix4fc {

    /^*
     * Bit returned by {@link #properties()} to indicate that the matrix represents an unknown transformation.
     ^/
    byte PROPERTY_UNKNOWN = 0;
    /^*
     * Bit returned by {@link #properties()} to indicate that the matrix represents a perspective transformation.
     ^/
    byte PROPERTY_PERSPECTIVE = 1<<0;
    /^*
     * Bit returned by {@link #properties()} to indicate that the matrix represents an affine transformation.
     ^/
    byte PROPERTY_AFFINE = 1<<1;
    /^*
     * Bit returned by {@link #properties()} to indicate that the matrix represents the identity transformation.
     * This implies {@link #PROPERTY_AFFINE}, {@link #PROPERTY_TRANSLATION} and {@link #PROPERTY_ORTHONORMAL}.
     ^/
    byte PROPERTY_IDENTITY = 1<<2;
    /^*
     * Bit returned by {@link #properties()} to indicate that the matrix represents a pure translation transformation.
     * This implies {@link #PROPERTY_AFFINE} and {@link #PROPERTY_ORTHONORMAL}.
     ^/
    byte PROPERTY_TRANSLATION = 1<<3;
    /^*
     * Bit returned by {@link #properties()} to indicate that the upper-left 3x3 submatrix represents an orthogonal
     * matrix (i.e. orthonormal basis). For practical reasons, this property also always implies
     * {@link #PROPERTY_AFFINE} in this implementation.
     ^/
    byte PROPERTY_ORTHONORMAL = 1<<4;

    /^*
     * Return the assumed properties of this matrix. This is a bit-combination of
     * {@link #PROPERTY_IDENTITY}, {@link #PROPERTY_AFFINE},
     * {@link #PROPERTY_TRANSLATION} and {@link #PROPERTY_PERSPECTIVE}.
     * 
     * @return the properties of the matrix
     ^/
    int properties();

    /^*
     * Return the value of the matrix element at column 0 and row 0.
     * 
     * @return the value of the matrix element
     ^/
    float m00();

    /^*
     * Return the value of the matrix element at column 0 and row 1.
     * 
     * @return the value of the matrix element
     ^/
    float m01();

    /^*
     * Return the value of the matrix element at column 0 and row 2.
     * 
     * @return the value of the matrix element
     ^/
    float m02();

    /^*
     * Return the value of the matrix element at column 0 and row 3.
     * 
     * @return the value of the matrix element
     ^/
    float m03();

    /^*
     * Return the value of the matrix element at column 1 and row 0.
     * 
     * @return the value of the matrix element
     ^/
    float m10();

    /^*
     * Return the value of the matrix element at column 1 and row 1.
     * 
     * @return the value of the matrix element
     ^/
    float m11();

    /^*
     * Return the value of the matrix element at column 1 and row 2.
     * 
     * @return the value of the matrix element
     ^/
    float m12();

    /^*
     * Return the value of the matrix element at column 1 and row 3.
     * 
     * @return the value of the matrix element
     ^/
    float m13();

    /^*
     * Return the value of the matrix element at column 2 and row 0.
     * 
     * @return the value of the matrix element
     ^/
    float m20();

    /^*
     * Return the value of the matrix element at column 2 and row 1.
     * 
     * @return the value of the matrix element
     ^/
    float m21();

    /^*
     * Return the value of the matrix element at column 2 and row 2.
     * 
     * @return the value of the matrix element
     ^/
    float m22();

    /^*
     * Return the value of the matrix element at column 2 and row 3.
     * 
     * @return the value of the matrix element
     ^/
    float m23();

    /^*
     * Return the value of the matrix element at column 3 and row 0.
     * 
     * @return the value of the matrix element
     ^/
    float m30();

    /^*
     * Return the value of the matrix element at column 3 and row 1.
     * 
     * @return the value of the matrix element
     ^/
    float m31();

    /^*
     * Return the value of the matrix element at column 3 and row 2.
     * 
     * @return the value of the matrix element
     ^/
    float m32();

    /^*
     * Return the value of the matrix element at column 3 and row 3.
     * 
     * @return the value of the matrix element
     ^/
    float m33();
}
*///?}
