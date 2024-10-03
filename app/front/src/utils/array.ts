export const trierTableauDObjetsParOrdreAlphabétique = <O>(tableau: O[], propriété: keyof O): O[] => {
  return [...tableau].sort((a, b) => {
    if (a[propriété] < b[propriété]) {
      return -1;
    }

    if (a[propriété] > b[propriété]) {
      return 1;
    }

    return 0;
  });
};
