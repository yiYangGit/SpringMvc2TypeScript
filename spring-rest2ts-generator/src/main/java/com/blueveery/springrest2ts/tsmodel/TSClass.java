package com.blueveery.springrest2ts.tsmodel;


import com.blueveery.springrest2ts.implgens.ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSInterfaceReference;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by tomaszw on 31.07.2017.
 */
public class TSClass extends TSComplexElement {
    private TSClassReference extendsClass;
    private SortedSet<TSInterfaceReference> implementsInterfaces = new TreeSet<TSInterfaceReference>();
    private Map<TSInterfaceReference, List<TSType>> interfaceActualTypesMap = new HashMap<>();
    private boolean isAbstract;

    private List<TsClassWriteCallBack> afterTsClassWrite = new ArrayList<>();
    private List<TsClassWriteCallBack> beforeTsClassWrite = new ArrayList<>();



    public TSClass(String name, TSModule module, ImplementationGenerator implementationGenerator) {

        super(name, module, implementationGenerator);
        isAbstract = false;
    }

    public List<TsClassWriteCallBack> getAfterTsClassWrite() {
        return afterTsClassWrite;
    }

    public void setAfterTsClassWrite(List<TsClassWriteCallBack> afterTsClassWrite) {
        this.afterTsClassWrite = afterTsClassWrite;
    }

    public List<TsClassWriteCallBack> getBeforeTsClassWrite() {
        return beforeTsClassWrite;
    }

    public void setBeforeTsClassWrite(List<TsClassWriteCallBack> beforeTsClassWrite) {
        this.beforeTsClassWrite = beforeTsClassWrite;
    }

    public TSClassReference getExtendsClass() {
        return extendsClass;
    }

    public void setExtendsClass(TSClassReference extendsClass) {
        if (extendsClass != null) {
            module.scopedTypeUsage(extendsClass);
        }
        this.extendsClass = extendsClass;
    }

    public SortedSet<TSInterfaceReference> getImplementsInterfaces() {
        return implementsInterfaces;
    }

    public void addImplementsInterfaces(TSInterfaceReference tsInterfaceReference) {
        getModule().scopedTypeUsage(tsInterfaceReference);
        implementsInterfaces.add(tsInterfaceReference);
    }

    public void addImplementsInterfacesWithActualType(TSInterfaceReference tsInterfaceReference, List<TSType> types) {
        getModule().scopedTypeUsage(tsInterfaceReference);
        implementsInterfaces.add(tsInterfaceReference);
        interfaceActualTypesMap.put(tsInterfaceReference, types);
    }

    @Override
    public boolean isInstanceOf(TSComplexElement tsComplexType) {
        if (tsComplexType instanceof TSClass) {
            if (extendsClass == null) {
                return false;
            }
            if (extendsClass.getReferencedType() == tsComplexType) {
                return true;
            }

            return extendsClass.getReferencedType().isInstanceOf(tsComplexType);
        }
        for (TSInterfaceReference implementedInterface : implementsInterfaces) {
            if (implementedInterface.getReferencedType() == tsComplexType) {
                return true;
            }

            return implementedInterface.getReferencedType().isInstanceOf(tsComplexType);
        }
        return false;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        tsComment.write(writer);
        List<TSDecorator> decorators = implementationGenerator.getDecorators(this);
        writeDecorators(writer, decorators);
        writeDecorators(writer, getTsDecoratorList());
        if (this.beforeTsClassWrite != null) {
            for (TsClassWriteCallBack beforeWrite : this.beforeTsClassWrite) {
                beforeWrite.listenWrite(writer);
            }
        }
        writer.write("export");
        if (isAbstract) {
            writer.write(" abstract");
        }
        writer.write(" class " + getName() + " ");
        writer.write(typeParametersToString());
        if(extendsClass!=null){
            writer.write("extends " + extendsClass.getName() + " ");
        }

        if(!implementsInterfaces.isEmpty()){
            writer.write("implements ");
            Iterator<TSInterfaceReference> iterator = implementsInterfaces.iterator();
            while (iterator.hasNext()){
                TSInterfaceReference next = iterator.next();
                writer.write(next.getName());
                List<TSType> tsTypes = interfaceActualTypesMap.get(next);
                if (tsTypes != null) {
                    writer.write(IParameterizedWithTypes.getTypeParameterStringFromTsType(tsTypes));
                }
                if(iterator.hasNext()){
                    writer.write(", ");
                }
            }
        }

        writer.write("{");
        writeMembers(writer);
        writer.write("}");
        if (this.afterTsClassWrite != null) {
            for (TsClassWriteCallBack afterTs : this.afterTsClassWrite) {
                afterTs.listenWrite(writer);
            }
        }
    }
}
