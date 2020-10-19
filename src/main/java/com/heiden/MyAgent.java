package com.heiden;

import com.heiden.config.Config;

import com.heiden.logging.api.ILog;
import com.heiden.logging.api.LogManager;
import com.heiden.service.KafkaService;

import javassist.*;
import javassist.bytecode.MethodInfo;
import com.heiden.config.PkgConfig;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.lang.annotation.Annotation;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyAgent {
    private static final ILog logger = LogManager.getLogger(MyAgent.class);
    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        //System.out.println("Hello javaagent premain:" + args);
        String[] paramsAry = args.split(",");
        Map<String,String> agentParam = new HashMap<>();
        for (int i = 0; i < paramsAry.length; i++){
            String[] param = paramsAry[i].split("=");
            if (param != null && param.length > 0){
                agentParam.put(param[0], param[1]);
            }
        }
        if (agentParam.containsKey(Config.AppPkgKey)){
            Config.AppPkgName = agentParam.get(Config.AppPkgKey);
            logger.info("agent will process class under package " + Config.AppPkgName);
        }else{
            logger.error("Cannot find Dest Project Package configuration,need add " + Config.AppPkgKey + " parameter,exit");
            return;
        }

        if (agentParam.containsKey(Config.KafkaServerKey)){
            KafkaService.setBorkerAddress(agentParam.getOrDefault(Config.KafkaServerKey,"127.0.0.1:9092"));
            logger.info("agent will send msg to kafka  " + agentParam.get(Config.KafkaServerKey));
        }else{
            logger.error("Cannot find kafka server configuration,need add " + Config.KafkaServerKey + ",exit");
            return;
        }
        if (agentParam.containsKey(Config.KafkaTopicKey)){
            Config.KafkaTopicName = agentParam.get(Config.KafkaTopicKey);
            logger.info("agent will send msg to kafka topic " + Config.KafkaTopicName);
        }else{
            logger.info("agent will default kafka topic " + Config.KafkaTopicName + " unless you configure " + Config.KafkaTopicKey + " parameter");
        }
        if (agentParam.containsKey(Config.KafkaTopicKeyKey)){
            Config.KafkaTopicKeyName = agentParam.get(Config.KafkaTopicKeyKey);
        }else{
            logger.info("agent will default kafka topic key " + Config.KafkaTopicKeyName + " unless you configure " + Config.KafkaTopicKeyKey + " parameter");
        }
        if (agentParam.containsKey(Config.ClassFileSaveDirKey)){
            Config.ClassFileSaveDir = agentParam.get(Config.ClassFileSaveDirKey);
            logger.info("agent save class file to  " + Config.ClassFileSaveDir );
        }else{
            logger.info("agent will not save class unless you configure SaveDir parameter");
        }


        instrumentation.addTransformer(new EbClassFileTransformer(),true);

  /*      MyServer myServer = new MyServer();
        ClassPool pool = new ClassPool();
        pool.appendSystemPath();
        CtClass ctClass = pool.get(MyServer.class.getName());
        CtMethod ctMethod = ctClass.getDeclaredMethod("sayHello");
        byte[] bytes = new byte[0];
        try{
            ctMethod.insertBefore("StackTraceElement[] elements = Thread.currentThread().getStackTrace();\n" +
                    "        StringBuffer buffer = new StringBuffer();\n" +
                    "        int last_index = 2;\n" +
                    "        buffer.append(\"Parent method index: \").append(last_index).append(\" ClassName: \").append(elements[last_index].getClassName())\n" +
                    "                .append(\" Method Name : \" + elements[last_index].getMethodName());\n" +
                    "        System.out.println(buffer.toString());");
            bytes = ctClass.toBytecode();
        }catch (Exception e){
            e.printStackTrace();
        }
        instrumentation.redefineClasses(new ClassDefinition(MyServer.class, bytes));
        */

    }


}
