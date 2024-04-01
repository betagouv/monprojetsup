package fr.gouv.monprojetsup.app.db;

import fr.gouv.monprojetsup.suggestions.algos.Profile;
import fr.gouv.monprojetsup.app.tools.Sanitizer;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.data.tools.csv.CsvTools;
import fr.gouv.monprojetsup.app.auth.Authenticator;
import fr.gouv.monprojetsup.app.auth.Credential;
import fr.gouv.monprojetsup.app.db.model.*;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.dto.ProfileDTO;
import fr.gouv.monprojetsup.app.dto.ProfileUpdateDTO;
import fr.gouv.monprojetsup.app.log.Log;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.app.mail.AccountManagementEmails;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.app.services.accounts.CreateAccountService;
import fr.gouv.monprojetsup.app.services.accounts.PasswordLoginService;
import fr.gouv.monprojetsup.app.services.teacher.GetGroupDetailsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static fr.gouv.monprojetsup.app.db.DBExceptions.*;
import static fr.gouv.monprojetsup.app.db.DBExceptions.UserInputException.*;
import static fr.gouv.monprojetsup.app.db.model.Groups.computeMissingGroups;
import static fr.gouv.monprojetsup.app.db.model.User.*;
import static fr.gouv.monprojetsup.app.db.model.User.UserTypes.lyceen;
import static java.lang.System.currentTimeMillis;

@SuppressWarnings("UnusedAssignment")
@Service
public abstract class DB {

    /* the file used by the backend */
    public static final String DEFAULT_SUPERUSER = "hugo.gimbert@gmail.com";
    public static final String LYCEE_DEMO = "LyceeDemo";
    public static final String CLASSE_DEMO = "PremiereDemo";
    private static final Logger LOGGER = Logger.getLogger(DB.class.getName());
    /**
     * authantication module.
     * Thread-safe.
     */
    public static final Authenticator authenticator = new Authenticator();
    protected static final String REGEXP_LOGIN_PATTERN = "^[\\p{L}\\p{N}0-9_\\-.@]{1,64}$";
    private final ConcurrentMap<String, Integer> failedLogin = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Object> logginIn = new ConcurrentHashMap<>();

    public static String lyceeToGroupName(String lycee, String classe) {
        return lycee + " / " + classe;
    }


    public @NotNull String generateUserEmailResetToken(@NotNull String login) throws UnknownUserException {
        String token = RandomStringUtils.random(30, true, false);
        setEmailResetToken(login, token);
        return token;
    }

    public void logTrace(@NotNull String origin, @NotNull String event) {
        logTrace(origin, event, null);
    }

    protected void setCreationConfirmationNotNeeded(String login) {
        updateUserField(login, REQUIRE_ADMIN_CONFIRMATION_FIELD, false);
        updateUserField(login, EMAIL_CONFIRMATION_TOKEN_FIELD, null);
    }


    public static void setModeration(boolean moderation, @NotNull String type) throws IOException {
        boolean moderateAcccountCreation = WebServer.config().isRequireAdminConfirmationOnAccountCreation();
        boolean checkEmails = WebServer.config().isConfirmEmailOnAccountCreation();
        if (Objects.equals(type, "creation")) moderateAcccountCreation = moderation;
        if (Objects.equals(type, "email")) checkEmails = moderation;
        WebServer.config().setModeration(moderateAcccountCreation, checkEmails);
        WebServer.config().save();
    }

    public void switchTemporarilyToLyceenUserType(String login, boolean doSwitch) {
        updateUserField(login, TEMP_TYPE_FIELD, doSwitch ? lyceen : null);
    }

    public void setNewPasswordWithTokenFromEmail(String login, String token, String newPassword) throws UnknownUserException, InvalidTokenException, NoSuchAlgorithmException, InvalidKeySpecException {
        login = normalizeUser(login);
        User user = getUser(login);
        if (!user.checkEmailResetToken(token)) {
            Log.logTrace(login, token);
            throw new InvalidTokenException("échec vérification token lors du changement de mot de passe pour email '" + login + "'");
        }
        Credential cred = Credential.getNewCredential(newPassword);
        updateUserField(login, CR_FIELD, cred);
        updateUserField(login, EMAIL_RESET_TOKEN_FIELD, null);
    }



