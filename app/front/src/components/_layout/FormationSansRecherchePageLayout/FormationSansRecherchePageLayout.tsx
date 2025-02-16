import { type FormationSansRecherchePageLayoutProps } from "./FormationSansRecherchePageLayout.interface";

const FormationSansRecherchePageLayout = ({ children }: FormationSansRecherchePageLayoutProps) => {
  return (
    <div className={`h-full bg-white lg:bg-gradient-to-r lg:from-[--background-contrast-beige-gris-galet] lg:from-50% lg:to-white lg:to-50%`}>
      <div className="fr-container h-full lg:grid lg:grid-cols-[450px_1fr] xl:grid-cols-[490px_1fr]">{children}</div>
    </div>
  );
};

export default FormationSansRecherchePageLayout;
