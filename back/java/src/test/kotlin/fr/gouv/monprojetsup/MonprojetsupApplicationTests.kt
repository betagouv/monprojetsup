package fr.gouv.monprojetsup

import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Slf4j
class MonprojetsupApplicationTests {

	@Test
	fun contextLoads() {
		fr.gouv.monprojetsup.web.server.WebServer.LOGGER.info("Loading data for WebServer...")
		ServerData.load()
	}

}
