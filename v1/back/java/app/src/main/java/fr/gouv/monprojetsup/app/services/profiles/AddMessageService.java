package fr.gouv.monprojetsup.app.services.profiles;

import fr.gouv.monprojetsup.app.db.DB;
import fr.gouv.monprojetsup.app.server.Helpers;
import fr.gouv.monprojetsup.app.server.MyAppService;
import fr.gouv.monprojetsup.app.server.WebServer;
import fr.gouv.monprojetsup.app.tools.Sanitizer;
import fr.gouv.monprojetsup.app.server.ResponseHeader;
import org.jetbrains.annotations.NotNull;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AddMessageService extends MyAppService<AddMessageService.Request, AddMessageService.Response> {

    public AddMessageService() {
        super(Request.class);
    }

    public record Request(
            @NotNull String login,
            @NotNull String token,
            String group,
            @NotNull String student,

            @NotNull String topic,
            String comment
    ) {
    }

    private static final PolicyFactory policyCommentBuilder = new HtmlPolicyBuilder()
            .allowAttributes("src").onElements("img")
            .allowAttributes("href").onElements("a")
            // Allow some URLs through.
            .allowStandardUrlProtocols()
            .allowElements(
                    "a", "label", "h1", "h2", "h3", "h4", "h5", "h6",
                    "p", "i", "b", "u", "strong", "em", "small", "big", "pre", "code",
                    "cite", "samp", "sub", "sup", "strike", "center", "blockquote",
                    "hr", "br", "col", "font", "span", "div", "img",
                    "ul", "ol", "li", "dd", "dt", "dl", "tbody", "thead", "tfoot",
                    "table", "td", "th", "tr", "colgroup", "fieldset", "legend"
            )
            .toFactory();
    private static final PolicyFactory policyTopicBuilder = new HtmlPolicyBuilder().toFactory();

    public record Response(
            ResponseHeader header
    ) {
        public Response() { this(new ResponseHeader()); }
    }

    @Override
    protected @NotNull Response handleRequest(@NotNull Request req) throws Exception {
        DB.authenticator.tokenAuthenticate(req.login, req.token);

        if(!Objects.equals(req.login,req.student)) {
            boolean isGroupAdmin = WebServer.db().isAdminOfGroup(req.login(), req.group);
            if (!isGroupAdmin) {
                throw new RuntimeException("opération réservée aux admins de groupe");
            }
            boolean isMemberOfGroup = WebServer.db().isMemberOfGroup(req.student, req.group);
            if (!isMemberOfGroup) {
                throw new RuntimeException("le membre n'appartient pas au groupe");
            }
        }

        Helpers.LOGGER.info("Changing comment of student '" + req.student() + "' for topic '" + req.topic + "'");

        WebServer.db().addMessage(
                Sanitizer.sanitize(req.login),
                Sanitizer.sanitize(req.student),
                Sanitizer.sanitize(req.topic),
                policyCommentBuilder.sanitize(req.comment)
        );

        return new Response();
    }

}
