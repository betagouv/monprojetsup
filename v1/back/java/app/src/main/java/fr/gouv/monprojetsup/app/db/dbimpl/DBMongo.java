package fr.gouv.monprojetsup.app.db.dbimpl;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import fr.gouv.monprojetsup.app.db.model.*;
import fr.gouv.monprojetsup.data.dto.ProfileDTO;
import fr.gouv.monprojetsup.data.tools.Serialisation;
import fr.gouv.monprojetsup.app.auth.Credential;
import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.db.DBExceptions;
import fr.gouv.monprojetsup.app.dto.AdminInfosDTO;
import fr.gouv.monprojetsup.app.dto.GroupDTO;
import fr.gouv.monprojetsup.app.dto.ProfileDb;
import fr.gouv.monprojetsup.app.log.ServerError;
import fr.gouv.monprojetsup.app.log.ServerTrace;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.server.WebServerConfig;
import fr.gouv.monprojetsup.app.services.accounts.CreateAccountService;
import fr.gouv.monprojetsup.app.services.teacher.GetGroupDetailsService;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static fr.gouv.monprojetsup.app.db.model.Group.*;
import static fr.gouv.monprojetsup.app.db.model.Lycee.*;
import static fr.gouv.monprojetsup.app.db.model.User.*;
import static fr.gouv.monprojetsup.app.db.model.User.Role.ADMIN;
import static fr.gouv.monprojetsup.app.db.model.User.UserTypes.*;

@SuppressWarnings("SameParameterValue")
@Service
public class DBMongo extends DB implements Closeable {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(DBMongo.class.getName());

    static final String ID = "id";

    public DBMongo(
            @Autowired LyceesRepository lyceesDb,
            @Autowired GroupsRepository groupsDb,
            @Autowired TracesRepository tracesDb,
            @Autowired ErrorsRepository errorsDb,
            @Autowired MongoTemplate mongoTemplate
) {
        this.lyceesDb = lyceesDb;
        this.groupsDb = groupsDb;
        this.tracesDb = tracesDb;
        this.errorsDb = errorsDb;
        this.mongoTemplate = mongoTemplate;
    }
    MongoTemplate mongoTemplate;

    private final LyceesRepository lyceesDb;

    private final GroupsRepository groupsDb;

    private final TracesRepository tracesDb;

    private final ErrorsRepository errorsDb;

    public static final String ERRORS_COLL_NAME = "errors";
    public static final String TRACES_COLL_NAME = "traces";
    private static final String BACK = "back";
    private static final String FRONT = "front";



    private final transient Set<String> lyceesExpeENS = new HashSet<>();
    private final transient Set<String> lyceesExpeIndivisible = new HashSet<>();

    @Override
    public synchronized void load(WebServerConfig config) throws IOException, DBExceptions.ModelException {

        List<Lycee> lycees = lyceesDb.findAll();

        lyceesExpeENS.clear();
        lyceesExpeENS.addAll(lycees.stream().filter(Lycee::isExpeENS).map(Lycee::getId).toList());

        lyceesExpeIndivisible.clear();
        lyceesExpeIndivisible.addAll(lycees.stream().filter(Lycee::isExpeIndivisible).map(Lycee::getId).toList());

        init(config.getAdmins());
    }

    @Override
    public void stop() {
        /* nop */
    }


    @Override
    protected synchronized void init(Set<String> admins) throws DBExceptions.ModelException {

        removeLyceeeField();

        setProfileLoginToUserId();

        super.init(admins);

    }

    private void setProfileLoginToUserId() {
        // Define the update pipeline
        var updatePipeline = List.of(
                new Document("$set", new Document("pf.login", "$id"))
        );
        collection(USERS_COLL_NAME).updateMany(new Document(), updatePipeline);
    }

    private void removeLyceeeField() {
        collection(USERS_COLL_NAME).updateMany(new Document(), Updates.unset("lycee"));
    }



