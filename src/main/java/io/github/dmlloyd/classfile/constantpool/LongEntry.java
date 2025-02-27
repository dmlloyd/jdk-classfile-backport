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
package io.github.dmlloyd.classfile.constantpool;

import io.github.dmlloyd.classfile.TypeKind;

import io.github.dmlloyd.classfile.impl.AbstractPoolEntry;

/**
 * Models a {@code CONSTANT_Long_info} structure, or a {@code long} constant, in
 * the constant pool of a {@code class} file.
 * <p>
 * The use of a {@code LongEntry} is modeled by a {@code long}.  Conversions are
 * through {@link ConstantPoolBuilder#longEntry(long)} and {@link #longValue()}.
 * <p>
 * A long entry has a {@linkplain #width() width} of {@code 2}, making its
 * subsequent constant pool index valid and unusable.
 *
 * @see ConstantPoolBuilder#longEntry ConstantPoolBuilder::longEntry
 * @jvms 4.4.5 The {@code CONSTANT_Long_info} and {@code CONSTANT_Double_info}
 *             Structures
 * @since 24
 */
public sealed interface LongEntry
        extends AnnotationConstantValueEntry, ConstantValueEntry
        permits AbstractPoolEntry.LongEntryImpl {

    /**
     * {@return the {@code long} value}
     *
     * @see ConstantPoolBuilder#longEntry(long)
     *      ConstantPoolBuilder::longEntry(long)
     */
    long longValue();

    /**
     * {@return the type of the constant}
     */
    @Override
    default TypeKind typeKind() {
        return TypeKind.LONG;
    }
}