    /**
     * validation d'un compte
     *
     * @param login l'email de l'utilisateur
     * @param token le token de validation
     * @return a pair with the first element being true if the account is activated, the second element being the message
     * @throws InvalidTokenException si le token est invalide
     * @throws UnknownUserException  si l'utilisateur est inconnu
     */
    public Pair<Boolean, String> validateAccount(
            @NotNull String login,
            @NotNull String token) throws UnknownUserException, InvalidTokenException {
        User user = getUser(login);
        if (!user.requiresEmailValidation()) {
            return Pair.of(user.isActivated(), user.getActivationMessage());
        }
        String goodToken = user.moderateToken();
        if (goodToken == null) {
            throw new InvalidTokenException("erreur de vérification de token lors de la validation du compte par email '" + login + "': pas de token");
        }
        if (!goodToken.equals(token)) {
            throw new InvalidTokenException("erreur de vérification de token lors de la validation du compte par email '" + login + "': token erroné " + token);
        }

        setEmailValidationNotNeeded(user);
        return Pair.of(user.isActivated(), user.getActivationMessage());
    }

    private void setEmailValidationNotNeeded(@NotNull User user) {
        user.setEmailValidationNotNeeded();
        updateUserField(user.login(), EMAIL_CONFIRMATION_TOKEN_FIELD, null);
    }

    public void rejectUserCreation(@NotNull String login) {
        login = normalizeUser(login);
        updateUserField(login, REQUIRE_ADMIN_CONFIRMATION_FIELD, true);
        updateUserField(login, LYCEES_FIELD, Set.of());
        forgetUserInGroups(login);
    }

    /**
     * gets profile of a user
     *
     * @param login the login
     * @return the profile
     * @throws UnknownUserException if the user is unknown
     */
    public ProfileDTO getProfile(String login) throws UnknownUserException {
        return getUser(login).pf().toDTO();
    }

    public void updateProfile(
            @NotNull String login,
            @NotNull ProfileUpdateDTO unsanitizedProfile) throws UnknownUserException {
        User user = getUser(login);
        ProfileUpdateDTO updatedProfile = unsanitizedProfile.sanitize();
        user.updateProfile(updatedProfile);
        ProfileDTO pf = user.getProfile().toDTO();
        //LOGGER.info("updateUserField " + new Gson().toJson(pf));
        updateUserField(login, PF_FIELD, pf);
    }

    public void updateProfile(
            @NotNull String login,
            @NotNull ProfileDTO unsanitizedProfile) throws UnknownUserException {
        User user = getUser(login);
        user.updateProfile(unsanitizedProfile.sanitize());
        updateUserField(login, PF_FIELD, user.getProfile().toDTO());
    }


    /**
     * creates a new user account
     *
     * @param data the data
     * @return the login answer
     * @throws NoSuchAlgorithmException                                   if the password cannot be hashed
     * @throws InvalidKeySpecException                                    if the password cannot be hashed
     * @throws InvalidPasswordException   if the password is invalid
     * @throws UserAlreadyExistsException if the user already exists
     */
    public PasswordLoginService.LoginAnswer createAccount(
            @NotNull CreateAccountService.CreateAccountRequest data
    ) throws DBException, NoSuchAlgorithmException, InvalidKeySpecException, InterruptedException {
        @NotNull String login = data.login();

        LOGGER.info("Creating new profile with login %s".formatted(login));

        createNewUser(
                data,
                WebServer.config().isConfirmEmailOnAccountCreation()
        );

        return authenticate(data.login(), data.password());
    }



    protected String getDemoGroupId() {
        return Group.lyceeToGroupId(LYCEE_DEMO, CLASSE_DEMO);
    }




    protected void saveGroups(List<Group> changed) {
        for (Group g : changed) {
            saveGroup(g);
        }
    }


    protected List<GetGroupDetailsService.StudentDetails> getMemberDetails(Set<String> members) {
        return getUsers(members).stream()
                .map(this::getMemberDetails)
                .sorted(Comparator.comparing(d -> d.name().toLowerCase()))
                .toList();
    }


    private GetGroupDetailsService.StudentDetails getMemberDetails(@NotNull User user) {
        Profile pf = user.pf();
        String name = pf.getName();
        int level = pf.getCompletenessPercent();
        int health = 0;
        String msg = "";
        if(level < 70) {
            msg += "Profil " + level + "%. ";
            health++;
        }
        int likes = pf.suggApproved().size();
        int dislikes = pf.suggRejected().size();
        if(likes == 0) {
            health++;
            msg += "Aucun favori.";
        }
        /* rq: pas de contisions sur le ratio accepté / rejeté */
        return new GetGroupDetailsService.StudentDetails(
                user.login(),
                name,
                level,
                likes,
                dislikes,
                msg,
                health);
    }