    protected void setSuperAdminUserTypes(Set<String> admins) {
        updateMany(
                USERS_COLL_NAME,
                in(ID, admins),
                set(User.USER_TYPE_FIELD, superadmin.toString())
        );
        updateMany(
                USERS_COLL_NAME,
                and(
                        not(in(ID, admins)),
                        eq(User.USER_TYPE_FIELD, superadmin.toString())
                ),
                set(User.USER_TYPE_FIELD, lyceen.toString())
        );
    }

    public void setFlagEvalENS() {
        List<User> users = getUsers();
        users.forEach(user -> {
            boolean evalENS = user.getLycees().stream().anyMatch(lyceesExpeENS::contains);
            if(evalENS != user.getConfig().isEvalENS()) {
                updateUserField(user.login(),CONFIG_EVAL_ENS_FIELD, evalENS);
            }
        });

    }

    @Override
    protected synchronized void assignDemoGroupToAllUsersAtLeastPPExceptHackersGroup() throws DBExceptions.ModelException {
        @NotNull Group demo = findOrCreateDemoGroup();
        List<User> usersThatShouldReceiveAccessToDemo =
                new ArrayList<>(
                find(
                        USERS_COLL_NAME,
                                and(
                                        or(
                                                eq(User.USER_TYPE_FIELD, pp.toString()),
                                                eq(User.USER_TYPE_FIELD, proviseur.toString()),
                                                eq(User.USER_TYPE_FIELD, psyen.toString())
                                        ), not(in(ID, demo.admins()))
                                ),
                        User.class)
                );

        //Removing this highschool hackers guys
        usersThatShouldReceiveAccessToDemo.removeIf(u -> u.getLycees().stream().anyMatch(l -> l.contains("HACK")));
        if (!usersThatShouldReceiveAccessToDemo.isEmpty()) {
            usersThatShouldReceiveAccessToDemo.forEach(user -> demo.addAdmin(user.login()));
            saveGroup(demo);
        }
    }


    private void setUserField(String login, String field, String value) {
        login = normalizeUser(login);
        // Define the filter to find the document
        Document filter = new Document(ID, login);
        // Define the update operation
        Document updateOperation = new Document("$set", new Document(field, value));
        // Perform the update operation
        updateOne(USERS_COLL_NAME, filter, updateOperation);
    }

    private void setUserField(String login, String field, Document value) {
        login = normalizeUser(login);
        // Define the filter to find the document
        Document filter = new Document(ID, login);
        // Define the update operation
        Document updateOperation = new Document("$set", new Document(field, value));
        // Perform the update operation
        updateOne(USERS_COLL_NAME,filter, updateOperation);
    }


    @Override
    public @Nullable String getUserEmailConfirmationToken(@NotNull String login) {
        return getField(USERS_COLL_NAME, normalizeUser(login), EMAIL_CONFIRMATION_TOKEN_FIELD);
    }

    @Override
    public List<Lycee> getLycees() {
        return lyceesDb.findAll();
    }

    @Override
    protected void setEmailResetToken(@NotNull String emailAddress, @NotNull String emailToken) {
        setUserField(emailAddress, EMAIL_RESET_TOKEN_FIELD, emailToken);
    }

    @Override
    public void logBackError(@NotNull String error) {
        errorsDb.insert(new ServerError(error,BACK));
    }

    @Override
    public void logFrontError(@NotNull String error) {
        errorsDb.insert(new ServerError(error,FRONT));
    }

    @Override
    public void logTrace(@NotNull String origin, @NotNull String event, @Nullable Object o) {
        tracesDb.insert(new ServerTrace(origin, event, o));
    }

    @Override
    public void resetUserPassword(@NotNull String login, @NotNull String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Credential cred = Credential.getNewCredential(password);
        setUserField(login, "cr", new Document("salt", cred.salt()).append("hash", cred.hash()));
    }

    /** gets the element with id login from collection USERS */
    @Override
    @NotNull
    public User getUser(String login) throws DBExceptions.UnknownUserException {
        return findUser(login);
    }

    @Override
    public List<User> getUsers() {
        return collection(USERS_COLL_NAME).find().map(doc -> new Gson().fromJson(doc.toJson(), User.class)).into(new ArrayList<>());
    }

