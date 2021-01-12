/*
 * Copyright (c)  2019,  Marmot
 * All rights reserved.
 *
 * Id:CodeBuilderTest.java   2019-05-04 12:10 AM wangqiming
 */

import generator.CodeBuilder;
import generator.entity.user.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

//import org.junit.Test;

/**
 * Title:
 *
 * <p>Description:
 *
 * <p>Copyright: Copyright (c) 2019
 *
 * <p>Company/Department: Marmot
 *
 * @author wangqiming
 * @version 1.0
 * @since 2019-05-04 12:10 AM
 */
@Slf4j
public class CodeBuilderTest {

    @Test
    public void generator() {
        new CodeBuilder("/Users/wangqiming/Desktop/marmot-code-generator/src/main/resources/code.properties").generator(UserEntity.class);
    }
}