    /**
     * adds a message
     * @param author the author
     * @param student the student
     * @param topic the topic
     * @param comment the comment
     * @throws UnknownUserException if the user is unknown
     */
    public void addMessage(@NotNull String author, @NotNull String student, @NotNull String topic, @NotNull String comment) throws UnknownUserException {
        topic = topic.replace(".", "_");
        addToUserArrayField(
                student,
                "pf.msgs." + topic,
                new Message(author, comment)
        );
    }

    /**
     * gets the messages
     * @param login the login
     * @param topic the topic
     * @return the messages
     * @throws ModelException if the model is invalid
     */
    public Map<String, List<Message>> getSanitizedMessages(@NotNull String login, @Nullable String topic) throws ModelException {
        login = normalizeUser(login);
        User user = getUser(login);
        @NotNull Profile pf = user.pf();
        if(topic == null || topic.isEmpty() || pf.chats() == null) {
            return new HashMap<>();
        } else {
            Map<String, List<Message>> answer = new HashMap<>();
            answer.put(
                    Sanitizer.sanitize(topic),
                    pf.chats() == null ? Collections.emptyList() : pf.chats().getOrDefault(topic, new ArrayList<>()).stream().map(Message::sanitize).toList()
            );
            return answer;
        }
    }

    /**
     * authenticate a user and create a session token
     * authentification requires
     * 1) password and password in db do match
     * 2) the account does not exists yet, any password is accepted
     *
     * @param login the login
     * @param inputPassword the password
     * @return the login answer
     * @throws NoSuchAlgorithmException if the password cannot be hashed
     * @throws InvalidKeySpecException if the password cannot be hashed
     * @throws InvalidPasswordException if the password is invalid
     */
    public PasswordLoginService.LoginAnswer authenticate(
            @NotNull String login,
            @NotNull String inputPassword
    ) throws NoSuchAlgorithmException,
            InvalidKeySpecException,
            DBException,
            InterruptedException {
        if (!login.matches(REGEXP_LOGIN_PATTERN)) {
            throw new UnauthorizedLoginException();
        }
        login = normalizeUser(login);

        try {
            /* we add some timing in case of double auth for the same login from two different clients */
            synchronized (logginIn) {
                if (logginIn.containsKey(login)) {
                    long now = currentTimeMillis();
                    while( currentTimeMillis() - now <= WebServerConfig.INVALID_PASSWORD_DELAY_MS) {
                        logginIn.wait(WebServerConfig.INVALID_PASSWORD_DELAY_MS);
                    }
                    throw new DoubleAuthException(login);
                }
                logginIn.put(login, "");
            }
            /* we add some timing in case of lot of failed logins */
            if (failedLogin.getOrDefault(login, 0) > WebServerConfig.FAILED_LOGIN_MAX_NB_LOCK) {
                LOGGER.warning("Delaying request due to too many failed logins");
                Thread.sleep(WebServerConfig.INVALID_PASSWORD_DELAY_MS);
            }
            boolean sendConfirmationLink = false;
            String activationMessage = "";
            boolean adminActionRequired = false;

            try {
                @NotNull User user = getUser(login);
                updateUserField(login, TEMP_TYPE_FIELD, null);
                if(user.isActivated()) {
                    String token = authenticator.authenticateAndGetToken(user, inputPassword);
                    failedLogin.put(login, 0);
                    updateUserField(login, EMAIL_RESET_TOKEN_FIELD, null);
                    return PasswordLoginService.LoginAnswer.getAnswerWithValidToken(
                            login,
                            token,
                            getAdminInfos(login)
                    );
                } else {
                    adminActionRequired = user.requireAdminConfirmation() && !user.requiresEmailValidation();
                    sendConfirmationLink = user.requiresEmailValidation();
                    activationMessage = user.getActivationMessage();
                }
            } catch (UnknownUserException e) {
                throw new InvalidPasswordException();
            }

            if(sendConfirmationLink) {
                String confirmationToken = getUserEmailConfirmationToken(login);
                if (confirmationToken == null) {
                    throw new UserAlreadyEnabledException();
                }
                AccountManagementEmails.sendConfirmationLink(login, confirmationToken);
            }
            if(adminActionRequired) {
                AccountManagementEmails.informSupport(login, activationMessage);
            }
            return PasswordLoginService.LoginAnswer.getAnswerWithValidationRequired(activationMessage);

        } catch (InvalidPasswordException |
                 UserAlreadyEnabledException e) {
            /* we add some timing in case of failed login */
            LOGGER.warning("Delaying request due to invalid login");
            failedLogin.put(login, 1 + failedLogin.getOrDefault(login, 0));
            Thread.sleep(WebServerConfig.INVALID_PASSWORD_DELAY_MS);
            throw e;
        } finally {
            logginIn.keySet().remove(login);
        }
    }


