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
package io.github.dmlloyd.classfile.instruction;

import io.github.dmlloyd.classfile.CodeBuilder;
import io.github.dmlloyd.classfile.CodeElement;
import io.github.dmlloyd.classfile.CodeModel;
import io.github.dmlloyd.classfile.CodeTransform;
import io.github.dmlloyd.classfile.Label;
import io.github.dmlloyd.classfile.PseudoInstruction;
import io.github.dmlloyd.classfile.attribute.CodeAttribute;

import io.github.dmlloyd.classfile.impl.LabelImpl;

/**
 * A pseudo-instruction which indicates that the specified label corresponds to
 * the current position in the {@code Code} attribute.  Delivered as a {@link
 * CodeElement} during traversal of the elements of a {@link CodeModel}.
 * <p>
 * This can be used to inspect the target position of labels across {@linkplain
 * CodeTransform transformations}, as {@linkplain CodeAttribute#labelToBci bci}
 * is not stable.
 * <p>
 * When passed to a {@link CodeBuilder}, this pseudo-instruction sets the
 * specified label to be bound at the current position in the builder.
 * <p>
 * By design, {@code LabelTarget} cannot be created by users and can only be
 * read from a code model.  Use {@link CodeBuilder#labelBinding
 * CodeBuilder::labelBinding} to bind arbitrary labels to a {@code CodeBuilder}.
 * <p>
 * For a {@code CodeBuilder cob}, a {@code LabelTarget lt}, these two calls are
 * equivalent:
 * {@snippet lang=java :
 * cob.with(lt); // @link substring="with" target="CodeBuilder#with"
 * // @link substring="labelBinding" target="CodeBuilder#labelBinding" :
 * cob.labelBinding(lt.label()); // @link regex="label(?=\()" target="#label"
 * }
 *
 * @see Label
 * @see CodeBuilder#labelBinding CodeBuilder::labelBinding
 * @since 24
 */
public sealed interface LabelTarget extends PseudoInstruction
        permits LabelImpl {

    /**
     * {@return the label corresponding to this target}
     */
    Label label();
}
