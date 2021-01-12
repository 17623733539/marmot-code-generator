/*
 * Copyright (c)  2019,  Marmot
 * All rights reserved.
 *
 * Id:CodeConfig.java   2019-05-03 11:47 PM wangqiming
 */
package generator;

import jodd.datetime.JDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Title:
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
 * @since 2019-05-03 11:47 PM
 */
@Slf4j
@Setter
@Getter
public class CodeConfig {

    public static final String CODE_GENERATOR = "marmot-code-generator";

    /***
     * CODE文件名
     */
    public static final String CODE_PROP = "code.properties";
    // public static final String CODE_PROP="codeOPS.properties";
    /***
     * 模板ClassPath 加载路径
     */
    public static final String CLASSPATH_TEMPLATE_PATH = "/template";
    /***
     * Mapper 模板名称
     */
    public static final String MAPPER = "Mapper";
    /***
     * 前缀
     */
    public static final String MAPPER_MD_PREFIX = "/sql/mapper/";
    /***
     * Service 模板名称
     */
    public static final String SERVICE = "Service";
    /***
     * Service_Impl 模板名称
     */
    public static final String SERVICE_IMPL = "ServiceImpl";

    /***
     * Mapper 模板名称
     */
//    public static final String REPOSITORY_IMPL = "Mapper.md";
    /**
     * Controller模板名称
     */
    public static final String CONTROLLER_BTL = "Controller";
    /**
     * DeleteReqVo 模板名称
     */
    public static final String DELETE_REQ_VO = "DeleteReqVo";
    /**
     * HandleVo 模板名称
     */
    public static final String CREATE_REQ_VO = "CreateReqVo";
    /**
     * PageReVo 模板名称
     */
    public static final String PAGE_REQ_VO = "PageReqVo";
    /**
     * PageRespVo 模板名称
     */
    public static final String PAGE_RESP_VO = "PageRespVo";
    /**
     * QueryRespVo 模板名称
     */
    public static final String QUERY_RESP_VO = "QueryRespVo";
    /**
     * UpdateReqVo 模板名称
     */
    public static final String UPDATE_REP_VO = "UpdateReqVo";
    /***
     *  默认有权限配置
     */
    private static final String controllerPrefix = "auth";
    private static final String SEPARATOR = "/";
    /***
     * 作者
     */
    private String user = System.getenv("USER");

    /***
     *  工程路径
     */
    private String projectPath = StringUtils.EMPTY;


    private Map<String, Object> shared = new HashMap<>();

    public CodeConfig() {
        this.init();
    }

    private void init() {
        this.projectPath = System.getProperty("user.dir") + SEPARATOR;
        this.projectPath = this.projectPath.replace(CODE_GENERATOR + SEPARATOR, StringUtils.EMPTY);
        log.info("projectPath:{}", projectPath);
        this.shared.put("USER", this.user);
        JDateTime dateTime = new JDateTime();
        this.shared.put("YEAR", dateTime.toString("YYYY"));
        this.shared.put("MONTH", dateTime.toString("MM"));
        this.shared.put("DAY", dateTime.toString("DD"));
        this.shared.put("TIME", dateTime.toString("hh:mm:ss"));

    }
}
