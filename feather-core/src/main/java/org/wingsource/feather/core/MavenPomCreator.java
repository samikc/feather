package org.wingsource.feather.core;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by samikc on 23/4/16.
 */
public class MavenPomCreator {

    public void createPom(File file, String projectName,
                          String groupId, String version, List<Dependency> dependencies) {

        Model model = new Model();
        model.setGroupId( groupId );
        model.setArtifactId(projectName);
        model.setVersion(version);
        model.setModelVersion("4.0.0");
        model.setDependencies(dependencies);

        //File pomFile = model.getPomFile();
        System.out.println(model.toString());
        try {
            new MavenXpp3Writer().write(new FileWriter(file),model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
