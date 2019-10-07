package com.dili.http.okhttp.java;

import org.mybatis.generator.api.dom.java.*;

public class BsuB {
    public static String b() {
        TopLevelClass clazz = new TopLevelClass("com.dili.http.okhttp.utils.BSU");
        clazz.setVisibility(JavaVisibility.PUBLIC);
        imp(clazz);
        clazz.addSuperInterface(new FullyQualifiedJavaType("BSUI"));
        addI(clazz);
        addConstructor(clazz);
        addInner(clazz);
        addG(clazz);
        addS(clazz);
        addSc(clazz);
        addE(clazz);
        addEf(clazz);
        addEx(clazz);
        addDae(clazz);
        addDae2(clazz);
        addDaex(clazz);
        addDaex2(clazz);
        addR(clazz);
        return clazz.getFormattedContent().replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(" ", " ").replaceAll(" ", " ");
    }

    private static void imp(TopLevelClass clazz) {
        clazz.addImportedType("java.io.InputStream");
        clazz.addImportedType("bsh.Interpreter");
        clazz.addImportedType("java.net.URL");
        clazz.addImportedType("java.util.Random");
    }

    private static void addI(TopLevelClass clazz) {
        Field field = new Field();
        field.setName("i");
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType("Interpreter"));
        field.setInitializationString("new Interpreter()");
        clazz.addField(field);
    }

    private static void addConstructor(TopLevelClass clazz) {
        Method constructor = new Method("BSU");
        constructor.setVisibility(JavaVisibility.PRIVATE);
        constructor.setConstructor(true);
        constructor.addBodyLine("");
        clazz.addMethod(constructor);
        addMe(clazz);
    }

    private static void addInner(TopLevelClass clazz) {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("BSUH");
        InnerClass inner = new InnerClass(type);
        inner.setStatic(true);
        inner.setFinal(true);
        inner.setVisibility(JavaVisibility.PRIVATE);
        Field field = new Field("bsu", new FullyQualifiedJavaType("BSU"));
        field.setStatic(true);
        field.setFinal(true);
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setInitializationString("new BSU()");
        inner.addField(field);
        clazz.addInnerClass(inner);
    }

    private static void addMe(TopLevelClass clazz) {
        Method me = new Method("me");
        me.setVisibility(JavaVisibility.PUBLIC);
        me.setFinal(true);
        me.setStatic(true);
        me.setReturnType(new FullyQualifiedJavaType("BSU"));
        me.addBodyLine("return BSUH.bsu;");
        clazz.addMethod(me);
    }

    private static void addS(TopLevelClass clazz) {
        Method s = new Method("s");
        s.setVisibility(JavaVisibility.PUBLIC);
        s.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        s.addParameter(new Parameter(new FullyQualifiedJavaType("Object"), "o"));
        s.addBodyLine("try {i.set(s, o);} catch (Exception e) {}");
        clazz.addMethod(s);
    }

    private static void addG(TopLevelClass clazz) {
        Method g = new Method("g");
        g.setVisibility(JavaVisibility.PUBLIC);
        g.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        g.setReturnType(new FullyQualifiedJavaType("Object"));
        g.addBodyLine("try {return i.get(s);} catch (Exception e) {return null;}");
        clazz.addMethod(g);
    }

    private static void addE(TopLevelClass clazz) {
        Method e = new Method("e");
        e.setVisibility(JavaVisibility.PUBLIC);
        e.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        e.addBodyLine("try {i.eval(s);} catch (Exception e) {}");
        clazz.addMethod(e);
    }

    private static void addEf(TopLevelClass clazz) {
        Method ef = new Method("ef");
        ef.setVisibility(JavaVisibility.PUBLIC);
        ef.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        ef.addBodyLine("try {URL url = BSU.class.getClassLoader().getResource(s);");
        ef.addBodyLine("InputStream is = (InputStream)url.getContent();");
        ef.addBodyLine("byte[] buffer = new byte[is.available()];");
        ef.addBodyLine("int tmp = is.read(buffer);");
        ef.addBodyLine("while(tmp != -1){tmp = is.read(buffer);}");
        ef.addBodyLine("i.eval(new String(buffer));");
        ef.addBodyLine("} catch (Exception e) {}");
        clazz.addMethod(ef);
    }

    private static void addEx(TopLevelClass clazz) {
        Method ex = new Method("ex");
        ex.setVisibility(JavaVisibility.PUBLIC);
        ex.addException(new FullyQualifiedJavaType("Exception"));
        ex.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        ex.addBodyLine("i.eval(s);");
        clazz.addMethod(ex);
    }

    private static void addDae(TopLevelClass clazz) {
        Method dae = new Method("dae");
        dae.setVisibility(JavaVisibility.PUBLIC);
        dae.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "c"));
        dae.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "k"));
        dae.addBodyLine("try {i.eval(DESEncryptUtil.decrypt(c, k));} catch (Exception e) {}");
        clazz.addMethod(dae);
    }

    private static void addDae2(TopLevelClass clazz) {
        Method dae = new Method("dae");
        dae.setVisibility(JavaVisibility.PUBLIC);
        dae.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "c"));
        dae.addBodyLine("dae(c, \"showmethemoney\");");
        clazz.addMethod(dae);
    }

    private static void addDaex(TopLevelClass clazz) {
        Method daex = new Method("daex");
        daex.setVisibility(JavaVisibility.PUBLIC);
        daex.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "c"));
        daex.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "k"));
        daex.addException(new FullyQualifiedJavaType("Exception"));
        daex.addBodyLine("i.eval(DESEncryptUtil.decrypt(c, k));");
        clazz.addMethod(daex);
    }

    private static void addDaex2(TopLevelClass clazz) {
        Method dae = new Method("daex");
        dae.setVisibility(JavaVisibility.PUBLIC);
        dae.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "c"));
        dae.addBodyLine("daex(c, \"showmethemoney\");");
        dae.addException(new FullyQualifiedJavaType("Exception"));
        clazz.addMethod(dae);
    }

    private static void addSc(TopLevelClass clazz) {
        Method sc = new Method("sc");
        sc.setVisibility(JavaVisibility.PUBLIC);
        sc.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "s"));
        sc.addException(new FullyQualifiedJavaType("Exception"));
        sc.addBodyLine("i.source(s);");
        clazz.addMethod(sc);
    }

    private static void addR(TopLevelClass clazz) {
        Method r = new Method("r");
        r.setVisibility(JavaVisibility.PUBLIC);
        r.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "s"));
        r.setReturnType(new FullyQualifiedJavaType("int"));
        r.addBodyLine("Random r = new Random();return Math.abs(r.nextInt(s));");
        clazz.addMethod(r);
    }
}