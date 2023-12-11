package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;
import com.blueveery.springrest2ts.extensions.ModelConversionExtension;
import com.blueveery.springrest2ts.extensions.RestConversionExtension;
import com.blueveery.springrest2ts.filters.JavaTypeFilter;
import com.blueveery.springrest2ts.filters.OrFilterOperator;
import com.blueveery.springrest2ts.filters.RejectJavaTypeFilter;
import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSModule;
import com.blueveery.springrest2ts.tsmodel.TSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class Rest2tsGenerator2 {

    static Logger logger = LoggerFactory.getLogger("gen-logger");
    public static boolean generateAmbientModules = false;
    private Map<Class, TSType> customTypeMapping = new HashMap<>();

    private JavaTypeFilter modelClassesCondition = new RejectJavaTypeFilter();
    private JavaTypeFilter restClassesCondition = new RejectJavaTypeFilter();

    private NullableTypesStrategy nullableTypesStrategy = new DefaultNullableTypesStrategy();

    private JavaPackageToTsModuleConverter javaPackageToTsModuleConverter = new TsModuleCreatorConverter(1000);
    private SearchExtendsClass searchExtendsClass;
    private ComplexTypeConverter enumConverter = new JavaEnumToTsEnumConverter();;
    private ModelClassesAbstractConverter modelClassesConverter;
    private RestClassConverter restClassesConverter;

    private RestInterfaceConverter restInterfaceConverter;
    public SearchExtendsClass getSearchModelClassOnRestClass() {
        return searchExtendsClass;
    }

    public void setExtendsModelClass(SearchExtendsClass searchExtendsClass) {
        this.searchExtendsClass = searchExtendsClass;
    }

    public void setRestInterfaceConverter(RestInterfaceConverter restInterfaceConverter) {
        this.restInterfaceConverter = restInterfaceConverter;
    }

    public Map<Class, TSType> getCustomTypeMapping() {
        return customTypeMapping;
    }

    public void setModelClassesCondition(JavaTypeFilter modelClassesCondition) {
        this.modelClassesCondition = modelClassesCondition;
    }

    public void setRestClassesCondition(JavaTypeFilter restClassesCondition) {
        this.restClassesCondition = restClassesCondition;
    }

    public void setJavaPackageToTsModuleConverter(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter) {
        this.javaPackageToTsModuleConverter = javaPackageToTsModuleConverter;
    }

    public JavaPackageToTsModuleConverter getJavaPackageToTsModuleConverter() {
        return javaPackageToTsModuleConverter;
    }

    public void setEnumConverter(ComplexTypeConverter enumConverter) {
        this.enumConverter = enumConverter;
    }

    public void setModelClassesConverter(ModelClassesAbstractConverter modelClassesConverter) {
        this.modelClassesConverter = modelClassesConverter;
    }

    public void setRestClassesConverter(RestClassConverter restClassesConverter) {
        this.restClassesConverter = restClassesConverter;
    }

    public void setNullableTypesStrategy(NullableTypesStrategy nullableTypesStrategy) {
        this.nullableTypesStrategy = nullableTypesStrategy;
    }

    public SortedSet<TSModule> generate(Set<String> inputPackagesNames, File outputDir, Set<Class<?>> baseModelClasses,Set<Class<?>> baseRestClasses) throws IOException {
        Set<Class> modelClasses = new HashSet<>();
        if (baseModelClasses != null) {
            modelClasses.addAll(baseModelClasses);
        }
        Set<Class> restClasses = new HashSet<>();
        if (baseRestClasses != null) {
            restClasses.addAll(baseRestClasses);
        }
        Set<Class> enumClasses = new HashSet<>();
        Set<Class> restInterfaces = new HashSet<>();
        Set<String> packagesNames = new HashSet<>(inputPackagesNames);
        applyConversionExtension(packagesNames);

        logger.info("Scanning model classes");
        long searchClassStart = System.currentTimeMillis();
        List<Class> loadedClasses= loadClasses(packagesNames);
        searchClasses(loadedClasses, modelClassesCondition, modelClasses, enumClasses, logger);
        logger.info("Scanning rest controllers classes");
        searchClasses(loadedClasses, restClassesCondition, restClasses, enumClasses, logger);
        searchRestCascadeClass(restClasses,restInterfaces);
        searchModelClassesOnRestClass(loadedClasses, restClassesCondition, restClasses, enumClasses, modelClasses,logger);
        searchModelCascadeClass(modelClasses,enumClasses);
        logger.info("Scanning class use " + (System.currentTimeMillis() - searchClassStart));

        registerCustomTypesMapping(customTypeMapping);
        long convertTypeStart = System.currentTimeMillis();
        exploreRestClasses(restClasses, modelClassesCondition, modelClasses);
        exploreModelClasses(modelClasses, restClassesCondition);

        convertModules(enumClasses, javaPackageToTsModuleConverter);
        convertModules(modelClasses, javaPackageToTsModuleConverter);
        convertModules(restClasses, javaPackageToTsModuleConverter);
        convertModules(restInterfaces, javaPackageToTsModuleConverter);

        convertTypes(enumClasses, javaPackageToTsModuleConverter, enumConverter);
        if (!modelClasses.isEmpty()) {
            if (modelClassesConverter == null) {
                throw new IllegalStateException("Model classes converter is not set");
            }
            convertTypes(modelClasses, javaPackageToTsModuleConverter, modelClassesConverter);
        }

        if (!restInterfaces.isEmpty()) {
            if (restInterfaceConverter == null) {
                throw new IllegalStateException("Rest classes converter is not set");
            }
            convertTypes(restInterfaces, javaPackageToTsModuleConverter, restInterfaceConverter);
        }

        if (!restClasses.isEmpty()) {
            if (restClassesConverter == null) {
                throw new IllegalStateException("Rest classes converter is not set");
            }
            convertTypes(restClasses, javaPackageToTsModuleConverter, restClassesConverter);
        }
        logger.info("convertTypes use " + (System.currentTimeMillis() - convertTypeStart));

        long writeStart = System.currentTimeMillis();
        writeTSModules(javaPackageToTsModuleConverter.getTsModules(), outputDir, logger);
        logger.info("write types use " + (System.currentTimeMillis() - writeStart));
        return javaPackageToTsModuleConverter.getTsModules();
    }

    //搜索restClass的父类和父接口
    private void searchRestCascadeClass(Set<Class> restClasses, Set<Class> restInterfaces) {

        Set<Class> cascadeCass = new HashSet<>();

        outer: for (Class restClass : restClasses) {
            searchRestInterface(restClass, restInterfaces);
            Class superclass = restClass.getSuperclass();
            inner:
            while (superclass != null && !Object.class.equals(superclass)) {
                if (!cascadeCass.contains(superclass) && !restClasses.contains(superclass)) {
                    searchRestInterface(superclass, restInterfaces);
                    cascadeCass.add(superclass);
                    superclass = superclass.getSuperclass();
                } else {
                    break inner;
                }
            }
        }
        restClasses.addAll(cascadeCass);
    }

    public static void  searchRestInterface(Class restClass,Set<Class> restInterface) {
        while (restClass != null && !Object.class.equals(restClass)) {
            Class[] interfaces = restClass.getInterfaces();
            for (Class anInterface : interfaces) {
                if (restInterface.contains(anInterface)) {
                    continue;
                }
                if (anInterface.getAnnotation(Controller.class) != null) {
                    restInterface.add(anInterface);
                    searchRestInterface(anInterface, restInterface);
                }
            }
            restClass = restClass.getSuperclass();
        }
    }
    //搜索model类级联的父类和属性中级联的其他类
    private void searchModelCascadeClass(Set<Class> modelClasses, Set<Class> enumClasses) {
        Set<Class> tempModelClass = new HashSet<>();
        for (Class searchClass : modelClasses) {
            cascadeSearch(tempModelClass, enumClasses, searchClass);
        }
        modelClasses.addAll(tempModelClass);
    }

    public static void cascadeSearch(Set<Class> modelClasses, Set<Class> enumClasses, Type searchType) {

        if (searchType instanceof Class) {
            Class searchClass = (Class) searchType;
            if (Enum.class.isAssignableFrom(searchClass)) {
                enumClasses.add(searchClass);
                return;
            }
            if (modelClasses.contains(searchClass)) {
                return;
            }
            if (isActualClass(searchType)) {
                modelClasses.add(searchClass);
                for (Field declaredField : searchClass.getDeclaredFields()) {
                    declaredField.setAccessible(true);
                    Type genericType = declaredField.getGenericType();
                    cascadeSearch(modelClasses, enumClasses, genericType);
                }
                Type genericSuperclass = searchClass.getGenericSuperclass();
                if (genericSuperclass != null) {
                    cascadeSearch(modelClasses, enumClasses, genericSuperclass);
                }
            }
        } else if (searchType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) searchType;
            Type rawType = parameterizedType.getRawType();
            if (isActualClass(rawType)) {
                cascadeSearch(modelClasses, enumClasses, rawType);
            }
            Type[] arguments = parameterizedType.getActualTypeArguments();
            for (Type argument : arguments) {
                cascadeSearch(modelClasses, enumClasses, argument);
            }
        }



    }
    public static boolean isActualClass(Type type) {
        if (type instanceof Class) {
            if (type.equals(Object.class)) {
                return false;
            }
            Class searchTypeWithClass = (Class) type;
            if (!searchTypeWithClass.isInterface() && !searchTypeWithClass.isPrimitive()) {
                //java包下面的ts代码全部都部生成
                if (searchTypeWithClass.getPackage().getName().startsWith("java")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void searchModelClassesOnRestClass(List<Class> loadedClasses, JavaTypeFilter restClassesCondition, Set<Class> restClasses, Set<Class> enumClasses, Set<Class> modelClasses, Logger logger) {
        if (this.searchExtendsClass != null) {
            Set<Class> classes1 = this.searchExtendsClass.searchModelClassOnRestClass(loadedClasses,modelClasses,restClasses,enumClasses);
            modelClasses.addAll(classes1);
        }
    }

    private void applyConversionExtension(Set<String> packagesNames) {
        List<JavaTypeFilter> modelClassFilterList = new ArrayList<>();
        List<ModelConversionExtension> modelConversionExtensionList = getModelConversionExtensions();
        for (ModelConversionExtension extension : modelConversionExtensionList) {
            if (extension.getJavaTypeFilter() != null) {
                modelClassFilterList.add(extension.getJavaTypeFilter());
            }
            packagesNames.addAll(extension.getAdditionalJavaPackages());
            modelClassesConverter.getObjectMapperMap().putAll(extension.getObjectMapperMap());
            modelClassesConverter.getConversionListener().getConversionListenerSet().add(extension);
        }

        List<JavaTypeFilter> restClassFilterList = new ArrayList<>();
        if (restClassesConverter != null) {
            for (RestConversionExtension extension : restClassesConverter.getConversionExtensionList()) {
                if (extension.getJavaTypeFilter() != null) {
                    restClassFilterList.add(extension.getJavaTypeFilter());
                }
                packagesNames.addAll(extension.getAdditionalJavaPackages());
                restClassesConverter.getConversionListener().getConversionListenerSet().add(extension);
            }
            ImplementationGenerator implementationGenerator = restClassesConverter.getImplementationGenerator();
            implementationGenerator.setExtensions(restClassesConverter.getConversionExtensionList());
        }


        if (!modelClassFilterList.isEmpty()) {
            if (modelClassesConverter == null) {
                throw new IllegalStateException("There is installed extension which requires model classes converter");
            }
            modelClassFilterList.add(modelClassesCondition);
            OrFilterOperator orFilterOperator = new OrFilterOperator(modelClassFilterList);
            modelClassesCondition = orFilterOperator;
        }
        if (!restClassFilterList.isEmpty()) {
            if (restClassesConverter == null) {
                throw new IllegalStateException("There is installed extension which requires REST classes converter");
            }
            restClassFilterList.add(restClassesCondition);
            OrFilterOperator orFilterOperator = new OrFilterOperator(restClassFilterList);
            restClassesCondition = orFilterOperator;
        }

    }

    private List<ModelConversionExtension> getModelConversionExtensions() {
        List<ModelConversionExtension> modelConversionExtensionList = new ArrayList<>();
        if (restClassesConverter != null) {
            for (RestConversionExtension restConversionExtension : restClassesConverter.getConversionExtensionList()) {
                ModelConversionExtension modelConversionExtension = restConversionExtension.getModelConversionExtension();
                if (modelConversionExtension != null) {
                    modelConversionExtensionList.add(modelConversionExtension);
                }
            }
        }
        if (modelClassesConverter != null) {
            modelConversionExtensionList.addAll(modelClassesConverter.getConversionExtensionList());
        }
        return modelConversionExtensionList;
    }

    private void registerCustomTypesMapping(Map<Class, TSType> customTypeMapping) {
        for (Class nextJavaType : customTypeMapping.keySet()) {
            TSType tsType = customTypeMapping.get(nextJavaType);
            TypeMapper.registerTsType(nextJavaType, tsType);
        }
    }

    private void writeTSModules(SortedSet<TSModule> tsModuleSortedSet, File outputDir, Logger logger) throws IOException {
        tsModuleSortedSet.stream().forEach(tsModule -> {
            try {
                tsModule.writeModule(outputDir.toPath(), logger);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("general Ts file Error");
                System.exit(1);
            }
        });

    }

    public static void convertModules(Set<Class> javaClasses, JavaPackageToTsModuleConverter javaPackageToTsModuleConverter) {
        for (Class javaType : javaClasses) {
            javaPackageToTsModuleConverter.mapJavaTypeToTsModule(javaType);
        }
    }

    private void convertTypes(Set<Class> javaTypes, JavaPackageToTsModuleConverter tsModuleSortedMap, ComplexTypeConverter complexTypeConverter) {
        Set<Class> preConvertedTypes = new HashSet<>();
        for (Class javaType : javaTypes) {
            if (complexTypeConverter.preConverted(tsModuleSortedMap, javaType)) {
                preConvertedTypes.add(javaType);
            }
        }

        for (Class javaType : preConvertedTypes) {
            complexTypeConverter.convertInheritance(javaType);
        }

        for (Class javaType : preConvertedTypes) {
            complexTypeConverter.convert(javaType, nullableTypesStrategy);
        }

    }

    private void exploreModelClasses(Set<Class> modelClasses, JavaTypeFilter javaTypeFilter) {

    }

    private void exploreRestClasses(Set<Class> restClasses, JavaTypeFilter javaTypeFilter, Set<Class> modelClasses) {

    }

    private void searchClasses(List<Class> loadedClasses, JavaTypeFilter javaTypeFilter, Set<Class> classSet, Set<Class> enumClassSet, Logger logger) throws IOException {
        for (Class foundClass : loadedClasses) {
            //logger.info(String.format("Found class : %s", foundClass.getName()));
            if (Enum.class.isAssignableFrom(foundClass)) {
                //logger.info(String.format("Found enum class : %s", foundClass.getName()));
                enumClassSet.add(foundClass);
                continue;
            }

            if (javaTypeFilter.accept(foundClass)) {
                classSet.add(foundClass);
            }else{
                //logger.warn(String.format("Class filtered out : %s", foundClass.getSimpleName()));
            }
            javaTypeFilter.explain(foundClass, logger, "");
        }
    }


    private List<Class> loadClasses(Set<String> packageSet) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        List<Class> classList = new ArrayList<>();
        for (String packageName : packageSet) {
            Enumeration<URL> urlEnumeration = classLoader.getResources(packageName.replace(".", "/"));
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                URI uri = null;
                try {
                    uri = url.toURI();
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
                try {
                    FileSystems.newFileSystem(uri, Collections.emptyMap());} catch (Exception ignore) {}
                    Path path = Paths.get(uri);
                    scanPackagesRecursively(classLoader, path, packageName, classList);
            }
        }
        return classList;
    }

    private void scanPackagesRecursively(ClassLoader classLoader, Path currentPath, String packageName, List<Class> classList) throws IOException {
        for (Path nextPath : Files.newDirectoryStream(currentPath)) {
            if (Files.isDirectory(nextPath)) {
                scanPackagesRecursively(classLoader, nextPath, packageName+"."+nextPath.getFileName(), classList);
            } else {
                if (nextPath.toString().endsWith(".class")) {
                    String className = (packageName + "/" + nextPath.getFileName().toString()).replace(".class", "").replace("/", ".");
                    try {
                        Class<?> loadedClass = classLoader.loadClass(className);
                        loadedClass.getSimpleName();
                        if (!loadedClass.isAnnotation()) {
                            addNestedClasses(loadedClass.getDeclaredClasses(), classList);
                            classList.add(loadedClass);
                        }
                    } catch (Error | Exception e) {
                        System.out.println(String.format("Failed to load class %s due to error %s:%s", className, e.getClass().getSimpleName(), e.getMessage()));
                    }
                }
            }
        }
    }

    private void addNestedClasses(Class<?>[] nestedClasses, List<Class> classList) {
        for (Class<?> nestedClass : nestedClasses) {
            if (!nestedClass.isAnnotation()) {
                classList.add(nestedClass);
            }
            addNestedClasses(nestedClass.getDeclaredClasses(), classList);
        }
    }

}
