package fr.gouv.monprojetsup.app.db.model;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.logging.Logger;

import static fr.gouv.monprojetsup.app.db.model.Lycee.LYCEES_COLL_NAME;

@Document(LYCEES_COLL_NAME)
public final class Lycee {

    public static final String LYCEES_COLL_NAME = "lycees";
    private static final Logger LOGGER = Logger.getLogger(Lycee.class.getName());
    private final @Field("id") @Getter String id;
    private final @Getter String name;
    private @Getter List<Classe> classes = new ArrayList<>();
    private @Getter Set<String> proviseurs = new HashSet<>();

    public static String EXPE_ENS = "expeENS";
    private @Setter @Getter boolean expeENS;

    public static String EXPE_INDIVISIBLE = "expeIndivisible";
    private @Setter @Getter boolean expeIndivisible;

    public Lycee(String id, String name, List<Classe> classes, @Nullable Set<String> proviseurs) {
        this.id = id;
        this.name = name;
        this.classes.addAll(classes);
        if(proviseurs != null) this.proviseurs.addAll(proviseurs);
        this.expeENS = false;
        this.expeIndivisible = false;
    }

    public boolean updateFrom(Lycee other) {
        boolean changed = false;
        if (other.proviseurs != null) {
            changed |= proviseurs.addAll(other.proviseurs);
        }
        changed |= expeIndivisible != other.expeIndivisible;
        expeIndivisible = other.expeIndivisible;

        changed |= expeENS != other.expeENS;
        expeENS = other.expeENS;

        for (Classe classe : other.classes) {
            if (classes.stream().noneMatch(c -> c.index().equals(classe.index()))) {
                LOGGER.info("Création d'une nouvelle classe " + classe + " dans le lycée " + this.id);
                classes.add(classe);
                changed = true;
            }
        }
        return changed;
    }

    public void init() {
        if (classes == null) classes = new ArrayList<>();
        classes.removeIf(Objects::isNull);
        classes.removeIf(c -> c.index() == null || c.index().isEmpty());
        if (proviseurs == null) proviseurs = new HashSet<>();
    }

    public void addProviseur(String proviseur) {
        proviseurs.add(proviseur);
    }

    public void addClassse(Classe classe) {
        classes.add(classe);
    }

}