    /**
     * authenticate a user with (trusted) oidc answer
     * @param email the user email
     * @return the login answer
     */
    @NotNull
    public PasswordLoginService.LoginAnswer authenticateWithOidc(@NotNull String email) throws DBException {

        String activationMessage = "";

        if(existsUserWithLogin(email)) {
            User user = getUser(email);
            updateUserField(email, TEMP_TYPE_FIELD, null);
            setEmailValidationNotNeeded(user);
            if (!user.requiresAdminConfirmation()) {
                String token = authenticator.getTokenForUserAuthenticatedViaOidbc(email);
                failedLogin.put(email, 0);
                return PasswordLoginService.LoginAnswer.getAnswerWithValidToken(
                        email,
                        token,
                        getAdminInfos(email)
                );
            } else {
                activationMessage = user.getActivationMessage();
            }
        } else{
            //not a user we know about yet
            //but a user with an email trusted from the oidbc transaction
            //however we miss the group access token at this point
            //thus for the moment we cannot create a new user
            throw new AccountCreationFromOidcNotAvailableYet();
        }

        AccountManagementEmails.informSupport(email, activationMessage);
        return PasswordLoginService.LoginAnswer.getAnswerWithValidationRequired(activationMessage);
    }


    public @NotNull Group findOrCreateDemoGroup() throws ModelException {
        try {
            return findGroup(getDemoGroupId());
        } catch (UnknownGroupException e) {
            Lycee lycDemo = new Lycee(LYCEE_DEMO, LYCEE_DEMO + " (lycée fictif)",
                    List.of(new Classe(CLASSE_DEMO, CLASSE_DEMO + " (classe fictive)",
                            Classe.Niveau.premiere,
                            false,
                            false,
                            "G"
                            ,"")
                    ),
                    new HashSet<>()
            );
            saveLycee(lycDemo);
            return createNewGroup(LYCEE_DEMO, CLASSE_DEMO);
        }
    }


    protected synchronized void init(Set<String> admins) throws DBExceptions.ModelException {
        setSuperAdminUserTypes(admins);

        findOrCreateDemoGroup();
        assignDemoGroupToAllUsersAtLeastPPExceptHackersGroup();



        List<Group> newGroups = computeMissingGroups(getLycees(), getAllGroups().stream().map(Group::getId).collect(Collectors.toSet()));

        newGroups.forEach(this::saveGroup);

        List<Group> allGroups = getAllGroups();

        //initGroups
        for (Group group : allGroups) {
            boolean changed = group.init();
            if (changed) {
                saveGroup(group);
            }
        }

    }

    public abstract List<Group> getAllGroups();

    /**********************************************************************************/
    /**********************************************************************************/
    /**************************** ABSTRACT METHODS ************************************/
    /**********************************************************************************/
    /**********************************************************************************/
    /**********************************************************************************/

    protected abstract void assignDemoGroupToAllUsersAtLeastPPExceptHackersGroup() throws DBExceptions.ModelException;

    protected abstract void setSuperAdminUserTypes(Set<String> admins);

    public abstract Groups getGroupsAndLycees();

    public abstract Collection<Group> getGroupsOfLycee(String uai);

    public abstract void load(WebServerConfig config) throws IOException, ModelException;

    public abstract void stop();

    public abstract void logBackError(@NotNull String error);

    public abstract void logFrontError(@NotNull String error);


    public abstract void logTrace(@NotNull String origin, @NotNull String event, @Nullable Object o);

    public abstract List<Lycee> getLycees();


    public abstract @Nullable String getUserEmailConfirmationToken(@NotNull String login) throws UnknownUserException;



    protected abstract void setEmailResetToken(@NotNull String emailAdress, @NotNull String emailToken) throws UnknownUserException;


    public abstract void resetUserPassword(@NotNull String user, @NotNull String password) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownUserException;

    public abstract void acceptUserCreation(@NotNull String login) throws ModelException;


    protected abstract void updateUserField(String id, String key, Object value);


    public abstract @NotNull User findUser(@Nullable String login) throws UnknownUserException;

    protected abstract void forgetUserInGroups(String user);

    protected abstract @NotNull Group findGroup(String groupId) throws UnknownGroupException;

    public abstract void createNewUser(
            @NotNull CreateAccountService.CreateAccountRequest data,
            boolean confirmEmailOnAccountCreation) throws ModelException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidPasswordException, WrongAccessCodeException, InvalidGroupTokenException, UserAlreadyExistsException, UnauthorizedLoginException;

    public abstract List<Group> getGroupsOfLycee(Collection<String> uai);

