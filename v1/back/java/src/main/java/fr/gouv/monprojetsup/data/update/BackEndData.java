package fr.gouv.monprojetsup.data.update;

import fr.gouv.monprojetsup.data.model.cities.CitiesBack;
import fr.gouv.monprojetsup.data.update.psup.PsupData;
import fr.gouv.monprojetsup.data.update.onisep.OnisepData;
import fr.gouv.monprojetsup.data.update.rome.RomeData;

public record BackEndData(
        PsupData psupData,
        OnisepData onisepData,
        CitiesBack cities,

        RomeData romeData) {

}

