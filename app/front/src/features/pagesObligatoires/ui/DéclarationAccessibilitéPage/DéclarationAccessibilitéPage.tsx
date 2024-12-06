import LienExterne from "@/components/Lien/LienExterne/LienExterne";
import LienInterne from "@/components/Lien/LienInterne/LienInterne";
import Titre from "@/components/Titre/Titre.tsx";
import { constantes } from "@/configuration/constantes";
import { environnement } from "@/configuration/environnement";
import { i18n } from "@/configuration/i18n/i18n.ts";

const DéclarationAccessibilitéPage = () => {
  return (
    <div className="fr-container pb-20 pt-12 [&_h2]:mb-2 [&_h2]:pt-5">
      <Titre niveauDeTitre="h1">{i18n.PAGE_DÉCLARATION_ACCESSIBILITÉ.TITRE_PAGE}</Titre>
      <p>
        {i18n.APP.NOM} s’engage à rendre son site internet accessible conformément à l’article 47 de la loi n°2005-102
        du 11 février 2005. <br role="presentation" />
        Cette déclaration d’accessibilité s’applique à{" "}
        <LienInterne
          ariaLabel={environnement.VITE_APP_URL}
          href="/"
          variante="simple"
        >
          {environnement.VITE_APP_URL}
        </LienInterne>
        .
      </p>
      <Titre niveauDeTitre="h2">État de conformité</Titre>
      <p>
        {i18n.APP.NOM} est non conforme avec le référentiel général d’amélioration de l’accessibilité (RGAA), version 4.
        <br role="presentation" />
        Le site n'ayant pas encore été audité.
      </p>
      <Titre niveauDeTitre="h2">Établissement de cette déclaration d’accessibilité</Titre>
      <p>Cette déclaration a été établie le 05/12/2024.</p>
      <Titre niveauDeTitre="h2">Technologies utilisées pour la réalisation de {i18n.APP.NOM}</Titre>
      <ul>
        <li>HTML5</li>
        <li>CSS</li>
        <li>Javascript</li>
        <li>WAI-ARIA</li>
      </ul>
      <Titre niveauDeTitre="h2">Retour d’information et contact</Titre>
      <p>
        Si vous n’arrivez pas à accéder à un contenu ou à un service, vous pouvez contacter le responsable de{" "}
        {i18n.APP.NOM} pour être orienté vers une alternative accessible ou obtenir le contenu sous une autre forme.
      </p>
      <ul>
        <li>
          Email:{" "}
          <LienExterne
            ariaLabel={constantes.CONTACT.EMAIL}
            href={`mailto:${constantes.CONTACT.EMAIL}`}
            variante="simple"
          >
            {constantes.CONTACT.EMAIL}
          </LienExterne>
        </li>
        <li>Adresse : {constantes.CONTACT.ADRESSE}</li>
      </ul>
      <Titre niveauDeTitre="h2">Voies de recours</Titre>
      <p>
        Si vous constatez un défaut d’accessibilité vous empêchant d’accéder à un contenu ou une fonctionnalité du site,
        que vous nous le signalez et que vous ne parvenez pas à obtenir une réponse de notre part, vous êtes en droit de
        faire parvenir vos doléances ou une demande de saisine au Défenseur des droits.
      </p>
      <p>Plusieurs moyens sont à votre disposition :</p>
      <ul>
        <li>
          <LienExterne
            ariaLabel="Écrire un message au Défenseur des droits"
            href="https://formulaire.defenseurdesdroits.fr"
            variante="simple"
          >
            Écrire un message au Défenseur des droits
          </LienExterne>
        </li>
        <li>
          <LienExterne
            ariaLabel="Contacter le délégué du Défenseur des droits dans votre région"
            href="https://www.defenseurdesdroits.fr/saisir/delegues"
            variante="simple"
          >
            Contacter le délégué du Défenseur des droits dans votre région
          </LienExterne>
        </li>
        <li>
          Envoyer un courrier par la poste (gratuit, ne pas mettre de timbre)
          <br role="presentation" />
          Défenseur des droits
          <br role="presentation" />
          Libre réponse 71120
          <br role="presentation" />
          75342 Paris CEDEX 07
          <br role="presentation" />
        </li>
      </ul>
    </div>
  );
};

export default DéclarationAccessibilitéPage;
