/*
 * Copyright (c) 2022, 2024, Oracle and/or its affiliates. All rights reserved.
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

import io.github.dmlloyd.classfile.Attribute;
import io.github.dmlloyd.classfile.ClassElement;
import io.github.dmlloyd.classfile.CodeElement;
import io.github.dmlloyd.classfile.FieldElement;
import io.github.dmlloyd.classfile.MethodElement;
import io.github.dmlloyd.classfile.TypeAnnotation;
import java.util.List;

import io.github.dmlloyd.classfile.impl.BoundAttribute;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;

/**
 * Models the {@code RuntimeVisibleTypeAnnotations} attribute (JVMS {@jvms 4.7.20}), which
 * can appear on classes, methods, fields, and code attributes. Delivered as a
 * {@link io.github.dmlloyd.classfile.ClassElement}, {@link io.github.dmlloyd.classfile.FieldElement},
 * {@link io.github.dmlloyd.classfile.MethodElement}, or {@link CodeElement} when traversing
 * the corresponding model type.
 * <p>
 * The attribute does not permit multiple instances in a given location.
 * Subsequent occurrence of the attribute takes precedence during the attributed
 * element build or transformation.
 * <p>
 * The attribute was introduced in the Java SE Platform version 8.
 *
 * @since 24
 */
public sealed interface RuntimeVisibleTypeAnnotationsAttribute
        extends Attribute<RuntimeVisibleTypeAnnotationsAttribute>,
                ClassElement, MethodElement, FieldElement, CodeElement
        permits BoundAttribute.BoundRuntimeVisibleTypeAnnotationsAttribute,
                UnboundAttribute.UnboundRuntimeVisibleTypeAnnotationsAttribute {

    /**
     * {@return the runtime-visible type annotations on parts of this class, field, or method}
     */
    List<TypeAnnotation> annotations();

    /**
     * {@return a {@code RuntimeVisibleTypeAnnotations} attribute}
     * @param annotations the annotations
     */
    static RuntimeVisibleTypeAnnotationsAttribute of(List<TypeAnnotation> annotations) {
        return new UnboundAttribute.UnboundRuntimeVisibleTypeAnnotationsAttribute(annotations);
    }

    /**
     * {@return a {@code RuntimeVisibleTypeAnnotations} attribute}
     * @param annotations the annotations
     */
    static RuntimeVisibleTypeAnnotationsAttribute of(TypeAnnotation... annotations) {
        return of(List.of(annotations));
    }
}
