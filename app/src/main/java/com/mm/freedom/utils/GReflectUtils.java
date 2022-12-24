package com.mm.freedom.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;


/**
 * 反射工具类, 对反射的各种封装
 */
public class GReflectUtils {

    private static final HashMap<String, Field> fieldCache = new HashMap<>();
    private static final HashMap<String, Method> methodCache = new HashMap<>();

    /**
     * 从 XposedHelpers 中移植出来的通用工具方法
     * <p>
     * 获取某个类下的某个字段, 从当前类向上查找(父类), 直到找到为止
     *
     * @param clazz     目标类
     * @param fieldName 字段名
     * @return 找到的字段
     * @throws NoSuchFieldError 未找到异常
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        String fullFieldName = clazz.getName() + '#' + fieldName;

        if (fieldCache.containsKey(fullFieldName)) {
            Field field = fieldCache.get(fullFieldName);
            if (field == null)
                throw new NoSuchFieldError(fullFieldName);
            return field;
        }

        try {
            Field field = findFieldRecursiveImpl(clazz, fieldName);
            field.setAccessible(true);
            fieldCache.put(fullFieldName, field);
            return field;
        } catch (NoSuchFieldException e) {
            fieldCache.put(fullFieldName, null);
            throw new NoSuchFieldError(fullFieldName);
        }
    }

    //向上父类查找, 直到Object为止
    private static Field findFieldRecursiveImpl(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class))
                    break;

                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }

    /**
     * 从 XposedHelpers 中移植出来的通用工具方法
     * <p>
     * 获取某个类下的某个方法, 从当前类向上查找(父类), 直到找到为止
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @return 找到的方法
     * @throws NoSuchFieldError 未找到异常
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        String fullMethodName = clazz.getName() + '#' + methodName + "(...)";
        Method[] methods = clazz.getDeclaredMethods();
        if (methodCache.containsKey(fullMethodName)) {
            Method method = methodCache.get(fullMethodName);
            if (method == null)
                throw new NoSuchFieldError(fullMethodName);
            return method;
        }
        return null;
    }


    /**
     * 获取某个类的类结构
     *
     * @param clazz 被获取的类
     * @return 类结构字符串
     */
    public static String getClassStructure(Class<?> clazz) {
        StringBuilder builder = new StringBuilder();

        ///包名: package xxx.xxx.xx
        Package clazzPackage = clazz.getPackage();
        builder.append("package ");
        if (clazzPackage != null && !clazzPackage.getName().trim().isEmpty()) {
            builder.append(clazzPackage.getName());
        }
        builder.append(";\n");

        //构建类 public abstract class XXX {...}
        builder.append(_recursionClassStructure(clazz, ""));

        return builder.toString();
    }

