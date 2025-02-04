/*
 * Copyright (c) 2022, 2025, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package io.github.dmlloyd.classfile.attribute;

import io.github.dmlloyd.classfile.AnnotationValue;
import io.github.dmlloyd.classfile.Attribute;
import io.github.dmlloyd.classfile.AttributeMapper;
import io.github.dmlloyd.classfile.AttributeMapper.AttributeStability;
import io.github.dmlloyd.classfile.Attributes;
import io.github.dmlloyd.classfile.ClassFile;
import io.github.dmlloyd.classfile.MethodElement;
import java.lang.reflect.Method;

import io.github.dmlloyd.classfile.impl.BoundAttribute;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;

/**
 * Models the {@link Attributes#annotationDefault() AnnotationDefault} attribute
 * (JVMS {@jvms 4.7.22}), which records the default value (JLS {@jls 9.6.2}) for
 * the annotation interface element defined by this method.
 * <p>
 * This attribute only appears on methods, and does not permit {@linkplain
 * AttributeMapper#allowMultiple multiple instances} in a method.  It has a
 * data dependency on the {@linkplain AttributeStability#CP_REFS constant pool}.
 * <p>
 * This attribute was introduced in the Java SE Platform version 5.0, major
 * version {@value ClassFile#JAVA_5_VERSION}.
 *
 * @see Attributes#annotationDefault()
 * @jls 9.6.2 Defaults for Annotation Interface Elements
 * @jvms 4.7.22 The {@code AnnotationDefault} Attribute
 * @since 24
 */
public sealed interface AnnotationDefaultAttribute
        extends Attribute<AnnotationDefaultAttribute>, MethodElement
        permits BoundAttribute.BoundAnnotationDefaultAttr,
                UnboundAttribute.UnboundAnnotationDefaultAttribute {

    /**
     * {@return the default value of the annotation interface element defined by
     * the enclosing method}
     *
     * @see Method#getDefaultValue()
     */
    AnnotationValue defaultValue();

    /**
     * {@return an {@code AnnotationDefault} attribute}
     * @param annotationDefault the default value of the annotation interface element
     */
    static AnnotationDefaultAttribute of(AnnotationValue annotationDefault) {
        return new UnboundAttribute.UnboundAnnotationDefaultAttribute(annotationDefault);
    }
}
