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

import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.instruction.LocalVariable;
import java.lang.constant.ClassDesc;

import io.github.dmlloyd.classfile.impl.BoundLocalVariable;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;
import io.github.dmlloyd.classfile.impl.Util;

/**
 * Models a single local variable in the {@link LocalVariableTableAttribute}.
 *
 * @see LocalVariableTableAttribute#localVariables()
 * @see LocalVariable
 * @jvms 4.7.13 The {@code LocalVaribleTable} Attribute
 * @since 24
 */
public sealed interface LocalVariableInfo
        permits UnboundAttribute.UnboundLocalVariableInfo, BoundLocalVariable {

    /**
     * {@return the index into the code array, inclusive, at which the scope of
     * this variable begins}
     */
    int startPc();

    /**
     * {@return the length of the region of the code array in which this
     * variable is in scope}
     */
    int length();

    /**
     * {@return the name of the local variable}
     */
    Utf8Entry name();

    /**
     * {@return the field descriptor string of the local variable}
     */
    Utf8Entry type();

    /**
     * {@return the field descriptor of the local variable}
     */
    default ClassDesc typeSymbol() {
        return Util.fieldTypeSymbol(type());
    }

    /**
     * {@return the index into the local variable array of the current frame
     * which holds this local variable}
     */
    int slot();
}
