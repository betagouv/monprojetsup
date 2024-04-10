import { statSync } from "node:fs";
import { readdir, readFile } from "node:fs/promises";

export class JSONHandler {
  public async trouverFichiersJSON(cheminDuDossier: string) {
    const fichiersJSON = [];

    const fichiers = await readdir(cheminDuDossier);

    for (const fichier of fichiers) {
      const cheminDuFichier = `${cheminDuDossier}/${fichier}`;

      if (!statSync(cheminDuFichier).isDirectory() && /.*\.json/u.test(fichier)) {
        fichiersJSON.push(cheminDuFichier);
      }
    }

    return fichiersJSON;
  }

  public async lire<T>(cheminDuFichier: string): Promise<T> {
    const contenu = await readFile(cheminDuFichier, "utf8");
    return JSON.parse(contenu);
  }
}
