package com.blueveery.springrest2ts.tsmodel;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by tomaszw on 30.07.2017.
 */
public class TSImport extends TSElement{
    private TSModule currentModule;
    private TSModule fromModule;
    private SortedSet<TSScopedElement> what = new TreeSet<>();

    public TSImport(TSModule fromModule, TSModule tsCurrentModule) {
        super(fromModule.getName());
        this.fromModule = fromModule;
        this.currentModule = tsCurrentModule;
    }

    public TSModule getFromModule() {
        return fromModule;
    }

    public SortedSet<TSScopedElement> getWhat() {
        return what;
    }

    public TSImport(TSModule fromModule) {
        super(fromModule.getName());
        this.fromModule = fromModule;
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        StringBuilder whatString = new StringBuilder();
        int i=0;
        for(TSScopedElement tsScopedElement :getWhat()){
            whatString.append(tsScopedElement.getName());
            if((i++)<getWhat().size()-1) {
                whatString.append(", ");
            }
        }
        if (fromModule.getPackageName() != null) {
            String[] packageName = fromModule.getPackageName();
            StringBuilder importRelativePath = new StringBuilder();
            int packageDepthLength = 0;
            if (this.currentModule.getPackageName() != null) {
                packageDepthLength = this.currentModule.getPackageName().length;
            }
            for (int i1 = 0; i1 < packageDepthLength; i1++) {
                importRelativePath.append("../");
            }
            writer.write("import {" + whatString.toString() + "} from '" + importRelativePath + String.join("/",packageName) + "/" + fromModule.getFileName() + "'");
        } else {
            String relativePath = fromModule.getModuleRelativePath() == null ? "" : fromModule.getModuleRelativePath() + "/";
            relativePath = relativePath.replaceAll("\\\\", "/");
            writer.write("import {" + whatString.toString() + "} from '" + relativePath + getFromModule().getName()+"';");
        }
    }
}
