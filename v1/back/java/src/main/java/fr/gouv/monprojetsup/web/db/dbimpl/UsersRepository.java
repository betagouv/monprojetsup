package fr.gouv.monprojetsup.web.db.dbimpl;

import fr.gouv.monprojetsup.web.db.model.User;
import org.bson.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Component;

@Component
public interface UsersRepository extends MongoRepository<User, String> {

    @Query("{id:'?0'}")
    Document findDocById(String id);

}

