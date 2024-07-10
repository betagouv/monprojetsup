package fr.gouv.monprojetsup.suggestions.data.model.cities;

import java.util.HashSet;
import java.util.Set;

public record CitiesFront(

        //indexed by zip code, one arbitrary representative when several (Paris / Nantes ....)
        Set<String> cities
)
{

    public CitiesFront() {
        this(new HashSet<>());
    }

}
