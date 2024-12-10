package fr.gouv.monprojetsup.suggestions.server;

import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class MyService<T,U> {

    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    protected abstract @NotNull U handleRequest(@NotNull T req) throws Exception;

    public @NotNull U handleRequestAndExceptions(@NotNull T req) throws MyServiceException {
        try {
            logger.debug("\n*******************************************************\n\n\t{}\n\n", getServiceName());
            logger.debug(new GsonBuilder().setPrettyPrinting().create().toJson(req));
            return handleRequest(req);
        } catch (Exception e) {
            throw new MyServiceException(e, req);
        }
    }

    public abstract String getServiceName();


}
