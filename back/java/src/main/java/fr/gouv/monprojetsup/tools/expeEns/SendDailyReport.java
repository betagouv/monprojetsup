package fr.gouv.monprojetsup.tools.expeEns;

import fr.gouv.monprojetsup.web.db.DBExceptions;
import fr.gouv.monprojetsup.web.mail.MailSender;
import fr.gouv.monprojetsup.web.server.WebServerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"fr.gouv.monprojetsup"})
public class SendDailyReport {

    static String filePath = "suivi.txt";

    public static void main(String[] args) throws IOException, DBExceptions.UnknownGroupException, DBExceptions.EmptyGroupIdException, InterruptedException {

        WebServerConfig config = WebServerConfig.load();

        try(InputStreamReader is = new InputStreamReader(Files.newInputStream(Path.of(filePath)), StandardCharsets.UTF_8)) {
            //stores all the data available in is into a string
            StringWriter sw = new StringWriter();
            is.transferTo(sw);

            Thread thread = MailSender.send(
                    config.getEmailConfig(),
                    List.of("hugo.gimbert@gmail.com","louis.gleyo@gmail.com"),
                    "suivi MonProjetSup",
                    sw.toString().replace("\n", "<br/>")
            );
            thread.join();
        }

    }
}
