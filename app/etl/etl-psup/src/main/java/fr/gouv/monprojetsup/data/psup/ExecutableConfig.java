package fr.gouv.monprojetsup.data.psup;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutableConfig<T> {

    private static final Logger LOGGER = Logger.getLogger(ExecutableConfig.class.getSimpleName());

    public T fromFile(String defaultFilename, String envVariableName, Class<T> classe, T defaultValue) throws IOException, JAXBException {
        String filename = filenameFromEnv(envVariableName, defaultFilename);
        if (new File(filename).exists()) {
            try(FileInputStream fis = new FileInputStream(filename);
                BufferedInputStream in = new BufferedInputStream(fis)
            ) {
                try {
                    JAXBContext context = JAXBContext.newInstance(classe);
                    Unmarshaller m = context.createUnmarshaller();
                    Object o = m.unmarshal(in);
                    if(classe.isInstance(o)) {
                        return classe.cast(o);
                    } else {
                        throw new RuntimeException("Unexpected deserialization error: mismatched type");
                    }
                } catch(JAXBException ex) {
                    LOGGER.severe("erreur de désérialisation du fichier  '" +  filename + "' :"+ ex.getMessage());
                    LOGGER.severe("exemple de contenu attendu:");
                    System.out.println(getExemple(classe, defaultValue));
                    throw new RuntimeException("Veuillez corriger le contenu du fichier de config " + filename);
                }
            }
        } else {
            throw new FileNotFoundException(
                    "Le fichier de configuration " + filename + " est introuvable," +
                            "exemple de contenu attendu:\n\n" + getExemple(classe, defaultValue) + "\n\n");
        }
    }

    public static String filenameFromEnv(String envVariableName, String defaultFilename) {
        String filename = defaultFilename;
        try {
            filename = System.getenv(envVariableName);
        } catch (NullPointerException | SecurityException ex) {
            LOGGER.log(Level.WARNING, "Echec de la lecture de la variable d'environnement " + envVariableName);
        }
        if(filename == null) {
            LOGGER.log(Level.WARNING, "La variable d'environnement "
                    + envVariableName + " n''est pas positionn\u00e9e ou accessible, utilisation du fichier de param\u00e8tres par d\u00e9faut: " + defaultFilename
            );
            filename = defaultFilename;

        }
        return filename;
    }

    public String getExemple(Class<T> classe, T defaultValue) throws JAXBException, IOException {
        Marshaller marshaller = JAXBContext.newInstance(classe).createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", true);
        try (StringWriter out = new StringWriter()) {
            marshaller.marshal(defaultValue, out);
            return out.toString();
        }
    }


}
