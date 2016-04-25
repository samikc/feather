package org.wingsource.feather.core;



import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by samikc on 16/4/16.
 */
public class DBPropertiesCreator {

    public void createPropertiesFile(File file, String url, String password,
                                     String userid, String driverClass) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc.driverClassName=").append(driverClass).append("\n");
        sb.append("jdbc.url=").append(url).append("\n");
        sb.append("jdbc.username=").append(userid).append("\n");
        sb.append("jdbc.password=").append(password);
        if(!file.exists()) {
            file.createNewFile();
            FileUtils.write(file,sb.toString());
        }
    }
}
