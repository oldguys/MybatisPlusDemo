#####SpringBoot+Mybatis-plus 配置多数据源

>之前则介绍了怎样从SpringBoot源码中，摘出配置MyBatis的方法。这次，则开始摘出Jpa的配置方法。
>GitHub [https://github.com/oldguys/MybatisPlusDemo](https://github.com/oldguys/MybatisPlusDemo)

**目录结构**

![02.jpg](https://upload-images.jianshu.io/upload_images/14387783-13d749dcc3534cac.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


Step1： 剔除自动装配

```
package com.example.oldguy;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(
        exclude = {
                DataSourceAutoConfiguration.class,
                MybatisPlusAutoConfiguration.class,
        }
)
public class MybatisPlusDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusDemoApplication.class, args);
    }

}

```


Step2： 从框架中抽出源码 com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration

![01.jpg](https://upload-images.jianshu.io/upload_images/14387783-2b038f61096aa608.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

根据框架源码编写抽象类
com.example.oldguy.configs.AbstractMybatisPlusConfiguration
```
package com.example.oldguy.configs;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @ClassName: AbstractMybatisPlusConfiguration
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 上午 11:02
 **/
public class AbstractMybatisPlusConfiguration {

    protected SqlSessionFactory getSqlSessionFactory(
            DataSource dataSource,
            MybatisPlusProperties properties,
            ResourceLoader resourceLoader,
            Interceptor[] interceptors,
            DatabaseIdProvider databaseIdProvider,
            ApplicationContext applicationContext
    ) throws Exception {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(properties.getConfigLocation())) {
            factory.setConfigLocation(resourceLoader.getResource(properties.getConfigLocation()));
        }
        applyConfiguration(factory, properties);
        if (properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(interceptors)) {
            factory.setPlugins(interceptors);
        }
        if (databaseIdProvider != null) {
            factory.setDatabaseIdProvider(databaseIdProvider);
        }
        if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        // TODO 自定义枚举包
        if (StringUtils.hasLength(properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(properties.getTypeEnumsPackage());
        }
        if (properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
            factory.setMapperLocations(properties.resolveMapperLocations());
        }
        // TODO 此处必为非 NULL
        GlobalConfig globalConfig = properties.getGlobalConfig();
        //注入填充器
        if (applicationContext.getBeanNamesForType(MetaObjectHandler.class,
                false, false).length > 0) {
            MetaObjectHandler metaObjectHandler = applicationContext.getBean(MetaObjectHandler.class);
            globalConfig.setMetaObjectHandler(metaObjectHandler);
        }
        //注入主键生成器
        if (applicationContext.getBeanNamesForType(IKeyGenerator.class, false,
                false).length > 0) {
            IKeyGenerator keyGenerator = applicationContext.getBean(IKeyGenerator.class);
            globalConfig.getDbConfig().setKeyGenerator(keyGenerator);
        }
        //注入sql注入器
        if (applicationContext.getBeanNamesForType(ISqlInjector.class, false,
                false).length > 0) {
            ISqlInjector iSqlInjector = applicationContext.getBean(ISqlInjector.class);
            globalConfig.setSqlInjector(iSqlInjector);
        }
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }

    private void applyConfiguration(MybatisSqlSessionFactoryBean factory, MybatisPlusProperties properties) {
        MybatisConfiguration configuration = properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(properties.getConfigLocation())) {
            configuration = new MybatisConfiguration();
        }
//        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
//            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
//                customizer.customize(configuration);
//            }
//        }
        factory.setConfiguration(configuration);
    }


    public SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory, MybatisPlusProperties properties) {
        ExecutorType executorType = properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

}

```
Step3：根据抽象类写实现类

数据库1：com.example.oldguy.configs.Demo1Configuration

```
package com.example.oldguy.configs;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * @ClassName: Demo1Configuration
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 上午 11:03
 **/
@MapperScan(basePackages = "com.example.oldguy.modules.model1.dao.jpas",
        sqlSessionTemplateRef = "demo1SqlSessionTemplate",
        sqlSessionFactoryRef = "demo1SqlSessionFactory")
@Configuration
public class Demo1Configuration extends AbstractMybatisPlusConfiguration {

    @Bean(name = "demo1DruidDataSource")
    @ConfigurationProperties(prefix = "demo1.datasource")
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "demo1MybatisPlusProperties")
    @ConfigurationProperties(prefix = "mybatis-plus.demo1")
    public MybatisPlusProperties mybatisPlusProperties() {
        return new MybatisPlusProperties();
    }


    @Bean(name = "demo1SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("demo1DruidDataSource") DruidDataSource dataSource,
                                                   @Qualifier("demo1MybatisPlusProperties") MybatisPlusProperties properties,
                                                   ResourceLoader resourceLoader,
//                                                   Interceptor[] interceptors,
//                                                   DatabaseIdProvider databaseIdProvider,
                                                   ApplicationContext applicationContext) throws Exception {
        return getSqlSessionFactory(dataSource,
                properties,
                resourceLoader,
                null,
                null,
                applicationContext);
    }

    @Bean(name = "demo1SqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("demo1MybatisPlusProperties") MybatisPlusProperties properties,
                                                 @Qualifier("demo1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return getSqlSessionTemplate(sqlSessionFactory, properties);
    }
}

```

数据库2：com.example.oldguy.configs.Demo2Configuration
```
package com.example.oldguy.configs;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * @ClassName: Demo1Configuration
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 上午 11:03
 **/
@MapperScan(basePackages = "com.example.oldguy.modules.model2.dao.jpas",
        sqlSessionTemplateRef = "demo2SqlSessionTemplate",
        sqlSessionFactoryRef = "demo2SqlSessionFactory")
@Configuration
public class Demo2Configuration extends AbstractMybatisPlusConfiguration {

    @Bean(name = "demo2DruidDataSource")
    @ConfigurationProperties(prefix = "demo2.datasource")
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "demo2MybatisPlusProperties")
    @ConfigurationProperties(prefix = "mybatis-plus.demo2")
    public MybatisPlusProperties mybatisPlusProperties() {
        return new MybatisPlusProperties();
    }


    @Bean(name = "demo2SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean(@Qualifier("demo2DruidDataSource") DruidDataSource dataSource,
                                                   @Qualifier("demo2MybatisPlusProperties") MybatisPlusProperties properties,
                                                   ResourceLoader resourceLoader,
//                                                   Interceptor[] interceptors,
//                                                   DatabaseIdProvider databaseIdProvider,
                                                   ApplicationContext applicationContext) throws Exception {
        return getSqlSessionFactory(dataSource,
                properties,
                resourceLoader,
                null,
                null,
                applicationContext);
    }

    @Bean(name = "demo2SqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("demo2MybatisPlusProperties") MybatisPlusProperties properties,
                                                 @Qualifier("demo2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return getSqlSessionTemplate(sqlSessionFactory, properties);
    }
}

```

Step4：配置yml文件

```
demo1:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mybatis-plus-1?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true
    username: root
    password: root
    driver-class-name:  com.mysql.jdbc.Driver

demo2:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/mybatis-plus-2?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true
    username: root
    password: root
    driver-class-name:  com.mysql.jdbc.Driver

mybatis-plus:
  demo1:
    config-location: classpath:configs/myBatis-config.xml
    mapper-locations: classpath:mapper/model1/**/*.xml
    typeAliasesPackage: com.example.oldguy.modules.model1.dao.entities

  demo2:
    config-location: classpath:configs/myBatis-config.xml
    mapper-locations: classpath:mapper/model2/**/*.xml
    typeAliasesPackage: com.example.oldguy.modules.model2.dao.entities

logging:
  level:
    com.example.oldguy: debug
```







