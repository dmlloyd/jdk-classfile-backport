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
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;

import io.github.dmlloyd.classfile.impl.BoundAttribute;
import io.github.dmlloyd.classfile.impl.TemporaryConstantPool;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;

/**
 * Models the {@code CompilationID} attribute (@@@ need reference), which can
 * appear on classes and records the compilation time of the class.  Delivered
 * as a {@link io.github.dmlloyd.classfile.ClassElement} when traversing the elements of
 * a {@link io.github.dmlloyd.classfile.ClassModel}.
 * <p>
 * The attribute does not permit multiple instances in a given location.
 * Subsequent occurrence of the attribute takes precedence during the attributed
 * element build or transformation.
 *
 * @since 24
 */
public sealed interface CompilationIDAttribute
        extends Attribute<CompilationIDAttribute>, ClassElement
        permits BoundAttribute.BoundCompilationIDAttribute,
                UnboundAttribute.UnboundCompilationIDAttribute {

    /**
     * {@return the compilation ID}  The compilation ID is the value of
     * {@link System#currentTimeMillis()} when the classfile is generated.
     */
    Utf8Entry compilationId();

    /**
     * {@return a {@code CompilationID} attribute}
     * @param id the compilation ID
     */
    static CompilationIDAttribute of(Utf8Entry id) {
        return new UnboundAttribute.UnboundCompilationIDAttribute(id);
    }

    /**
     * {@return a {@code CompilationID} attribute}
     * @param id the compilation ID
     */
    static CompilationIDAttribute of(String id) {
        return new UnboundAttribute.UnboundCompilationIDAttribute(TemporaryConstantPool.INSTANCE.utf8Entry(id));
    }
}
