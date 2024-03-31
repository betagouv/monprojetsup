package fr.gouv.monprojetsup.tools.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyServiceException extends Exception {

    @Nullable
    public final transient Object request;

    MyServiceException(@NotNull Exception inner, @Nullable Object request) {
        super(inner);
        this.request = request;
    }

}
