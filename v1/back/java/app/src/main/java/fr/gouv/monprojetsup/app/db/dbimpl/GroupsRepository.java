package fr.gouv.monprojetsup.app.db.dbimpl;

import fr.gouv.monprojetsup.app.db.model.Group;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface GroupsRepository extends MongoRepository<Group, String> {

    @Query("{id:'?0'}")
    Document findItemById(String id);

}

