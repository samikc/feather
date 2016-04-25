package org.wingsource.feather.mybatis.plugin;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.DefaultXmlFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.wingsource.wingweb.http.core.Service;
import org.wingsource.wingweb.http.core.ServiceRegistry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samikc on 16/4/16.
 */
public class RestPlugin extends PluginAdapter {

    private Document doc = null;
    private GeneratedJavaFile mybatisWingWebJavaFile = null;
    private GeneratedJavaFile wingWebServerJavaFile = null;
    private ServiceRegistry serviceRegistry = new ServiceRegistry();



    public RestPlugin() {
        super();
    }
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable table) {
        List<GeneratedJavaFile> javaFiles = new ArrayList<GeneratedJavaFile>();
        if (mybatisWingWebJavaFile == null) {
            createMybatisWingWebJavaFile(table);
            javaFiles.add(mybatisWingWebJavaFile);
        }

        if (wingWebServerJavaFile == null) {
            createWingWebMain(table);
            javaFiles.add(wingWebServerJavaFile);
        }

        javaFiles.add(createJavaFile(table));

        //table.getTableConfiguration()
        //GeneratedJavaFile javaFile = new GeneratedJavaFile()
        return javaFiles;
    }


    public boolean modelExampleClassGenerated(TopLevelClass tlc, IntrospectedTable table){

        tlc.addImportedType("javax.xml.bind.annotation.XmlRootElement");
        tlc.addAnnotation("@XmlRootElement(name = \""+table.getFullyQualifiedTable().getDomainObjectName()+"\")");
        return true;
    }

    public boolean modelPrimaryKeyClassGenerated(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType("javax.xml.bind.annotation.XmlRootElement");
        tlc.addAnnotation("@XmlRootElement(name = \""+table.getFullyQualifiedTable().getDomainObjectName()+"\")");
        return true;
    }

    public boolean modelBaseRecordClassGenerated(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType("javax.xml.bind.annotation.XmlRootElement");
        tlc.addAnnotation("@XmlRootElement(name = \""+table.getFullyQualifiedTable().getDomainObjectName()+"\")");
        return true;
    }

    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass tlc, IntrospectedTable table) {
        tlc.addImportedType("javax.xml.bind.annotation.XmlRootElement");
        tlc.addAnnotation("@XmlRootElement(name = \""+table.getFullyQualifiedTable().getDomainObjectName()+"\"");
        return true;
    }



    private void createWingWebMain(IntrospectedTable table){
        String target_base_package = table.getContext().getProperty("TARGET_BASE_PACKAGE");
        target_base_package = target_base_package + ".service";
        TopLevelClass serverClass = new TopLevelClass(target_base_package+"."
                +"WingWebServer");
        serverClass.addImportedType("org.wingsource.wingweb.http.core.ServerStartUp");
        Method mainMethod = new Method("main");
        Parameter param = new Parameter(new FullyQualifiedJavaType("String[]"),"args");
        mainMethod.addParameter(param);
        mainMethod.setVisibility(JavaVisibility.PUBLIC);
        mainMethod.setStatic(true);
        mainMethod.setReturnType(new FullyQualifiedJavaType("void"));
        mainMethod.addException(new FullyQualifiedJavaType("InstantiationException"));
        mainMethod.addException(new FullyQualifiedJavaType("IllegalAccessException"));
        mainMethod.addException(new FullyQualifiedJavaType("ClassNotFoundException"));
        mainMethod.addBodyLine("ServerStartUp.initServer();");
        serverClass.addMethod(mainMethod);
        String targetProject = table.getContext().getProperty("TARGET_PROJECT_JAVA");
        wingWebServerJavaFile = new GeneratedJavaFile(serverClass,targetProject,new DefaultJavaFormatter());
    }

    private GeneratedJavaFile createJavaFile(IntrospectedTable table) {
        String target_base_package = table.getContext().getProperty("TARGET_BASE_PACKAGE");
        target_base_package = target_base_package + ".service";
        TopLevelClass compilationUnit = new TopLevelClass(target_base_package+"."
                +table.getFullyQualifiedTable().getDomainObjectName()+"Service");
        Service service = new Service();
        service.setName(target_base_package+"."
                +table.getFullyQualifiedTable().getDomainObjectName()+"Service");
        serviceRegistry.getService().add(service);
        compilationUnit.setVisibility(JavaVisibility.PUBLIC);
        compilationUnit.addImportedType("javax.ws.rs.core.Response");
        compilationUnit.addImportedType("javax.ws.rs.Consumes");
        compilationUnit.addImportedType("javax.ws.rs.Path");
        compilationUnit.addImportedType("javax.ws.rs.Produces");
        compilationUnit.addImportedType("javax.ws.rs.POST");
        compilationUnit.addImportedType("javax.ws.rs.PUT");
        compilationUnit.addImportedType("javax.ws.rs.GET");
        compilationUnit.addImportedType("javax.ws.rs.DELETE");
        compilationUnit.addImportedType("javax.ws.rs.PathParam");

        compilationUnit.addImportedType("org.apache.ibatis.session.SqlSession");
        compilationUnit.addImportedType(table.getContext().getProperty("TARGET_BASE_PACKAGE")+
                ".model."+table.getFullyQualifiedTable().getDomainObjectName());
        compilationUnit.addImportedType(table.getContext().getProperty("TARGET_BASE_PACKAGE")+
                ".dao."+table.getFullyQualifiedTable().getDomainObjectName()+"Mapper");
        String path = table.getFullyQualifiedTable().getDomainObjectName()+"Service";
        path = path.toLowerCase();
        compilationUnit.addAnnotation("@Path(\"/"+path+"/\")");
        compilationUnit.addAnnotation("@Produces(\"application/json\")");
        compilationUnit.addAnnotation("@Consumes(\"application/json\")");


        Method insertMethod = new Method();
        insertMethod.addAnnotation("@POST");
        insertMethod.addAnnotation("@Path(\"/insert/\")");
        insertMethod.setName("insert");
        insertMethod.setVisibility(JavaVisibility.PUBLIC);
        insertMethod.setReturnType(new FullyQualifiedJavaType("Response"));

        Parameter param = new Parameter(new FullyQualifiedJavaType(table.getFullyQualifiedTable().getDomainObjectName()),"record");
        insertMethod.addParameter(param);
        insertMethod.addBodyLine("Response r = null;");
        insertMethod.addBodyLine("SqlSession sqlSession = WingwebMybatisUtil.getSqlSessionFactory().openSession();");
        insertMethod.addBodyLine("try {");
        StringBuilder sb = new StringBuilder();
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper ");
        sb.append("mapper = sqlSession.getMapper(");
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper.class);");
        sb.append("");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("mapper.insert(record);");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.commit();");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.ok().build();");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("} catch(Exception e) {");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.notModified().build();");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}finally{").append("");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.close();");
        insertMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}");
        insertMethod.addBodyLine(sb.toString());
        insertMethod.addBodyLine("return r;");
        Method updateMethod = new Method();
        updateMethod.addAnnotation("@PUT");
        updateMethod.addAnnotation("@Path(\"/update/\")");
        updateMethod.setName("update");
        updateMethod.setVisibility(JavaVisibility.PUBLIC);
        updateMethod.setReturnType(new FullyQualifiedJavaType("Response"));
        Parameter param1 = new Parameter(new FullyQualifiedJavaType(table.getFullyQualifiedTable().getDomainObjectName()),"record");
        updateMethod.addParameter(param1);
        updateMethod.addBodyLine("Response r = null;");
        updateMethod.addBodyLine("SqlSession sqlSession = WingwebMybatisUtil.getSqlSessionFactory().openSession();");
        updateMethod.addBodyLine("try {");
        sb.setLength(0);
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper ");
        sb.append("mapper = sqlSession.getMapper(");
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper.class);");
        sb.append("");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("mapper.updateByPrimaryKey(record);");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.commit();");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.ok().build();");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("} catch(Exception e) {");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.notModified().build();");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}finally{").append("");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.close();");
        updateMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}");
        updateMethod.addBodyLine(sb.toString());
        updateMethod.addBodyLine("return r;");
        Method deleteMethod = new Method();
        deleteMethod.addAnnotation("@DELETE");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@Path(\"/delete/");

        int counter = 0;
        for (IntrospectedColumn introspectedColumn : table.getPrimaryKeyColumns()) {
            stringBuilder.append("{id").append(counter).append("}/");
            counter++;
        }
        stringBuilder.append("\")");
        deleteMethod.addAnnotation(stringBuilder.toString());
        deleteMethod.setName("delete");
        deleteMethod.setVisibility(JavaVisibility.PUBLIC);
        deleteMethod.setReturnType(new FullyQualifiedJavaType("Response"));
        counter = 0;
        prepareParams(table, counter, deleteMethod);
        deleteMethod.addBodyLine("Response r = null;");
        deleteMethod.addBodyLine("SqlSession sqlSession = WingwebMybatisUtil.getSqlSessionFactory().openSession();");
        deleteMethod.addBodyLine("try {");
        sb.setLength(0);
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper ");
        sb.append("mapper = sqlSession.getMapper(");
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper.class);");
        sb.append("");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("mapper.deleteByPrimaryKey(record0");
        counter = 0;
        for (IntrospectedColumn introspectedColumn : table.getPrimaryKeyColumns()) {
            if (counter != 0){
                sb.append(",record").append(counter);
            }
            counter++;
        }
        sb.append(");");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.commit();");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.ok().build();");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("} catch(Exception e) {");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("r = Response.notModified().build();");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}finally{").append("");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.close();");
        deleteMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}");
        deleteMethod.addBodyLine(sb.toString());
        deleteMethod.addBodyLine("return r;");
        Method getMethod = new Method();
        getMethod.addAnnotation("@GET");
        stringBuilder.setLength(0);
        stringBuilder.append("@Path(\"/get/");

        counter = 0;
        for (IntrospectedColumn introspectedColumn : table.getPrimaryKeyColumns()) {
            stringBuilder.append("{id").append(counter).append("}/");
            counter++;
        }
        stringBuilder.append("\")");
        getMethod.addAnnotation(stringBuilder.toString());
        getMethod.setName("get");
        getMethod.setVisibility(JavaVisibility.PUBLIC);
        counter = 0;
        getMethod.setReturnType(new FullyQualifiedJavaType(table.getFullyQualifiedTable().getDomainObjectName()));
        prepareParams(table, counter, getMethod);

        getMethod.addBodyLine("SqlSession sqlSession = WingwebMybatisUtil.getSqlSessionFactory().openSession();");
        getMethod.addBodyLine("try {");
        sb.setLength(0);
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper ");
        sb.append("mapper = sqlSession.getMapper(");
        sb.append(table.getFullyQualifiedTable().getDomainObjectName()).append("Mapper.class);");
        sb.append("");
        getMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("return mapper.selectByPrimaryKey(record0");
        counter = 0;
        for (IntrospectedColumn introspectedColumn : table.getPrimaryKeyColumns()) {
            if (counter != 0){
               sb.append(",record").append(counter);
            }
            counter++;
        }
        sb.append(");");
        getMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}finally{").append("");
        getMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("sqlSession.close();");
        getMethod.addBodyLine(sb.toString());
        sb.setLength(0);
        sb.append("}");
        getMethod.addBodyLine(sb.toString());

        compilationUnit.addMethod(insertMethod);

        compilationUnit.addMethod(updateMethod);

        compilationUnit.addMethod(deleteMethod);

        compilationUnit.addMethod(getMethod);
        String targetProject = table.getContext().getProperty("TARGET_PROJECT_JAVA");
        GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,targetProject,new DefaultJavaFormatter());
        createServiceRegistry(table);
        return gjf;
    }

    private void createServiceRegistry(IntrospectedTable table) {
        File file = new File(table.getContext().getProperty("TARGET_PROJECT_RESOURCES")+"services.json");

        Gson gson = new Gson();
        try {
            file.createNewFile();
            String jsonStr = gson.toJson(serviceRegistry);
            FileWriter fout = new FileWriter(file);
            fout.write(jsonStr);
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareParams(IntrospectedTable table, int counter, Method getMethod) {
        for (IntrospectedColumn introspectedColumn : table.getPrimaryKeyColumns()) {
            Parameter param3 = new Parameter(introspectedColumn.getFullyQualifiedJavaType(),"record"+counter);
            param3.addAnnotation("@PathParam(\"id"+counter+"\")");
            getMethod.addParameter(param3);
            counter++;
        }
    }

    private void createMybatisWingWebJavaFile(IntrospectedTable table) {
        String target_base_package = table.getContext().getProperty("TARGET_BASE_PACKAGE");
        target_base_package = target_base_package + ".service";

        TopLevelClass compilationUnit = new TopLevelClass(target_base_package+"."+"WingwebMybatisUtil");
        compilationUnit.addImportedType("java.io.IOException");
        compilationUnit.addImportedType("java.io.Reader");
        compilationUnit.addImportedType("org.apache.ibatis.io.Resources");
        compilationUnit.addImportedType("org.apache.ibatis.session.SqlSessionFactory");
        compilationUnit.addImportedType("org.apache.ibatis.session.SqlSessionFactoryBuilder");
        Field field = new Field();
        field.setName("factory");
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PRIVATE);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType("org.apache.ibatis.session.SqlSessionFactory");
        field.setType(type);
        compilationUnit.addField(field);
        InitializationBlock initBlock = new InitializationBlock(true);
        initBlock.addBodyLine("Reader reader = null;");
        initBlock.addBodyLine("try {");
        initBlock.addBodyLine("reader = Resources.getResourceAsReader(\"mybatis-config.xml\");");
        initBlock.addBodyLine("} catch (IOException e) {");
        initBlock.addBodyLine("throw new RuntimeException(e.getMessage());");
        initBlock.addBodyLine("}");
        initBlock.addBodyLine("factory = new SqlSessionFactoryBuilder().build(reader);");
        compilationUnit.addInitializationBlock(initBlock);
        Method factoryGetter = new Method();
        factoryGetter.setVisibility(JavaVisibility.PUBLIC);
        factoryGetter.setStatic(true);
        factoryGetter.setName("getSqlSessionFactory");
        factoryGetter.setReturnType(new FullyQualifiedJavaType("org.apache.ibatis.session.SqlSessionFactory"));
        factoryGetter.addBodyLine("return factory;");
        compilationUnit.addMethod(factoryGetter);
        String targetProject = table.getContext().getProperty("TARGET_PROJECT_JAVA");
        GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,targetProject,new DefaultJavaFormatter());
        mybatisWingWebJavaFile = gjf;
    }

    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable table){
        List<GeneratedXmlFile> xmlFiles = new ArrayList<GeneratedXmlFile>();
        if (doc == null) {
            doc = new Document(XmlConstants.MYBATIS3_MAPPER_CONFIG_PUBLIC_ID,XmlConstants.MYBATIS3_MAPPER_CONFIG_SYSTEM_ID);
            XmlElement configuration = new XmlElement("configuration");
            XmlElement properties = new XmlElement("properties");
            properties.addAttribute(new Attribute("resource", "db.properties"));
            configuration.addElement(properties);
            XmlElement environments = new XmlElement("environments");
            environments.addAttribute(new Attribute("default", "development"));
            XmlElement environment = new XmlElement("environment");
            environment.addAttribute(new Attribute("id", "development"));
            XmlElement transactionManager = new XmlElement("transactionManager");
            transactionManager.addAttribute(new Attribute("type", "JDBC"));
            environment.addElement(transactionManager);
            XmlElement dataSource = new XmlElement("dataSource");
            dataSource.addAttribute(new Attribute("type", "POOLED"));

            XmlElement property1 = new XmlElement("property");
            property1.addAttribute(new Attribute("name", "driver"));
            property1.addAttribute(new Attribute("value", "${jdbc.driverClassName}"));
            dataSource.addElement(property1);

            XmlElement property2 = new XmlElement("property");
            property2.addAttribute(new Attribute("name", "url"));
            property2.addAttribute(new Attribute("value", "${jdbc.url}"));
            dataSource.addElement(property2);

            XmlElement property3 = new XmlElement("property");
            property3.addAttribute(new Attribute("name", "username"));
            property3.addAttribute(new Attribute("value", "${jdbc.username}"));
            dataSource.addElement(property3);

            XmlElement property4 = new XmlElement("property");
            property4.addAttribute(new Attribute("name", "password"));
            property4.addAttribute(new Attribute("value", "${jdbc.password}"));
            dataSource.addElement(property4);

            environment.addElement(dataSource);

            environments.addElement(environment);
            configuration.addElement(environments);
            XmlElement mappers = new XmlElement("mappers");
            XmlElement mapper = new XmlElement("mapper");
            String mapperPackage = table.getContext().getProperty("TARGET_CONFIG_PACKAGE_MAPPER");
            String mp = mapperPackage.replaceAll("\\.", "/");
            mapper.addAttribute(new Attribute("resource", mp + "/" + table.getMyBatis3XmlMapperFileName()));
            mappers.addElement(mapper);
            configuration.addElement(mappers);
            doc.setRootElement(configuration);
        } else {
            XmlElement element = (XmlElement) doc.getRootElement().getElements().get(doc.getRootElement().getElements().size() - 1);
            XmlElement mapper = new XmlElement("mapper");
            String mapperPackage = table.getContext().getProperty("TARGET_CONFIG_PACKAGE_MAPPER");
            String mp = mapperPackage.replaceAll("\\.", "/");
            mapper.addAttribute(new Attribute("resource", mp + "/" + table.getMyBatis3XmlMapperFileName()));
            element.addElement(mapper);
        }
        String targetPackage = table.getContext().getProperty("TARGET_CONFIG_PACKAGE");
        String targetProject = table.getContext().getProperty("TARGET_PROJECT_RESOURCES");
        boolean isMergeable = false;
        GeneratedXmlFile xmlFile = new GeneratedXmlFile(doc,"mybatis-config.xml",targetPackage,
                targetProject,isMergeable, new DefaultXmlFormatter());
        xmlFiles.add(xmlFile);
        return xmlFiles;
    }
}
