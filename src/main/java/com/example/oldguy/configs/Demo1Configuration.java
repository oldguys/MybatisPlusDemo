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
//        return null;
    }

    @Bean(name = "demo1SqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("demo1MybatisPlusProperties") MybatisPlusProperties properties,
                                                 @Qualifier("demo1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return getSqlSessionTemplate(sqlSessionFactory, properties);
//        return null;
    }
}