    @Override
    protected List<User> getUsers(Set<String> members) {
        return collection(USERS_COLL_NAME).find(in(ID, members)).map(doc -> new Gson().fromJson(doc.toJson(), User.class)).into(new ArrayList<>());
    }

    @Override
    protected  void addToUserArrayField(String id, String field, Object val) {
        addToUserArrayField(USERS_COLL_NAME, id, field, val);
    }

    private void addToUserArrayField(String colName, String id, String field, Object val) {
        collection(colName).updateOne(new Document(ID,id), push(field, val));
    }


    @Override
    public synchronized void acceptUserCreation(@NotNull String login) throws DBExceptions.ModelException {
        User user = getUser(login);
        setCreationConfirmationNotNeeded(login);
        if (user.isAtLeastTeacher() && user.getLycees().stream().noneMatch(l -> l.contains("HACK"))) {
            @NotNull Group demo = findOrCreateDemoGroup();
            demo.addAdmin(login);
            saveGroup(demo);
        }
        updateMany(GROUPS_COLL_NAME, in(WAITING_FIELD, login), addToSet(Group.MEMBERS_FIELD, login));
        updateMany(GROUPS_COLL_NAME, in(WAITING_FIELD, login), pull(WAITING_FIELD, login));
    }

    @Override
    protected void updateUserField(String id, String key, Object value) {
        //LOGGER.info("Updating user field %s for user %s object %s".formatted(key, id, new Gson().toJson(value)));
        updateCollectionItemField(USERS_COLL_NAME, id, key, value);
    }

    private void updateCollectionItemField(String collectionName, String id, String key, Object value) {
        collection(collectionName).updateOne( new Document(ID, id),set(key, value));
    }

    /**
     *
     * @return the set of users whose lycees intersect in lycees
     */
    public Set<String> getUsersFromLycees(Set<String> lycees) {
        return lycees.stream()
                .filter(Objects::nonNull)
                .flatMap(this::getUsersFromLycee)
                .collect(Collectors.toSet());
    }

    /**
     * @return the list of users whose lycee is lycee
     */
    private Stream<String> getUsersFromLycee(@NotNull String lycee) {
        return find(USERS_COLL_NAME,
                        eq(User.LYCEES_FIELD, lycee),
                User.class
                ).stream().map(User::login);
    }


    /**
     * @return the list of all users which are not activated
     */
    @Override
    protected Collection<User> getNotActivatedUsers() {
        return find(
                USERS_COLL_NAME,
                or(
                        ne(EMAIL_CONFIRMATION_TOKEN_FIELD, null),
                        eq(User.REQUIRE_ADMIN_CONFIRMATION_FIELD, true)
                        ),
                User.class
        );
    }

    /**
     * @return the list of all users
     */
    public Collection<String> getUsersLogins() {
        return getAllFields(USERS_COLL_NAME, "id");
    }



    @Override
    public boolean existsUserWithLogin(String login) {
        login = normalizeUser(login);
        return collection(USERS_COLL_NAME).find(new Document(ID, login)).first() != null;
    }

    @Override
    protected synchronized void saveLycee(Lycee lycee) {
        upsert(LYCEES_COLL_NAME, lycee.getId(), lycee);
    }

    @Override
    protected synchronized void saveGroup(Group group) {
        upsert(GROUPS_COLL_NAME, group.getId(), group);
    }

    @Override
    protected synchronized void saveUser(User user) {
        upsert(USERS_COLL_NAME, user.login(), user);
    }

    @Override
    @NotNull
    public User findUser(@Nullable String id) throws DBExceptions.UnknownUserException {
        if(id == null) throw new DBExceptions.UnknownUserException("null");
        id = normalizeUser(id);
        Document doc = collection(USERS_COLL_NAME).find(new Document(ID, id)).first();
        if(doc == null) {
            throw new DBExceptions.UnknownUserException(id);
        }
        return new Gson().fromJson(doc.toJson(), User.class);
        /*
        try {
            User user =  usersDb.findById(id).orElse(null);
            if(user == null) {
                throw new DBExceptions.UnknownUserException(id);
            }
            return user;
        } catch (MappingException unused) {
            Document doc = usersDb.findDocById(id);
            if(doc == null) {
                throw new DBExceptions.UnknownUserException(id);
            }
            return new Gson().fromJson(doc.toJson(), User.class);
        }*/
    }

