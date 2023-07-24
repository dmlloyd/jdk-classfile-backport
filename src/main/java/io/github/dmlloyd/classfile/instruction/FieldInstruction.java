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
package io.github.dmlloyd.classfile.instruction;

import java.lang.constant.ClassDesc;

import io.github.dmlloyd.classfile.CodeElement;
import io.github.dmlloyd.classfile.CodeModel;
import io.github.dmlloyd.classfile.constantpool.ClassEntry;
import io.github.dmlloyd.classfile.Instruction;
import io.github.dmlloyd.classfile.Opcode;
import io.github.dmlloyd.classfile.constantpool.FieldRefEntry;
import io.github.dmlloyd.classfile.constantpool.NameAndTypeEntry;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.impl.AbstractInstruction;
import io.github.dmlloyd.classfile.impl.TemporaryConstantPool;
import io.github.dmlloyd.classfile.impl.Util;

/**
 * Models a field access instruction in the {@code code} array of a {@code Code}
 * attribute.  Corresponding opcodes will have a {@code kind} of {@link
 * Opcode.Kind#FIELD_ACCESS}.  Delivered as a {@link CodeElement} when
 * traversing the elements of a {@link CodeModel}.
 */
public sealed interface FieldInstruction extends Instruction
        permits AbstractInstruction.BoundFieldInstruction, AbstractInstruction.UnboundFieldInstruction {
    /**
     * {@return the {@link FieldRefEntry} constant described by this instruction}
     */
    FieldRefEntry field();

    /**
     * {@return the class holding the field}
     */
    default ClassEntry owner() {
        return field().owner();
    }

    /**
     * {@return the name of the field}
     */
    default Utf8Entry name() {
        return field().nameAndType().name();
    }

    /**
     * {@return the field descriptor of the field}
     */
    default Utf8Entry type() {
        return field().nameAndType().type();
    }

    /**
     * {@return a symbolic descriptor for the type of the field}
     */
    default ClassDesc typeSymbol() {
        return Util.fieldTypeSymbol(field().nameAndType());
    }

    /**
     * {@return a field access instruction}
     *
     * @param op the opcode for the specific type of field access instruction,
     *           which must be of kind {@link Opcode.Kind#FIELD_ACCESS}
     * @param field a constant pool entry describing the field
     */
    static FieldInstruction of(Opcode op, FieldRefEntry field) {
        Util.checkKind(op, Opcode.Kind.FIELD_ACCESS);
        return new AbstractInstruction.UnboundFieldInstruction(op, field);
    }

    /**
     * {@return a field access instruction}
     *
     * @param op the opcode for the specific type of field access instruction,
     *           which must be of kind {@link Opcode.Kind#FIELD_ACCESS}
     * @param owner the class holding the field
     * @param name the name of the field
     * @param type the field descriptor
     */
    static FieldInstruction of(Opcode op,
                               ClassEntry owner,
                               Utf8Entry name,
                               Utf8Entry type) {
        return of(op, owner, TemporaryConstantPool.INSTANCE.nameAndTypeEntry(name, type));
    }

    /**
     * {@return a field access instruction}
     *
     * @param op the opcode for the specific type of field access instruction,
     *           which must be of kind {@link Opcode.Kind#FIELD_ACCESS}
     * @param owner the class holding the field
     * @param nameAndType the name and field descriptor of the field
     */
    static FieldInstruction of(Opcode op,
                               ClassEntry owner,
                               NameAndTypeEntry nameAndType) {
        return of(op, TemporaryConstantPool.INSTANCE.fieldRefEntry(owner, nameAndType));
    }
}
