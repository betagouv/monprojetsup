/* Copyright 2020 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of orientation-parcoursup.

    Algorithmes-de-parcoursup is free software: you can redistribute it and/or modify
    it under the terms of the Affero GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Algorithmes-de-parcoursup is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    Affero GNU General Public License for more details.

    You should have received a copy of the Affero GNU General Public License
    along with Algorithmes-de-parcoursup.  If not, see <http://www.gnu.org/licenses/>.

 */

package fr.gouv.monprojetsup.data.psup;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement
public class OrientationConfig implements Serializable {

    private static final String DEFAULT_FILENAME = "orientation_config.xml";

    private static final String DEFAULT_MODEL_FILENAME = "modele.xml";

    private static final String ENV_VARIABLE = "ORIENTATION_CONFIG_FILENAME";

    public final List<String> lyceesConcernes = new ArrayList<>();

    public final ConnectionParams statsDB;

    public final ConnectionParams exportConseils;

    public final DatabaseConfig trainingDB;

    public final DatabaseConfig testingDB;

    public final boolean useExistingModel;

    public final String modelName;

    public final boolean outputJson;

    public final boolean outputObj;

    public final boolean inputJson;

    public final boolean inputObj;

    public final int minNbCandidatsPourTauxAcces;

    public OrientationConfig() {
        modelName = DEFAULT_MODEL_FILENAME;
        trainingDB = new DatabaseConfig();
        testingDB = new DatabaseConfig();
        statsDB = new ConnectionParams();
        exportConseils = new ConnectionParams();
        useExistingModel = false;
        inputJson = true;
        inputObj = true;
        outputJson = false;
        outputObj = false;
        minNbCandidatsPourTauxAcces = 5;
    }

    public static OrientationConfig fromFile() throws IOException, JAXBException {
        return (new ExecutableConfig<OrientationConfig>())
                .fromFile(DEFAULT_FILENAME, ENV_VARIABLE, OrientationConfig.class, new OrientationConfig());
    }

    public  void save() throws JAXBException {
        Marshaller m = JAXBContext.newInstance(this.getClass()).createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(this, new File(this.getClass().getSimpleName() + ".xml"));
    }

    public String getOutputFilename(String prefix) {
        return prefix;
    }

    public String getInputFilename(String prefix) {
        return prefix ;
    }
    
}