    @Override
    public synchronized void deleteGroup(@NotNull String gid) {
        deleteOne(GROUPS_COLL_NAME, eq(ID, gid));
    }


    /**
     *
     * @param lycees list of lycees
     * @return true if one of the lycees is evaluable by indivisible
     */
    @Override
    protected boolean isEvalIndivisible(Set<String> lycees) {
        return hasAny(
                LYCEES_COLL_NAME,
                and(in(ID, lycees), eq(EXPE_INDIVISIBLE, true))
        );
    }

    @Override
    protected boolean isEvalENS(Set<String> lycees) {
        return hasAny(
                LYCEES_COLL_NAME,
                and(in(ID, lycees), eq(EXPE_ENS, true))
        );
    }


    @Override
    public void deleteUser(String user) {
        user = normalizeUser(user);
        /* remove the user from the USER collection */
        deleteOne(USERS_COLL_NAME,eq(ID, user));
        forgetUserInGroups(user);
    }

    @Override
    protected synchronized void forgetUserInGroups(String user) {
        /* remove the user from the groups */
        updateMany(
                GROUPS_COLL_NAME,
                in(Group.MEMBERS_FIELD, user),
                pull(Group.MEMBERS_FIELD, user)
        );
        updateMany(
                GROUPS_COLL_NAME,
                in(Group.ADMINS_FIELD, user),
                pull(Group.ADMINS_FIELD, user)
        );
        updateMany(
                GROUPS_COLL_NAME,
                in(WAITING_FIELD, user),
                pull(Group.WAITING_FIELD, user)
        );
    }


    @Override
    public synchronized void close() {
        /* nop */
    }


    public void exportTracesToFile(String s) throws IOException {
        LOGGER.info("Export de straces ver sun fichier local");
        Serialisation.toJsonFile(s, tracesDb.findAll().stream()
                .sorted(Comparator.comparing(ServerTrace::timestamp))
                .toList(), true);
    }

    @Override
    public List<ServerTrace> getTraces() {
        return tracesDb.findAll();
    }

    public void exportErrorsToFile(String s, boolean emptyCollection) throws IOException {
        Serialisation.toJsonFile(s, errorsDb.findAll()
                .stream().sorted(Comparator.comparing(ServerError::timestamp))
                .toList(), true);
        if(emptyCollection) {
            clear(ERRORS_COLL_NAME);
        }
    }

    public List<Lycee> getExpeENSLycees() {
        return collection(LYCEES_COLL_NAME)
                .find(eq(EXPE_ENS, true))
                .map(doc -> new Gson().fromJson(doc.toJson(), Lycee.class))
                .into(new ArrayList<>());
    }

    public List<User> getExpeENSUsers() {
        Set<String> lycees = getExpeENSLycees().stream().map(Lycee::getId).collect(Collectors.toSet());
        return getUsers().stream().filter(u -> u.getLycees().stream().anyMatch(lycees::contains)).toList();
    }

    public void saveUsers(List<User> users) {
        // saves all users in the db
        users.forEach(this::saveUser);
    }

    public synchronized void reinitTreatmentGroupRegistrationCodes() {
        //get all groups from db whose expeENSGroupe is T
        List<Group> testGroups = collection(GROUPS_COLL_NAME).find(eq(Group.EXPE_ENS_GROUPE_FIELD, "T"))
                .map(doc -> new Gson().fromJson(doc.toJson(), Group.class))
                .into(new ArrayList<>());
        testGroups.forEach(group -> {
            group.resetRegistrationCode();
            saveGroup(group);
        });
    }

    private MongoCollection<Document> collection(String colName) {
        return mongoTemplate.getCollection(colName);
    }
    void updateMany(String colName, Bson in, Bson set) {
        collection(colName).updateMany(in, set);
    }

