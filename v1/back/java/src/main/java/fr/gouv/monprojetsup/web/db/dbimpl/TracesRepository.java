package fr.gouv.monprojetsup.web.db.dbimpl;

import fr.gouv.monprojetsup.web.log.ServerTrace;
import org.springframework.stereotype.Component;

@Component
public interface TracesRepository extends org.springframework.data.mongodb.repository.MongoRepository<ServerTrace, String> {

}
