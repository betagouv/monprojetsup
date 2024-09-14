package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.etl.MpsDataFiles
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import fr.gouv.monprojetsup.data.etl.loaders.DataSources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(classes = [MpsDataPort::class, MpsDataFiles::class, DataSources::class] )
open class DataPortTest {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

}