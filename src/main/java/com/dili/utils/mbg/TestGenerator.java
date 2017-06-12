package com.dili.utils.mbg;

import org.junit.Before;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by asiam on 2017/4/7 0007.
 */
public class TestGenerator {
    private File configFile;

    @Before
    public void before() {
        //读取mybatis参数
        configFile = new File("E:/workspace/dili-workspace/ms-workspace/sharp-sword/src/main/resources/generator/generatorConfig1.xml");
    }

    @Test
    public void generate() throws Exception{
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}
