package com.company.engine.loaders.assimp;

import org.omg.CORBA.UNKNOWN;

public enum ModelFileType {

    /**
     *
     */

    OBJ(".obj"),
    ANY(""),
    UNKNOWN(null);

    private String mFileExtension;

    ModelFileType(String fileExtension) {
        mFileExtension = fileExtension;
    }

    public String getFileExtension() {
        return mFileExtension;
    }
}