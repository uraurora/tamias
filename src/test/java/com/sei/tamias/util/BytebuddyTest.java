package com.sei.tamias.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.joor.Reflect;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.annotation.Target;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.sei.tamias.util.Options.setOf;
import static com.sei.tamias.util.Reflects.call;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.implementation.MethodDelegation.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;
import static com.sei.tamias.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-11-18 22:01
 * @description :
 */
public class BytebuddyTest {


    public static class GreetingInterceptor {
        // 方法签名随意
        @RuntimeType
        public Object greet(@Argument(0) Object argument) {
            return "Hello from " + argument;
        }

        public String greet(String argument) {
            return "Hello from String " + argument;
        }

    }

    @Test
    public void delegateTest() throws IllegalAccessException, InstantiationException {
        Class<? extends Function> dynamicType = new ByteBuddy()
                // 实现一个Function子类
                .subclass(Function.class)
                .method(named("apply"))
                // 拦截Function.apply调用，委托给GreetingInterceptor处理
                .intercept(to(new GreetingInterceptor()))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        Function function = dynamicType.newInstance();
        assertEquals("Hello from Byte Buddy", function.apply("Byte Buddy"));

    }

    @Test
    public void helloWorldTest() throws IllegalAccessException, InstantiationException {
        // hello world，拦截toSring方法返回固定字符串
        Class<?> aClass = new ByteBuddy()
                .subclass(Object.class)
                .method(isToString())
                .intercept(FixedValue.value("hello world"))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        assertEquals("hello world", aClass.newInstance().toString());
    }

    public static class HelloWorld {
        @RuntimeType
        public String hello() {
            return "hello world";
        }
    }

