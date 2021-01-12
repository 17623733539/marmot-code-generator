/*
 * Copyright (c)  2019,  Marmot
 * All rights reserved.
 *
 * Id:ObjectField.java   2019-05-04 3:32 AM wangqiming
 */
package generator;

/**
 * <p>
 * Title: 对象字段定义
 * </p>
 * <p>
 * Description: 对象字段定义
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
 * @since 2019-05-04 3:32 AM
 */
public class ObjectField {

    private String name;

    private String upperName;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpperName() {
        return upperName;
    }

    public void setUpperName(String upperName) {
        this.upperName = upperName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.startsWith("class java.lang")) {
            type = type.substring(type.indexOf("java.lang.") + 10);
        }

        if (type.startsWith("class")) {
            type = type.substring(type.indexOf("class") + 5);
        }
        this.type = type;
    }
}

