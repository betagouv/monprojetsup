package fr.gouv.monprojetsup.app.db.dbimpl;

import fr.gouv.monprojetsup.app.db.model.Lycee;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface LyceesRepository extends MongoRepository<Lycee, String> {

    @Query("{id:'?0'}")
    Document findDocById(String id);

}

