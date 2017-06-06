package de.predic8;

import de.predic8.routes.ArchiverRoutes;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Calendar;

public class ArchiverRouteTest extends CamelTestSupport {

    private Calendar cal;
    private String year, month;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new ArchiverRoutes();
    }

    public File[] finder(String dir) {
        File _dir = new File(dir);
        return _dir.listFiles((dir1, name) -> name.endsWith("_test.txt"));
    }

    @Before
    public void setUp() throws Exception {
        deleteDirectory("document-archive/archive");
        deleteDirectory("document-archive/in");
        deleteDirectory("document-archive/logs");
        deleteDirectory("document-archive/notify");

        cal = Calendar.getInstance();
        year = Integer.toString(cal.get(Calendar.YEAR));
        String currentMonth = Integer.toString(cal.get(Calendar.MONTH) + 1);

        month = ((cal.get(Calendar.MONTH) + 1) < 10) ?
                String.format("0%s", currentMonth) : currentMonth;

        super.setUp();
    }

    @Test
    public void testArchiveFile() throws Exception {
        template.sendBodyAndHeader("file://document-archive/in",
                "Testing", Exchange.FILE_NAME, "test.txt");

        Thread.sleep(3000);

        File dir = new File(String.format("document-archive/archive/%s/%s", year, month));
        File target = dir.listFiles((dir1, name) -> name.endsWith("_test.txt"))[0];

        assertTrue("File not moved", target.exists());

        String content = context.getTypeConverter().convertTo(String.class, target);
        assertEquals("Testing", content);
    }
}
