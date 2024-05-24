package fr.gouv.monprojetsup.commun.helper

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}
