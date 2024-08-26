package fr.gouv.monprojetsup.data.poc;

import fr.gouv.monprojetsup.data.infrastructure.carte.JsonCarte;
import fr.gouv.monprojetsup.data.infrastructure.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.infrastructure.onisep.OnisepData;
import fr.gouv.monprojetsup.data.infrastructure.psup.PsupData;

public record BackEndData(
        PsupData psupData,
        OnisepData onisepData,
        CitiesBack cities,
        JsonCarte carte) {

}