    @Test
    public void helloWorldTest2() throws IllegalAccessException, InstantiationException {
        // 拦截一个类的方法，调用其方法则会映射到其他方法去
        String r = new ByteBuddy()
                .subclass(Foo.class)
                .method(named("sayHelloFoo")
                        .and(isDeclaredBy(Foo.class)
                                .and(returns(String.class))))
                .intercept(MethodDelegation.to(Bar.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded()
                .newInstance()
                .sayHelloFoo();
        assertEquals(Bar.sayHelloBar(), r);
    }

    public static class Bar {

        @BindingPriority(3)
        public static String sayHelloBar() {
            return "Holla in Bar!";
        }

        @BindingPriority(1)
        public static String sayBar() {
            return "bar";
        }
    }

    public static class Foo {
        public String sayHelloFoo() {
            return "Hello in Foo!";
        }
    }

    @Test
    public void addFieldAndMethodTest() throws Exception {
        // 创建一个新的类，以及新的方法和字段
        Class<?> type = new ByteBuddy()
                .subclass(Object.class)
                .name("MyClassName")
                .defineMethod("custom", String.class, Modifier.PUBLIC)
                .intercept(MethodDelegation.to(Bar.class))
                .defineField("x", String.class, Modifier.PUBLIC)
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        Method m = type.getDeclaredMethod("custom", null);
        assertEquals(m.invoke(type.newInstance()), Bar.sayHelloBar());
        assertNotNull(type.getDeclaredField("x"));
    }

    @Test
    public void listTest() throws Exception {
        Class<? extends List> loaded = new ByteBuddy()
                .subclass(List.class)
                .name("MyList")
                .defineMethod("listOf", ArrayList.class, Visibility.PUBLIC)
                .withParameters(Collection.class)
                .intercept(to(Lists.class))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        Method m = loaded.getDeclaredMethod("listOf", Collection.class);
        @SuppressWarnings({"unchecked"})
        List<Integer> list = (List<Integer>) m.invoke(loaded.newInstance(), Sets.newHashSet(1, 2, 3, 4, 5));
        assertEquals(Lists.newArrayList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void agentTest() {
        Instrumentation install = ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(Foo.class)
                .method(named("sayHelloFoo"))
                .intercept(FixedValue.value("Hello Foo Redefined"))
                .make()
                .load(
                        Foo.class.getClassLoader(),
                        ClassReloadingStrategy.of(install)
                        // ClassReloadingStrategy.fromInstalledAgent()
                );

        Foo f = new Foo();

        assertEquals(f.sayHelloFoo(), "Hello Foo Redefined");

    }

    @Test
    public void interfaceTest() throws NoSuchMethodException {
        Class<?> myInterface = new ByteBuddy()
                .makeInterface()
                .name("MyInterface")
                .modifiers(Visibility.PUBLIC, TypeManifestation.ABSTRACT)
                .defineMethod("myMethod", void.class, Visibility.PUBLIC)
                .withParameter(String.class, "myDeprecatedParameter")
                .annotateParameter(AnnotationDescription.Builder.ofType(Deprecated.class)
                        .build())
                .withoutCode()
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
        System.out.println(Modifier.toString(myInterface.getModifiers())); // public abstract interface
        System.out.println(myInterface.getSimpleName()); // MyInterface
        System.out.println(Arrays.toString(myInterface.getDeclaredMethods())); // [public abstract void MyInterface.myMethod(java.lang.String)]

        Method method = myInterface.getDeclaredMethod("myMethod", String.class);
        System.out.println(method.getName()); // myMethod
        System.out.println(Arrays.toString(method.getParameters())); // [java.lang.String myDeprecatedParameter]

        Parameter parameter = method.getParameters()[0];
        System.out.println(parameter); // java.lang.String myDeprecatedParameter
        System.out.println(parameter.getName()); // myDeprecatedParameter
        System.out.println(Arrays.toString(parameter.getAnnotations())); // [@java.lang.Deprecated()]

        Annotation annotation = parameter.getAnnotations()[0];
        System.out.println(annotation); // @java.lang.Deprecated()
    }

    @Test
    public void genericTest() throws Exception {
        // 泛型字段的创建
        TypeDescription.Generic generic = TypeDescription.Generic.Builder
                .parameterizedType(List.class, Integer.class).build();
        Class<? extends List> loaded = new ByteBuddy().subclass(List.class)
                .defineField("names", generic, Visibility.PRIVATE).make()
                .load(getClass().getClassLoader())
                .getLoaded();
        Field field = loaded.getDeclaredField("names");
        Type fieldType = field.getGenericType();
        Assert.assertTrue(fieldType instanceof ParameterizedType);
        ParameterizedType genericFieldType = (ParameterizedType) fieldType;
        Assert.assertEquals(Integer.class, genericFieldType.getActualTypeArguments()[0]);
        System.out.println(genericFieldType.getRawType());
        System.out.println(genericFieldType.getActualTypeArguments()[0]);
    }


    @Test
    public void genericTest2() throws Exception {
        Class<? extends TestClass> clazz = Reflects.enhance(TestClass.class, this::buildDefaultType);
        Integer call = call(clazz, Integer.class, "hello");
        assertEquals(java.util.Optional.of(0).get(), call);
        int hello = buildDefaultType(TestClass.class).newInstance().hello();
        assertEquals(0, hello);
        assertNull(buildDefaultType(TestClass.class).newInstance().str());
        assertTrue(buildDefaultType(TestClass.class).newInstance().bool());
    }

    private <T> Class<? extends T> buildDefaultType(Class<? extends T> clazz){
        return new ByteBuddy()
                .subclass(clazz)
                .method(isAnnotatedWith(DefaultReturn.class))
                .intercept(to(Interceptor.class))
                .method(isDeclaredBy(clazz).and(returns(Void.class)))
                .intercept(FixedValue.value(TypeDescription.VOID))
                .method(isDeclaredBy(clazz).and(
                        returns(TypeDescription.CLASS).or(returns(TypeDescription.OBJECT)).or(returns(TypeDescription.STRING))
                ))
                .intercept(FixedValue.nullValue())
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();
    }

    public static class Interceptor{

        private static final String BOOLEAN = "boolean";

        private static final String INT = "int";


        public static boolean interceptorBoolean(@Origin Method method){
            return true;
        }

        public static int interceptorInt(@Origin Method method){
            return 0;
        }

    }

    public static class TestClass{
        @DefaultReturn("int")
        public int hello(){
            return 14;
        }

        @DefaultReturn()
        public String str(){
            return "banana";
        }

        @DefaultReturn()
        public boolean bool(){
            return false;
        }
    }

    @Target(ElementType.METHOD)
    @Retention(RUNTIME)
    public @interface DefaultReturn{
        String value() default "";
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void extensionMethodTest() {
        Reflects.ClassEnhancer<List> enhancer = Reflects.ClassEnhancer.<List>builder()
                .withClazz(List.class)
                .withEnhancer(c -> new ByteBuddy()
                        .subclass(ArrayList.class)
                        .name("MyList")
                        .defineMethod("listOf", ArrayList.class, Visibility.PUBLIC)
                        .withParameters(Collection.class)
                        .intercept(to(Lists.class))
                        .make()
                        .load(getClass().getClassLoader())
                        .getLoaded())
                .build();

        List<Integer> list = enhancer.call(List.class, "listOf", setOf(1,2,3));
        assertEquals(listOf(1,2,3), list);
        assertEquals("MyList", enhancer.getName());
        System.out.println(enhancer.getExtensionMethods());

        List<Integer> list1 = enhancer.create();
        for (int i = 0; i < 3; i++) {
            list1.add(i);
        }
        assertEquals(listOf(0,1,2), list1);

        // enhancer.call(String.class, "test");
    }

    public static class LoggerAdvisor {
        @Advice.OnMethodEnter
        public static void onMethodEnter(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments) {
            if (method.getAnnotation(Log.class) != null) {
                System.out.println("Enter " + method.getName() + " with arguments: " + Arrays.toString(arguments));
            }
        }

        @Advice.OnMethodExit
        public static void onMethodExit(@Advice.Origin Method method, @Advice.AllArguments Object[] arguments, @Advice.Return Object ret) {
            if (method.getAnnotation(Log.class) != null) {
                System.out.println("Exit " + method.getName() + " with arguments: " + Arrays.toString(arguments) + " return: " + ret);
            }
        }
    }

    public static class Service {
        @Log
        public int foo(int value) {
            System.out.println("foo: " + value);
            return value;
        }

        public int bar(int value) {
            System.out.println("bar: " + value);
            return value;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Log {}

    @Test
    public void adviceTest() throws IllegalAccessException, InstantiationException {
        Service service = new ByteBuddy()
                .subclass(Service.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(LoggerAdvisor.class))
                .make()
                .load(Service.class.getClassLoader())
                .getLoaded()
                .newInstance();
        service.bar(123);
        service.foo(456);
    }

    @Test
    public void compiletTest(){
        Reflect.compile("org.joor.test.Test1",
                "package org.joor.test; public class Test1 implements org.joor.test.I {}");
    }
    



}