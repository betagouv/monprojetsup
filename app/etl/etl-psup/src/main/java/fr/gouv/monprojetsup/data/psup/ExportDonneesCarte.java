/* Copyright 2019 © Ministère de l'Enseignement Supérieur, de la Recherche et de
l'Innovation,
    David Auber (david.auber@u-bordeaux.fr)
    Hugo Gimbert (hugo.gimbert@enseignementsup.gouv.fr)

    This file is part of Algorithmes-de-parcoursup.

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExportDonneesCarte {

    private static final Logger LOGGER = Logger.getLogger(ExportDonneesCarte.class.getSimpleName());


    /* nom du xml regroupant les noms des fichiers de données */
    private static final String DATAS_FILENAME = "files.xml";

    /* suffixe variable des noms de fichiers de données */
    /* taille du buffer pour le zip */
    private static final int BUFFER_SIZE = 8192;
     

    public static void zipper(String filename) throws IOException {
        String output = filename + ".zip";

        try (
                BufferedInputStream buffer = new BufferedInputStream(Files.newInputStream(Paths.get(filename)), BUFFER_SIZE);
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(Paths.get(output))))) {
            out.setMethod(ZipOutputStream.DEFLATED);
            out.setLevel(9);
            
            File file = new File(filename);
            
            ZipEntry entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);

            byte[] data = new byte[BUFFER_SIZE];

            while (true) {
                int count = buffer.read(data, 0, BUFFER_SIZE);
                if (count <= 0) {
                    break;
                }
                out.write(data, 0, count);
            }
        }

    }
    
 



}
