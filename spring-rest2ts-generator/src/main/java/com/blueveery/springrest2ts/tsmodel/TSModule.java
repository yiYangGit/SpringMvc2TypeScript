package com.blueveery.springrest2ts.tsmodel;



import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import com.blueveery.springrest2ts.Rest2tsGenerator;
import com.blueveery.springrest2ts.converters.TypeMapper;

import com.blueveery.springrest2ts.tsmodel.generics.IParameterizedWithFormalTypes;
import com.blueveery.springrest2ts.tsmodel.generics.TSClassReference;
import com.blueveery.springrest2ts.tsmodel.generics.TSFormalTypeParameter;
import com.blueveery.springrest2ts.tsmodel.generics.TSParameterizedTypeReference;
import org.slf4j.Logger;

import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.implementation;
import static com.blueveery.springrest2ts.tsmodel.ModuleExtensionType.typing;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSModule extends TSElement {
    private String fileName;
    private String[] packageName;
    protected boolean isExternal = false;
    protected Map<TSModule, TSImport> importMap = new TreeMap<>();
    protected SortedSet<TSScopedElement> scopedTypesSet = new TreeSet<>();
    protected Path moduleRelativePath;
    private ModuleExtensionType moduleExtensionType = typing;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String[] getPackageName() {
        return packageName;
    }



    //生成的代码只有一层文件目录
    public TSModule(String name, Path moduleRelativePath, boolean isExternal) {
        super(name);
        this.moduleRelativePath = moduleRelativePath;
        this.isExternal = isExternal;
    }

    public TSModule(String name, String[] packageName,String fileName,Path moduleRelativePath, boolean isExternal) {
        super(name);
        this.packageName = packageName;
        this.fileName = fileName;
        this.moduleRelativePath = moduleRelativePath;
        this.isExternal = isExternal;
    }

    public Path getModuleRelativePath() {
        return moduleRelativePath;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void writeModule(Path outputDir, Logger logger) throws IOException {
        Path tsModuleDir;
        if (this.packageName != null) {
            tsModuleDir = outputDir.resolve(String.join(File.separator, this.packageName));
        } else {
            tsModuleDir = outputDir.resolve(moduleRelativePath);
        }
        if (!Files.exists(tsModuleDir)) {
            File file = tsModuleDir.toFile();
            file.mkdirs();
        }
        Path tsModuleFile;
        if (this.fileName != null) {
            tsModuleFile = tsModuleDir.resolve(this.fileName + "." + moduleExtensionType);
        } else {
            tsModuleFile = tsModuleDir.resolve(getName() + "." + moduleExtensionType);
        }
        if (!Files.exists(tsModuleFile)) {
            Files.createFile(tsModuleFile);
        }
        //logger.info(String.format("Generating module into %s", tsModuleFile.toAbsolutePath().normalize().toUri()));
        BufferedWriter writer = Files.newBufferedWriter(tsModuleFile, StandardCharsets.UTF_8);
        write(writer);
        writer.close();
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        writer.write("//此代码为生成的代码请勿修改 !!!");
        writer.newLine();
        this.writeImportBlock(writer);
        writer.newLine();
        this.writeScopedElements(writer);
    }

    protected void writeImportBlock(BufferedWriter writer) throws IOException {
        for (TSImport tsImport : importMap.values()) {
            tsImport.write(writer);
            writer.newLine();
        }
    }

    protected void writeScopedElements(BufferedWriter writer) throws IOException {
        for (TSScopedElement tsScopedElement : sort(scopedTypesSet)) {
            tsScopedElement.write(writer);
            writer.newLine();
            writer.newLine();
        }
    }

    protected List<? extends TSScopedElement> sort(SortedSet<TSScopedElement> scopedTypesSet) {
        List<TSScopedElement> sortedElements = new ArrayList<>();
        List<TSVariable> tsVariableList = new ArrayList<>();
        for (TSScopedElement tsScopedElement : scopedTypesSet) {
            if(tsScopedElement instanceof TSInterface){
                sortedElements.add(tsScopedElement);
                continue;
            }
            if(tsScopedElement instanceof TSClass){
                TSClass tsClass = (TSClass) tsScopedElement;
                addDependantClasses(tsClass, sortedElements);
                continue;
            }
            if(tsScopedElement instanceof TSVariable){
                tsVariableList.add((TSVariable) tsScopedElement);
                continue;
            }
            sortedElements.add(tsScopedElement);

        }
        sortedElements.addAll(tsVariableList);
        return sortedElements;
    }

    private void addDependantClasses(TSClass tsClass, List<TSScopedElement> sortedElements) {
        TSClassReference extendsClassReference = tsClass.getExtendsClass();
        if (extendsClassReference != null) {
            TSClass tsBaseClass = extendsClassReference.getReferencedType();
            if (tsBaseClass.getModule() == this) {
                addDependantClasses(tsBaseClass, sortedElements);
            }
        }
        if (!sortedElements.contains(tsClass)) {
            sortedElements.add(tsClass);
        }
    }

    public void addScopedElement(TSScopedElement tsScopedElement) {
        scopedTypesSet.add(tsScopedElement);
        if(tsScopedElement instanceof TSClass || !Rest2tsGenerator.generateAmbientModules){
            moduleExtensionType = implementation;
        }
    }

    public void scopedTypeUsage(TSType tsType) {
        if (tsType instanceof TSParameterizedTypeReference) {
            scopedTypeUsage(((TSParameterizedTypeReference) tsType));
        }
        if (tsType instanceof TSScopedElement) {
            scopedTypeUsage(((TSScopedElement) tsType));
        }
    }
    public void scopedTypeUsage(TSParameterizedTypeReference<?> typeReference) {
        IParameterizedWithFormalTypes referencedType = typeReference.getReferencedType();
        if (referencedType instanceof TSScopedElement) {
            TSScopedElement referencedScopedType = (TSScopedElement) referencedType;
            scopedTypeUsage(referencedScopedType);
        }
        if (referencedType instanceof TSFormalTypeParameter) {
            TSFormalTypeParameter formalTypeParameter = (TSFormalTypeParameter) referencedType;
            scopedTypeUsage(formalTypeParameter.getBoundTo());
        }
        for (TSType tsType : typeReference.getTsTypeParameterList()) {
            if (tsType instanceof TSParameterizedTypeReference) {
                scopedTypeUsage((TSParameterizedTypeReference<?>) tsType);
            }
        }
    }
    public void scopedTypeUsage(TSScopedElement tsScopedElement) {
        TSModule module = tsScopedElement.getModule();
        if(module != this && module != TypeMapper.systemModule){
            TSImport tsImport = importMap.get(module);
            if(tsImport == null){
                tsImport = new TSImport(module, this);
                importMap.put(module, tsImport);
            }
            tsImport.getWhat().add(tsScopedElement);
        }
    }

    @Override
    public int hashCode() {
        return moduleRelativePath != null ? getName().hashCode() : moduleRelativePath.resolve(getName()).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TSModule)) {
            return false;
        }
        TSModule otherTsModule = (TSModule) object;
        return getName().equals(otherTsModule.getName()) && Objects.equals(moduleRelativePath, otherTsModule.moduleRelativePath);
    }


}
