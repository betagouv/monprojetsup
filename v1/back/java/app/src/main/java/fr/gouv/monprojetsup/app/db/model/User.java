package fr.gouv.monprojetsup.app.db.model;

import fr.gouv.monprojetsup.app.auth.Credential;
import fr.gouv.monprojetsup.app.dto.ProfileDTO;
import fr.gouv.monprojetsup.app.dto.ProfileUpdateDTO;
import fr.gouv.monprojetsup.app.server.WebServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static fr.gouv.monprojetsup.app.db.model.Profile.PROFILE_COMPLETENESS_LEVEL_OK;
import static fr.gouv.monprojetsup.app.db.model.User.USERS_COLL_NAME;
import static fr.gouv.monprojetsup.app.db.model.User.UserTypes.*;

@SuppressWarnings("ALL")
@Document(USERS_COLL_NAME)
@Data
@AllArgsConstructor
public final class User {
    public static final String USERS_COLL_NAME = "users";
    public static final String USER_TYPE_FIELD = "userType";
    public static final String REQUIRE_ADMIN_CONFIRMATION_FIELD = "requireAdminConfirmation";
    public static final String EMAIL_CONFIRMATION_TOKEN_FIELD = "emailConfirmationToken";
    public static final String EMAIL_RESET_TOKEN_FIELD = "emailResetToken";
    public static final String LYCEES_FIELD = "lycees";
    public static final String TEMP_TYPE_FIELD = "tempType";
    public static final String CONFIG_FIELD = "config";
    public static final String EVAL_ENS_FIELD = "evalENS";
    public static final String CONFIG_EVAL_ENS_FIELD = CONFIG_FIELD + EVAL_ENS_FIELD;
    public static final String PF_FIELD = "pf";
    public static final String CR_FIELD = "cr";

    private @Field("id") /*@Nullable*/ String id;
    private @NotNull Credential cr;
    private @NotNull Profile pf;
    private @Nullable String cguVersion;
    private @Nullable UserTypes userType;
    private @Nullable Boolean requireAdminConfirmation;
    private @Nullable String emailConfirmationToken;
    private @Nullable String emailResetToken;
    private @NotNull Set<String> lycees = new HashSet<>();
    private UserConfig config = new UserConfig();
    private UserTypes tempType = null;

    private static Random rand = new Random();

    public static User createUser(
            @NotNull String id,
            @NotNull Credential cr,
            @NotNull Profile pf,
            @NotNull String cguVersion,
            @NotNull UserTypes userType,
            @Nullable String lycee,
            boolean requireEmailConfirmation,//the group for which a request has been made to join the group
            boolean requireAdminConfirmation,//the account awaits admin confirmation before being created
            @Nullable String emailConfirmationToken//non-null = user should be validated. token that should be provided to validate initial registration
    ) {
        if (requireEmailConfirmation && emailConfirmationToken == null) {
            throw new RuntimeException("token email null");
        }
        Set<String> lycees = (lycee == null ? Set.of() : Set.of(lycee));
        User u = new User(
                id,
                cr,
                pf,
                cguVersion,
                userType,
                requireAdminConfirmation,
                emailConfirmationToken,
                null,
                lycees,
                new UserConfig(),
                null
        );
        u.getConfig().setStatsVisibilityRandomisationENS(rand.nextBoolean());
        return u;
    }

    /*
    private User(
            @NotNull String id,
            @NotNull Credential cr,
            @NotNull Profile pf,
            @NotNull Set<String> lycees,
            @Nullable String cguVersion,
            @NotNull UserTypes userType,
            boolean requireAdminConfirmation,
            @Nullable String emailConfirmationToken,
            @Nullable String emailResetToken,
            @NotNull UserConfig config
    ) {
        this.id = id;
        this.cr = cr;
        this.pf = pf;
        this.cguVersion = cguVersion;
        this.userType = userType;
        this.requireAdminConfirmation = requireAdminConfirmation;
        this.emailConfirmationToken = emailConfirmationToken;
        this.emailResetToken = emailResetToken;
        this.lycees.addAll(lycees);
        this.config = config;
    }
*/
    public static String normalizeUser(String user) {
        if (user == null) return null;
        return user.contains("@") ? user.toLowerCase() : user;
    }

    public boolean isActivated() {
        return emailConfirmationToken == null && !requiresAdminConfirmation();
    }

    public boolean requiresAdminConfirmation() {
        return requireAdminConfirmation != null && requireAdminConfirmation;
    }

    public void setCredentials(Credential cred) {
        this.cr = cred;
    }

    public void setEmailResetToken(@Nullable String emailToken) {
        this.emailResetToken = emailToken;
    }

    public boolean checkEmailResetToken(@NotNull String emailToken) {
        return Objects.equals(this.emailResetToken, emailToken);
    }

