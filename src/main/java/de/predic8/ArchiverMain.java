package de.predic8;

import de.predic8.routes.ArchiverRoutes;
import org.apache.camel.main.Main;

public class ArchiverMain {

    private Main main;

    public void boot() throws Exception {
        main = new Main();
        main.addRouteBuilder(new ArchiverRoutes());
        main.run();
    }
}