    <T> Collection<T> find(String colName, Bson and, Class<T> t) {
        return collection(colName).find(and)
                .map(document -> new Gson().fromJson(document.toJson(), t))
                .into(new ArrayList<>());
    }

    Collection<String> getAllFields(String colName, String field) {
        return collection(colName).find()
                .map(doc -> doc.getString(field))
                .into(new ArrayList<>());
    }

    void updateOne(String colName, Document filter, Document updateOperation) {
        collection(colName).updateOne(filter, updateOperation);
    }

    public void deleteOne(String colName, Bson eq) {
        collection(colName).deleteOne(eq);
    }

    public boolean hasAny(String colName, Bson and) {
        return collection(colName).find(and).first() != null;
    }

    public void deleteMany(String colName, Document document) {
        collection(colName).deleteMany(document);
    }

    public void insert(String colName, List<Document> collect) {
        mongoTemplate.insert(collect, colName);
    }

    public void clear(String colName) {
        deleteMany(colName, new Document());
    }

    public String getField(String colName, String login, String field) {
        Document doc =  collection(colName).find(eq(ID, login)).first();
        if(doc == null) return null;
        return doc.getString(field);
    }


    public <T> void upsert(String colName, String id, T item) {
        collection(colName).replaceOne(
                eq(ID, id),
                Document.parse(new Gson().toJson(item)),
                new com.mongodb.client.model.ReplaceOptions().upsert(true)
        );
    }

    @Override
    protected @NotNull Group findGroup(String id) throws DBExceptions.UnknownGroupException {
        if (id == null || id.isEmpty()) {
            throw new DBExceptions.UnknownGroupException();
        }
        Group group = groupsDb.findById(id).orElse(null);
        if (group == null) {
            throw new DBExceptions.UnknownGroupException();
        }
        return group;
    }

    protected @NotNull Lycee findLycee(String id) throws DBExceptions.UnknownGroupException {
        if (id == null || id.isEmpty()) {
            throw new DBExceptions.UnknownGroupException();
        }
        Lycee ly = lyceesDb.findById(id).orElse(null);
        if (ly == null) {
            throw new DBExceptions.UnknownGroupException();
        }
        return ly;
    }

    @Override
    public List<Group> getGroupsOfLycee(String uai) {
        return collection(GROUPS_COLL_NAME).find(
                        in(Group.LYCEES_FIELD, uai)
        ).map(document -> new Gson().fromJson(document.toJson(), Group.class)).into(new ArrayList<>());
    }

    @Override
    public List<Group> getGroupsOfLycee(Collection<String> uai) {
        return collection(GROUPS_COLL_NAME).find(
                in(Group.LYCEES_FIELD, uai)
        ).map(document -> new Gson().fromJson(document.toJson(), Group.class)).into(new ArrayList<>());
    }

    @Override
    public List<Group> getGroupsWithAdmin(String login) {
        return collection(GROUPS_COLL_NAME).find(
                in(ADMINS_FIELD, login)
        ).map(document -> new Gson().fromJson(document.toJson(), Group.class)).into(new ArrayList<>());
    }


    @Override
    public boolean isAdminOfGroup(String login, String groupId) throws DBExceptions.ModelException {
        if (isSuperadmin(login)) return true;
        @NotNull Group group = findGroup(groupId);
        return group.hasAdmin(login);
    }

    @Override
    public boolean isAdminOfUser(String login, String user) throws DBExceptions.UnknownUserException {
        login = normalizeUser(login);
        if (isSuperadmin(login)) return true;
        //finds groups whose members contain user and admins contain login
        return existsGroupWithSpecifiedUserAndAdmin(login, user);
    }


    @Override
    public void addGroupAdmin(String groupId, String groupAdminLogin, boolean addAdmin) throws DBExceptions.UnknownGroupException {
        groupAdminLogin = normalizeUser(groupAdminLogin);
        @NotNull Group group = findGroup(groupId);
        if (addAdmin) {
            group.addAdmin(groupAdminLogin);
        } else {
            group.removeAdmin(groupAdminLogin);
        }
        saveGroup(group);
    }

