package fr.gouv.monprojetsup.commun.utilitaires

fun <T> List<T>.recupererUniqueValeur(predicate: (T) -> Boolean): T? =
    this.filter(predicate).takeUnless { it.isEmpty() }
        ?.takeUnless { it.size > 1 }?.first()

fun <T> List<T>.aUneValeurCommune(listAComparer: List<T>?): Boolean {
    return listAComparer?.toSet()?.let { setAComparer ->
        this.any { it in setAComparer }
    } ?: false
}
