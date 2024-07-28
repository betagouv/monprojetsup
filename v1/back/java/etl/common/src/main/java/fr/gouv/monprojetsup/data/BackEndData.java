package fr.gouv.monprojetsup.data;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.psup.PsupData;
import fr.gouv.monprojetsup.data.onisep.OnisepData;
import fr.gouv.monprojetsup.data.carte.modele.modele.JsonCarte;

public record BackEndData(
        PsupData psupData,
        OnisepData onisepData,
        CitiesBack cities,
        JsonCarte carte) {

}

