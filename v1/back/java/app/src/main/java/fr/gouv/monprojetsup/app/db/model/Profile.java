package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.app.db.DBTools;
import fr.gouv.monprojetsup.data.ServerData;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.dto.ProfileUpdateDTO;
import fr.gouv.monprojetsup.data.dto.SuggestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.beans.Transient;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.model.User.normalizeUser;
import static fr.gouv.monprojetsup.data.dto.SuggestionDTO.SUGG_APPROVED;
import static fr.gouv.monprojetsup.data.dto.SuggestionDTO.SUGG_REJECTED;

@Data
@AllArgsConstructor
public final class Profile {

    private static final int PROFILE_COMPLETENESS_LEVEL_PROFILE = 0;
    private static final int PROFILE_COMPLETENESS_LEVEL_PREFS = 1;
    public static final int PROFILE_COMPLETENESS_LEVEL_OK = 2;

    private static final Logger LOGGER = Logger.getLogger(Profile.class.getSimpleName());

    private String login;
    private String nom;
    private String prenom;
    private String niveau;
    private String ine;
    private String bac;
    private String duree;
    private String apprentissage;
    private Set<String> geo_pref = new HashSet<>();
    private Set<String> spe_classes= new HashSet<>();

    /** maps various things to integer interests chosen by the student.
     E.g. interets "T-ITM.1020" --> 5
     E.g. interests "T-IDEO2.4819" --> 3
     E.g. metiers ids "MET.7776" --> 4
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

    public Profile(String login) {
        this.login = login;
        this.nom = null;
        this.prenom = null;
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
        result = isOk(bac);
        result += isOk(duree);
        result += isOk(apprentissage);
        result += isOk(geo_pref);
        result += isOk(spe_classes);
        return result * 100 / 5;
    }

    @Transient
    public boolean isIncomplete() {
        return isOk(nom) == 0 || isOk(prenom) == 0 || isOk(niveau) == 0 || choices.isEmpty();
    }

    @Transient
    public int getProfileCompleteness() {
        if(isOk(nom) == 0 || isOk(prenom) == 0 || isOk(niveau) == 0) return PROFILE_COMPLETENESS_LEVEL_PROFILE;
        if(choices.isEmpty() && scores.isEmpty()) return PROFILE_COMPLETENESS_LEVEL_PREFS;
        return PROFILE_COMPLETENESS_LEVEL_OK;
    }

    public static Profile getNewProfile(String login) {
        return new Profile(normalizeUser(login));
    }



    public void addMessage(String topic, Message msg) {
        if (msgs == null) {
            msgs = new HashMap<>();
        }
        List<Message> msgss = this.msgs.computeIfAbsent(topic, z -> new ArrayList<>());
        msgss.add(msg);
    }

    public String login() {
        return normalizeUser(login);
    }

    public String bac() {
        return bac;
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
            return prenom + " " + nom.toUpperCase(Locale.ROOT);
        }
    }

    private void setValue(@NotNull String name, String value, boolean add) {
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
            case "interests":
                if(add) scores.put(value.replace(".","_"), 1);
                else scores.remove(value.replace(".","_"));
                break;
            case "geo_pref": if(add) geo_pref.add(value); else geo_pref.remove(value); break;
            case "spe_classes": if(add) spe_classes.add(value); else spe_classes.remove(value); break;
            case "ine": ine = value; break;
            default:
        }
    }


    public void updateProfile(
            @NotNull ProfileUpdateDTO update) {

        if(update.name() != null) {
            boolean add = Objects.equals(update.action(), "add");
            this.setValue(update.name(), update.value(), add);
        }
        if(update.suggestions() != null) {
            update.suggestions().forEach(suggestion -> {
                String fl = suggestion.fl();
                if(fl != null) {
                    Integer status = suggestion.status();
                    if (status == null) {
                        choices.remove(fl);
                    } else {
                        SuggestionDTO current = choices.getOrDefault(fl, new SuggestionDTO(
                                fl,
                                suggestion.status(),
                                suggestion.date()
                                )
                        );
                        choices.put(fl.replace(".", "_"),
                                current.updateStatus(
                                        suggestion.status()
                                )
                        );
                    }
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

    public ProfileDb toDTO() {
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
                choices
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

}
