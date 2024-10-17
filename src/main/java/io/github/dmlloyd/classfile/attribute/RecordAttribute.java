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
import java.util.List;

import io.github.dmlloyd.classfile.impl.BoundAttribute;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;
import io.github.dmlloyd.classfile.extras.PreviewFeature;

/**
 * Models the {@code Record} attribute (JVMS {@jvms 4.7.30}), which can
 * appear on classes to indicate that this class is a record class.
 * Delivered as a {@link io.github.dmlloyd.classfile.ClassElement} when
 * traversing the elements of a {@link io.github.dmlloyd.classfile.ClassModel}.
 * <p>
 * The attribute does not permit multiple instances in a given location.
 * Subsequent occurrence of the attribute takes precedence during the attributed
 * element build or transformation.
 * <p>
 * The attribute was introduced in the Java SE Platform version 16.
 *
 * @since 22
 */
@PreviewFeature(feature = PreviewFeature.Feature.CLASSFILE_API)
public sealed interface RecordAttribute extends Attribute<RecordAttribute>, ClassElement
        permits BoundAttribute.BoundRecordAttribute, UnboundAttribute.UnboundRecordAttribute {

    /**
     * {@return the components of this record class}
     */
    List<RecordComponentInfo> components();

    /**
     * {@return a {@code Record} attribute}
     * @param components the record components
     */
    static RecordAttribute of(List<RecordComponentInfo> components) {
        return new UnboundAttribute.UnboundRecordAttribute(components);
    }

    /**
     * {@return a {@code Record} attribute}
     * @param components the record components
     */
    static RecordAttribute of(RecordComponentInfo... components) {
        return of(List.of(components));
    }
}
