/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.gizmo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InvokeSpecialInterfaceTestCase {

    @Test
    public void testInvokeSpecial() throws Exception {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());
        try (ClassCreator creator = ClassCreator.builder().classOutput(cl).className("com.MyTest").interfaces(MyInterface.class)
                .build()) {
            MethodCreator method = creator.getMethodCreator("val", String.class);
            ResultHandle superRet = method.invokeSpecialMethod(
                    MethodDescriptor.ofMethod(MyInterface.class, "val", String.class),
                    method.getThis(), true);
            ResultHandle ret = method.invokeVirtualMethod(MethodDescriptor.ofMethod(String.class, "toLowerCase", String.class),
                    superRet);
            method.returnValue(ret);
        }
        Class<?> clazz = cl.loadClass("com.MyTest");
        MyInterface myTest = (MyInterface) clazz.newInstance();
        assertEquals("foo", myTest.val());
    }

    public interface MyInterface {

        default String val() {
            return "FOO";
        }
    }

}
