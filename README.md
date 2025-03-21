Add a directory `resources` in the `main` directory with a file name `application.properties` and content:
```
# DataSource settings
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate properties
spring.jpa.database-platform = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql = false
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.implicit-strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true

#config data
calendar.rehearsal.min-hour=7
calendar.rehearsal.max-hour=23
```

Add a directory `resources` in the `test` directory with a file name `application-test.properties` and content:
```
spring.config.activate.on-profile=test
logging.level.root=DEBUG

# DataSource settings
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate properties
spring.jpa.database-platform = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql = false
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.implicit-strategy = org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl
spring.jpa.properties.hibernate.format_sql=true

logging.level.org.springframework=DEBUG
```

The data Source settings are to be filed.