    public String getActivationMessage() {
        String lycee = "";
        if (!lycees.isEmpty()) {
            lycee = "dans le lycée '" + lycees.stream().toList() + "'";
        }
        String classe = "";

        if (isActivated()) {
            return String.format("""
                    Votre compte est activé. <br/>
                    Vous pouvez maintenant <a href="%s">accéder au site</a>
                    """, WebServer.config().getUrl());
        }
        if (requiresEmailValidation()) {
            return String.format("""
                    <p>Veuillez valider la création de votre compte en suivant les instructions
                    envoyées par mèl à l'adresse '%s'.</p>
                    <p>Si vous ne trouvez pas cet email,
                    merci de vérifier votre dossier spam puis de contacter <b>support@monprojetsup.fr</b>.</p>
                    """, login());
        }
        if (userType == lyceen) {
            return String.format(
                    """
                            <p>Ta demande de création de compte doit être validée par ton professeur principal ou
                             un un référent.</p><p>
                            En cas de problème, contacte <b>support@monprojetsup.fr</b>.</p>
                            """)
                    ;
        }
        if (userType == pp) {
            return String.format(
                    """
                            <p>Votre demande de création de compte de type '%s' %s %s 
                            a été transmise à un modérateur (proviseur ou administrateur du site),
                            vous serez prévenu(e) par email dès que votre compte sera activé.
                            </p><p>
                            En cas de problème, veuillez contacter <b>support@monprojetsup.fr</b>.</p>
                            """, userType, lycee, classe)
                    ;
        }
        if (userType == psyen) {
            return String.format(
                    """
                               <p>Votre demande de création de compte de type '%s' %s a été transmise à 
                               un modérateur (proviseur ou administrateur du site),
                            vous serez prévenu(e) par email dès que votre compte sera activé.
                            </p><p>
                               En cas de problème, veuillez contacter <b>support@monprojetsup.fr</b>.</p>
                               """, userType, lycee)
                    ;
        }
        return String.format(
                """
                        <p>Votre demande de création de compte de type '%s' %s a été transmise à un modérateur,
                         vous serez prévenu(e) par email dès que votre compte sera activé.
                        </p><p>
                        En cas de problème, veuillez contacter <b>support@monprojetsup.fr</b>.</p>
                        """, userType, lycee)
                ;

    }

    public static boolean isAtLeastTeacher(UserTypes type) {
        return type != null && (type == superadmin || type == pp || type == psyen || type == proviseur);
    }

    public boolean isAtLeastTeacher() {
        return isAtLeastTeacher(userType);
    }

    public void updateProfile(ProfileUpdateDTO profile) {
        pf.updateProfile(profile);
    }

    public void updateProfile(ProfileDTO profile) {
        pf.updateProfile(profile);
    }

    public UserTypes getTempType() {
        return tempType == null ? userType : min(userType, tempType);
    }

    public boolean isAtLeastTeacherInLycee(String lycee) {
        return userType == superadmin
                || (isAtLeastTeacher() && lycees.contains(lycee));
    }

    public boolean isProviseur(String lycee) {
        if (userType == superadmin) return true;
        if (userType != proviseur) return false;
        return isAtLeastTeacherInLycee(lycee);
    }

    public boolean isProviseur() {
        return userType == superadmin || userType == proviseur;
    }

    public Role getRole() {
        UserTypes type = getTempType();
        if(type == superadmin) return User.Role.ADMIN;
        if(isAtLeastTeacher(type)) return User.Role.TEACHER;
        return User.Role.USER;
    }

    public boolean isIn(@Nullable Set<String> lycees) {
        if (lycees == null) return false;
        return lycees.stream().anyMatch(x -> this.lycees.contains(x));
    }

    public boolean isSuperAdmin() {
        return userType == superadmin;
    }

    public int getProfileCompleteness() {
        return  getRole().equals(Role.USER) ? pf.getProfileCompleteness() : PROFILE_COMPLETENESS_LEVEL_OK;
    }

    public boolean isProviseur(Set<String> lycees) {
        return lycees.stream().anyMatch(this::isProviseur);
    }

    public @NotNull UserConfig getConfig() {
        if(config == null) config = new UserConfig();
        return config;
    }

    /**
     * @return remove personal information from profile
     */
    public void anonymize() {
        cr = new Credential("","");
        pf.anonymize();
    }

    public void setEvalENS(boolean evalENS) {
        config.setEvalENS(evalENS);
    }

    public void setEvalIndivisible(boolean evalIndivisible) {
        config.setEvalIndivisible(evalIndivisible);
    }

    @NotNull
    public String getHashAndSaltConcatenated() {
        return cr.hash() + "$--$" + cr.salt();
    }


    public Profile getProfile() {
        return pf;
    }


    public void cleanupBeforeSave() {
        if(pf != null) pf.removeDotsFromKeys();
    }



    public enum Role {USER, TEACHER, ADMIN}

    public enum UserTypes {
        lyceen,
        pp,
        psyen,
        proviseur,

        superadmin;

        public static UserTypes min(UserTypes type1, UserTypes type2) {
            if (type1 == null) return type2;
            if (type2 == null) return type1;
            if (type1 == lyceen || type2 == lyceen) return lyceen;
            if (type1 == pp || type2 == pp) return pp;
            if (type1 == psyen || type2 == psyen) return psyen;
            if (type1 == proviseur || type2 == proviseur) return proviseur;
            if (type1 == superadmin && type2 == superadmin) return superadmin;
            return lyceen;
        }
    }

    public boolean requiresEmailValidation() {
        return emailConfirmationToken != null;
    }

    public void setEmailValidationNotNeeded() {
        this.emailConfirmationToken = null;
    }

    public @NotNull Credential cr() {
        return cr;
    }

    public @NotNull Profile pf() {
        return pf;
    }

    public String login() {
        return pf.login();
    }

    public @Nullable String moderateToken() {
        return emailConfirmationToken;
    }

    public boolean requireAdminConfirmation() {
        return requireAdminConfirmation != null && requireAdminConfirmation;
    }

    public void removeCredentials() {
        cr = new Credential("","");
    }

    @Data
    public static final class UserConfig {

        @Setter
        private boolean evalIndivisible = false;

        @Setter
        private boolean evalENS = false;

        @Setter
        private Boolean statsVisibilityRandomisationENS = null;

        @Setter
        private String expeENSGroup = "";

        public UserConfig(boolean evalIndivisible, boolean evalENS) {
            this.evalIndivisible = evalIndivisible;
            this.evalENS = evalENS;
        }

        public UserConfig() {}
    }
}
