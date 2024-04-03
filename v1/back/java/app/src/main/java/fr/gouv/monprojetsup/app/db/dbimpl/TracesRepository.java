package fr.gouv.monprojetsup.app.db.dbimpl;

import fr.gouv.monprojetsup.app.log.ServerTrace;
import org.springframework.stereotype.Component;

@Component
public interface TracesRepository extends org.springframework.data.mongodb.repository.MongoRepository<ServerTrace, String> {

}
