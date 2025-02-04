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

import io.github.dmlloyd.classfile.CodeBuilder;
import io.github.dmlloyd.classfile.instruction.LineNumber;

import io.github.dmlloyd.classfile.impl.UnboundAttribute;

/**
 * Models a single line number entry in the {@link LineNumberTableAttribute}.
 *
 * @see LineNumberTableAttribute#lineNumbers()
 * @see LineNumber
 * @since 24
 */
public sealed interface LineNumberInfo
        permits UnboundAttribute.UnboundLineNumberInfo {

    /**
     * {@return the index into the code array at which the code for this line
     * begins}
     */
    int startPc();

    /**
     * {@return the line number within the original source file}
     */
    int lineNumber();

    /**
     * {@return a line number description}
     *
     * @apiNote
     * The created entry cannot be written to a {@link CodeBuilder}.  Call
     * {@link CodeBuilder#lineNumber CodeBuilder::lineNumber} in the correct
     * order instead.
     *
     * @param startPc the starting index of the code array for this line
     * @param lineNumber the line number within the original source file
     */
    public static LineNumberInfo of(int startPc, int lineNumber) {
        return new UnboundAttribute.UnboundLineNumberInfo(startPc, lineNumber);
    }
}
