package fr.gouv.monprojetsup.suggestions.poc;

import fr.gouv.monprojetsup.suggestions.infrastructure.model.cities.CitiesBack;
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.PsupData;
import fr.gouv.monprojetsup.suggestions.infrastructure.onisep.OnisepData;
import fr.gouv.monprojetsup.suggestions.infrastructure.carte.JsonCarte;

public record BackEndData(
        PsupData psupData,
        OnisepData onisepData,
        CitiesBack cities,
        JsonCarte carte) {

}

