package fr.gouv.monprojetsup.app.db.dbimpl;

import fr.gouv.monprojetsup.app.log.ServerError;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ErrorsRepository extends MongoRepository<ServerError, String> {

}
