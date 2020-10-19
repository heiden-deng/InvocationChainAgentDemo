package com.heiden;

import com.heiden.config.Config;
import com.heiden.config.PkgConfig;
import com.heiden.logging.api.ILog;
import com.heiden.logging.api.LogManager;
import javassist.*;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;

public class EbClassFileTransformer implements ClassFileTransformer {
    private static final ILog logger = LogManager.getLogger(EbClassFileTransformer.class);
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        Set<String> innerPkg = PkgConfig.innerPkg;
        if (className.indexOf('$') > -1){
            return null;
        }
        //logger.info("start process class " + className);
        if (!className.replaceAll("/",".").startsWith(Config.AppPkgName)){
            return null;
        }
        logger.info("start process class ... " + className);
       /* for (String pkgName : innerPkg){
            if (className.startsWith(pkgName)){
                return null;
            }
        }*/
        try{
            return buildMonitorClass2(className);
        }catch (Exception e){
            logger.error(e,"inject %s exception",className);
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] buildMonitorClass(String className) throws Exception{
        ClassPool pool = new ClassPool();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(className.replace("/","."));
        pool.importPackage("com.heiden.service");
        List<MethodInfo> methodInfoList = ctClass.getClassFile().getMethods();
        for (MethodInfo methodInfo : methodInfoList){
            if (methodInfo.isConstructor() || methodInfo.getName().startsWith("main(")){
                continue;
            }
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodInfo.getName());
            CtMethod copyMethod = CtNewMethod.copy(ctMethod,ctClass,new ClassMap());
            ctMethod.setName("sayHello$agent");
            copyMethod.setBody("{\n" +
                    "    long begin = System.nanoTime();\n" +
                    "    try {\n" +
                    "        return sayHello$agent($1,$2);\n" +
                    "    } finally {\n" +
                    "        System.out.println(System.nanoTime() - begin);}\n" +
                    "    }");
            ctClass.addMethod(copyMethod);
        }

