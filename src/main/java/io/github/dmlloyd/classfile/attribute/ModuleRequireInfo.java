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

import io.github.dmlloyd.classfile.constantpool.ModuleEntry;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.extras.constant.ModuleDesc;
import java.lang.module.ModuleDescriptor;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import io.github.dmlloyd.classfile.impl.TemporaryConstantPool;
import io.github.dmlloyd.classfile.impl.UnboundAttribute;
import io.github.dmlloyd.classfile.impl.Util;

/**
 * Models a single "requires" declaration in the {@link ModuleAttribute}.
 *
 * @see ModuleAttribute#requires()
 * @see ModuleDescriptor#requires()
 * @jvms 4.7.25 The {@code Module} Attribute
 * @since 24
 */
public sealed interface ModuleRequireInfo
        permits UnboundAttribute.UnboundModuleRequiresInfo {

    /**
     * {@return The module on which the current module depends}
     */
    ModuleEntry requires();

    /**
     * {@return the flags associated with this require declaration, as a bit mask}
     * It is in the range of unsigned short, {@code [0, 0xFFFF]}.
     *
     * @see ModuleDescriptor.Requires#modifiers()
     * @see AccessFlag.Location#MODULE_REQUIRES
     */
    int requiresFlagsMask();

    /**
     * {@return the flags associated with this require declaration, as a set of
     * flag enums}
     *
     * @throws IllegalArgumentException if the flags mask has any undefined bit set
     * @see ModuleDescriptor.Requires#accessFlags()
     * @see AccessFlag.Location#MODULE_REQUIRES
     */
    default Set<AccessFlag> requiresFlags() {
        return AccessFlag.maskToAccessFlags(requiresFlagsMask(), AccessFlag.Location.MODULE_REQUIRES);
    }

    /**
     * {@return the required version of the required module, if present}
     *
     * @see ModuleDescriptor.Requires#rawCompiledVersion()
     */
    Optional<Utf8Entry> requiresVersion();

    /**
     * {@return whether the specific access flag is set}
     *
     * @param flag the access flag
     * @see AccessFlag.Location#MODULE_REQUIRES
     */
    default boolean has(AccessFlag flag) {
        return Util.has(AccessFlag.Location.MODULE_REQUIRES, requiresFlagsMask(), flag);
    }

    /**
     * {@return a module requirement description}
     *
     * @param requires the required module
     * @param requiresFlags the require-specific flags
     * @param requiresVersion the required version, may be {@code null}
     */
    static ModuleRequireInfo of(ModuleEntry requires, int requiresFlags, Utf8Entry requiresVersion) {
        return new UnboundAttribute.UnboundModuleRequiresInfo(requires, requiresFlags, Optional.ofNullable(requiresVersion));
    }

    /**
     * {@return a module requirement description}
     *
     * @param requires the required module
     * @param requiresFlags the require-specific flags
     * @param requiresVersion the required version, may be {@code null}
     * @throws IllegalArgumentException if any flag cannot be applied to the
     *         {@link AccessFlag.Location#MODULE_REQUIRES} location
     */
    static ModuleRequireInfo of(ModuleEntry requires, Collection<AccessFlag> requiresFlags, Utf8Entry requiresVersion) {
        return of(requires, Util.flagsToBits(AccessFlag.Location.MODULE_REQUIRES, requiresFlags), requiresVersion);
    }

    /**
     * {@return a module requirement description}
     *
     * @param requires the required module
     * @param requiresFlags the require-specific flags
     * @param requiresVersion the required version, may be {@code null}
     */
    static ModuleRequireInfo of(ModuleDesc requires, int requiresFlags, String requiresVersion) {
        return new UnboundAttribute.UnboundModuleRequiresInfo(TemporaryConstantPool.INSTANCE.moduleEntry(TemporaryConstantPool.INSTANCE.utf8Entry(requires.name())), requiresFlags, Optional.ofNullable(requiresVersion).map(s -> TemporaryConstantPool.INSTANCE.utf8Entry(s)));
    }

    /**
     * {@return a module requirement description}
     *
     * @param requires the required module
     * @param requiresFlags the require-specific flags
     * @param requiresVersion the required version, may be {@code null}
     * @throws IllegalArgumentException if any flag cannot be applied to the
     *         {@link AccessFlag.Location#MODULE_REQUIRES} location
     */
    static ModuleRequireInfo of(ModuleDesc requires, Collection<AccessFlag> requiresFlags, String requiresVersion) {
        return of(requires, Util.flagsToBits(AccessFlag.Location.MODULE_REQUIRES, requiresFlags), requiresVersion);
    }
}
