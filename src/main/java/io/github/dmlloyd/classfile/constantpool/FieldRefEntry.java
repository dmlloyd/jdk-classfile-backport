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

import java.lang.constant.ClassDesc;

import io.github.dmlloyd.classfile.impl.AbstractPoolEntry;
import io.github.dmlloyd.classfile.impl.Util;

/**
 * Models a {@code CONSTANT_Fieldref_info} structure, or a symbolic reference
 * to a field, in the constant pool of a {@code class} file.
 * <p>
 * A field reference constant pool entry is composite:
 * {@snippet lang=text :
 * // @link substring="FieldRefEntry" target="ConstantPoolBuilder#fieldRefEntry(ClassEntry, NameAndTypeEntry)" :
 * FieldRefEntry(
 *     ClassEntry owner, // @link substring="owner" target="#owner()"
 *     NameAndTypeEntry nameAndType // @link substring="nameAndType" target="#nameAndType()"
 * )
 * }
 * where the {@link #type() nameAndType.type()} represents a {@linkplain
 * #typeSymbol() field descriptor} string.
 *
 * @see ConstantPoolBuilder#fieldRefEntry ConstantPoolBuilder::fieldRefEntry
 * @jvms 4.4.2 The {@code CONSTANT_Fieldref_info}, {@code
 *             CONSTANT_Methodref_info}, and {@code
 *             CONSTANT_InterfaceMethodref_info} Structures
 * @since 24
 */
public sealed interface FieldRefEntry extends MemberRefEntry
        permits AbstractPoolEntry.FieldRefEntryImpl {

    /**
     * {@return a symbolic descriptor for the {@linkplain #type() field type}}
     */
    default ClassDesc typeSymbol() {
        return Util.fieldTypeSymbol(type());
    }
}
