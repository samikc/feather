package org.wingsource.feather.core;

import org.apache.commons.cli.*;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.wingsource.feather.core.db.H2DBSetup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by samikc on 16/4/16.
 */
public class Generator {

    private static final Logger LOG = Logger.getLogger(Generator.class.getName());
    public static final String ORG_WINGSOURCE_FEATHER_MYBATIS_PLUGIN_REST_PLUGIN = "org.wingsource.feather.mybatis.plugin.RestPlugin";
    public static final String WINGWEB = "WINGWEB";
    public static final String MY_BATIS_3 = "MyBatis3";
    public static final String FLAT = "FLAT";
    public static final String XMLMAPPER = "XMLMAPPER";
    public static final String SRC_MAIN_JAVA = "/src/main/java";
    public static final String DAO_PACKAGE = ".dao";
    public static final String MODEL_PACKAGE = ".model";
    public static final String MAPPER_XML_PACKAGE = ".xml";
    public static final String SRC_MAIN_RESOURCES = "/src/main/resources";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("s",true,"SQL file which creates the tables");
        options.addOption("d",true,"Project directory where the project will be created");
        options.addOption("n",true,"project name");
        options.addOption("p",true,"base java package used for creating the file");
        if (args.length < 4) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "generator", options );
        }

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "generator", options );
            System.exit(-1);
        }
        String scriptFile = cmd.getOptionValue("s");
        if (scriptFile == null) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "generator", options );
            System.exit(-1);
        }
        File file = new File(scriptFile);
        if (file.exists()) {
            System.out.println("Generating Wingweb project with file: "+file.getName());
            String baseDir = cmd.getOptionValue("d");
            String projectName = cmd.getOptionValue("n");
            String basePackage = cmd.getOptionValue("p");
            ProjectCreator pc = new ProjectCreator();
            pc.createProject(projectName,baseDir,basePackage);

            DBPropertiesCreator dbpc = new DBPropertiesCreator();
            dbpc.createPropertiesFile(new File(baseDir+"/"+projectName+ SRC_MAIN_RESOURCES+"/db.properties"),"","","","");


            Generator g = new Generator();
            g.generate(file,baseDir,projectName,basePackage);
        }
    }

    public void generate(File file, String baseDir, String projectName, String basePackage) throws Exception {
        // Setup the DB first
        //MySQLDBSetup dbSetup = new MySQLDBSetup();
        H2DBSetup dbSetup = new H2DBSetup();
        // run sql script to create the tables
        dbSetup.runScript(file);
        List<String> tables = dbSetup.listTables();
        // create the MyBatis generator configuration - programmatically
        Configuration config = new Configuration();
        Context context = new Context(ModelType.getModelType(FLAT));
        context.setId(WINGWEB);
        context.addProperty("TARGET_CONFIG_PACKAGE","");
        context.addProperty("TARGET_BASE_PACKAGE",basePackage);
        context.addProperty("TARGET_CONFIG_PACKAGE_MAPPER",basePackage+ MAPPER_XML_PACKAGE);
        context.addProperty("TARGET_PROJECT_RESOURCES",baseDir+"/"+projectName+ SRC_MAIN_RESOURCES + "/");
        context.addProperty("TARGET_PROJECT_JAVA",baseDir+"/"+projectName+ SRC_MAIN_JAVA + "/");
        context.setTargetRuntime(MY_BATIS_3);
        PluginConfiguration plugin = new PluginConfiguration();
        plugin.setConfigurationType(ORG_WINGSOURCE_FEATHER_MYBATIS_PLUGIN_REST_PLUGIN);
        context.addPluginConfiguration(plugin);

        for (String table : tables) {
            TableConfiguration tableConfig = new TableConfiguration(context);
            tableConfig.setSchema("");
            tableConfig.setTableName(table);
            context.addTableConfiguration(tableConfig);
        }

        JavaClientGeneratorConfiguration clintGenConfig = new JavaClientGeneratorConfiguration();
        clintGenConfig.setTargetPackage(basePackage+ DAO_PACKAGE);
        clintGenConfig.setTargetProject(baseDir+"/"+projectName+ SRC_MAIN_JAVA);
        clintGenConfig.setConfigurationType(XMLMAPPER);
        context.setJavaClientGeneratorConfiguration(clintGenConfig);
        JavaModelGeneratorConfiguration modelGenConfig = new JavaModelGeneratorConfiguration();
        modelGenConfig.setTargetProject(baseDir+"/"+projectName+SRC_MAIN_JAVA);
        modelGenConfig.setTargetPackage(basePackage+ MODEL_PACKAGE);
        context.setJavaModelGeneratorConfiguration(modelGenConfig);
        JDBCConnectionConfiguration jdbcConnConfig = new JDBCConnectionConfiguration();
        jdbcConnConfig.setConnectionURL(dbSetup.getDbConnectionUrl());
        jdbcConnConfig.setDriverClass(dbSetup.getDbDriver());
        jdbcConnConfig.setPassword(dbSetup.getDbPassword());
        jdbcConnConfig.setUserId(dbSetup.getDbUser());
        context.setJdbcConnectionConfiguration(jdbcConnConfig);
        SqlMapGeneratorConfiguration sqlMapperConfig = new SqlMapGeneratorConfiguration();
        sqlMapperConfig.setTargetPackage(basePackage+ MAPPER_XML_PACKAGE);
        sqlMapperConfig.setTargetProject(baseDir+"/"+projectName+ SRC_MAIN_RESOURCES);
        context.setSqlMapGeneratorConfiguration(sqlMapperConfig);
        config.addContext(context);
        // run the generator to generate the code and project
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);

        for (String warning : warnings) {
            LOG.warning(warning);
        }

    }
}
