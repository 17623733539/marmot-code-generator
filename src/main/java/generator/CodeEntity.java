/*
 * Copyright (c)  2019,  Marmot
 * All rights reserved.
 *
 * Id:CodeEntity.java   2019-05-03 11:32 PM wangqiming
 */
package generator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * Title: 代码生成器Bean
 * </p>
 * <p>
 * Description:  代码生成器Bean
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
 * @since 2019-05-03 11:32 PM
 */
@Setter
@Getter
@ToString
public class CodeEntity {
    /***
     * 实体名称
     */
    private String entityName;
    /***
     * 实体类
     */
    private Class<?> entity;
    /***
     * 实体类
     */
    private String entityPackage;


    /***
     * 实体模块抽像类
     */
    private String abstractEntityModule;

    /***
     * 实体模块
     */
    private String entityModule;

    /***
     * repository 包名
     */
    private String repositoryPackage;

    /***
     * repositoryImpl包名
     */
    private String repositoryImplPackage;


    /***
     * repository实体模块
     */
    private String repositoryModule;


    /***
     *service 包名
     */
    private String servicePackage;
    /***
     * serviceImpl 包名
     */
    private String serviceImplPackage;

    /***
     * service实体模块
     */
    private String serviceModule;

    /***
     * Controller 包名
     */
    private String controllerPackage;

    /***
     * Controller Vo包名
     */
    private String controllerPackageVo;

    /***
     * controller实体模块
     */
    private String controllerModule;

    /***
     * 作者
     */
    private String author;


    /***
     * ClassName
     */
    private String className;


    /***
     * class
     */
    private String classNameLowerCase;

    /***
     *表名
     */
    private String tableName;

    /***
     * 描述
     */
    private String description;

    /***
     * 主键类型
     */
    private String primaryKeyType;

    private CodeConfig codeConfig;
}
