package fr.gouv.monprojetsup.app.db.dbimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.Collections;

@Configuration
public class MongoConfig {

    @Autowired
    private MongoDatabaseFactory mongoDbFactory;

    @Autowired
    private MongoMappingContext mongoMappingContext;


    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        mongoMappingContext.setAutoIndexCreation(false);

        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), mongoMappingContext);
        converter.setCustomConversions(new MongoCustomConversions(Collections.emptyList())); // if you have custom conversions
        converter.afterPropertiesSet();
        converter.setMapKeyDotReplacement("_"); // to avoid issues with keys containing dots

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);
        return mongoTemplate;
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}