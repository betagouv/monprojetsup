/* eslint-disable unicorn/prefer-string-replace-all */
import { statSync } from "node:fs";
import { mkdir, readdir, rm, writeFile } from "node:fs/promises";
import path, { dirname } from "node:path";

export class SQLHandler {
  public async supprimerFichiersSQL(cheminDuDossier: string) {
    const fichiers = await this._trouverFichiersSQL(cheminDuDossier);
    for (const fichier of fichiers) {
      await rm(fichier);
    }
  }

  private async _trouverFichiersSQL(cheminDuDossier: string) {
    const fichiersSQL = [];

    const fichiers = await readdir(cheminDuDossier);

    for (const fichier of fichiers) {
      const cheminDuFichier = `${cheminDuDossier}/${fichier}`;

      if (!statSync(cheminDuFichier).isDirectory() && /.*\.sql/u.test(fichier)) {
        fichiersSQL.push(cheminDuFichier);
      }
    }

    return fichiersSQL;
  }

  public générerInsert(table: string, colonnes: string[], lignes: unknown[][], update = true) {
    const lignesSQL: string[] = [];

    if (!update) {
      lignesSQL.push(`TRUNCATE TABLE ${table};\n`);
    }

    const values = lignes
      .map((ligne) =>
        ligne
          .map((valeur) => {
            if (valeur === null || valeur === undefined) return "NULL";
            if (Array.isArray(valeur))
              return `ARRAY ${JSON.stringify(valeur).replace(/'/gu, "''").replace(/"/gu, "'")}`;

            if (typeof valeur === "string") {
              if (valeur.startsWith("[{")) {
                return `ARRAY [${valeur
                  .replace(/'/gu, "''")
                  .replace(/^\[/gu, "'")
                  .replace(/\]$/gu, "'")
                  .replace(/"\},\{"/gu, "\"}','{\"")}]::jsonb[]`;
              }

              if (valeur === "[]") {
                return "ARRAY []::jsonb[]";
              }

              return `'${valeur.replace(/'/gu, "''")}'`;
            }

            if (typeof valeur === "object") return `'${JSON.stringify(valeur)}'`;
            return valeur;
          })
          .join(",\n"),
      )
      .join("\n),\n(\n");

    lignesSQL.push(`INSERT INTO ${table}(${colonnes.join(", ")})`);
    lignesSQL.push("VALUES");
    lignesSQL.push(`(\n${values}\n)`);

    if (update) {
      lignesSQL.push("ON CONFLICT (id) DO UPDATE SET");
      lignesSQL.push(colonnes.map((colonne) => `${colonne} = excluded.${colonne}`).join(",\n"));
    }

    lignesSQL.push(";");
    return lignesSQL.join("\n");
  }

  public async créerFichier(nomDuFichier: string, contenu: string) {
    const cheminDuFichier = path.join(__dirname, "../sqlDeSortie", `${nomDuFichier}.sql`);
    await mkdir(dirname(cheminDuFichier), { recursive: true });

    await writeFile(cheminDuFichier, contenu);
  }
}
