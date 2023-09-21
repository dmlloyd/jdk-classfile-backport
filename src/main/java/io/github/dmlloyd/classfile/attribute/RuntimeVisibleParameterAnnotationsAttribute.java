/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.Attribute;
import io.github.dmlloyd.classfile.MethodElement;
import io.github.dmlloyd.classfile.MethodModel;
import io.github.dmlloyd.classfile.impl.BoundAttribute;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;

/**
 * Models the {@code RuntimeVisibleParameterAnnotations} attribute {@jvms 4.7.18}, which
 * can appear on methods. Delivered as a {@link MethodElement}
 * when traversing a {@link MethodModel}.
 *
 * @apiNote The attribute does not permit multiple instances in a given location.
 * Subsequent occurrence of the attribute takes precedence during the attributed
 * element build or transformation.
 */
public sealed interface RuntimeVisibleParameterAnnotationsAttribute
        extends Attribute<RuntimeVisibleParameterAnnotationsAttribute>, MethodElement
        permits BoundAttribute.BoundRuntimeVisibleParameterAnnotationsAttribute,
                UnboundAttribute.UnboundRuntimeVisibleParameterAnnotationsAttribute {

    /**
     * {@return the list of annotations corresponding to each method parameter}
     * The element at the i'th index corresponds to the annotations on the i'th
     * parameter.
     */
    List<List<Annotation>> parameterAnnotations();

    /**
     * {@return a {@code RuntimeVisibleParameterAnnotations} attribute}
     * @param parameterAnnotations a list of parameter annotations for each parameter
     */
    static RuntimeVisibleParameterAnnotationsAttribute of(List<List<Annotation>> parameterAnnotations) {
        return new UnboundAttribute.UnboundRuntimeVisibleParameterAnnotationsAttribute(parameterAnnotations);
    }
}
