package io.quarkus.gizmo;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.Test;

import io.quarkus.gizmo.Switch.StringSwitch;

public class Gizmo2Test {

    // public class MyTest implements Supplier, Consumer {
    // 
    //  public Object get() {
    //     StringBuilder stringBuilder = new StringBuilder();
    //     stringBuilder.append("foo");
    //     stringBuilder.append("bar");
    //     stringBuilder.append("baz");
    //  return ((Object)stringBuilder).toString();
    //  }
    // 
    //  public void accept(Object object) {
    //    switch(object.toString()) {
    //      case "foo" -> System.out.println("Accepted: foo");
    //      case "bar" -> System.out.println("Accepted: bar");
    //      case "baz" -> System.out.println("Accepted: baz");
    //    }
    //  }
    // }
    @SuppressWarnings("unchecked")
    @Test
    public void testComponentsLoop() throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        TestClassLoader cl = new TestClassLoader(getClass().getClassLoader());

        try (ClassCreator creator = ClassCreator.builder().classOutput(cl)
                .className("com.MyTest")
                .interfaces(Supplier.class, Consumer.class)
                .build()) {

            // StringBuilder#append(String)
            MethodDescriptor stringBuilderAppend = MethodDescriptor.ofMethod(StringBuilder.class, "append", StringBuilder.class,
                    String.class);

            // Here we have a list components found during discovery at build time
            List<String> components = List.of("foo", "bar", "baz");

            // Object get()
            MethodCreator get = creator.getMethodCreator("get", Object.class);
            // StringBuilder ret = new StringBuilder();
            ResultHandle getRet = get.newInstance(MethodDescriptor.ofConstructor(StringBuilder.class));

            // void accept(Object o)
            MethodCreator accept = creator.getMethodCreator("accept", void.class, Object.class);
            ResultHandle oStr = Gizmo.toString(accept, accept.getMethodParam(0));
            StringSwitch strSwitch = accept.stringSwitch(oStr);

            for (String component : components) {
                // ret.append(component)
                get.invokeVirtualMethod(stringBuilderAppend, getRet, get.load(component));
                strSwitch.caseOf(component, bc -> Gizmo.systemOutPrintln(bc, bc.load("Accepted: " + component)));
            }

            accept.returnVoid();
            get.returnValue(Gizmo.toString(get, getRet));
        }

        Object myTest = cl.loadClass("com.MyTest").getDeclaredConstructor().newInstance();
        Supplier<?> supplier = (Supplier<?>) myTest;
        assertEquals("foobarbaz", supplier.get());
        Consumer<Object> consumer = (Consumer<Object>) myTest;
        consumer.accept("foo");
    }

}
