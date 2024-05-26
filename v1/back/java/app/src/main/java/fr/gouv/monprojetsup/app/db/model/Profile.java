package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.app.db.DBTools;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.dto.ProfileUpdateDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.model.User.normalizeUser;
import static fr.gouv.monprojetsup.data.Helpers.*;
import static fr.gouv.monprojetsup.data.dto.SuggestionDTO.*;

@Data
@AllArgsConstructor
public final class Profile {

    private static final int PROFILE_COMPLETENESS_LEVEL_PROFILE = 0;
    private static final int PROFILE_COMPLETENESS_LEVEL_PREFS = 1;
    public static final int PROFILE_COMPLETENESS_LEVEL_OK = 2;

    public static final String RETOURS_FIELD = "retours";

    private static final Logger LOGGER = Logger.getLogger(Profile.class.getSimpleName());

    private String login;
    private String nom;
    private String prenom;
    private String niveau;
    private String ine;
    private String bac;
    private String duree;
    private String statut;
    private String apprentissage;
    private Set<String> geo_pref = new HashSet<>();
    private Set<String> spe_classes= new HashSet<>();
    private List<ProfileDb.Retour> retours = new ArrayList<>();

    /** maps various things to integer interests chosen by the student.
     E.g. interets "T-ITM.1020" --> 5
     E.g. interests "T-IDEO2.4819" --> 3
     E.g. metiers keys "MET.7776" --> 4
     E.g. other keywords "innovations" --> 3

     Scores are in the interval [0,100].
     The higher the score, the preferred by the student.

     An fl absent from the key set is interpreted as score 0.
     */
    @NotNull private Map<String, Integer> scores = new HashMap<>();
    private String mention;
    private String moygen;
    @NotNull private Map<String, List<Message>> msgs = new HashMap<>();
    /* the choices of the student, indexed by key */
    @NotNull private Map<String, SuggestionDTO> choices = new HashMap<>();


    public Profile(String login, String nom, String prenom) {
        this.login = login;
        this.nom = nom;
        this.prenom = prenom;
        this.bac = null;
        this.duree = null;
        this.apprentissage = null;
        this.mention = null;
        this.moygen = null;
        this.niveau = null;//inconnu
        this.ine = null;

    }


    int isOk(String s) {
        if (s == null || s.isEmpty()) return 0;
        return 1;
    }

    @Transient
    int isOk(Collection<String> s) {
        return s.isEmpty() ? 0 : 1;
    }

    @Transient
    public int getCompletenessPercent() {
        int result;
        int criterieNb = 0;
        result = isOk(bac); criterieNb++;
        result += isOk(duree); criterieNb++;
        result += isOk(apprentissage); criterieNb++;
        result += isOk(geo_pref); criterieNb++;
        result += isOk(spe_classes); criterieNb++;
        result += hasAtLeastOneProfesionalDomain() ? 1 : 0; criterieNb++;
        result += hasAtLEastOneInterest() ? 1 : 0; criterieNb++;
        return result * 100 / criterieNb;
    }

    private boolean hasAtLeastOneProfesionalDomain() {
        return scores.entrySet().stream().anyMatch(e -> e.getValue() > 0 && isTheme(e.getKey()));
    }
    private boolean hasAtLEastOneInterest() {
        return scores.entrySet().stream().anyMatch(e -> e.getValue() > 0 && isInteret(e.getKey()));
    }

    @Transient
    public int getProfileCompleteness() {
        if(isOk(nom) == 0 || isOk(prenom) == 0 || isOk(niveau) == 0) return PROFILE_COMPLETENESS_LEVEL_PROFILE;
        if(choices.isEmpty() && scores.isEmpty()) return PROFILE_COMPLETENESS_LEVEL_PREFS;
        return PROFILE_COMPLETENESS_LEVEL_OK;
    }

    public static Profile getNewProfile(String login, String nom, String prenom) {
        return new Profile(normalizeUser(login), nom, prenom);
    }


    public String login() {
        return normalizeUser(login);
    }

    @Transient
    public List<SuggestionDTO> suggApproved() {
        return choices.values().stream().filter(s -> Objects.equals(s.status(), SUGG_APPROVED)).toList();
    }

    @Transient
    public List<SuggestionDTO> suggRejected() {
        return choices.values().stream().filter(s -> Objects.equals(s.status(), SUGG_REJECTED)).toList();
    }

    @Transient
    public Map<String, List<Message>> chats() {
        return msgs;
    }



    @Transient
    public @NotNull String getName() {
        if((nom == null || nom.isEmpty()) && (prenom == null || prenom.isEmpty())) {
            return login;
        } else if(nom == null || nom.isEmpty()) {
            return prenom;
        } else if((prenom == null || prenom.isEmpty())){
            return nom;
        } else {
            return prenom + " " + nom;
        }
    }

    private void setValue(@NotNull String name, String value, boolean add, boolean clear) {
        if( (add || !clear) && value == null) throw new RuntimeException("Value is null for " + name);
        //LOGGER.info("Setting " + name + " to " + value + " for " + login + " add=" + add);
        switch (name) {
            case "nom": nom = value; break;
            case "prenom": prenom = value; break;
            case "bac": bac = value; break;
            case "apprentissage": apprentissage = value; break;
            case "mention": mention = value; break;
            case "moygen": moygen = value; break;
            case "duree": duree = value; break;
            case "niveau": niveau = value; break;
            case "statut": statut = value; break;
            case "scores":
            case "interests":
                if(add) scores.put(value.replace(".","_"), 1);
                else if(clear) scores.clear();
                else scores.remove(value.replace(".","_"));
                break;
            case "geo_pref":
                if(add) geo_pref.add(value);
                else if(clear) geo_pref.clear();
                else geo_pref.remove(value);
                break;
            case "spe_classes":
                if(add) spe_classes.add(value);
                else if(clear) spe_classes.clear();
                else spe_classes.remove(value); break;
            case "ine": ine = value; break;
            default:
        }
    }