    public abstract List<Group> getGroupsWithAdmin(String login);

    /**
     * checks admin over a group
     *
     * @param login   the login
     * @param groupId the group id
     * @return true if admin
     */
    public abstract boolean isAdminOfGroup(String login, String groupId) throws ModelException;


    /**
     * check admin over a user
     *
     * @param login the login
     * @param user  the user
     * @return true if admin
     */
    public abstract boolean isAdminOfUser(String login, String user) throws UnknownUserException;

    /**
     * checks member of a group
     *
     * @param login   the login
     * @param groupId the group id
     * @return true if member
     */
    public abstract boolean isMemberOfGroup(String login, String groupId) throws ModelException;

    public abstract boolean existsUserWithLogin(String login);


    protected abstract void saveLycee(Lycee lycDemo);

    /**
     * creates a new group
     *
     * @param lycee the id of the lycee
     * @param sid   the id of the group
     * @return the group
     * @throws ModelException if the group cannot be created
     */
    public abstract Group createNewGroup(@NotNull String lycee, @NotNull String sid) throws ModelException;


    public abstract void addOrRemoveMember(String groupId, String memberLogin, boolean addMember);


    /**
     * remove a  group (if exists)
     *
     * @param gid the group id
     */
    public abstract void deleteGroup(@NotNull String gid);

    public abstract void deleteUser(String user) throws EmptyUserNameException, UnknownUserException;


    /**
     * gets the profile of the member of a group
     *
     * @param grpId       the group id
     * @param memberLogin the member login
     * @return the profile
     */
    public abstract ProfileDTO getGroupMemberProfile(String grpId, String memberLogin) throws ModelException;


    /**
     * gets the details of a group
     *
     * @param groupId the id of the group
     * @return the details
     */
    public abstract GetGroupDetailsService.GroupDetails getGroupDetails(String groupId) throws ModelException;

    protected abstract List<User> getUsers();

    protected abstract List<User> getUsers(Set<String> members);

    protected abstract void addToUserArrayField(String id, String field, Object val);

    public abstract AdminInfosDTO getAdminInfos(String login) throws UnknownUserException, UnknownGroupException;

    protected abstract boolean isEvalIndivisible(Set<String> lycees);

    protected abstract boolean isEvalENS(Set<String> lycees);

    protected abstract Collection<String> getUsersFromLycees(Set<String> lycees);

    protected abstract Collection<User> getNotActivatedUsers();

    protected abstract Collection<String> getUsersLogins();

    protected abstract void saveUser(User user);

    protected abstract @NotNull User getUser(String login) throws UnknownUserException;

    public abstract void exportUsersToFile(String filename, boolean expeENS, boolean anonymize) throws IOException;

    public void exportGroupsToFile(String filename) throws IOException {
        LOGGER.info("Export des groupes vers un fichier local.");
        getGroupsAndLycees().save(filename);
    }
    public void exportGroupsNonENSToFile(String filename) throws IOException {
        LOGGER.info("Export des groupes vers un fichier local.");
        List<Group> groups = new ArrayList<>(getGroupsAndLycees().getGroups());
        groups.removeIf(g -> ! g.getLycee().equals("graves")
                && !g.getLycee().equals("bremonthier")
                && !g.getLycee().equals("vaclav")
                && !g.getLycee().equals("libourne")
        );
        Serialisation.toJsonFile(filename, groups, true);
        List<String> admins = groups.stream().flatMap(g -> g.getAdmins().stream()).toList();
        try(CsvTools tool = new CsvTools(filename + ".csv", ';')) {
            for (String admin : admins) {
                tool.append(admin);
                tool.newLine();
            }
        }
    }

    protected abstract void saveGroup(Group group);

    public void injectFromCsvFile(String filename) {
        Pair<List<Group>, List<Lycee>> injectes = getGroupsAndLycees().loadGroupsToInjectFromCsvFile(filename);
        LOGGER.info("Updating in db " + injectes.getLeft().size() + " groups and " + injectes.getRight().size() + " lycees");
        injectes.getLeft().forEach(this::saveGroup);
        injectes.getRight().forEach(this::saveLycee);
    }

    public abstract boolean hasRightToAddAdmin(String login, String groupId, String groupAdminLogin, boolean addAdmin) throws UnknownGroupException;

    protected boolean isSuperadmin(String login) {
        return WebServer.config().getAdmins().contains(normalizeUser(login));
    }

    public abstract void addGroupAdmin(String groupId, String groupAdminLogin, boolean addAdmin) throws UnknownGroupException;

    public abstract List<ServerTrace> getTraces();
}


