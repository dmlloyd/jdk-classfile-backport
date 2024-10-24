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
package io.github.dmlloyd.classfile.impl;

import io.github.dmlloyd.classfile.Label;
import io.github.dmlloyd.classfile.PseudoInstruction;
import io.github.dmlloyd.classfile.constantpool.ClassEntry;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.instruction.CharacterRange;
import io.github.dmlloyd.classfile.instruction.ExceptionCatch;
import io.github.dmlloyd.classfile.instruction.LocalVariable;
import io.github.dmlloyd.classfile.instruction.LocalVariableType;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public abstract sealed class AbstractPseudoInstruction
        extends AbstractElement
        implements PseudoInstruction {

    @Override
    public abstract void writeTo(DirectCodeBuilder writer);

    public static final class ExceptionCatchImpl
            extends AbstractPseudoInstruction
            implements ExceptionCatch {

        public final ClassEntry catchTypeEntry;
        public final Label handler;
        public final Label tryStart;
        public final Label tryEnd;

        public ExceptionCatchImpl(Label handler, Label tryStart, Label tryEnd,
                                  ClassEntry catchTypeEntry) {
            this.catchTypeEntry = catchTypeEntry;
            this.handler = requireNonNull(handler);
            this.tryStart = requireNonNull(tryStart);
            this.tryEnd = requireNonNull(tryEnd);
        }

        public ExceptionCatchImpl(Label handler, Label tryStart, Label tryEnd,
                                  Optional<ClassEntry> catchTypeEntry) {
            this(handler, tryStart, tryEnd, catchTypeEntry.orElse(null));
        }

        @Override
        public Label tryStart() {
            return tryStart;
        }

        @Override
        public Label handler() {
            return handler;
        }

        @Override
        public Label tryEnd() {
            return tryEnd;
        }

        @Override
        public Optional<ClassEntry> catchType() {
            return Optional.ofNullable(catchTypeEntry);
        }

        ClassEntry catchTypeEntry() {
            return catchTypeEntry;
        }

        @Override
        public void writeTo(DirectCodeBuilder writer) {
            writer.addHandler(this);
        }

        @Override
        public String toString() {
            return String.format("ExceptionCatch[catchType=%s]", catchTypeEntry == null ? "<any>" : catchTypeEntry.name().stringValue());
        }
    }

    public static final class UnboundCharacterRange
            extends AbstractPseudoInstruction
            implements CharacterRange {

        public final Label startScope;
        public final Label endScope;
        public final int characterRangeStart;
        public final int characterRangeEnd;
        public final int flags;

        public UnboundCharacterRange(Label startScope, Label endScope, int characterRangeStart,
                                     int characterRangeEnd, int flags) {
            this.startScope = requireNonNull(startScope);
            this.endScope = requireNonNull(endScope);
            this.characterRangeStart = characterRangeStart;
            this.characterRangeEnd = characterRangeEnd;
            this.flags = flags;
        }

        @Override
        public Label startScope() {
            return startScope;
        }

        @Override
        public Label endScope() {
            return endScope;
        }

        @Override
        public int characterRangeStart() {
            return characterRangeStart;
        }

        @Override
        public int characterRangeEnd() {
            return characterRangeEnd;
        }

        @Override
        public int flags() {
            return flags;
        }

        @Override
        public void writeTo(DirectCodeBuilder writer) {
            writer.addCharacterRange(this);
        }

    }

    private abstract static sealed class AbstractLocalPseudo extends AbstractPseudoInstruction
            implements Util.WritableLocalVariable {
        protected final int slot;
        protected final Utf8Entry name;
        protected final Utf8Entry descriptor;
        protected final Label startScope;
        protected final Label endScope;

        public AbstractLocalPseudo(int slot, Utf8Entry name, Utf8Entry descriptor, Label startScope, Label endScope) {
            BytecodeHelpers.validateSlot(slot);
            this.slot = slot;
            this.name = requireNonNull(name);
            this.descriptor = requireNonNull(descriptor);
            this.startScope = requireNonNull(startScope);
            this.endScope = requireNonNull(endScope);
        }

        public int slot() {
            return slot;
        }

        public Utf8Entry name() {
            return name;
        }

        public String nameString() {
            return name.stringValue();
        }

        public Label startScope() {
            return startScope;
        }

        public Label endScope() {
            return endScope;
        }

        @Override
        public boolean writeLocalTo(BufWriterImpl b) {
            var lc = b.labelContext();
            int startBci = lc.labelToBci(startScope());
            int endBci = lc.labelToBci(endScope());
            if (startBci == -1 || endBci == -1) {
                return false;
            }
            int length = endBci - startBci;
            b.writeU2U2(startBci, length);
            b.writeU2U2U2(b.cpIndex(name), b.cpIndex(descriptor), slot());
            return true;
        }
    }

    public static final class UnboundLocalVariable extends AbstractLocalPseudo
            implements LocalVariable {

        public UnboundLocalVariable(int slot, Utf8Entry name, Utf8Entry descriptor, Label startScope, Label endScope) {
            super(slot, name, descriptor, startScope, endScope);
        }

        @Override
        public Utf8Entry type() {
            return descriptor;
        }

        @Override
        public void writeTo(DirectCodeBuilder writer) {
            writer.addLocalVariable(this);
        }

        @Override
        public String toString() {
            return "LocalVariable[Slot=" + slot()
                   + ", name=" + nameString()
                   + ", descriptor='" + type().stringValue()
                   + "']";
        }
    }

    public static final class UnboundLocalVariableType extends AbstractLocalPseudo
            implements LocalVariableType {

        public UnboundLocalVariableType(int slot, Utf8Entry name, Utf8Entry signature, Label startScope, Label endScope) {
            super(slot, name, signature, startScope, endScope);
        }

        @Override
        public Utf8Entry signature() {
            return descriptor;
        }

        @Override
        public void writeTo(DirectCodeBuilder writer) {
            writer.addLocalVariableType(this);
        }

        @Override
        public String toString() {
            return "LocalVariableType[Slot=" + slot()
                   + ", name=" + nameString()
                   + ", signature='" + signature().stringValue()
                   + "']";
        }
    }
}
