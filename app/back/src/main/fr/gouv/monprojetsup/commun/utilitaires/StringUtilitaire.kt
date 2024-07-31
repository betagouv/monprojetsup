package fr.gouv.monprojetsup.commun.utilitaires

import java.text.Normalizer

fun String.retirerAccents() = Normalizer.normalize(this, Normalizer.Form.NFD).replace("\\p{Mn}+".toRegex(), "")
