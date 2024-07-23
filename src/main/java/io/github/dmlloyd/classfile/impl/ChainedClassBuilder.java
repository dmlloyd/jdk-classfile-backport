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

import java.util.Optional;
import java.util.function.Consumer;

import io.github.dmlloyd.classfile.*;
import io.github.dmlloyd.classfile.constantpool.ConstantPoolBuilder;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;

public final class ChainedClassBuilder
        implements ClassBuilder, Consumer<ClassElement> {
    private final DirectClassBuilder terminal;
    private final Consumer<ClassElement> consumer;

    public ChainedClassBuilder(ClassBuilder downstream,
                               Consumer<ClassElement> consumer) {
        this.consumer = consumer;
        this.terminal = downstream instanceof ChainedClassBuilder cb ? cb.terminal : (DirectClassBuilder) downstream;
    }

    @Override
    public ClassBuilder with(ClassElement element) {
        consumer.accept(element);
        return this;
    }

    @Override
    public Optional<ClassModel> original() {
        return terminal.original();
    }

    @Override
    public ClassBuilder withField(Utf8Entry name, Utf8Entry descriptor, Consumer<? super FieldBuilder> handler) {
        consumer.accept(new BufferedFieldBuilder(terminal.constantPool, terminal.context,
                                                        name, descriptor, null)
                                       .run(handler)
                                       .toModel());
        return this;
    }

    @Override
    public ClassBuilder transformField(FieldModel field, FieldTransform transform) {
        BufferedFieldBuilder builder = new BufferedFieldBuilder(terminal.constantPool, terminal.context,
                                                                field.fieldName(), field.fieldType(),
                                                                field);
        builder.transform(field, transform);
        consumer.accept(builder.toModel());
        return this;
    }

    @Override
    public ClassBuilder withMethod(Utf8Entry name, Utf8Entry descriptor, int flags,
                                   Consumer<? super MethodBuilder> handler) {
        consumer.accept(new BufferedMethodBuilder(terminal.constantPool, terminal.context,
                                                         name, descriptor, null)
                                       .run(handler)
                                       .toModel());
        return this;
    }

    @Override
    public ClassBuilder transformMethod(MethodModel method, MethodTransform transform) {
        BufferedMethodBuilder builder = new BufferedMethodBuilder(terminal.constantPool, terminal.context,
                                                                  method.methodName(), method.methodType(), method);
        builder.transform(method, transform);
        consumer.accept(builder.toModel());
        return this;
    }

    @Override
    public ConstantPoolBuilder constantPool() {
        return terminal.constantPool();
    }

}
