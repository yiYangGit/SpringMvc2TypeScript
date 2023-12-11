package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.converters.*;

import com.blueveery.springrest2ts.filter.GenTsController;
import com.blueveery.springrest2ts.filter.GenTsModel;
import com.blueveery.springrest2ts.filters.*;

import com.blueveery.springrest2ts.implgens.Angular4ImplementationGenerator;
import com.blueveery.springrest2ts.tsmodel.TSClass;
import com.blueveery.springrest2ts.tsmodel.TSField;
import com.blueveery.springrest2ts.tsmodel.TSScopedElement;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class FastCodeGenerator {

    public static void tsModuleCreatorConverter(File outPudDir,
                                                boolean isDeleteAllFileInOutPudDir,
                                                Set<String> packageNameSets,
                                                String urlPredix,
                                                FastCodeGeneratorConfig generatorConfig

    ) throws IOException {
        if (!outPudDir.exists()) {
            outPudDir.mkdirs();
        }
        if (isDeleteAllFileInOutPudDir) {
            toDeleteDir(outPudDir);
        }


        Rest2tsGenerator2 tsGenerator = new Rest2tsGenerator2();
        tsGenerator.setNullableTypesStrategy(generatorConfig.getNullableTypesStrategy());
//        //set java type filters
        //     tsGenerator.setModelClassesCondition(new ExtendsJavaTypeFilter(ParametrizedBaseDTO.class));
//        tsGenerator.setRestClassesCondition(new ExtendsJavaTypeFilter(BaseCtrl.class));

        tsGenerator.setModelClassesCondition(new HasAnnotationJavaTypeFilter(GenTsModel.class));
        tsGenerator.setRestClassesCondition(new HasAnnotationJavaTypeFilter(GenTsController.class));

        //set model class converter
        DefaultObjectMapper defaultObjectMapper = new DefaultObjectMapper();
        ModelClassesToTsInterfacesConverter modelClassesConverter = new ModelClassesToTsInterfacesConverter(defaultObjectMapper) {
            @Override
            public List<TSField> getReadOnlyStaticFieldConverter(Class javaClass, TSClass tsClass) {
                return StaticFieldConverter.getGenStaticField(javaClass, tsClass, generatorConfig.getObject2JsonMapper());
            }
        };
        Set<ConversionListener> conversionListenerSet = modelClassesConverter.getConversionListener().getConversionListenerSet();
        //监听回调 tsclass的生成
        Map<TSClass, Class> tsClassClassMap = new HashMap<>();
        conversionListenerSet.add(new ConversionListener() {
            @Override
            public void tsScopedTypeCreated(Class javaType, TSScopedElement tsScopedElement) {
                if (tsScopedElement instanceof TSClass) {
                    TSClass tsClass = (TSClass) tsScopedElement;
                    tsClassClassMap.put(tsClass, javaType);
                    if (generatorConfig.getTsClassCreateListens() != null) {
                        for (TsClassCreateListen tsClassCreateListen : generatorConfig.getTsClassCreateListens()) {
                            if (tsClassCreateListen.onTsClassCreate(tsClass, javaType)) {
                                tsClass.getBeforeTsClassWrite().add(bufferedWriter -> {
                                    tsClassCreateListen.beforeTsClassWrite(bufferedWriter, tsClass, javaType);
                                });
                                tsClass.getAfterTsClassWrite().add(bufferedWriter -> {
                                    tsClassCreateListen.afterTsClassWrite(bufferedWriter, tsClass, javaType);
                                });
                            }
                        }
                    }
                }
            }
        });
        tsGenerator.setModelClassesConverter(modelClassesConverter);

        //set rest class converter
        Angular4ImplementationGenerator implementationGenerator = new Angular4ImplementationGenerator(null, urlPredix);
        SpringRestToTsConverter restClassesConverter = new SpringRestToTsConverter(implementationGenerator) {
            @Override
            public List<TSField> getReadOnlyStaticFieldConverter(Class javaClass, TSClass tsClass) {
                return StaticFieldConverter.getGenStaticField(javaClass, tsClass, generatorConfig.getObject2JsonMapper());
            }
        };
        tsGenerator.setRestClassesConverter(restClassesConverter);
        tsGenerator.setRestInterfaceConverter(new RestInterfaceConverter(implementationGenerator));
        TsModuleCreatorConverter moduleConverter = new TsModuleCreatorConverter(1000);
        tsGenerator.setJavaPackageToTsModuleConverter(moduleConverter);
        tsGenerator.setExtendsModelClass(new SearchExtendsClass() {
            @Override
            public Set<Class> searchModelClassOnRestClass(List<Class> classes, Set<Class> modelClasses, Set<Class> restClasses, Set<Class> enumClasses) {
                Set<Class> modelSets = new HashSet<>();
                for (Class restClass : restClasses) {
                    modelSets.addAll(SpringRestToTsConverter.searchModelClassOnRestClass(restClass));
                }
                return modelSets;
            }
        });
        tsGenerator.generate(packageNameSets, outPudDir,generatorConfig.getBaseModelClass(),generatorConfig.getBaseRestClass());
    }

    private static void toDeleteDir(File outPudDir) throws IOException {
        checkDir(outPudDir);
        deleteDir(outPudDir);
    }

    private static void deleteDir(File outPudDir) throws IOException {
        for (File path : outPudDir.listFiles()) {
            if (path.isDirectory()) {
                try {
                    deleteDir(path);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            } else {
                try {
                    path.delete();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        outPudDir.delete();
    }

    private static void checkDir(File outPudDir) throws IOException {
        for (File path : outPudDir.listFiles()) {
            if (!path.isDirectory() && !path.getPath().endsWith(".ts")) {
                throw new IllegalStateException("无法删除生成ts代码目录,含有后缀名不是.ts的文件" + path.getAbsolutePath());
            }
            if (path.isDirectory()) {
                try {
                    checkDir(path);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }


}