    //递归 类、内部类
    private static StringBuilder _recursionClassStructure(Class<?> clazz, String indent) {
        StringBuilder builder = new StringBuilder();

        //获取所有字段
        Field[] fields = clazz.getDeclaredFields();
        //获取所有构造方法
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        //获取所有方法
        Method[] methods = clazz.getDeclaredMethods();
        //获取内部类
        Class<?>[] classes = clazz.getDeclaredClasses();
        Class<?> superclass = clazz.getSuperclass();
        //接口
        Class<?>[] interfaces = clazz.getInterfaces();
        //接口中的方法
        Set<List<Method>> interfacesMethods = new HashSet<>();

        ///类头: public abstract class XXX {
        builder.append("\n");
        builder.append(indent); //缩进

        //权限(public ...)
        String clazzMod = Modifier.toString(clazz.getModifiers());
        if (clazz.isAnnotation()) {
            clazzMod = clazzMod.replaceAll("abstract|interface", "").trim();
            builder.append(clazzMod);
            builder.append(" @interface");
        } else if (clazz.isInterface()) {
            clazzMod = clazzMod.replaceAll("abstract|interface", "").trim();
            builder.append(clazzMod);
            builder.append(" interface");
        } else if (clazz.isEnum()) {
            builder.append(clazzMod);
            builder.append(" enum");
        } else {
            builder.append(clazzMod);
            builder.append(" class");
        }
        //类名
        builder.append(" ");
        builder.append(clazz.getSimpleName());

        //父类 extends EEE
        if (superclass != null && superclass != Object.class) {
            builder.append(" extends ");
            builder.append(superclass.getSimpleName());
        }

        //接口 implements III
        if (interfaces.length != 0) {
            builder.append(" implements ");
            for (Class<?> anInterface : interfaces) {
                String typeName = anInterface.getCanonicalName();
                builder.append(typeName);
                builder.append(", ");
            }
            //最后一个逗号去除
            builder.delete(builder.length() - 2, builder.length());
        }
        //大括号开始
        builder.append(" {\n");

        ///类体
        //字段 public String name;
        if (fields.length != 0) {
            builder.append(indent).append("\t"); //缩进
            builder.append("// fields\n");
            for (Field field : fields) {
                //获取权限(public ...)
                String fieldMod = Modifier.toString(field.getModifiers());
                builder.append(indent).append("\t"); //缩进
                if (fieldMod.length() > 0) {
                    builder.append(fieldMod);
                    builder.append(" ");
                }
                builder.append(field.getType().getCanonicalName());
                builder.append(" ");
                builder.append(field.getName());
                builder.append(";\n");
            }
        }

        //构造方法
        if (constructors.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// constructors\n");
            for (Constructor<?> constructor : constructors) {
                builder.append(indent).append("\t"); //缩进
                //获取权限(public ...)
                String methodMod = Modifier.toString(constructor.getModifiers());
                if (methodMod.length() > 0) {
                    builder.append(methodMod);
                    builder.append(" ");
                }
                builder.append(constructor.getName());
                builder.append("(");
                //参数: String abc;
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length != 0) {
                    for (Class<?> parameterType : parameterTypes) {
                        builder.append(parameterType.getCanonicalName());
                        //参数逗号分割
                        builder.append(", ");
                    }
                    //最后一个逗号去除
                    builder.delete(builder.length() - 2, builder.length());
                }
                builder.append(");\n");
            }
        }

