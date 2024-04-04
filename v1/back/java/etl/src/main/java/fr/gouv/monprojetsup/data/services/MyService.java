package fr.gouv.monprojetsup.data.services;

import fr.gouv.monprojetsup.common.server.ErrorResponse;
import fr.gouv.monprojetsup.common.server.ResponseHeader;
import fr.gouv.monprojetsup.data.ServerData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Slf4j
@Service
public abstract class MyService<T,U> extends fr.gouv.monprojetsup.common.server.MyService<T,U> {

    protected MyService(@NotNull Type classT) {
       super(classT);
    }

    protected boolean isServerReady() { return ServerData.onisepData != null && ServerData.backPsupData != null; }

    @Override
    public ErrorResponse handleException(@Nullable Throwable e, @Nullable Object o, @Nullable String uri) {
        if( e == null) {
            return new ErrorResponse(new ResponseHeader(
                    ResponseHeader.SERVER_ERROR,
                    "Null exception"
            ));
        }
        log.warn("handleException", e.getMessage());
        return new ErrorResponse(new ResponseHeader(
                ResponseHeader.SERVER_ERROR,
                e.getMessage()
        ));
    }

}
