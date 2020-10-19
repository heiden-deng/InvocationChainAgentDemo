package com.heiden;

import com.heiden.config.Config;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class EbExprEditor extends ExprEditor {
    private String className;

    public EbExprEditor(String className) {
        super();
        this.className = className;
    }

    @Override
    public void edit(MethodCall m) throws CannotCompileException {
        //super.edit(m);
        String methodName = m.getClassName();
        if (this.className.startsWith(Config.AppPkgName) && methodName.startsWith(Config.AppPkgName)) {
            String methodSignature = m.getClassName() + "." + m.getMethodName() + m.getSignature();
            String methodCallBody = "{" +
                    "com.heiden.core.ContextManager.createLocalSpan(\"" + methodSignature + "\",\"\");" +
                    "$_ = $proceed($$); " +
                    "com.heiden.core.ContextManager.stopSpan();" +
                    "}";
            m.replace(methodCallBody);
        }
    }
}
