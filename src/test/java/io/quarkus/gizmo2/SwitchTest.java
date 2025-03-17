package io.quarkus.gizmo2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import io.github.dmlloyd.classfile.Signature.ClassTypeSig;
import io.github.dmlloyd.classfile.Signature.TypeArg;

public class SwitchTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testSwitch() {
        TestClassMaker tcm = new TestClassMaker();
        Gizmo g = Gizmo.create(tcm);
        g.class_("io.quarkus.gizmo2.SwitchFun", cc -> {
            // implements Function<String,String>
            cc.implements_(ClassTypeSig.of(Function.class.getName(), TypeArg.of(ClassTypeSig.of(String.class.getName()))));
            cc.defaultConstructor();
            cc.method("apply", mc -> {
                ParamVar t = mc.parameter("t", String.class);
                mc.body(bc -> {
                    // String ret;
                    // switch(t) {
                    //    case "foo":
                    //       ret = "oof";
                    //       break;
                    //    case "bar":
                    //       ret = "rab";
                    //       break;
                    //    default:
                    //       ret = "def"
                    // }
                    // return ret;
                    LocalVar ret = bc.declare("ret", String.class);
                    bc.switch_(t, swc -> {
                        swc.case_(Constant.of("foo"), cb -> {
                            cb.set(ret, Constant.of("oof"));
                        });
                        swc.case_(Constant.of("bar"), cb -> {
                            cb.set(ret, Constant.of("rab"));
                        });
                        swc.default_(dcb -> {
                            dcb.set(ret, Constant.of("def"));
                        });
                    });
                    bc.return_(ret);
                });
            });
        });
        Function<String, String> fun = tcm.noArgsConstructor(Function.class);
        assertEquals("oof", fun.apply("foo"));
        assertEquals("rab", fun.apply("bar"));
        assertEquals("def", fun.apply("foos"));
    }

}
