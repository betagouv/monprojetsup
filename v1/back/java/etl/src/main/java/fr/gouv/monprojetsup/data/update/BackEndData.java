package fr.gouv.monprojetsup.data.update;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.rome.RomeData;
import fr.gouv.parcoursup.carte.modele.modele.JsonCarte;

public record BackEndData(
        PsupData psupData,
        OnisepData onisepData,
        CitiesBack cities,
        JsonCarte carte) {

}

