package com.lanxing.pay.data;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.List;

/**
 * MybatisPlus代码生成器
 *
 * @author chenlanxing
 */
public class MybatisPlusCodeGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/easy_pay?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";
        String outputDir = "data/src/main/java";
        String author = "chenlanxing";
        String module = "com.lanxing.pay.data";
        List<String> tables = List.of("wechat_user");
        generateCode(url, username, password, outputDir, author, module, tables);
    }

    public static void generateCode(String url,
                                    String username,
                                    String password,
                                    String outputDir,
                                    String author,
                                    String module,
                                    List<String> tables) {
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .disableOpenDir()
                        .outputDir(outputDir)
                        .author(author))
                .packageConfig(builder -> builder
                        .parent(module)
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl"))
                .templateConfig(builder -> builder
                        .entity("/templates/entity.java")
                        .mapper("/templates/mapper.java")
                        .service("/templates/service.java")
                        .serviceImpl("/templates/serviceImpl.java")
                        .disable(TemplateType.CONTROLLER, TemplateType.XML))
                .strategyConfig(builder -> builder
                        .enableSkipView()
                        .addInclude(tables)
                        .entityBuilder()
                        .fileOverride()
                        .disableSerialVersionUID()
                        .enableColumnConstant()
                        .enableChainModel()
                        .enableLombok()
                        .versionColumnName("version")
                        .logicDeleteColumnName("deleted")
                        .addTableFills(new Column("create_time", FieldFill.INSERT), new Column("update_time", FieldFill.INSERT_UPDATE))
                        .idType(IdType.AUTO)
                        .formatFileName("%sEntity")
                        .mapperBuilder()
                        .fileOverride()
                        .superClass(BaseMapper.class)
                        .enableMapperAnnotation()
                        .formatMapperFileName("%sMapper")
                        .serviceBuilder()
                        .fileOverride()
                        .superServiceClass(IService.class)
                        .superServiceImplClass(ServiceImpl.class)
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
