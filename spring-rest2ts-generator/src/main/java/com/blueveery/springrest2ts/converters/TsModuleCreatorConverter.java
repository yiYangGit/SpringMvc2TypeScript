package com.blueveery.springrest2ts.converters;

import com.blueveery.springrest2ts.tsmodel.TSModule;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 03.08.2017.
 */
public class TsModuleCreatorConverter implements JavaPackageToTsModuleConverter {
    private int numberOfSubPackages;
    private Map<String, TSModule> packagesMap = new HashMap<>();
    private SortedSet<TSModule> tsModuleSortedSet = new TreeSet<>();

    public TsModuleCreatorConverter(int numberOfSubPackages) {
        this.numberOfSubPackages = numberOfSubPackages;
    }

    @Override
    public void mapJavaTypeToTsModule(Class javaType) {
        getTsModule(javaType);
    }

    @Override
    public SortedSet<TSModule> getTsModules() {
        return tsModuleSortedSet;
    }

    @Override
    public TSModule getTsModule(Class javaType) {

        String moduleName = javaType.getName();
        TSModule tsModule = packagesMap.get(moduleName);
        if (tsModule == null) {
            String packageName = javaType.getPackage().getName();
            String[] subPackages = packageName.split("\\.");
            tsModule = new TSModule(moduleName, subPackages, moduleName.substring(moduleName.lastIndexOf(".") + 1), Paths.get("."), false);
            packagesMap.put(moduleName, tsModule);
            tsModuleSortedSet.add(tsModule);
        }
        return tsModule;
    }
}
