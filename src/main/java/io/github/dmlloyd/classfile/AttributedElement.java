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
package io.github.dmlloyd.classfile;

import io.github.dmlloyd.classfile.attribute.CodeAttribute;
import io.github.dmlloyd.classfile.attribute.RecordComponentInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.github.dmlloyd.classfile.impl.AbstractUnboundModel;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ClassFileElement} describing a {@code class} file structure that has
 * attributes, such as a {@code class} file, a field, a method, a {@link
 * CodeAttribute Code} attribute, or a record component.
 * <p>
 * Unless otherwise specified, most attributes that can be discovered in a
 * {@link CompoundElement} implements the corresponding {@linkplain
 * ClassFileElement##membership membership subinterface} of {@code
 * ClassFileElement}, and can be sent to a {@link ClassFileBuilder} to be
 * integrated into the built structure.
 *
 * @see io.github.dmlloyd.classfile.attribute
 * @jvms 4.7 Attributes
 * @sealedGraph
 * @since 24
 */
public sealed interface AttributedElement extends ClassFileElement
        permits ClassModel, CodeModel, FieldModel, MethodModel,
                RecordComponentInfo, AbstractUnboundModel {

    /**
     * {@return the attributes of this structure}
     */
    List<Attribute<?>> attributes();

    /**
     * Finds an attribute by name.  This is suitable to find attributes that
     * {@linkplain AttributeMapper#allowMultiple() allow at most one instance}
     * in one structure.  If this is used to find attributes that allow multiple
     * instances in one structure, the first matching instance is returned.
     *
     * @apiNote
     * This can easily find an attribute and send it to another {@link
     * ClassFileBuilder}, which is a {@code Consumer}:
     * {@snippet lang=java :
     * MethodModel method = null; // @replace substring=null; replacement=...
     * MethodBuilder mb = null; // @replace substring=null; replacement=...
     * method.findAttribute(Attributes.code()).ifPresent(mb);
     * }
     *
     * @param attr the attribute mapper
     * @param <T> the type of the attribute
     * @return the attribute, or {@code Optional.empty()} if the attribute
     * is not present
     */
    default <T extends Attribute<T>> Optional<T> findAttribute(AttributeMapper<T> attr) {
        requireNonNull(attr);
        for (Attribute<?> la : attributes()) {
            if (la.attributeMapper() == attr) {
                @SuppressWarnings("unchecked")
                var res = Optional.of((T) la);
                return res;
            }
        }
        return Optional.empty();
    }

    /**
     * Finds attributes by name.  This is suitable to find attributes that
     * {@linkplain AttributeMapper#allowMultiple() allow multiple instances}
     * in one structure.
     *
     * @param attr the attribute mapper
     * @param <T> the type of the attribute
     * @return the attributes, or an empty {@code List} if the attribute
     * is not present
     */
    default <T extends Attribute<T>> List<T> findAttributes(AttributeMapper<T> attr) {
        requireNonNull(attr);
        var list = new ArrayList<T>();
        for (var a : attributes()) {
            if (a.attributeMapper() == attr) {
                @SuppressWarnings("unchecked")
                T t = (T)a;
                list.add(t);
            }
        }
        return Collections.unmodifiableList(list);
    }
}
