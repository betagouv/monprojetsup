package fr.gouv.monprojetsup.web.db.dbimpl;

import fr.gouv.monprojetsup.web.log.ServerError;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface ErrorsRepository extends MongoRepository<ServerError, String> {

}
