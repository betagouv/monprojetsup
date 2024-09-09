package fr.gouv.monprojetsup.suggestions.server;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class MySuggService<T,U> extends MyService<T,U> {

    protected MySuggService() {
       super();
    }

    protected abstract @NotNull U handleRequest(@NotNull T req) throws Exception;

}
