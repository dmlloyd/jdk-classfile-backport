#!/usr/bin/perl

use strict;
use warnings;

chdir "jdk";

my $usage = "Usage: $0 <old-JDK-rev> <new-JDK-rev>";

my $from = shift @ARGV or die $usage;
my $to = shift @ARGV or die $usage;

open my $git_fh, "-|", "git", "diff", "-M75", $from."..".$to, "--",
    "src/java.base/share/classes/java/lang/classfile",
    "src/java.base/share/classes/jdk/internal/classfile",
    "src/java.base/share/classes/java/lang/reflect/AccessFlag.java",
    "src/java.base/share/classes/java/lang/reflect/ClassFileFormatVersion.java",
    "src/java.base/share/classes/java/lang/constant/ModuleDesc.java",
    "src/java.base/share/classes/java/lang/constant/PackageDesc.java",
    "src/java.base/share/classes/jdk/internal/constant/ConstantUtils.java",
    "src/java.base/share/classes/jdk/internal/constant/ModuleDescImpl.java",
    "src/java.base/share/classes/jdk/internal/constant/PackageDescImpl.java"
    or die "Failed to run git: $?";

while ($_ = <$git_fh>) {
    s[jdk\.internal\.javac\.PreviewFeature][io.github.dmlloyd.classfile.extras.PreviewFeature]g;
    s[java(.)lang.reflect.(AccessFlag|ClassFileFormatVersion)][io$1github$1dmlloyd$1classfile$1extras$1reflect$1$2]g;
    s[java(.)lang.constant.(ModuleDesc|PackageDesc)][io$1github$1dmlloyd$1classfile$1extras$1constant$1$2]g;
    s[jdk(.)internal.constant.(ModuleDescImpl|PackageDescImpl|ConstantUtils)][io$1github$1dmlloyd$1classfile$1extras$1constant$1$2]g;
    s[ConstantDescs\.INIT_NAME][ExtraConstantDescs.INIT_NAME]g;
    s[ConstantDescs\.CLASS_INIT_NAME][ExtraConstantDescs.CLASS_INIT_NAME]g;
    s[ClassDesc\.ofInternalName][ExtraClassDesc.ofInternalName]g;
    s[java(.)lang.classfile][io$1github$1dmlloyd$1classfile]g;
    s[jdk(.)internal.classfile][io$1github$1dmlloyd$1classfile]g;
    s[(import jdk\.internal\.constant\.(ReferenceClass|PrimitiveClass|ClassOrInterface)DescImpl.*)][//$1]g;
    s[jdk(.)internal.constant][io$1github$1dmlloyd$1classfile$1extras$1constant]g;
    s[package java\.lang\.reflect][package io.github.dmlloyd.classfile.extras.reflect]g;
    s[package java\.lang\.constant][package io.github.dmlloyd.classfile.extras.constant]g;
    s[src/java\.base/share/classes][src/main/java]g;
    s[(import jdk\.internal\..*)][//$1]g;
    s[(import sun\..*)][//$1]g;
    print $_;
}
