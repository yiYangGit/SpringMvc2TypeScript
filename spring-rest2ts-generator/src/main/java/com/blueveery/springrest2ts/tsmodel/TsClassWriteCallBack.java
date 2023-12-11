package com.blueveery.springrest2ts.tsmodel;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by yangyi0 on 2021/2/5.
 */
public interface TsClassWriteCallBack {
    void listenWrite(BufferedWriter bufferedWriter) throws IOException;
}
