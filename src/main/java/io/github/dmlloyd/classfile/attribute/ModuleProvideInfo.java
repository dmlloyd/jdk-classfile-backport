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

import io.github.dmlloyd.classfile.constantpool.ClassEntry;
import java.lang.constant.ClassDesc;
import java.lang.module.ModuleDescriptor;
import java.util.Arrays;
import java.util.List;

import io.github.dmlloyd.classfile.impl.TemporaryConstantPool;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;
import io.github.dmlloyd.classfile.impl.Util;

/**
 * Models a single "provides" declaration in the {@link ModuleAttribute}.
 *
 * @see ModuleAttribute#provides()
 * @see ModuleDescriptor.Provides
 * @jvms 4.7.25 The {@code Module} Attribute
 * @since 24
 */
public sealed interface ModuleProvideInfo
        permits UnboundAttribute.UnboundModuleProvideInfo {

    /**
     * {@return the service interface representing the provided service}
     *
     * @see ModuleDescriptor.Provides#service()
     */
    ClassEntry provides();

    /**
     * {@return the classes providing the service implementation}  The list
     * should not be empty.
     *
     * @see ModuleDescriptor.Provides#providers()
     */
    List<ClassEntry> providesWith();

    /**
     * {@return a service provision description}
     * @param provides the service class interface
     * @param providesWith the service class implementations, must not be empty
     */
    static ModuleProvideInfo of(ClassEntry provides,
                                List<ClassEntry> providesWith) {
        return new UnboundAttribute.UnboundModuleProvideInfo(provides, providesWith);
    }

    /**
     * {@return a service provision description}
     * @param provides the service class interface
     * @param providesWith the service class implementations, must not be empty
     */
    static ModuleProvideInfo of(ClassEntry provides,
                                ClassEntry... providesWith) {
        return of(provides, List.of(providesWith));
    }

    /**
     * {@return a service provision description}
     * @param provides the service class interface
     * @param providesWith the service class implementations, must not be empty
     * @throws IllegalArgumentException if {@code provides} or any of {@code
     *         providesWith} represents a primitive type
     */
    static ModuleProvideInfo of(ClassDesc provides,
                                List<ClassDesc> providesWith) {
        return of(TemporaryConstantPool.INSTANCE.classEntry(provides), Util.entryList(providesWith));
    }

    /**
     * {@return a service provision description}
     * @param provides the service class interface
     * @param providesWith the service class implementations, must not be empty
     * @throws IllegalArgumentException if {@code provides} or any of {@code
     *         providesWith} represents a primitive type
     */
    static ModuleProvideInfo of(ClassDesc provides,
                                ClassDesc... providesWith) {
        // List view, since ref to providesWith is temporary
        return of(provides, Arrays.asList(providesWith));
    }
}
