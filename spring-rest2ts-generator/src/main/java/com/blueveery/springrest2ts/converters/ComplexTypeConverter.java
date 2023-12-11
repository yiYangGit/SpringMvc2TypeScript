package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.naming.ClassNameMapper;
import com.blueveery.springrest2ts.naming.NoChangeClassNameMapper;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.blueveery.springrest2ts.tsmodel.TSSimpleType;
import com.blueveery.springrest2ts.tsmodel.TSType;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by tomaszw on 31.07.2017.
 */
public abstract class ComplexTypeConverter {
    protected static Logger logger = LoggerFactory.getLogger("gen-logger");
    protected DispatcherConversionListener conversionListener = new DispatcherConversionListener();
    protected ImplementationGenerator implementationGenerator;
    protected ClassNameMapper classNameMapper = new NoChangeClassNameMapper();


    protected ComplexTypeConverter(ImplementationGenerator implementationGenerator) {
        this.implementationGenerator = implementationGenerator;
    }

    public ComplexTypeConverter(ImplementationGenerator implementationGenerator, ClassNameMapper classNameMapper) {
        this(implementationGenerator);
        this.classNameMapper = classNameMapper;
    }

    public DispatcherConversionListener getConversionListener() {
        return conversionListener;
    }

    public abstract boolean preConverted(JavaPackageToTsModuleConverter javaPackageToTsModuleConverter, Class javaClass);
    public abstract void convert(Class javaClass, NullableTypesStrategy nullableTypesStrategy);

    public ClassNameMapper getClassNameMapper() {
        return classNameMapper;
    }

    public void setClassNameMapper(ClassNameMapper classNameMapper) {
        this.classNameMapper = classNameMapper;
    }

    public ImplementationGenerator getImplementationGenerator() {
        return implementationGenerator;
    }

    protected String createTsClassName(Class javaClass) {
        List<String> classNameComponentList = new ArrayList<>();
        do {
            classNameComponentList.add(classNameMapper.mapJavaClassNameToTs(javaClass.getSimpleName()));
            javaClass = javaClass.getDeclaringClass();
        }while (javaClass != null);
        Collections.reverse(classNameComponentList);

        return String.join("$", classNameComponentList);
    }

    public void convertInheritance(Class javaType){

    }

    protected void convertFormalTypeParameters(TypeVariable<Class>[] typeVariables, TSParameterizedTypeReference typeReference){
        for (TypeVariable<Class> typeVariable : typeVariables) {
            TSFormalTypeParameter tsFormalTypeParameter = new TSFormalTypeParameter(typeVariable.getName());
            if (typeVariable.getBounds().length>0) {
                TSType boundToType = TypeMapper.map(typeVariable.getBounds()[0]);
                if (boundToType.getClass().equals(TSSimpleType.class) || (boundToType != TypeMapper.tsObject && boundToType instanceof TSParameterizedTypeReference)) {
                    tsFormalTypeParameter.setBoundTo(boundToType);
                    if (typeReference.getReferencedType() instanceof TSScopedElement) {
                        TSScopedElement referencedType = (TSScopedElement) typeReference.getReferencedType();
                        referencedType.getModule().scopedTypeUsage(boundToType);
                    }
                }
            }
            typeReference.getReferencedType().getTsTypeParameterList().add(tsFormalTypeParameter);
        }
    }
}
