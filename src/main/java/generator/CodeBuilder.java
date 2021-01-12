/*
 * Copyright (c)  2019,  Marmot
 * All rights reserved.
 *
 * Id:CodeBuilder.java   2019-05-03 11:53 PM wangqiming
 */
package generator;

import com.google.common.base.Charsets;;
import io.common.commom.constant.Separator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.sql.core.annotatoin.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Title: 代码构建器
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2019
 * </p>
 * <p>
 * Company/Department: Marmot
 * </p>
 *
 * @author wangqiming
 * @version 1.0
 * @since 2019-05-03 11:53 PM
 */
@Slf4j
public class CodeBuilder {

    private String filePath;

    public CodeBuilder(String path) {
        this.filePath = path;
    }

    public void generator(Class entityClass) {

        String className = entityClass.getName();

        log.info("className:{}", className);

        String entityName = className;
        String entityPackage = entityName.substring(0, entityName.lastIndexOf(Separator.DOT));
        CodeBuilder codeBuilder = new CodeBuilder(filePath);
        try {
            codeBuilder.codeEntity(entityClass, entityName, entityPackage);
            codeBuilder.builder();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 去删除*内的内容
     */
    public static final Pattern PATTERN = Pattern.compile("\\{([^}]*)\\}");
    /***
     * 代码配置
     */
    CodeConfig codeConfig = new CodeConfig();
    /***
     * 实体类
     */
    CodeEntity codeEntity;


    /***
     * 获取模块加载
     * @return
     */
    public GroupTemplate getTemplateLoader() {
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(CodeConfig.CLASSPATH_TEMPLATE_PATH);
        Configuration cfg = null;
        try {
            cfg = Configuration.defaultConfiguration();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        GroupTemplate groupTemplate = new GroupTemplate(resourceLoader, cfg);
        groupTemplate.setSharedVars(codeConfig.getShared());
        return groupTemplate;
    }

    /***
     * 加载配置文件
     * @return
     * @throws IOException
     */
    public Properties loadProperties() throws IOException {
        File file = new File(filePath);
//        InputStream in = CodeBuilder.class.getResourceAsStream("/Users/wangqiming/Desktop/marmot-code-generator/src/main/resources/code.properties");
        InputStream in = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(in);
        return properties;
    }

    public CodeEntity codeEntity(Class<?> clazz, String entityName, String entityPackage) throws IOException, URISyntaxException, ClassNotFoundException {
        codeEntity = new CodeEntity();
        codeEntity.setEntity(clazz);
        codeEntity.setClassName(entityName);
        codeEntity.setEntityName(this.codeEntity.getClassName().substring(this.codeEntity.getClassName().lastIndexOf(Separator.DOT) + 1));
        codeEntity.setControllerModule((String) loadProperties().get("module.controller"));
        codeEntity.setEntityModule((String) loadProperties().get("module.entity"));
        codeEntity.setServiceModule((String) loadProperties().get("module.service"));
        codeEntity.setRepositoryModule((String) loadProperties().get("module.repository"));
        String entityApi = (String) loadProperties().get("module.entity.api");
        codeEntity.setAbstractEntityModule(entityApi);
        String entityFilePath = codeConfig.getProjectPath() + codeEntity.getEntityModule() + Separator.BACKSLASH + "target" + Separator.BACKSLASH + "classes" + Separator.BACKSLASH;
        String entityAbstractPath = codeConfig.getProjectPath() + entityApi + Separator.BACKSLASH + "target" + Separator.BACKSLASH + "classes" + Separator.BACKSLASH;


        log.info("entityFilePath:{}", entityFilePath);
        URL[] urls = {new URL("file:" + entityFilePath), new URL("file:" + entityAbstractPath)};
        System.out.println(urls[0].toURI().toString());
        URLClassLoader cl = new URLClassLoader(urls, this.getClass().getClassLoader());
        Class<?> clz = Class.forName(entityName, false, cl);
        codeEntity.setEntity(clz);
        codeEntity.setCodeConfig(codeConfig);

        //设置包名
        codeEntity.setControllerPackage((String) loadProperties().get("controller.package"));
        codeEntity.setControllerPackageVo(codeEntity.getControllerPackage() + ".vo");
        codeEntity.setServicePackage((String) loadProperties().get("service.package"));
        codeEntity.setServiceImplPackage(codeEntity.getServicePackage() + ".impl");
        codeEntity.setRepositoryPackage((String) loadProperties().get("repository.package"));
        codeEntity.setRepositoryImplPackage(codeEntity.getRepositoryPackage() + ".impl");
        codeEntity.setEntityPackage(entityPackage);
        return codeEntity;
    }


    public void builderRepository() throws IOException {
        Map<String, String> params = new HashMap<>();
        String entityPackagePrefix = this.codeEntity.getEntityPackage().substring(this.codeEntity.getEntityPackage().lastIndexOf(Separator.DOT) + 1);
        this.codeEntity.setRepositoryPackage(this.codeEntity.getRepositoryPackage() + Separator.DOT + entityPackagePrefix);
        String className = this.codeEntity.getEntityName().replace("Entity", StringUtils.EMPTY);
        String modulePrefix = this.loadProperties().getProperty("controller.prefix");
        params.put("NAME", this.codeEntity.getEntityName());
        params.put("className", className);
        params.put("tableName", this.getTableName(this.codeEntity.getEntity()));
        params.put("entityPackagePrefix", entityPackagePrefix);
        params.put("entityShortName", this.codeEntity.getEntityName().replace("Entity", StringUtils.EMPTY).toLowerCase());
        params.put("mapperPackage", this.codeEntity.getRepositoryPackage());
        params.put("mapperImplPackage", this.codeEntity.getRepositoryImplPackage());
        params.put("entityPackage", this.codeEntity.getEntityPackage());
        params.put("modulePrefix", this.loadProperties().getProperty("controller.prefix"));
        String repositoryPath = this.codeEntity.getRepositoryPackage().replace(Separator.DOT, Separator.BACKSLASH);

        String repositoryImplPath = this.codeEntity.getRepositoryImplPackage().replace(Separator.DOT, Separator.BACKSLASH);
        //MD 文件生成
        String repositoryFileFullPath = this.createPath(this.codeEntity.getRepositoryModule(), repositoryPath, className + "Mapper");

        String repositoryImplFileFullPath = this.createPathMd(this.codeEntity.getRepositoryModule(), (CodeConfig.MAPPER_MD_PREFIX + Separator.BACKSLASH + modulePrefix + Separator.BACKSLASH + className).toLowerCase(), className.toLowerCase());

        log.info("mapperImplPackage:{}", repositoryFileFullPath);
        log.info("repositoryImplFileFullPath:{}", repositoryImplFileFullPath);


        String repositoryContent = this.getTemplate(CodeConfig.MAPPER, params).render();

//        String repositoryImplContent = this.getTemplate(CodeConfig.REPOSITORY_IMPL, params).render();

        this.writeFile(repositoryFileFullPath, repositoryContent);

//        this.writeFile(repositoryImplFileFullPath, repositoryImplContent);

    }

    /***
     * 获取数据
     * @param className
     * @return
     */
    private String getTableName(Class<?> className) {
        String tableName = StringUtils.EMPTY;
        log.info("className:{}", className.getClassLoader());
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass(className.getName());
            Table object = clazz.getAnnotation(Table.class);
            if (object != null) {
                tableName = object.name();
                log.info("tableName:{}", tableName);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tableName;
    }

    public void builderService() {

        Map<String, String> params = new HashMap<>(16);
        String entityPackagePrefix = this.codeEntity.getEntityPackage().substring(this.codeEntity.getEntityPackage().lastIndexOf(Separator.DOT) + 1);
        String className = this.codeEntity.getEntityName().replace("Entity", StringUtils.EMPTY);
        this.codeEntity.setServicePackage(this.codeEntity.getServicePackage() + Separator.DOT + entityPackagePrefix);
        this.codeEntity.setServiceImplPackage(this.codeEntity.getServicePackage() + ".impl");
        params.put("NAME", this.codeEntity.getEntityName());
        params.put("entityPackagePrefix", entityPackagePrefix);
        params.put("className", className);
        params.put("mapperPackage", this.codeEntity.getRepositoryPackage());
        params.put("servicePackage", this.codeEntity.getServicePackage());
        params.put("serviceImplPackage", this.codeEntity.getServiceImplPackage());
        params.put("entityPackage", this.codeEntity.getEntityPackage());

        String servicePath = this.codeEntity.getServicePackage().replace(Separator.DOT, Separator.BACKSLASH);

        String serviceImplPath = this.codeEntity.getServiceImplPackage().replace(Separator.DOT, Separator.BACKSLASH);

        String serviceFileFullPath = this.createPath(this.codeEntity.getServiceModule(), servicePath, className + "Service");

        String serviceImplFileFullPath = this.createPath(this.codeEntity.getServiceModule(), serviceImplPath, className + "ServiceImpl");

        log.info("ServiceFileFullPath:{}", serviceFileFullPath);

        log.info("ServiceImplFileFullPath:{}", serviceImplFileFullPath);

        String serviceContent = this.getTemplate(CodeConfig.SERVICE, params).render();

        String serviceImplContent = this.getTemplate(CodeConfig.SERVICE_IMPL, params).render();

        this.writeFile(serviceFileFullPath, serviceContent);

        this.writeFile(serviceImplFileFullPath, serviceImplContent);
    }


    public void builderController() throws IOException {
        Map<String, String> params = new HashMap<>(16);
        String entityPackagePrefix = this.codeEntity.getEntityPackage().substring(this.codeEntity.getEntityPackage().lastIndexOf(Separator.DOT) + 1);
        String className = this.codeEntity.getEntityName().replace("Entity", StringUtils.EMPTY);
        this.codeEntity.setControllerPackage(this.codeEntity.getControllerPackage() + Separator.DOT + entityPackagePrefix);
        params.put("NAME", this.codeEntity.getEntityName());
        params.put("entityPackagePrefix", entityPackagePrefix);
        params.put("className", className);
        params.put("controllerPathPrefix", StringUtils.lowerCase(className));
        params.put("servicePackage", this.codeEntity.getServicePackage());
        params.put("entityPackage", this.codeEntity.getEntityPackage());
        params.put("controllerPackage", this.codeEntity.getControllerPackage());
        params.put("controllerPrefix", this.loadProperties().getProperty("controller.prefix"));
        String controllerPackagePath = this.codeEntity.getControllerPackage().replace(Separator.DOT, Separator.BACKSLASH);
        String controllerFileFullPath = this.createPath(this.codeEntity.getControllerModule(), controllerPackagePath, className + "Controller");
        log.info("controllerFileFullPath:{}", controllerFileFullPath);
        String serviceContent = this.getTemplate(CodeConfig.CONTROLLER_BTL, params).render();
        this.writeFile(controllerFileFullPath, serviceContent);

    }

    public void builderControllerVo() {

        Map<String, String> params = new HashMap<>(16);
        String entityPackagePrefix = this.codeEntity.getEntityPackage().substring(this.codeEntity.getEntityPackage().lastIndexOf(Separator.DOT) + 1);
        String className = this.codeEntity.getEntityName().replace("Entity", StringUtils.EMPTY);
        this.codeEntity.setControllerPackageVo(this.codeEntity.getControllerPackage() + ".vo");
        params.put("NAME", this.codeEntity.getEntityName());
        params.put("controllerPackageVo", this.codeEntity.getControllerPackageVo());
        params.put("voContent", this.readEntityJava());
        params.put("className", className);
        String voPath = this.codeEntity.getControllerPackageVo().replace(Separator.DOT, Separator.BACKSLASH);

        Map<String, String> vos = new HashMap<>(16);
        vos.put("DeleteReqVo", CodeConfig.DELETE_REQ_VO);
        vos.put("CreateReqVo", CodeConfig.CREATE_REQ_VO);
        vos.put("PageReqVo", CodeConfig.PAGE_REQ_VO);
        vos.put("PageRespVo", CodeConfig.PAGE_RESP_VO);
        vos.put("QueryRespVo", CodeConfig.QUERY_RESP_VO);
        vos.put("UpdateReqVo", CodeConfig.UPDATE_REP_VO);
        vos.forEach((k, v) -> {
            String voFileFullPath = this.createPath(this.codeEntity.getControllerModule(), voPath, className + k);
            log.info("voFileFullPath:{},key:{}", voFileFullPath, k);
            String voContent = this.getTemplate(v, params).render();
            this.writeFile(voFileFullPath, voContent);
        });

    }

    public String readEntityJava() {
        String entityPath = this.codeEntity.getEntityPackage().replace(Separator.DOT, Separator.BACKSLASH);
        String entityJavaFile = this.createPath(this.codeEntity.getAbstractEntityModule(), entityPath, this.codeEntity.getEntityName());
        log.info("entityJavaFile:{}", entityJavaFile);

        String content = null;
        try {
            content = FileUtils.readFileToString(new File(entityJavaFile), Charsets.UTF_8.name());
            Matcher matcher = PATTERN.matcher(content);
            StringBuilder builder = new StringBuilder();
            while (matcher.find()) {
                builder.append(matcher.group());
            }
            //content=builder.toString().replaceAll("\n" +
            //        "    @Column\\([^)]*\\)", "@ApiModelProperty(value =\"TODO\")").replace("{\n", StringUtils.EMPTY).replace("}", StringUtils.EMPTY);

            content = builder.toString().replaceAll(" @\\([^)]*\\)", "@ApiModelProperty(value =\"TODO\")").replace("{\n", StringUtils.EMPTY).replace("}", StringUtils.EMPTY);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        content = content.replace("\n" +
                "    @CreatedBy", StringUtils.EMPTY).replace("\n" +
                "    @UpdateIgnore", StringUtils.EMPTY).replace("\n" +
                "    @CreatedDate", StringUtils.EMPTY).replace("\n" +
                "    @LastModifiedBy", StringUtils.EMPTY).replace("\n" +
                "\n" +
                "    @LastModifiedDate", StringUtils.EMPTY).replace("\n" +
                "    @Version", StringUtils.EMPTY).replace("\n" +
                "    @LogicDelete", StringUtils.EMPTY).replace("\n" +
                "    @ColumnIgnore(insert = false,update = false)", StringUtils.EMPTY)
                .replace("\n" +
                        "    @ColumnIgnore(insert = false,update = true)", StringUtils.EMPTY)
                .replace("\n" +
                        "    @LastModifiedDate", StringUtils.EMPTY);
        log.info("content:{}", content);
        return content;
    }

    /***
     * 自动生成代码
     */
    public void builder() throws IOException {
        this.builderRepository();
        this.builderService();
        this.builderController();
        this.builderControllerVo();
    }


    private List<ObjectField> getObjectValue(Class<?> clz) throws Exception {
        clz.getPackage().getName();
        Field[] fields = clz.getDeclaredFields();
        List<ObjectField> list = new ArrayList<>();
        for (Field field : fields) {
            //static类型的字段不处理
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Class type = field.getType();
            ObjectField field1 = new ObjectField();
            field1.setName(field.getName());
            String typeName = field.getType().toString();
            //只对简单类型的字段做处理
            if (!typeName.contains("java")) {
                continue;
            }
            field1.setType(field.getType().toString());
            field1.setUpperName(getMethodName(field.getName()));
            log.info(field.getName() + ":" + type.toString());
            list.add(field1);
        }
        return list;
    }

    /**
     * 把一个字符串的第一个字母大写、效率是最高的、
     *
     * @param fieldName 字段名称
     * @return 第一个字母大写后的字符串
     */
    private String getMethodName(String fieldName) {
        byte[] items = fieldName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }


    private String createPathMd(String moduleName, String filePath, String fileName) {
        return this.codeConfig.getProjectPath() + moduleName + Separator.BACKSLASH + "src" + Separator.BACKSLASH + "main" + Separator.BACKSLASH + "resources" + Separator.BACKSLASH + filePath + Separator.BACKSLASH + fileName + ".md";
    }

    private String createPath(String moduleName, String filePath, String fileName) {
        return this.codeConfig.getProjectPath() + moduleName + Separator.BACKSLASH + "src" + Separator.BACKSLASH + "main" + Separator.BACKSLASH + "java" + Separator.BACKSLASH + filePath + Separator.BACKSLASH + fileName + ".java";
    }

    /***
     * 写文件
     * @param fileName
     * @param fileContent
     */
    private void writeFile(String fileName, String fileContent) {
        try {
            File file = new File(fileName);
            if (file.exists()) {
                throw new RuntimeException("文件已经存在");
            }
            FileUtils.write(file, fileContent, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取模板
     *
     * @param templateName
     * @return Template
     */
    public Template getTemplate(String templateName) {
        return this.getTemplateLoader().getTemplate(Separator.BACKSLASH + templateName);
    }

    /**
     * 根据参数&获取模板
     *
     * @param templateName
     * @param params
     * @return Template
     */
    public Template getTemplate(String templateName, Map<String, String> params) {
        Template template = this.getTemplateLoader().getTemplate(Separator.BACKSLASH + templateName);
        params.forEach((k, v) -> {
            template.binding(k, v);
        });
        return template;
    }

}
