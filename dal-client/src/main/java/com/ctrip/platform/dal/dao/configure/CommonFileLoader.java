package com.ctrip.platform.dal.dao.configure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author c7ch23en
 */
public class CommonFileLoader extends AbstractResourceLoader {

    @Override
    protected String getResourceContent(String resourceName) throws IOException {
        try (FileInputStream stream = new FileInputStream(new File(resourceName))) {
            return getString(stream);
        }
    }

}
