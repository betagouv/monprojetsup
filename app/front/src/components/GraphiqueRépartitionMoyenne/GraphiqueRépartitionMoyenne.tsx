import { type GraphiqueRépartitionMoyenneProps } from "./GraphiqueRépartitionMoyenne.interface";
import { i18n } from "@/configuration/i18n/i18n";

const GraphiqueRépartitionMoyenne = ({ notes }: GraphiqueRépartitionMoyenneProps) => {
  return (
    <div>
      <div
        aria-hidden
        className="flex pb-2 text-right text-xs text-[--text-mention-grey] sm:text-sm"
      >
        <div className="w-[12%] sm:w-[8%]">
          <strong>{notes[0]}</strong>/20
        </div>
        <div className="w-[20%] ">
          <strong>{notes[1]}</strong>/20
        </div>
        <div className="w-[49.5%]">
          <strong>{notes[2]}</strong>/20
        </div>
        <div className="w-[20%]">
          <strong>{notes[3]}</strong>/20
        </div>
      </div>
      <div
        aria-hidden
        className="flex w-full gap-1"
      >
        <div className="h-2 w-[5%] bg-[--background-contrast-beige-gris-galet]" />
        <div className="h-2 w-[20%] bg-[--background-action-low-green-menthe-hover]" />
        <div className="h-2 w-[50%] bg-[#4F9D91]" />
        <div className="h-2 w-[20%] bg-[--background-action-low-green-menthe-hover]" />
        <div className="h-2 w-[5%] bg-[--background-contrast-beige-gris-galet]" />
      </div>
      <div
        aria-hidden
        className="flex pt-2 text-center font-bold"
      >
        <div className="w-[5%]" />
        <div className="w-[20%] text-[#419CA4]">20%</div>
        <div className="w-[50%] text-[#456350]">50%</div>
        <div className="w-[20%] text-[#419CA4]">20%</div>
        <div className="w-[5%]" />
      </div>
      <div>
        <details className="mt-8 text-center">
          <summary className="fr-link inline border-0 border-b border-solid border-[--underline-img] text-sm hover:border-b-[1.5px] hover:!bg-inherit">
            <span
              aria-hidden="true"
              className="fr-icon-menu-2-fill fr-icon--sm mr-2"
            />
            {i18n.ACCESSIBILITÉ.VERSION_TEXTE_GRAPHIQUE}
          </summary>
          <ul className="justify-start pt-4 text-left text-sm">
            <li>
              {i18n.PAGE_FORMATION.RÉPARTITION_MOYENNE.PREMIER_DÉCILE} {notes[0]}
            </li>
            <li>
              {i18n.PAGE_FORMATION.RÉPARTITION_MOYENNE.SECOND_DÉCILE} {notes[0]} et {notes[1]}
            </li>
            <li>
              {i18n.PAGE_FORMATION.RÉPARTITION_MOYENNE.TROISIÈME_DÉCILE} {notes[1]} et {notes[2]}
            </li>
            <li>
              {i18n.PAGE_FORMATION.RÉPARTITION_MOYENNE.QUATRIÈME_DÉCILE} {notes[3]}
            </li>
          </ul>
        </details>
      </div>
    </div>
  );
};

export default GraphiqueRépartitionMoyenne;
