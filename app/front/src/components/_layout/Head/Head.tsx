import { type HeadProps } from "./Head.interface";
import { Helmet } from "react-helmet-async";

const Head = ({ title }: HeadProps) => {
  return (
    <Helmet>
      <title>{title}</title>
    </Helmet>
  );
};

export default Head;