    private boolean existsGroupWithSpecifiedUserAndAdmin(String admin, String user) {
        return collection(GROUPS_COLL_NAME).find(
                and(
                        in(Group.MEMBERS_FIELD, normalizeUser(user)),
                        in(Group.ADMINS_FIELD, normalizeUser(admin))
                )
        ).first() != null;
    }

    @Override
    public boolean isMemberOfGroup(String login, String groupId) throws DBExceptions.ModelException {
        @NotNull Group group = findGroup(groupId);
        return group.hasMember(login);
    }

    @Override
    public void validateCode(UserTypes type, String code) throws DBExceptions.UserInputException.WrongAccessCodeException {
        if (code == null || code.isEmpty()) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }
        if (type == lyceen) {
            findGroupWithAccessCode(code);
        } else {
            findGroupWithAdminAccessCode(code);
        }
    }

    @Override
    public synchronized void createNewUser(
            @NotNull CreateAccountService.CreateAccountRequest data,
            boolean confirmEmailOnAccountCreation) throws DBExceptions.ModelException, NoSuchAlgorithmException, InvalidKeySpecException, DBExceptions.UserInputException.InvalidPasswordException, DBExceptions.UserInputException.WrongAccessCodeException, DBExceptions.UserInputException.InvalidGroupTokenException, DBExceptions.UserInputException.UserAlreadyExistsException, DBExceptions.UserInputException.UnauthorizedLoginException {
        final int maxLength = 64;

        if (data.password().length() > maxLength
                || data.login().length() > maxLength
                || data.accesGroupe() != null && data.accesGroupe().length() > maxLength
                || data.cguVersion().length() > maxLength
        ) throw new DBExceptions.UserInputException.InvalidPasswordException();

        if (!data.login().matches(REGEXP_LOGIN_PATTERN)) {
            throw new DBExceptions.UserInputException.UnauthorizedLoginException();
        }


        String code = data.accesGroupe();
        if (code == null || code.isEmpty()) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }

        final @NotNull Group group = data.type() == lyceen ? findGroupWithAccessCode(code) : findGroupWithAdminAccessCode(code);

        String login = normalizeUser(data.login());
        //remove this user from all groups
        if (existsUserWithLogin(login)) {
            throw new DBExceptions.UserInputException.UserAlreadyExistsException();
        }
        forgetUserInGroups(login);

        if (data.type() == lyceen) {
            group.addMember(login);
            saveGroup(group);
            //no need for email confirmation for lyceens
            confirmEmailOnAccountCreation = false;
        } else {
            group.addAdmin(login);
            saveGroup(group);
            Group demo = findOrCreateDemoGroup();
            demo.addAdmin(login);
            saveGroup(demo);
        }

        Credential cred = Credential.getNewCredential(data.password());
        Profile pf = Profile.getNewProfile(login, data.nom(), data.prenom());

        String emailToken = confirmEmailOnAccountCreation ? RandomStringUtils.random(64, true, false) : null;

        final boolean requireAdminConfirmationOnAccountCreation = false;

        User user = User.createUser(
                normalizeUser(data.login()),
                cred,
                pf,
                data.cguVersion(),
                data.type(),
                group.getLycee(),
                confirmEmailOnAccountCreation,
                requireAdminConfirmationOnAccountCreation,
                emailToken
        );

        user.setEvalENS(user.getLycees().stream().anyMatch(lyceesExpeENS::contains));
        user.setEvalIndivisible(user.getLycees().stream().anyMatch(lyceesExpeIndivisible::contains));

        saveUser(user);

    }

    private Group findGroupWithAccessCode(String code) throws DBExceptions.UserInputException.WrongAccessCodeException {
        Document group = collection(GROUPS_COLL_NAME).find(eq(Group.REGISTRATION_TOKEN_FIELD, code)).first();
        if (group == null) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }
        return new Gson().fromJson(group.toJson(), Group.class);
    }

    private Group findGroupWithAdminAccessCode(String code) throws DBExceptions.UserInputException.WrongAccessCodeException {
        Document group = collection(GROUPS_COLL_NAME).find(eq(Group.ADMIN_REGISTRATION_TOKEN_FIELD, code)).first();
        if (group == null) {
            throw new DBExceptions.UserInputException.WrongAccessCodeException();
        }
        return new Gson().fromJson(group.toJson(), Group.class);
    }


    @Override
    public synchronized @NotNull Group createNewGroup(@NotNull String lycee, @NotNull String sid) throws DBExceptions.ModelException {
        final Group group;
        Lycee lyc = findLycee(lycee);
        LOGGER.info("Création d'un nouveau groupe %s dans le lycée %s".formatted(sid, lyc));
        if (sid.isEmpty()) {
            throw new DBExceptions.EmptyGroupIdException();
        }
        String gid = Normalizer.normalize(lyceeToGroupId(lyc.getId(), sid).toLowerCase(), Normalizer.Form.NFKD);
        try {
            findGroup(gid);
            throw new RuntimeException("Already existing group");
        } catch (DBExceptions.UnknownGroupException ignored) {
            group = Group.getNewGroup(lycee, sid);
            saveGroup(group);
            return group;
        }
    }

    @Override
    public synchronized void addOrRemoveMember(String groupId, String memberLogin, boolean addMember) {

        try {
            memberLogin = normalizeUser(memberLogin);
            User user = getUser(memberLogin);
            Group group = findGroup(groupId);
            forgetUserInGroups(memberLogin);
            if (addMember) {
                //at this point the token is the right one
                group.addMember(memberLogin);
                setCreationConfirmationNotNeeded(user.login());
            }
            saveGroup(group);
        } catch (DBExceptions.UnknownGroupException |
                 DBExceptions.UnknownUserException ignored) {
            //nothing to do
        }
    }

    @Override
    public ProfileDb getGroupMemberProfile(String grpId, String memberLogin) throws DBExceptions.ModelException {
        @NotNull Group group = findGroup(grpId);
        if (!group.hasMember(memberLogin)) {
            return null;
        }
        User p = getUser(memberLogin);
        return p.pf().toDTO();
    }

    @Override
    public GetGroupDetailsService.GroupDetails getGroupDetails(String groupId) throws DBExceptions.ModelException {
        @NotNull Group group = findGroup(groupId);
        List<GetGroupDetailsService.StudentDetails> students = getMemberDetails(group.members());

        return new GetGroupDetailsService.GroupDetails(
                group.getName(),
                group.getId(),
                group.isOpened(),
                group.token(),
                students,
                group.admins(),
                group.membersWaiting().stream().toList(),
                group.getExpeENSGroupe()
        );
    }

    @Override
    public AdminInfosDTO getAdminInfos(String login) throws DBExceptions.UnknownUserException, DBExceptions.UnknownGroupException {
        //the precision of the data depends on the privilege
        login = normalizeUser(login);
        User user = getUser(login);
        Role role = user.getRole();
        int profileCompleteness = user.getProfileCompleteness();

        user.getConfig().setEvalENS(isEvalENS(user.getLycees()));
        user.getConfig().setEvalIndivisible(isEvalIndivisible(user.getLycees()));

        AdminInfosDTO result = getAdminInfos(
                login,
                role,
                user.getUserType(),
                user.getTempType(),
                user.getLycees(),
                profileCompleteness,
                user.getConfig()
        );

        //ajout des users
        if (result.role() == ADMIN) {
            //admin has all infos on users, including those not in groups
            result.users().addAll(getUsersLogins());

            /* retrieves the list of users whose flag isActivated is false, using a filter */
            Collection<User> noActivetedUsers = new ArrayList<>(getNotActivatedUsers());
            result.acccountCreationModeration().addAll(
                    noActivetedUsers.stream()
                            .map(User::login)
                            .filter(s -> result.users().contains(normalizeUser(s)))
                            .toList()
            );
            result.roles().putAll(
                    noActivetedUsers.stream()
                            .filter(e -> result.users().contains(normalizeUser(e.login())))
                            .filter(e -> e.getUserType() != null)
                            .collect(
                                    Collectors.toMap(
                                            User::login,
                                            User::getUserType
                                    )
                            )
            );
        } else if (user.isProviseur()) {
            //admin has all infos on users, including those not in groups
            result.users().addAll(
                    getUsersFromLycees(user.getLycees())
            );
        }
        result.sort();
        return result;
    }

    private AdminInfosDTO getAdminInfos(
            String login,
            Role role,
            UserTypes type, UserTypes appType, Set<String> lyceesUser, int profileCompleteness, UserConfig config) throws DBExceptions.UnknownGroupException {
        //on liste les lycées de l'utilisateur
        List<Lycee> lyceesUserItems = new ArrayList<>();
        if(role == User.Role.ADMIN) {
            lyceesUserItems.addAll(getLycees());
        } else {
            /*
            for (String s : lyceesUser) {
                Lycee lycee = findLycee(s);
                lyceesUserItems.add(lycee);
            }*/
        }

        AdminInfosDTO result = new AdminInfosDTO(
                role,
                type,
                appType,
                lyceesUserItems,
                WebServer.config().isConfirmEmailOnAccountCreation(),
                profileCompleteness,
                config
        );


        if (role == User.Role.ADMIN) {
            result.groups().clear();
            result.groups().addAll(getAllGroups().stream().map(Group::toDTO).toList());
            return result;
        } else if (role == User.Role.TEACHER) {
            //find groups with admin login
            val groups = getGroupsWithAdmin(login);
            result.groups().addAll(groups.stream().map(Group::toDTO).toList());

            List<GroupDTO> groupsOfLycee = getGroupsOfLycee(lyceesUser).stream().map(Group::toDTO).toList();
            result.openGroups().addAll(
                    groupsOfLycee.stream()
                            .filter(g -> !g.admins().contains(login))
                            .toList()
            );
            return result;
        } else {
            Group group = getGroup(login);
            if (group != null) {
                result.groups().add(group.miniGroup().toDTO());
            }
            return result;
        }
    }


    private @Nullable Group getGroup(String login) {
        return collection(GROUPS_COLL_NAME).find(
                in(Group.MEMBERS_FIELD, login)
        ).map(document -> new Gson().fromJson(document.toJson(), Group.class)).first();
    }

    @Override
    public void exportUsersToFile(String filename, boolean expeENS, boolean anonymize) throws IOException {
        LOGGER.info("Export des utilisateurs vers un fichier local.");
        List<User> users = new ArrayList<>(getUsers());
        users.forEach(User::removeCredentials);
        if(anonymize) {
            users.forEach(User::anonymize);
        }
        if(expeENS) {
            users.removeIf(user -> user.getLycees().stream().noneMatch(lyceesExpeENS::contains));
        }
        Serialisation.toJsonFile(filename, users, true);
    }

    @Override
    public Groups getGroupsAndLycees() {
        Groups groups = new Groups();
        groups.loadGroups(groupsDb.findAll());
        groups.loadLycees(lyceesDb.findAll());
        return groups;
    }

    @Override
    public boolean hasRightToAddAdmin(String login, String groupId, String groupAdminLogin, boolean addAdmin) throws DBExceptions.UnknownGroupException {
        login = normalizeUser(login);
        groupAdminLogin = normalizeUser(groupAdminLogin);

        if(isSuperadmin(login)) return true;

        if(!Objects.equals(login, groupAdminLogin))  return false;

        if(!addAdmin) return false;

        Group group = findGroup(groupId);
        String lycee = group.getLycee();
        return isAdminOfLycee(login, lycee);
    }

    private boolean isAdminOfLycee(String login, String lycee) {
        String finalLogin = normalizeUser(login);
        List<Group> groupsOfLycee = getGroupsOfLycee(lycee).stream().toList();
        return groupsOfLycee.stream().anyMatch(g -> g.hasAdmin(finalLogin));
    }

    public List<Group> getAllGroups() {
           return groupsDb.findAll();
    }


}
