package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.EmptyImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.*;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.lang.reflect.AnnotatedType;
import java.util.*;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class ModelClassesToTsInterfacesConverter extends ModelClassesAbstractConverter {

    public ModelClassesToTsInterfacesConverter(ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), objectMapper);
    }

    public ModelClassesToTsInterfacesConverter(ClassNameMapper classNameMapper, ObjectMapper objectMapper) {
        super(new EmptyImplementationGenerator(), classNameMapper, objectMapper);
    }

    @Override
    public boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass) {
        if (TypeMapper.map(javaClass) == TypeMapper.tsAny) {
            ObjectMapper objectMapper = selectObjectMapper(javaClass);
            if (objectMapper.filterClass(javaClass)) {
                TSModule tsModule = javaPackageToTsModuleConverter.getTsModule(javaClass);
                TSClass tsInterface = new TSClass(createTsClassName(javaClass), tsModule,implementationGenerator);
                tsModule.addScopedElement(tsInterface);
                TypeMapper.registerTsType(javaClass, tsInterface);
                return true;
            }
        }
        return false;
    }

    @Override
    public void convertInheritance(Class javaClass) {
        TSClassReference tsInterfaceReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsInterfaceReference.getReferencedType();
        if (!javaClass.isInterface()) {
            if (javaClass.getSuperclass() != Object.class) {
                TSType superClass = TypeMapper.map(javaClass.getAnnotatedSuperclass().getType());
                if (superClass instanceof TSClassReference) {
                    TSClassReference tsSuperClassInterface = (TSClassReference) superClass;
                    tsClass.setExtendsClass(tsSuperClassInterface);
                }
            }
        }
//        for (AnnotatedType annotatedInterface : javaClass.getAnnotatedInterfaces()) {
//            TSType superClass = TypeMapper.map(annotatedInterface.getType());
//            if (superClass instanceof TSInterfaceReference) {
//                TSInterfaceReference tsSuperClassInterface = (TSInterfaceReference) superClass;
//                tsInterface.addExtendsInterfaces(tsSuperClassInterface);
//            }
//        }
    }

    public List<TSField> getReadOnlyStaticFieldConverter(Class javaClass, TSClass tsInterface) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy) {
        ObjectMapper objectMapper = selectObjectMapper(javaClass);
        TSClassReference tsInterfaceReference = (TSClassReference) TypeMapper.map(javaClass);
        TSClass tsClass = tsInterfaceReference.getReferencedType();
        if (!tsClass.isConverted()) {
            tsClass.setConverted(true);
            convertFormalTypeParameters(javaClass.getTypeParameters(), tsInterfaceReference);
            List<TSField> readOnlyStaticFields = getReadOnlyStaticFieldConverter(javaClass,tsClass);
            for (TSField readOnlyStaticField : readOnlyStaticFields) {
                tsClass.addTsField(readOnlyStaticField);
            }
            LinkedHashSet<Property> propertySet = getClassProperties(javaClass, objectMapper);

            for (Property property : propertySet) {
                List<TSField> tsFieldList = objectMapper.mapJavaPropertyToField(property, tsClass, this, implementationGenerator, nullableTypesStrategy);
                if (tsFieldList.size() == 1) {
                    setAsNullableType(property, tsFieldList.get(0), nullableTypesStrategy);
                }
                for (TSField tsField : tsFieldList) {
                    tsClass.addTsField(tsField);
                    conversionListener.tsFieldCreated(property, tsField);
                }
            }

            objectMapper.addTypeLevelSpecificFields(javaClass, tsClass);
            tsClass.addAllAnnotations(javaClass.getAnnotations());
            conversionListener.tsScopedTypeCreated(javaClass, tsClass);
        }

    }

}