    public void     updateProfile(
            @NotNull ProfileUpdateDTO update) {

        if (update.name() != null) {
            boolean add = Objects.equals(update.action(), "add");
            boolean clear = Objects.equals(update.action(), "clear");
            this.setValue(update.name(), update.value(), add, clear);
        }
        if (update.suggestions() != null) {
            update.suggestions().forEach(suggestion -> {
                String fl = suggestion.fl();
                fl = fl.replace(".", "_");
                Integer status = suggestion.status();
                if (status != null && status == SUGG_PENDING) {
                    choices.remove(fl);
                } else {
                    SuggestionDTO current = choices.getOrDefault(fl, new SuggestionDTO(
                                    fl,
                                    suggestion.status(),
                                    suggestion.score()
                            )
                    );
                    if (status != null) current = current.updateStatus(status);
                    if (suggestion.score() != null) current = current.updateScore(suggestion.score());
                    choices.put(fl, current);
                }
            });
        }
    }

    public void updateProfile(ProfileDb p) {
        if(p.nom() != null) nom = p.nom();
        if(p.prenom() != null) prenom = p.prenom();
        if(p.bac() != null) bac = p.bac();
        if(p.apprentissage() != null) apprentissage = p.apprentissage();
        if(p.mention() != null) mention = p.mention();
        if(p.moygen() != null) moygen = p.moygen();
        if(p.duree() != null) duree = p.duree();
        if(p.niveau() != null) niveau = p.niveau();
        if(p.ine() != null) ine = p.ine();
        if(p.scores() != null) {
            scores.clear();
            scores.putAll(p.scores());
            DBTools.removeDotsFromKeys(scores);
        }
        if(p.geo_pref() != null) {
            geo_pref.clear();
            geo_pref.addAll(p.geo_pref());
        }
        if(p.spe_classes() != null) {
            spe_classes.clear();
            spe_classes.addAll(p.spe_classes());
        }
    }


    public void anonymize() {
        login = createRandomString();
        nom = null;
        prenom = null;
        msgs.clear();
        Map<String, SuggestionDTO> an
                = choices.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().anonymize()
                )
        );
        choices.clear();
        choices.putAll(an);
    }

    /**
     * @return a random string
     */
    private String createRandomString() {
        return UUID.randomUUID().toString();
    }

    public ProfileDb toDbo() {
        removeDotsFromKeys();
        return new ProfileDb(
                login,
                nom,
                prenom,
                niveau,
                ine,
                bac,
                duree,
                apprentissage,
                geo_pref,
                spe_classes,
                scores,
                mention,
                moygen,
                choices,
                statut,
                new ArrayList<>(retours)
        ).sanitize();
    }


    public String toExplanationString() {
        return "ProfileDTO{\n" +
                "niveau='" + niveau + "'\n" +
                ", bac='" + bac + "'\n" +
                ", duree='" + duree + "'\n" +
                ", apprentissage='" + apprentissage + "'\n" +
                ", geo_pref='" + geo_pref + "'\n" +
                ", spe_classes='" + spe_classes + "'\n" +
                ", interests='" + toExplanationString(scores) + "'\n" +
                ", mention='" + mention + "'\n" +
                ", moygen='" + moygen + "'\n" +
                ", choices=" + toExplanationString(suggApproved()) + "\n" +
                ", rejected=" + toExplanationString(suggRejected()) + "\n" +
                '}';
    }

    private String toExplanationString(List<SuggestionDTO> suggestions) {
        return suggestions.stream()
                .map(s -> ServerData.getDebugLabel(s.fl()))
                .reduce("\t", (a, b) -> a + "\n\t" + b);
    }

    private String toExplanationString(Map<String, Integer> scores) {
        return scores.entrySet().stream()
                .map(e -> ServerData.getDebugLabel(e.getKey()) + ":" + e.getValue())
                .reduce("\t", (a, b) -> a + "\n\t" + b);
    }

    @SuppressWarnings("unused")
    public Profile() {
    }

    public void removeDotsFromKeys() {
        DBTools.removeDotsFromKeys(scores);
        DBTools.removeDotsFromKeys(msgs);
        DBTools.removeDotsFromKeys(choices);
    }

    public int nbFormationsFavoris() {
        return (int) choices.values().stream().filter(s -> s.status() == SUGG_APPROVED && isFiliere(s.fl())).count();
    }
    public int nbMetiersFavoris() {
        return (int) choices.values().stream().filter(s -> s.status() == SUGG_APPROVED && isMetier(s.fl())).count();
    }

    public void setTeacherFeedback( @NotNull String author,@NotNull String key, @NotNull String type, @Nullable String content) {
        if(retours == null) retours = new ArrayList<>();
        retours.removeIf(r -> r.author().equals(author) && r.key().equals(key));
        if(content != null) {
            retours.add(new ProfileDb.Retour(author, type, key, content, LocalDate.now().toString()));
        }
    }

}
