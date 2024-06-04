package fr.gouv.monprojetsup.commun.utilitaires

fun <T> List<T>.recupererUniqueValeur(predicate: (T) -> Boolean): T? =
    this.filter(predicate).takeUnless { it.isEmpty() }
        ?.takeUnless { it.size > 1 }?.first()
