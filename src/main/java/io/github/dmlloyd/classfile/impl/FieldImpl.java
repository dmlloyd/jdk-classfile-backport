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

import io.github.dmlloyd.classfile.AccessFlags;
import io.github.dmlloyd.classfile.Attribute;
import io.github.dmlloyd.classfile.ClassModel;
import io.github.dmlloyd.classfile.ClassReader;
import io.github.dmlloyd.classfile.FieldElement;
import io.github.dmlloyd.classfile.FieldModel;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class FieldImpl
        extends AbstractElement
        implements FieldModel, Util.Writable {

    private final ClassReader reader;
    private final int startPos, endPos, attributesPos;
    private List<Attribute<?>> attributes;

    public FieldImpl(ClassReader reader, int startPos, int endPos, int attributesPos) {
        this.reader = reader;
        this.startPos = startPos;
        this.endPos = endPos;
        this.attributesPos = attributesPos;
    }

    @Override
    public AccessFlags flags() {
        return new AccessFlagsImpl(AccessFlag.Location.FIELD, reader.readU2(startPos));
    }

    @Override
    public Optional<ClassModel> parent() {
        if (reader instanceof ClassReaderImpl cri)
            return Optional.of(cri.getContainedClass());
        else
            return Optional.empty();
    }

    @Override
    public Utf8Entry fieldName() {
        return reader.readEntry(startPos + 2, Utf8Entry.class);
    }

    @Override
    public Utf8Entry fieldType() {
        return reader.readEntry(startPos + 4, Utf8Entry.class);
    }

    @Override
    public List<Attribute<?>> attributes() {
        if (attributes == null) {
            attributes = BoundAttribute.readAttributes(this, reader, attributesPos, reader.customAttributes());
        }
        return attributes;
    }

    @Override
    public void writeTo(BufWriterImpl buf) {
        if (buf.canWriteDirect(reader)) {
            reader.copyBytesTo(buf, startPos, endPos - startPos);
        }
        else {
            buf.writeU2U2U2(flags().flagsMask(),
                    buf.cpIndex(fieldName()),
                    buf.cpIndex(fieldType()));
            Util.writeAttributes(buf, attributes());
        }
    }

    // FieldModel

    @Override
    public void writeTo(DirectClassBuilder builder) {
        if (builder.canWriteDirect(reader)) {
            builder.withField(this);
        }
        else {
            builder.withField(fieldName(), fieldType(), Util.writingAll(this));
        }
    }

    @Override
    public void forEach(Consumer<? super FieldElement> consumer) {
        consumer.accept(flags());
        for (Attribute<?> attr : attributes()) {
            if (attr instanceof FieldElement e)
                consumer.accept(e);
        }
    }

    @Override
    public String toString() {
        return String.format("FieldModel[fieldName=%s, fieldType=%s, flags=%d]",
                fieldName().stringValue(), fieldType().stringValue(), flags().flagsMask());
    }
}
