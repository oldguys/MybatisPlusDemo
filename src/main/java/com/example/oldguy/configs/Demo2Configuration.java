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
