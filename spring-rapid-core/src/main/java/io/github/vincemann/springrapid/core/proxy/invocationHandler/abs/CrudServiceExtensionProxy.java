package io.github.vincemann.springrapid.core.proxy.invocationHandler.abs;

import io.github.vincemann.springrapid.core.util.Lists;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.util.NullableOptional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Slf4j
public abstract class CrudServiceExtensionProxy
        implements InvocationHandler
{

    @NoArgsConstructor
    @Getter
    public static class MethodHandle {
        private boolean voidMethod;
        private String name;
        private Method method;
        private Object target;

        public static MethodHandle create(Method method, Object target){
            Assert.isTrue(method.getName().length()>3,"Method names are expected to be at least 2 characters long");
            Assert.notNull(target,"Target Object must not be null");
            MethodHandle methodHandle =new MethodHandle();
            methodHandle.target=target;
            methodHandle.voidMethod= method.getReturnType().equals(Void.TYPE);
            methodHandle.name = method.getName();
            methodHandle.method = method;
            return methodHandle;
        }

        public boolean hasAnnotation(Class<? extends Annotation> type){
            return method.isAnnotationPresent(type);
        }

        public NullableOptional<Object> execute(Object[] args) throws Throwable {
            try {
                Object result = method.invoke(target, args);
                if(voidMethod){
                    return NullableOptional.empty();
                }else {
                    return NullableOptional.of(result);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }catch (InvocationTargetException e){
                throw e.getCause();
            }
        }
    }

    private List<String> ignoredMethods = Lists.newArrayList("getEntityClass","getRepository","toString","equals","hashCode","getClass","clone","notify","notifyAll","wait","finalize");
    private CrudService service;
    private final Map<String, Method> methods = new HashMap<>();

    public CrudServiceExtensionProxy(CrudService service, String... ignoredMethods) {
        this.service = service;
        for(Method method: service.getClass().getMethods()) {
            this.methods.put(method.getName(), method);
        }
        this.ignoredMethods.addAll(Lists.newArrayList(ignoredMethods));

    }

    @Override
    public final Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if(isMethodIgnored(method)){
            return getMethods().get(method.getName())
                    .invoke(getService(),args);
        }
        else{
            if(args==null){
                args=new Object[]{};
            }
            return proxy(o,method,args);
        }
    }

    protected MethodHandle findMethod(String name, Object target) {
        List<Method> methods = Arrays.stream(target.getClass().getMethods())
                .filter(m ->
                        m.getName().equals(name) &&
                        !m.isBridge()
                )
                .collect(Collectors.toList());
        if(methods.size()>1){
            return MethodHandle.create(extractOverridingMethod(methods),target);
        }
        //Assert.isTrue(!methods.isEmpty(),"Could not find method with name: " + name + " on target: " + target);
        return methods.isEmpty() ? null : MethodHandle.create(methods.get(0), target);
    }

    private Method extractOverridingMethod(List<Method> conflicting){
        //use method highest in the class hierachy -> the one that overrides
        //check if methods are in same class -> design flaw
        List<Class> classes = new ArrayList<>();
        Method highestInHierarchy = null;
        for (Method method : conflicting) {
            Class<?> declaringClass = method.getDeclaringClass();
            if (classes.contains(declaringClass)) {
                throw new IllegalArgumentException("Found multiple methods with same name -> illegal for rule/plugin hook methods");
            }
            if(highestInHierarchy==null){
                highestInHierarchy=method;
            }
            else if(highestInHierarchy.getDeclaringClass().isAssignableFrom(declaringClass)){
                highestInHierarchy=method;
            }
            classes.add(declaringClass);
        }
        return highestInHierarchy;
    }

    protected String createPrefixedMethodName(String prefix,String targetMethodName) {
        String capitalFirstLetterMethodName = targetMethodName.substring(0, 1).toUpperCase() + targetMethodName.substring(1);
        return prefix + capitalFirstLetterMethodName;
    }


    protected NullableOptional<Object> invokeAndAppendEntityClassArgIfNeeded(MethodHandle method, Object[] args) throws Throwable {
        List<Object> finalArgs = Lists.newArrayList(args);
        if(isEntityClassWanted(method,args.length)){
            finalArgs.add(getService().getEntityClass());
        }
        return method.execute(finalArgs.toArray());
    }

    private boolean isEntityClassWanted(MethodHandle methodHandle, int amountDefaultArgs){
        int amountArgs = methodHandle.getMethod().getParameterTypes().length;
        if(amountArgs==0){
            return false;
        }
        Class<?> lastArgType = methodHandle.getMethod().getParameterTypes()[amountArgs - 1];
        if(amountArgs==amountDefaultArgs+1 && lastArgType.equals(Class.class)){
            return true;
        }else {
            return false;
        }
    }

    protected abstract Object proxy(Object o, Method method, Object[] args) throws Throwable;

    public boolean isMethodIgnored(Method method){
        return getIgnoredMethods().contains(method.getName());
    }
}
