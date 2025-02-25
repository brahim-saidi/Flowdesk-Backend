package com.hahnSoftware.ticket.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    
 
    @Bean
    public DataSource dataSource() {
        logger.info("Configuring datasource...");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521/XEPDB1");
        dataSource.setUsername("system");
        dataSource.setPassword("pass123");
        logger.info("Datasource configured successfully with URL: {}", dataSource.getUrl());
        return dataSource;
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        logger.info("Initializing database with SQL scripts...");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        
        ClassPathResource scriptResource = new ClassPathResource("data.sql");
        if (scriptResource.exists()) {
            logger.info("Found data.sql script in classpath");
            resourceDatabasePopulator.addScript(scriptResource);
        } else {
            logger.error("Could not find data.sql script in classpath!");
        }
        
        resourceDatabasePopulator.setContinueOnError(true);
        
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        logger.info("Database initialization configured");
        return dataSourceInitializer;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // Use @Bean instead of @PostConstruct for initialization logic that requires other beans
    @Bean
    @DependsOn("dataSourceInitializer")
    public Boolean databaseInitializer(JdbcTemplate jdbcTemplate) {
        try {
            logger.info("Verifying database setup...");
            
            // Check if USERS table exists
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM user_tables WHERE table_name = 'USERS'"
            );
            
            if (tables.isEmpty()) {
                logger.error("USERS table does not exist! Make sure data.sql is executed.");
            } else {
                logger.info("USERS table exists in the database");
                
                // Check if there are any users
                Integer userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users", Integer.class
                );
                
                logger.info("Found {} users in the database", userCount);
                
                if (userCount == 0) {
                    logger.info("No users found. Creating default users...");
                    
                    // Insert default users directly
                    jdbcTemplate.execute(
                        "INSERT INTO users (user_id, username, password, email, role) " +
                        "VALUES (user_seq.NEXTVAL, 'emp2', 'emp2', 'emp@company.com', 'EMPLOYEE')"
                    );

                    jdbcTemplate.execute(
                        "INSERT INTO users (user_id, username, password, email, role) " +
                        "VALUES (user_seq.NEXTVAL, 'emp', 'emp', 'emp@company.com', 'EMPLOYEE')"
                    );
                    
                    jdbcTemplate.execute(
                        "INSERT INTO users (user_id, username, password, email, role) " +
                        "VALUES (user_seq.NEXTVAL, 'admin1', 'admin1', 'admin@company.com', 'IT_SUPPORT')"
                    );

                    jdbcTemplate.execute(
                        "INSERT INTO users (user_id, username, password, email, role) " +
                        "VALUES (user_seq.NEXTVAL, 'admin2', 'admin2', 'admin@company.com', 'IT_SUPPORT')"
                    );
                    
                    logger.info("Default users created successfully");
                }
            }
        } catch (Exception e) {
            logger.error("Error verifying database setup", e);
        }
        return true;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.hahnSoftware.ticket.entity");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        em.setJpaVendorAdapter(vendorAdapter);
        
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        em.setJpaProperties(properties);
        
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}