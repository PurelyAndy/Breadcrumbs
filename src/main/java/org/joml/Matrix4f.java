/*
 * The MIT License
 *
 * Copyright (c) 2015-2024 Richard Greenlees
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

import org.intellij.lang.annotations.MagicConstant;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/^*
 * Contains the definition of a 4x4 matrix of floats, and associated functions to transform
 * it. The matrix is column-major to match OpenGL's interpretation, and it looks like this:
 * <p>
 *      m00  m10  m20  m30<br>
 *      m01  m11  m21  m31<br>
 *      m02  m12  m22  m32<br>
 *      m03  m13  m23  m33<br>
 * 
 * @author Richard Greenlees
 * @author Kai Burjack
 ^/
public class Matrix4f implements Externalizable, Cloneable, Matrix4fc {

    private static final long serialVersionUID = 1L;

    int properties;
    float m00, m01, m02, m03;
    float m10, m11, m12, m13;
    float m20, m21, m22, m23;
    float m30, m31, m32, m33;

    /^*
     * Create a new {@link Matrix4f} and set it to {@link #identity() identity}.
     ^/
    public Matrix4f() {
        this._m00(1.0f)
            ._m11(1.0f)
            ._m22(1.0f)
            ._m33(1.0f)
            ._properties(PROPERTY_IDENTITY | PROPERTY_AFFINE | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL);
    }

    public Matrix4f set(float m00, float m01, float m02, float m03,
                        float m10, float m11, float m12, float m13,
                        float m20, float m21, float m22, float m23,
                        float m30, float m31, float m32, float m33) {
        return this
                ._m00(m00)
                ._m10(m10)
                ._m20(m20)
                ._m30(m30)
                ._m01(m01)
                ._m11(m11)
                ._m21(m21)
                ._m31(m31)
                ._m02(m02)
                ._m12(m12)
                ._m22(m22)
                ._m32(m32)
                ._m03(m03)
                ._m13(m13)
                ._m23(m23)
                ._m33(m33)
                .determineProperties();
    }
    /^*
     * Create a new 4x4 matrix using the supplied float values.
     * <p>
     * The matrix layout will be:<br><br>
     *   m00, m10, m20, m30<br>
     *   m01, m11, m21, m31<br>
     *   m02, m12, m22, m32<br>
     *   m03, m13, m23, m33
     *
     * @param m00
     *          the value of m00
     * @param m01
     *          the value of m01
     * @param m02
     *          the value of m02
     * @param m03
     *          the value of m03
     * @param m10
     *          the value of m10
     * @param m11
     *          the value of m11
     * @param m12
     *          the value of m12
     * @param m13
     *          the value of m13
     * @param m20
     *          the value of m20
     * @param m21
     *          the value of m21
     * @param m22
     *          the value of m22
     * @param m23
     *          the value of m23
     * @param m30
     *          the value of m30
     * @param m31
     *          the value of m31
     * @param m32
     *          the value of m32
     * @param m33
     *          the value of m33
     ^/
    public Matrix4f(float m00, float m01, float m02, float m03,
                    float m10, float m11, float m12, float m13,
                    float m20, float m21, float m22, float m23,
                    float m30, float m31, float m32, float m33) {
        this._m00(m00)
            ._m01(m01)
            ._m02(m02)
            ._m03(m03)
            ._m10(m10)
            ._m11(m11)
            ._m12(m12)
            ._m13(m13)
            ._m20(m20)
            ._m21(m21)
            ._m22(m22)
            ._m23(m23)
            ._m30(m30)
            ._m31(m31)
            ._m32(m32)
            ._m33(m33)
            .determineProperties();
    }

    Matrix4f _properties(int properties) {
        this.properties = properties;
        return this;
    }

    /^*
     * Assume the given properties about this matrix.
     * <p>
     * Use one or multiple of 0, {@link Matrix4fc#PROPERTY_IDENTITY},
     * {@link Matrix4fc#PROPERTY_TRANSLATION}, {@link Matrix4fc#PROPERTY_AFFINE},
     * {@link Matrix4fc#PROPERTY_PERSPECTIVE}, {@link Matrix4fc#PROPERTY_ORTHONORMAL}.
     *
     * @param properties
     *          bitset of the properties to assume about this matrix
     * @return this
     ^/
    public Matrix4f assume(@MagicConstant(intValues = {PROPERTY_UNKNOWN, PROPERTY_IDENTITY, PROPERTY_TRANSLATION, PROPERTY_AFFINE, PROPERTY_ORTHONORMAL, PROPERTY_PERSPECTIVE}) int properties) {
        this._properties(properties);
        return this;
    }

    /^*
     * Compute and set the matrix properties returned by {@link #properties()} based
     * on the current matrix element values.
     *
     * @return this
     ^/
    public Matrix4f determineProperties() {
        int properties = PROPERTY_UNKNOWN;
        if (m03() == 0.0f && m13() == 0.0f) {
            if (m23() == 0.0f && m33() == 1.0f) {
                properties |= PROPERTY_AFFINE;
                if (m00() == 1.0f && m01() == 0.0f && m02() == 0.0f && m10() == 0.0f && m11() == 1.0f && m12() == 0.0f
                        && m20() == 0.0f && m21() == 0.0f && m22() == 1.0f) {
                    properties |= PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL;
                    if (m30() == 0.0f && m31() == 0.0f && m32() == 0.0f)
                        properties |= PROPERTY_IDENTITY;
                }
                /^
                 * We do not determine orthogonality, since it would require arbitrary epsilons
                 * and is rather expensive (6 dot products) in the worst case.
                 ^/
            } else if (m01() == 0.0f && m02() == 0.0f && m10() == 0.0f && m12() == 0.0f && m20() == 0.0f && m21() == 0.0f
                    && m30() == 0.0f && m31() == 0.0f && m33() == 0.0f) {
                properties |= PROPERTY_PERSPECTIVE;
            }
        }
        this.properties = properties;
        return this;
    }

    @MagicConstant(intValues = {PROPERTY_UNKNOWN, PROPERTY_IDENTITY, PROPERTY_TRANSLATION, PROPERTY_AFFINE, PROPERTY_ORTHONORMAL, PROPERTY_PERSPECTIVE})
    public int properties() {
        return properties;
    }

    public float m00() {
        return m00;
    }
    public float m01() {
        return m01;
    }
    public float m02() {
        return m02;
    }
    public float m03() {
        return m03;
    }
    public float m10() {
        return m10;
    }
    public float m11() {
        return m11;
    }
    public float m12() {
        return m12;
    }
    public float m13() {
        return m13;
    }
    public float m20() {
        return m20;
    }
    public float m21() {
        return m21;
    }
    public float m22() {
        return m22;
    }
    public float m23() {
        return m23;
    }
    public float m30() {
        return m30;
    }
    public float m31() {
        return m31;
    }
    public float m32() {
        return m32;
    }
    public float m33() {
        return m33;
    }

    /^*
     * Set the value of the matrix element at column 0 and row 0.
     *
     * @param m00
     *          the new value
     * @return this
     ^/
    public Matrix4f m00(float m00) {
        this.m00 = m00;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m00 != 1.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 1.
     *
     * @param m01
     *          the new value
     * @return this
     ^/
    public Matrix4f m01(float m01) {
        this.m01 = m01;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m01 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 2.
     *
     * @param m02
     *          the new value
     * @return this
     ^/
    public Matrix4f m02(float m02) {
        this.m02 = m02;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m02 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 3.
     *
     * @param m03
     *          the new value
     * @return this
     ^/
    public Matrix4f m03(float m03) {
        this.m03 = m03;
        if (m03 != 0.0f)
            properties = PROPERTY_UNKNOWN;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 0.
     *
     * @param m10
     *          the new value
     * @return this
     ^/
    public Matrix4f m10(float m10) {
        this.m10 = m10;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m10 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 1.
     *
     * @param m11
     *          the new value
     * @return this
     ^/
    public Matrix4f m11(float m11) {
        this.m11 = m11;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m11 != 1.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 2.
     *
     * @param m12
     *          the new value
     * @return this
     ^/
    public Matrix4f m12(float m12) {
        this.m12 = m12;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m12 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 3.
     *
     * @param m13
     *          the new value
     * @return this
     ^/
    public Matrix4f m13(float m13) {
        this.m13 = m13;
        if (m13 != 0.0f)
            properties = PROPERTY_UNKNOWN;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 0.
     *
     * @param m20
     *          the new value
     * @return this
     ^/
    public Matrix4f m20(float m20) {
        this.m20 = m20;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m20 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 1.
     *
     * @param m21
     *          the new value
     * @return this
     ^/
    public Matrix4f m21(float m21) {
        this.m21 = m21;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m21 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 2.
     *
     * @param m22
     *          the new value
     * @return this
     ^/
    public Matrix4f m22(float m22) {
        this.m22 = m22;
        properties &= ~PROPERTY_ORTHONORMAL;
        if (m22 != 1.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_TRANSLATION);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 3.
     *
     * @param m23
     *          the new value
     * @return this
     ^/
    public Matrix4f m23(float m23) {
        this.m23 = m23;
        if (m23 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_AFFINE | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 0.
     *
     * @param m30
     *          the new value
     * @return this
     ^/
    public Matrix4f m30(float m30) {
        this.m30 = m30;
        if (m30 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 1.
     *
     * @param m31
     *          the new value
     * @return this
     ^/
    public Matrix4f m31(float m31) {
        this.m31 = m31;
        if (m31 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 2.
     *
     * @param m32
     *          the new value
     * @return this
     ^/
    public Matrix4f m32(float m32) {
        this.m32 = m32;
        if (m32 != 0.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_PERSPECTIVE);
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 3.
     *
     * @param m33
     *          the new value
     * @return this
     ^/
    public Matrix4f m33(float m33) {
        this.m33 = m33;
        if (m33 != 0.0f)
            properties &= ~(PROPERTY_PERSPECTIVE);
        if (m33 != 1.0f)
            properties &= ~(PROPERTY_IDENTITY | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL | PROPERTY_AFFINE);
        return this;
    }

    /^*
     * Set the value of the matrix element at column 0 and row 0 without updating the properties of the matrix.
     *
     * @param m00
     *          the new value
     * @return this
     ^/
    Matrix4f _m00(float m00) {
        this.m00 = m00;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 1 without updating the properties of the matrix.
     *
     * @param m01
     *          the new value
     * @return this
     ^/
    Matrix4f _m01(float m01) {
        this.m01 = m01;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 2 without updating the properties of the matrix.
     *
     * @param m02
     *          the new value
     * @return this
     ^/
    Matrix4f _m02(float m02) {
        this.m02 = m02;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 0 and row 3 without updating the properties of the matrix.
     *
     * @param m03
     *          the new value
     * @return this
     ^/
    Matrix4f _m03(float m03) {
        this.m03 = m03;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 0 without updating the properties of the matrix.
     *
     * @param m10
     *          the new value
     * @return this
     ^/
    Matrix4f _m10(float m10) {
        this.m10 = m10;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 1 without updating the properties of the matrix.
     *
     * @param m11
     *          the new value
     * @return this
     ^/
    Matrix4f _m11(float m11) {
        this.m11 = m11;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 2 without updating the properties of the matrix.
     *
     * @param m12
     *          the new value
     * @return this
     ^/
    Matrix4f _m12(float m12) {
        this.m12 = m12;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 1 and row 3 without updating the properties of the matrix.
     *
     * @param m13
     *          the new value
     * @return this
     ^/
    Matrix4f _m13(float m13) {
        this.m13 = m13;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 0 without updating the properties of the matrix.
     *
     * @param m20
     *          the new value
     * @return this
     ^/
    Matrix4f _m20(float m20) {
        this.m20 = m20;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 1 without updating the properties of the matrix.
     *
     * @param m21
     *          the new value
     * @return this
     ^/
    Matrix4f _m21(float m21) {
        this.m21 = m21;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 2 without updating the properties of the matrix.
     *
     * @param m22
     *          the new value
     * @return this
     ^/
    Matrix4f _m22(float m22) {
        this.m22 = m22;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 2 and row 3 without updating the properties of the matrix.
     *
     * @param m23
     *          the new value
     * @return this
     ^/
    Matrix4f _m23(float m23) {
        this.m23 = m23;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 0 without updating the properties of the matrix.
     *
     * @param m30
     *          the new value
     * @return this
     ^/
    Matrix4f _m30(float m30) {
        this.m30 = m30;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 1 without updating the properties of the matrix.
     *
     * @param m31
     *          the new value
     * @return this
     ^/
    Matrix4f _m31(float m31) {
        this.m31 = m31;
        return this;
    }
    /^*
     * Set the value of the matrix element at column 3 and row 2 without updating the properties of the matrix.
     *
     * @param m32
     *          the new value
     * @return this
     ^/
    Matrix4f _m32(float m32) {
        this.m32 = m32;
        return this;
    }

    /^*
     * Set the value of the matrix element at column 3 and row 3 without updating the properties of the matrix.
     *
     * @param m33
     *          the new value
     * @return this
     ^/
    Matrix4f _m33(float m33) {
        this.m33 = m33;
        return this;
    }

    public Matrix4f identity() {
        if ((properties & PROPERTY_IDENTITY) != 0)
            return this;
        return
        _m00(1.0f).
        _m01(0.0f).
        _m02(0.0f).
        _m03(0.0f).
        _m10(0.0f).
        _m11(1.0f).
        _m12(0.0f).
        _m13(0.0f).
        _m20(0.0f).
        _m21(0.0f).
        _m22(1.0f).
        _m23(0.0f).
        _m30(0.0f).
        _m31(0.0f).
        _m32(0.0f).
        _m33(1.0f).
        _properties(PROPERTY_IDENTITY | PROPERTY_AFFINE | PROPERTY_TRANSLATION | PROPERTY_ORTHONORMAL);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(m00());
        out.writeFloat(m01());
        out.writeFloat(m02());
        out.writeFloat(m03());
        out.writeFloat(m10());
        out.writeFloat(m11());
        out.writeFloat(m12());
        out.writeFloat(m13());
        out.writeFloat(m20());
        out.writeFloat(m21());
        out.writeFloat(m22());
        out.writeFloat(m23());
        out.writeFloat(m30());
        out.writeFloat(m31());
        out.writeFloat(m32());
        out.writeFloat(m33());
    }

    public void readExternal(ObjectInput in) throws IOException {
        this._m00(in.readFloat())
                ._m01(in.readFloat())
                ._m02(in.readFloat())
                ._m03(in.readFloat())
                ._m10(in.readFloat())
                ._m11(in.readFloat())
                ._m12(in.readFloat())
                ._m13(in.readFloat())
                ._m20(in.readFloat())
                ._m21(in.readFloat())
                ._m22(in.readFloat())
                ._m23(in.readFloat())
                ._m30(in.readFloat())
                ._m31(in.readFloat())
                ._m32(in.readFloat())
                ._m33(in.readFloat())
                .determineProperties();
    }
}

*///?}