        return ctClass.toBytecode();
    }

    private static byte[] buildMonitorClass2(String className) throws Exception{
        logger.info("enter  buildMonitorClass2-1 method ... " + className);
        ClassPool pool = ClassPool.getDefault();
        //pool.appendSystemPath();
        CtClass ctClass = pool.get(className.replace("/","."));
        pool.importPackage("com.heiden.service");
        //pool.importPackage("com.heiden.utils.ThreadLocalUtils");
        pool.importPackage("com.heiden.utils");
        pool.importPackage("com.heiden.context");
        pool.importPackage("com.heiden.core");
        pool.importPackage("com.heiden.logging");
        pool.importPackage("com.heiden.context");
        //pool.importPackage("com.heiden.config");
        //CtField threadLocalField = CtField.make("private com.heiden.utils.ThreadLocalUtils CEB_TRACE_CONTEXT;", ctClass);
        //ctClass.addField(threadLocalField,"com.heiden.utils.ThreadLocalUtils.init();");
        logger.info("Check class annotation ... " + className);
        boolean isControllerClass = false;
        Object[] annotations = ctClass.getAnnotations();
        for (Object obj : annotations){
            Annotation annotation = (Annotation)obj;
            Class<?> cls = annotation.annotationType();
            if (annotation.annotationType().getName().endsWith("Controller")){
                isControllerClass = true;
                break;
            }
        }
        if (!isControllerClass){//如果不是controller中的方法，需要新增方法，添加一个参数
            logger.info("process normal class" + className);
            if(buildNormalMethods(className, ctClass, pool) && Config.ClassFileSaveDir.length() > 0){
                logger.info("save class" + className + " dir " + Config.ClassFileSaveDir);
                ctClass.writeFile(Config.ClassFileSaveDir);
            }

            return ctClass.toBytecode();
        }

        logger.info("process Controller class" + className);
        //todo 处理Controller类
        //CtField ctField = new CtField();
        CtMethod[] methodInfoList = ctClass.getDeclaredMethods();
        for (int i = 0; i < methodInfoList.length; i++){
            CtMethod ctMethod = methodInfoList[i];
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            if (methodInfo.isConstructor()){
                continue;
            }
            boolean isRequestMethod = false;
            Object[] methodAnnotations = ctMethod.getAnnotations();
            for (Object obj : methodAnnotations){
                Annotation annotation = (Annotation)obj;
                String annationName = annotation.annotationType().getName();
                int pos = annationName.lastIndexOf('.');
                String annationShortName = annationName.substring(pos + 1);
                if (Config.RequestModeSet.contains(annationShortName)){
                    isRequestMethod = true;
                    break;
                }
            }
            if(isRequestMethod){//处理特殊方法，PostMapping,GetMapping等注解的方法
                logger.info("process request method " + className + "." + methodInfo.getName());
                pool.importPackage("org.springframework.web.context.request");
                //ctMethod.addParameter(pool.get("javax.servlet.http.HttpServletRequest"));
                String methodSignature = ctClass.getName() + "." + methodInfo.getName() + ctMethod.getSignature();
                String insertBeforeCode = "{" +
                        "org.springframework.web.context.request.ServletRequestAttributes servletRequestAttributes_eb_heiden123321 = (org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();\n" +
                        "javax.servlet.http.HttpServletRequest request_eb_heiden_vood123321 = servletRequestAttributes_eb_heiden123321.getRequest();" +
                        "String traceId_eb_heiden123321 = request_eb_heiden_vood123321.getHeader(\"traceId\");\n" +
                        "if (traceId_eb_heiden123321 == null || traceId_eb_heiden123321.length() == 0){\n" +
                        "   traceId_eb_heiden123321 = com.heiden.context.GlobalIdGenerator.generate();\n" +
                        "}\n" +
                        "com.heiden.core.ContextManager.createEntrySpan(\"" + methodSignature + "\",traceId_eb_heiden123321);" +
                        "}";
                ctMethod.insertBefore(insertBeforeCode);
                ctMethod.insertAfter("{com.heiden.core.ContextManager.stopSpan();}");
                ctMethod.instrument(new EbExprEditor(ctClass.getName()));

            }else{
                logger.info("process normal method " + className + "." + methodInfo.getName());
                buildMethodNormal(ctMethod, ctClass.getName(), methodInfo.getName());
                //CtMethod ctMethodExt = CtNewMethod.copy(ctMethod,ctClass, new ClassMap());
                //buildMethodExt(ctMethodExt, ctClass.getName(),methodInfo.getName(),pool);

            }

        }
        logger.info("process " + className + " finished");
        if(Config.ClassFileSaveDir.length() > 0){
            logger.info("save class" + className + " dir " + Config.ClassFileSaveDir);
            ctClass.writeFile(Config.ClassFileSaveDir);
        }

        return ctClass.toBytecode();
    }
    private static  boolean buildNormalMethods(String className, CtClass ctClass, ClassPool pool) throws CannotCompileException, NotFoundException {
        CtMethod[] methodInfoList = ctClass.getDeclaredMethods();
        boolean  isModified = false;
        for (int i = 0; i < methodInfoList.length; i++){
            CtMethod ctMethod = methodInfoList[i];
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            if (methodInfo.isConstructor() || methodInfo.getName().startsWith("main")){
                continue;
            }
            isModified = true;
            buildMethodNormal(ctMethod, ctClass.getName(), methodInfo.getName());

        }
        return isModified;
    }

    private static void buildMethodExt(CtMethod ctMethodExt,String className, String methodName, ClassPool pool) throws CannotCompileException, NotFoundException {
        ctMethodExt.addParameter(pool.get("com.heiden.core.TraceContext"));

        ctMethodExt.setBody("{" +
                "if(CEB_TRACE_CONTEXT.get() != null){CEB_TRACE_CONTEXT.remove();}" +
                "CEB_TRACE_CONTEXT.set((TraceContext)$args[$args.length - 1]);" +
                "StackTraceElement[] elements = Thread.currentThread().getStackTrace();\n" +
                "        StringBuffer buffer = new StringBuffer();\n" +
                "        int last_index = 2;\n" +
                "        int cur_index = 1;\n"  +
                "        buffer.append(\"Trace_ID:\").append(((TraceContext)$args[$args.length - 1]).getTraceId()).append(\" Current_ClassName:\").append(\"" + className.replaceAll("/",".") + "\")" +
                "                 .append(\" Current_MethodName:\").append(\"" + methodName + "\")" +
                "                 .append(\" Parent_ClassName:\").append(elements[last_index].getClassName())\n" +
                "                .append(\" Parent_MethodName:\" + elements[last_index].getMethodName());\n" +
                "        System.out.println(buffer.toString());" +
                "        KafkaService.sendMessage(buffer.toString());" +
                "        return "+ methodName + "($1,$2);}");
        //buildMethodNormal(ctMethodExt,className,methodName);
    }

    private static void buildMethodNormal(CtMethod ctMethod,String className, String methodName) throws CannotCompileException {
        logger.info("process " + className + "." + methodName + "byte inject");
        ctMethod.instrument(
                new EbExprEditor(className));
    }
}
