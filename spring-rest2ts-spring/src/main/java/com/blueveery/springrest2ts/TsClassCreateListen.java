package com.blueveery.springrest2ts;

import com.blueveery.springrest2ts.tsmodel.TSClass;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by yangyi on 2021/2/5.
 */
public interface TsClassCreateListen {


    /**
     *  //ts类 create时监听
     * @param tsClass
     * @param javaClass
     * @return true 将在生成ts类之前和之后 回调 beforeTsClassWrite afterTsClassWrite
     */
    boolean onTsClassCreate(TSClass tsClass, Class javaClass);

    //拓展接口,生成javaClass 对应的ts类前面可以提前写一些代码
   default void beforeTsClassWrite(BufferedWriter writer,TSClass tsClass,Class javaClass) throws IOException{};


    //拓展接口,生成javaClass 对应的ts类的后面 可以提前写一些代码
    void afterTsClassWrite(BufferedWriter writer, TSClass tsClass, Class javaClass) throws IOException;



}
