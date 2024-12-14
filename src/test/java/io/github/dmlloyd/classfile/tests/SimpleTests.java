package io.github.dmlloyd.classfile.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Objects;

import io.github.dmlloyd.classfile.ClassModel;
import io.github.dmlloyd.classfile.ClassTransform;
import io.github.dmlloyd.classfile.ClassFile;
import io.github.dmlloyd.classfile.MethodModel;
import io.github.dmlloyd.classfile.extras.reflect.AccessFlag;
import org.junit.jupiter.api.Test;

/**
 *
 */
public final class SimpleTests {
    @Test
    public void testParse() throws Exception {
        byte[] b;
        try (InputStream is = Objects.requireNonNullElseGet(SimpleTests.class.getClassLoader().getResourceAsStream(SimpleTests.class.getName().replace('.', '/') + ".class"), InputStream::nullInputStream)) {
            b = is.readAllBytes();
        }
        ClassModel model = ClassFile.of().parse(b);
        assertTrue(model.thisClass().name().equalsString(SimpleTests.class.getName().replace('.', '/')));
        boolean ok = false;
        for (MethodModel method : model.methods()) {
            if (method.methodName().equalsString("testParse")) {
                ok = true;
                assertTrue(method.flags().has(AccessFlag.PUBLIC));
            }
        }
        assertTrue(ok, "Didn't find the testParse method");
        ClassFile.of().transformClass(model, ClassTransform.ACCEPT_ALL);
    }
}
