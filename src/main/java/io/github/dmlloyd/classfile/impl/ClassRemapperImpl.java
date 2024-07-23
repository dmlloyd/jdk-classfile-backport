/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
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

import io.github.dmlloyd.classfile.Annotation;
import io.github.dmlloyd.classfile.AnnotationElement;
import io.github.dmlloyd.classfile.AnnotationValue;
import io.github.dmlloyd.classfile.ClassBuilder;
import io.github.dmlloyd.classfile.ClassElement;
import io.github.dmlloyd.classfile.ClassSignature;
import io.github.dmlloyd.classfile.CodeBuilder;
import io.github.dmlloyd.classfile.CodeElement;
import io.github.dmlloyd.classfile.CodeModel;
import io.github.dmlloyd.classfile.CodeTransform;
import io.github.dmlloyd.classfile.FieldBuilder;
import io.github.dmlloyd.classfile.FieldElement;
import io.github.dmlloyd.classfile.FieldModel;
import io.github.dmlloyd.classfile.FieldTransform;
import io.github.dmlloyd.classfile.Interfaces;
import io.github.dmlloyd.classfile.MethodBuilder;
import io.github.dmlloyd.classfile.MethodElement;
import io.github.dmlloyd.classfile.MethodModel;
import io.github.dmlloyd.classfile.MethodSignature;
import io.github.dmlloyd.classfile.MethodTransform;
import io.github.dmlloyd.classfile.Signature;
import io.github.dmlloyd.classfile.Superclass;
import io.github.dmlloyd.classfile.TypeAnnotation;
import io.github.dmlloyd.classfile.attribute.AnnotationDefaultAttribute;
import io.github.dmlloyd.classfile.attribute.EnclosingMethodAttribute;
import io.github.dmlloyd.classfile.attribute.ExceptionsAttribute;
import io.github.dmlloyd.classfile.attribute.InnerClassInfo;
import io.github.dmlloyd.classfile.attribute.InnerClassesAttribute;
import io.github.dmlloyd.classfile.attribute.ModuleAttribute;
import io.github.dmlloyd.classfile.attribute.ModuleProvideInfo;
import io.github.dmlloyd.classfile.attribute.NestHostAttribute;
import io.github.dmlloyd.classfile.attribute.NestMembersAttribute;
import io.github.dmlloyd.classfile.attribute.PermittedSubclassesAttribute;
import io.github.dmlloyd.classfile.attribute.RecordAttribute;
import io.github.dmlloyd.classfile.attribute.RecordComponentInfo;
import io.github.dmlloyd.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeInvisibleParameterAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeInvisibleTypeAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeVisibleParameterAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.RuntimeVisibleTypeAnnotationsAttribute;
import io.github.dmlloyd.classfile.attribute.SignatureAttribute;
import io.github.dmlloyd.classfile.components.ClassRemapper;
import io.github.dmlloyd.classfile.constantpool.Utf8Entry;
import io.github.dmlloyd.classfile.instruction.ConstantInstruction.LoadConstantInstruction;
import io.github.dmlloyd.classfile.instruction.ExceptionCatch;
import io.github.dmlloyd.classfile.instruction.FieldInstruction;
import io.github.dmlloyd.classfile.instruction.InvokeDynamicInstruction;
import io.github.dmlloyd.classfile.instruction.InvokeInstruction;
import io.github.dmlloyd.classfile.instruction.LocalVariable;
import io.github.dmlloyd.classfile.instruction.LocalVariableType;
import io.github.dmlloyd.classfile.instruction.NewMultiArrayInstruction;
import io.github.dmlloyd.classfile.instruction.NewObjectInstruction;
import io.github.dmlloyd.classfile.instruction.NewReferenceArrayInstruction;
import io.github.dmlloyd.classfile.instruction.TypeCheckInstruction;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.function.Function;

public record ClassRemapperImpl(Function<ClassDesc, ClassDesc> mapFunction) implements ClassRemapper {

