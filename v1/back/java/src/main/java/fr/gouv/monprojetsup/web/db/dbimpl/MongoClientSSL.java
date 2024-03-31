package fr.gouv.monprojetsup.web.db.dbimpl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;
import java.io.File;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class MongoClientSSL {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Value("${spring.data.mongodb.sslCAFile}")
    private String sslCAFile;

    @Bean
    public MongoClient mongoClient() throws SSLException {
        if(!connectionString.contains("tls=true") && !connectionString.contains("ssl=true")) { 
            return MongoClients.create(connectionString);
        } else {
            return createSslMongoClient();
        }
    }

    private MongoClient createSslMongoClient() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(new File(sslCAFile))
                .build();

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .streamFactoryFactory(NettyStreamFactoryFactory.builder().sslContext(sslContext).build())
                .codecRegistry(codecRegistry)
                .build());
    }

}
