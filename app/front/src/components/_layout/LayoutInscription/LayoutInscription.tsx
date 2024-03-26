import style from "./LayoutInscription.module.scss";
import { Outlet } from "@tanstack/react-router";

const LayoutInscription = () => {
  return (
    <div className={style.couleurDeFond}>
      <div className={style.imageDeFond}>
        <div className="fr-container">
          <div className="fr-grid-row fr-grid-row--center">
            <div className={style.carte}>
              <Outlet />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LayoutInscription;
