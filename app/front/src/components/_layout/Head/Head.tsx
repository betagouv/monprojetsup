import { type HeadProps } from "./Head.interface";
import { i18n } from "@/configuration/i18n/i18n";
import { Helmet } from "react-helmet-async";

const Head = ({ title }: HeadProps) => {
  return (
    <Helmet>
      <title>{`${title} - ${i18n.APP.NOM}`}</title>
    </Helmet>
  );
};

export default Head;
