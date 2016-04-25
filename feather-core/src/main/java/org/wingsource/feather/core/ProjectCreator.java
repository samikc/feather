package org.wingsource.feather.core;

import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by samikc on 16/4/16.
 */
public class ProjectCreator {

    private static final Logger LOG = Logger.getLogger(ProjectCreator.class.getName());
    /***
     * Given a project name and a directory where to write the project
     * we create it with a basic maven structure.
     *
     * projectDir/projectName/
     *                      src/
     *                          main/
     *                              java/
     *                              resources/
     *                          test/
     *                              java/
     *                              resources/
     *                      pom.xml
     * Here we also create an empty maven pom.xml.
     * @param projectName
     * @param projectDir
     * @param basePackage
     */
    public void createProject(String projectName, String projectDir, String basePackage) {
        File pDir = new File(projectDir);
        if (!pDir.isDirectory() && !pDir.exists()) {
            LOG.severe("ERROR: Project directory is not a directory or directory does not exists");
            throw new IllegalArgumentException("Project directory is not a directory " +
                    "or directory does not exists : "+pDir.getName());
        }

        if(!pDir.canWrite()) {
            LOG.severe("ERROR: We do not have permission to write to Project directory "+ pDir.getName());
            throw new IllegalArgumentException("ERROR: We do not have permission to write" +
                    " to Project directory "+ pDir.getName());
        }
        List<String> dirsToCreate = new ArrayList<String>();
        dirsToCreate.add(pDir.getAbsolutePath() + "/" + projectName + "/src/main/java/");
        dirsToCreate.add(pDir.getAbsolutePath() + "/" + projectName + "/src/main/resources/");
        dirsToCreate.add(pDir.getAbsolutePath() + "/" + projectName + "/src/test/java/");
        dirsToCreate.add(pDir.getAbsolutePath() + "/" + projectName + "/src/test/java/");
        for (String dir : dirsToCreate) {
            File f = new File(dir);
            f.mkdirs();
        }
        File pom = new File(pDir.getAbsolutePath() + "/" + projectName + "/pom.xml");
        try {
            if (!pom.exists()) {
                pom.createNewFile();
            }
            MavenPomCreator mpc = new MavenPomCreator();
            List<Dependency> dependencies = new ArrayList<Dependency>();
            // Mybatis dependency
            Dependency d = new Dependency();
            d.setGroupId("org.mybatis");
            d.setArtifactId("mybatis");
            d.setVersion("3.1.1");
            dependencies.add(d);
            // Wingweb dependency
            Dependency dependency = new Dependency();
            dependency.setVersion("0.0.1");
            dependency.setArtifactId("http-server-core");
            dependency.setGroupId("org.wingsource");
            dependencies.add(dependency);
            mpc.createPom(pom,projectName,basePackage,"0.0.1-SNAPSHOT",dependencies);

        } catch (IOException e) {
            LOG.severe("ERROR:Could not create pom.xml file");
            LOG.severe(e.getMessage());
        }
    }
}