    @Override
    public void accept(ClassBuilder clb, ClassElement cle) {
        if (cle instanceof FieldModel fm) 
            clb.withField(fm.fieldName().stringValue(), map(
                fm.fieldTypeSymbol()), fb -> fb.transform(fm, asFieldTransform()));
        else if (cle instanceof MethodModel mm)
            clb.withMethod(mm.methodName().stringValue(), mapMethodDesc(
                mm.methodTypeSymbol()), mm.flags().flagsMask(), mb -> mb.transform(mm, asMethodTransform()));
        else if (cle instanceof Superclass sc)
            clb.withSuperclass(map(sc.superclassEntry().asSymbol()));
        else if (cle instanceof Interfaces ins) 
            clb.withInterfaceSymbols(Util.mappedList(ins.interfaces(), in ->
                map(in.asSymbol())));
        else if (cle instanceof SignatureAttribute sa) 
            clb.with(SignatureAttribute.of(mapClassSignature(sa.asClassSignature())));
        else if (cle instanceof InnerClassesAttribute ica) 
            clb.with(InnerClassesAttribute.of(ica.classes().stream().map(ici ->
                InnerClassInfo.of(map(ici.innerClass().asSymbol()),
                    ici.outerClass().map(oc -> map(oc.asSymbol())),
                    ici.innerName().map(Utf8Entry::stringValue),
                    ici.flagsMask())).toList()));
        else if (cle instanceof EnclosingMethodAttribute ema) 
            clb.with(EnclosingMethodAttribute.of(map(ema.enclosingClass().asSymbol()),
                ema.enclosingMethodName().map(Utf8Entry::stringValue),
                ema.enclosingMethodTypeSymbol().map(this::mapMethodDesc)));
        else if (cle instanceof RecordAttribute ra) 
            clb.with(RecordAttribute.of(ra.components().stream()
                .map(this::mapRecordComponent).toList()));
        else if (cle instanceof ModuleAttribute ma) 
            clb.with(ModuleAttribute.of(ma.moduleName(), ma.moduleFlagsMask(),
                ma.moduleVersion().orElse(null),
                ma.requires(), ma.exports(), ma.opens(),
                ma.uses().stream().map(ce ->
                    clb.constantPool().classEntry(map(ce.asSymbol()))).toList(),
                ma.provides().stream().map(mp ->
                    ModuleProvideInfo.of(map(mp.provides().asSymbol()),
                        mp.providesWith().stream().map(pw ->
                            map(pw.asSymbol())).toList())).toList()));
        else if (cle instanceof NestHostAttribute nha) 
            clb.with(NestHostAttribute.of(map(nha.nestHost().asSymbol())));
        else if (cle instanceof NestMembersAttribute nma) 
            clb.with(NestMembersAttribute.ofSymbols(nma.nestMembers().stream()
                .map(nm -> map(nm.asSymbol())).toList()));
        else if (cle instanceof PermittedSubclassesAttribute psa) 
            clb.with(PermittedSubclassesAttribute.ofSymbols(
                psa.permittedSubclasses().stream().map(ps ->
                    map(ps.asSymbol())).toList()));
        else if (cle instanceof RuntimeVisibleAnnotationsAttribute aa) 
            clb.with(RuntimeVisibleAnnotationsAttribute.of(
                mapAnnotations(aa.annotations())));
        else if (cle instanceof RuntimeInvisibleAnnotationsAttribute aa) 
            clb.with(RuntimeInvisibleAnnotationsAttribute.of(
                mapAnnotations(aa.annotations())));
        else if (cle instanceof RuntimeVisibleTypeAnnotationsAttribute aa) 
            clb.with(RuntimeVisibleTypeAnnotationsAttribute.of(
                mapTypeAnnotations(aa.annotations())));
        else if (cle instanceof RuntimeInvisibleTypeAnnotationsAttribute aa) 
            clb.with(RuntimeInvisibleTypeAnnotationsAttribute.of(
                mapTypeAnnotations(aa.annotations())));
        else
            clb.with(cle);
    }