        //方法: public abstract XX method(XX xx);
        if (methods.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// methods\n");
            for (Method method : methods) {
                builder.append(indent).append("\t"); //缩进
                //获取权限(public ...)
                String methodMod = Modifier.toString(method.getModifiers());
                if (methodMod.length() > 0) {
                    builder.append(methodMod);
                    builder.append(" ");
                }
                builder.append(method.getReturnType().getCanonicalName());
                builder.append(" ");
                builder.append(method.getName());
                builder.append("(");

                //参数: String abc;
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 0) {
                    for (Class<?> parameterType : parameterTypes) {
                        builder.append(parameterType.getCanonicalName());
                        //参数逗号分割
                        builder.append(", ");
                    }
                    //最后一个逗号去除
                    builder.delete(builder.length() - 2, builder.length());
                }
                //方法尾
                builder.append(");\n");
            }
        }

        //内部类, 递归构建
        if (classes.length != 0) {
            builder.append("\n");
            builder.append(indent).append("\t"); //缩进
            builder.append("// inner class");
            for (Class<?> aClass : classes) {
                StringBuilder innerClass = _recursionClassStructure(aClass, indent + "\t");
                builder.append(innerClass);
            }
        }

        ///类尾, 大括号结束
        builder.append(indent).append("}\n");
        return builder;
    }

    /**
     * 反射获取某个类中所有被使用的类, 并将其转换为import语句输出
     * 该方法能捕获：成员字段、成员方法(方法参数)、类实现接口、类注解(字段/方法/参数)、方法抛出异常
     * 但是一旦方法体内部代码逻辑引入一个全新的类型作为局部变量, 该方法则无法捕获
     *
     * @param clazz 被操作的类, 如: Test.class
     */
    public static HashSet<String> getClassImports(Class<?> clazz) {
        HashSet<String> imports = new HashSet<>();

        // 获取类的所有注解
        addAnnotations(imports, clazz.getAnnotations());

        // 获取超类
        Class<?> superclass = clazz.getSuperclass();

        // 如果超类不为空，且不是 Object 类，则将其转换为 import 语句
        if (superclass != null && !superclass.equals(Object.class)) {
            String className = superclass.getCanonicalName();
            // 如果是 java.lang 包中的类，则跳过
            if (!className.contains("java.lang.")) {
                imports.add("import " + className + ";");
            }
        }

        // 获取类实现的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> inter : interfaces) {
            String className = inter.getCanonicalName();
            if (className.contains("java.lang.")) continue;
            imports.add("import " + className + ";");
        }

        // 获取类中所有属性的类型
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取属性的所有注解
            addAnnotations(imports, field.getAnnotations());
            // 获取属性的类型
            addClasses(imports, clazz, field.getType());
        }

        // 获取类中所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            // 获取方法抛出的所有异常
            Class<?>[] exceptions = method.getExceptionTypes();
            for (Class<?> exception : exceptions) {
                String className = exception.getCanonicalName();
                imports.add("import " + className + ";");
            }
            // 获取方法的参数类型
            Class<?>[] paramTypes = method.getParameterTypes();
            for (Class<?> paramType : paramTypes) {
                // 获取方法参数的所有注解
                addAnnotations(imports, paramType.getAnnotations());
                // 参数类型
                addClasses(imports, clazz, paramType);
            }
            // 获取方法的所有注解
            addAnnotations(imports, method.getAnnotations());
            // 获取方法return类型
            addClasses(imports, clazz, method.getReturnType());
        }

        // 递归处理内部类
        Class<?>[] innerClasses = clazz.getDeclaredClasses();
        for (Class<?> innerClass : innerClasses) {
            imports.addAll(getClassImports(innerClass));
        }

        return imports;
    }

    /**
     * 添加所有注解
     *
     * @param imports     import集合
     * @param annotations 被操作类中的注解语句
     */
    private static void addAnnotations(HashSet<String> imports, Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            // 获取注解的类型
            Class<?> annotationType = annotation.annotationType();
            // 将注解转换为import语句
            String className = annotationType.getCanonicalName();
            imports.add("import " + className + ";");
        }
    }

    /**
     * 添加所有类
     *
     * @param imports     import集合
     * @param clazz       被操作的类, 如: Test.class
     * @param memberClazz 被操作类中的成员类型, 可以是 field.getType()、method.getParameterTypes()等等
     */
    private static void addClasses(HashSet<String> imports, Class<?> clazz, Class<?> memberClazz) {
        memberClazz = getArrayType(memberClazz);
        // 如果返回类型是其他类，则将其转换为import语句
        if (!memberClazz.isPrimitive()) {
            String className = clazz.getCanonicalName();
            String memberClassName = memberClazz.getCanonicalName();
            //如果该类型是内部类, 跳过
            if (clazz.getDeclaringClass() != null) return;
            //如果成员类型是内部类, 跳过
            if (memberClazz.getDeclaringClass() != null) return;
            //如果该类型是它本身, 跳过
            if (memberClassName.equals(className)) return;
            //如果是java.lang包中的类, 跳过
            if (memberClassName.contains("java.lang.")) return;
            imports.add("import " + memberClassName + ";");
        }
    }

    /**
     * 获取数组元素类型
     *
     * @param clazz 被操作类中的成员类型, 该参数应该接收一个 array类型的数组, 如果不是则原型返回
     * @return 返回数组类型, 如果不是数组, 则返回本身的类型
     */
    public static Class<?> getArrayType(Class<?> clazz) {
        // 如果属性的类型是数组类型，则获取数组元素的类型
        if (clazz.isArray()) return clazz.getComponentType();
        return clazz;
    }
}
