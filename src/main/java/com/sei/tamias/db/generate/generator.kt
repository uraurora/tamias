package com.sei.tamias.db.generate

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.generator.AutoGenerator
import com.baomidou.mybatisplus.generator.InjectionConfig
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.config.po.TableInfo
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine
import java.util.*


// 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
object CodeGenerator {
    /**
     *
     *
     * 读取控制台内容
     *
     */
    fun scanner(tip: String): String {
        val scanner = Scanner(System.`in`)
        val help = StringBuilder()
        help.append("请输入$tip：")
        println(help.toString())
        if (scanner.hasNext()) {
            val ipt: String = scanner.next()
            if (ipt.isNotBlank()) {
                return ipt
            }
        }
        throw MybatisPlusException("请输入正确的$tip！")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 代码生成器
        val mpg = AutoGenerator()

        // 全局配置
        val gc = GlobalConfig()
        val projectPath = System.getProperty("user.dir")
        gc.setOutputDir("$projectPath/src/main/java")
        gc.setAuthor("sei")
        gc.setOpen(false)
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        mpg.globalConfig = gc

        // 数据源配置
        val dsc = DataSourceConfig()
        dsc.url = "jdbc:sqlite:src/main/resources/db/test.db"
        // dsc.setSchemaName("public");
        dsc.driverName = "org.sqlite.JDBC"
        dsc.username = null
        dsc.password = null
        mpg.dataSource = dsc

        // 包配置
        val pc = PackageConfig()
        pc.moduleName = "db"
        pc.parent = "com.sei.tamias"
        mpg.packageInfo = pc

        // 自定义配置
        val cfg: InjectionConfig = object : InjectionConfig() {
            override fun initMap() {
                // to do nothing
            }
        }

        // 如果模板引擎是 freemarker
        val templatePath = "/templates/mapper.xml.ftl"
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        val focList: MutableList<FileOutConfig> = ArrayList()
        // 自定义配置会被优先输出
        focList.add(object : FileOutConfig(templatePath) {
            override fun outputFile(tableInfo: TableInfo): String {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return (projectPath + "/src/main/resources/mapper/" + pc.moduleName
                        + "/" + tableInfo.entityName + "Mapper" + StringPool.DOT_XML)
            }
        })
        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */cfg.fileOutConfigList = focList
        mpg.cfg = cfg

        // 配置模板
        val templateConfig = TemplateConfig()

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();
        templateConfig.xml = null
        mpg.template = templateConfig

        // 策略配置
        val strategy = StrategyConfig()
        strategy.naming = NamingStrategy.underline_to_camel
        strategy.columnNaming = NamingStrategy.underline_to_camel
        // strategy.superEntityClass = "你自己的父类实体,没有就不用设置!"
        strategy.isEntityLombokModel = true
        strategy.isRestControllerStyle = true
        // 公共父类
        // strategy.superControllerClass = "你自己的父类控制器,没有就不用设置!"
        // 写于父类中的公共字段
        strategy.setSuperEntityColumns("id")
        strategy.setInclude(*scanner("表名，多个英文逗号分割").split(",".toRegex()).toTypedArray())
        strategy.isControllerMappingHyphenStyle = true
        strategy.setTablePrefix(pc.moduleName + "_")
        mpg.strategy = strategy
        mpg.templateEngine = FreemarkerTemplateEngine()
        mpg.execute()
    }
}
