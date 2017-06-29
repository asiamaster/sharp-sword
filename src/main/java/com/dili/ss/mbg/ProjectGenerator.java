package com.dili.ss.mbg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器java实现
 * Created by asiam on 2017/4/7 0007.
 */
public class ProjectGenerator {

    /**
     * 根据配置文件生成<br/>
     * 可以用于生成项目基本目录和文件
     * @param generatorConfigFilePath generatorConfig.xml配置文件绝对路径，例:<br/>
     *         E:/workspace/dili-workspace/ms-workspace/sharp-sword/src/main/resources/generator/generatorConfig.xml
     * @throws Exception
     */
    public static void generate(String generatorConfigFilePath) throws Exception{
        File configFile = new File(generatorConfigFilePath);
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }


}