    @Override
    public FieldTransform asFieldTransform() {
        return (FieldBuilder fb, FieldElement fe) -> {
            if (fe instanceof SignatureAttribute sa) 
                fb.with(SignatureAttribute.of(
                    mapSignature(sa.asTypeSignature())));
            else if (fe instanceof RuntimeVisibleAnnotationsAttribute aa) 
                fb.with(RuntimeVisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations())));
            else if (fe instanceof RuntimeInvisibleAnnotationsAttribute aa) 
                fb.with(RuntimeInvisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations())));
            else if (fe instanceof RuntimeVisibleTypeAnnotationsAttribute aa) 
                fb.with(RuntimeVisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else if (fe instanceof RuntimeInvisibleTypeAnnotationsAttribute aa) 
                fb.with(RuntimeInvisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else
                fb.with(fe);
        };
    }

    @Override
    public MethodTransform asMethodTransform() {
        return (MethodBuilder mb, MethodElement me) -> {
            if (me instanceof AnnotationDefaultAttribute ada) 
                mb.with(AnnotationDefaultAttribute.of(
                    mapAnnotationValue(ada.defaultValue())));
            else if (me instanceof CodeModel com) 
                mb.transformCode(com, asCodeTransform());
            else if (me instanceof ExceptionsAttribute ea) 
                mb.with(ExceptionsAttribute.ofSymbols(
                    ea.exceptions().stream().map(ce ->
                        map(ce.asSymbol())).toList()));
            else if (me instanceof SignatureAttribute sa) 
                mb.with(SignatureAttribute.of(
                    mapMethodSignature(sa.asMethodSignature())));
            else if (me instanceof RuntimeVisibleAnnotationsAttribute aa) 
                mb.with(RuntimeVisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations())));
            else if (me instanceof RuntimeInvisibleAnnotationsAttribute aa) 
                mb.with(RuntimeInvisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations())));
            else if (me instanceof RuntimeVisibleParameterAnnotationsAttribute paa) 
                mb.with(RuntimeVisibleParameterAnnotationsAttribute.of(
                    paa.parameterAnnotations().stream()
                        .map(this::mapAnnotations).toList()));
            else if (me instanceof RuntimeInvisibleParameterAnnotationsAttribute paa) 
                mb.with(RuntimeInvisibleParameterAnnotationsAttribute.of(
                    paa.parameterAnnotations().stream()
                        .map(this::mapAnnotations).toList()));
            else if (me instanceof RuntimeVisibleTypeAnnotationsAttribute aa) 
                mb.with(RuntimeVisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else if (me instanceof RuntimeInvisibleTypeAnnotationsAttribute aa) 
                mb.with(RuntimeInvisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else
                mb.with(me);
        };
    }

    @Override
    public CodeTransform asCodeTransform() {
        return (CodeBuilder cob, CodeElement coe) -> {
            if (coe instanceof FieldInstruction fai) 
                cob.fieldAccess(fai.opcode(), map(fai.owner().asSymbol()),
                    fai.name().stringValue(), map(fai.typeSymbol()));
            else if (coe instanceof InvokeInstruction ii) 
                cob.invoke(ii.opcode(), map(ii.owner().asSymbol()),
                    ii.name().stringValue(), mapMethodDesc(ii.typeSymbol()),
                    ii.isInterface());
            else if (coe instanceof InvokeDynamicInstruction idi) 
                cob.invokedynamic(DynamicCallSiteDesc.of(
                    mapDirectMethodHandle(idi.bootstrapMethod()), idi.name().stringValue(),
                    mapMethodDesc(idi.typeSymbol()),
                    idi.bootstrapArgs().stream().map(this::mapConstantValue).toArray(ConstantDesc[]::new)));
            else if (coe instanceof NewObjectInstruction c) 
                cob.new_(map(c.className().asSymbol()));
            else if (coe instanceof NewReferenceArrayInstruction c) 
                cob.anewarray(map(c.componentType().asSymbol()));
            else if (coe instanceof NewMultiArrayInstruction c) 
                cob.multianewarray(map(c.arrayType().asSymbol()), c.dimensions());
            else if (coe instanceof TypeCheckInstruction c) 
                cob.with(TypeCheckInstruction.of(c.opcode(), map(c.type().asSymbol())));
            else if (coe instanceof ExceptionCatch c) 
                cob.exceptionCatch(c.tryStart(), c.tryEnd(), c.handler(),c.catchType()
                    .map(d -> TemporaryConstantPool.INSTANCE.classEntry(map(d.asSymbol()))));
            else if (coe instanceof LocalVariable c) 
                cob.localVariable(c.slot(), c.name().stringValue(), map(c.typeSymbol()),
                    c.startScope(), c.endScope());
            else if (coe instanceof LocalVariableType c) 
                cob.localVariableType(c.slot(), c.name().stringValue(),
                    mapSignature(c.signatureSymbol()), c.startScope(), c.endScope());
            else if (coe instanceof LoadConstantInstruction ldc) 
                cob.loadConstant(ldc.opcode(),
                    mapConstantValue(ldc.constantValue()));
            else if (coe instanceof RuntimeVisibleTypeAnnotationsAttribute aa) 
                cob.with(RuntimeVisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else if (coe instanceof RuntimeInvisibleTypeAnnotationsAttribute aa) 
                cob.with(RuntimeInvisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations())));
            else
                cob.with(coe);
        };
    }

    @Override
    public ClassDesc map(ClassDesc desc) {
        if (desc == null) return null;
        if (desc.isArray()) return map(desc.componentType()).arrayType();
        if (desc.isPrimitive()) return desc;
        return mapFunction.apply(desc);
    }

    MethodTypeDesc mapMethodDesc(MethodTypeDesc desc) {
        return MethodTypeDesc.of(map(desc.returnType()),
                desc.parameterList().stream().map(this::map).toArray(ClassDesc[]::new));
    }

    ClassSignature mapClassSignature(ClassSignature signature) {
        return ClassSignature.of(mapTypeParams(signature.typeParameters()),
                mapSignature(signature.superclassSignature()),
                signature.superinterfaceSignatures().stream()
                        .map(this::mapSignature).toArray(Signature.ClassTypeSig[]::new));
    }

    MethodSignature mapMethodSignature(MethodSignature signature) {
        return MethodSignature.of(mapTypeParams(signature.typeParameters()),
                signature.throwableSignatures().stream().map(this::mapSignature).toList(),
                mapSignature(signature.result()),
                signature.arguments().stream()
                        .map(this::mapSignature).toArray(Signature[]::new));
    }

    RecordComponentInfo mapRecordComponent(RecordComponentInfo component) {
        return RecordComponentInfo.of(component.name().stringValue(),
                map(component.descriptorSymbol()),
                component.attributes().stream().map(atr ->
        atr instanceof SignatureAttribute sa ?
            SignatureAttribute.of(
                    mapSignature(sa.asTypeSignature()))
        : atr instanceof RuntimeVisibleAnnotationsAttribute aa ? 
            RuntimeVisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations()))
        : atr instanceof RuntimeInvisibleAnnotationsAttribute aa ? 
            RuntimeInvisibleAnnotationsAttribute.of(
                    mapAnnotations(aa.annotations()))
        : atr instanceof RuntimeVisibleTypeAnnotationsAttribute aa ? 
            RuntimeVisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations()))
        : atr instanceof RuntimeInvisibleTypeAnnotationsAttribute aa ? 
            RuntimeInvisibleTypeAnnotationsAttribute.of(
                    mapTypeAnnotations(aa.annotations()))
        : atr).toList());
    }

    DirectMethodHandleDesc mapDirectMethodHandle(DirectMethodHandleDesc dmhd) {
        return switch (dmhd.kind()) {
            case GETTER, SETTER, STATIC_GETTER, STATIC_SETTER ->
                MethodHandleDesc.ofField(dmhd.kind(), map(dmhd.owner()),
                        dmhd.methodName(),
                        map(ClassDesc.ofDescriptor(dmhd.lookupDescriptor())));
            default ->
                MethodHandleDesc.ofMethod(dmhd.kind(), map(dmhd.owner()),
                        dmhd.methodName(),
                        mapMethodDesc(MethodTypeDesc.ofDescriptor(dmhd.lookupDescriptor())));
        };
    }

    ConstantDesc mapConstantValue(ConstantDesc value) {
        return value instanceof ClassDesc cd ?
                map(cd)
            : value instanceof DynamicConstantDesc<?> dcd ?
                mapDynamicConstant(dcd)
            : value instanceof DirectMethodHandleDesc dmhd ?
                mapDirectMethodHandle(dmhd)
            : value instanceof MethodTypeDesc mtd ?
                mapMethodDesc(mtd)
            : value;
    }

    DynamicConstantDesc<?> mapDynamicConstant(DynamicConstantDesc<?> dcd) {
        return DynamicConstantDesc.ofNamed(mapDirectMethodHandle(dcd.bootstrapMethod()),
                dcd.constantName(),
                map(dcd.constantType()),
                dcd.bootstrapArgsList().stream().map(this::mapConstantValue).toArray(ConstantDesc[]::new));
    }

    @SuppressWarnings("unchecked")
    <S extends Signature> S mapSignature(S signature) {
        if (signature instanceof Signature.ArrayTypeSig ats)
            return (S) Signature.ArrayTypeSig.of(mapSignature(ats.componentSignature()));
        else if (signature instanceof Signature.ClassTypeSig cts) 
            return (S) Signature.ClassTypeSig.of(
                cts.outerType().map(this::mapSignature).orElse(null),
                map(cts.classDesc()),
                cts.typeArgs().stream().map(ta ->
                    ta instanceof Signature.TypeArg.Unbounded u ? u :
                    ta instanceof Signature.TypeArg.Bounded bta ? Signature.TypeArg.bounded(
                        bta.wildcardIndicator(), mapSignature(bta.boundType())) :
                    null
                ).toArray(Signature.TypeArg[]::new));
        else return signature;
    }

    List<Annotation> mapAnnotations(List<Annotation> annotations) {
        return annotations.stream().map(this::mapAnnotation).toList();
    }

    Annotation mapAnnotation(Annotation a) {
        return Annotation.of(map(a.classSymbol()), a.elements().stream().map(el ->
                AnnotationElement.of(el.name(), mapAnnotationValue(el.value()))).toList());
    }

    AnnotationValue mapAnnotationValue(AnnotationValue val) {
        if (val instanceof AnnotationValue.OfAnnotation oa)
            return AnnotationValue.ofAnnotation(mapAnnotation(oa.annotation()));
        else if (val instanceof AnnotationValue.OfArray oa) 
            return AnnotationValue.ofArray(oa.values().stream().map(this::mapAnnotationValue).toList());
        else if (val instanceof AnnotationValue.OfConstant oc) return oc;
        else if (val instanceof AnnotationValue.OfClass oc) 
            return AnnotationValue.ofClass(map(oc.classSymbol()));
        else if (val instanceof AnnotationValue.OfEnum oe) 
            return AnnotationValue.ofEnum(map(oe.classSymbol()), oe.constantName().stringValue());
        else throw new IllegalStateException();
    }

    List<TypeAnnotation> mapTypeAnnotations(List<TypeAnnotation> typeAnnotations) {
        return typeAnnotations.stream().map(a -> TypeAnnotation.of(a.targetInfo(),
                a.targetPath(), map(a.classSymbol()),
                a.elements().stream().map(el -> AnnotationElement.of(el.name(),
                        mapAnnotationValue(el.value()))).toList())).toList();
    }

    List<Signature.TypeParam> mapTypeParams(List<Signature.TypeParam> typeParams) {
        return typeParams.stream().map(tp -> Signature.TypeParam.of(tp.identifier(),
                tp.classBound().map(this::mapSignature),
                tp.interfaceBounds().stream()
                        .map(this::mapSignature).toArray(Signature.RefTypeSig[]::new))).toList();
    }

}
