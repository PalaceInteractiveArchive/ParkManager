package us.mcmagic.magicassistant.show.schedule;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import us.mcmagic.magicassistant.MagicAssistant;
import us.mcmagic.magicassistant.utils.FileUtil;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ShowScheduleSpreadsheet {

    public static final String GOOGLE_ACCOUNT_USERNAME = "mcmagicdev@gmail.com";
    public static final String GOOGLE_ACCOUNT_PASSWORD = "NqcL39EPdgcZhess";

    public static final String OAUTH2_CLIENT_ID = "855346591854-e00pj2orf8pdcg9fn2mr61jkqu0jumub.apps.googleusercontent.com";
    public static final String OAUTH2_CLIENT_SECRET = "z9Svr1I8T6CssCP5Kuj-xOIE";

    public static final String SPREADSHEET_SERVICE_URL = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
    public static final String SPREADSHEET_URL = "https://docs.google.com/spreadsheets/d/10TSt2OhCQGb8Wh_uUn-PLZExmuLPe6ROKXCaywN_Ai1U";

    private SpreadsheetService service = new SpreadsheetService("spreadsheetservice");

    public ShowScheduleSpreadsheet() {
        try {
            HttpTransport httpTransport = new NetHttpTransport();
            JacksonFactory jsonFactory = new JacksonFactory();
            String[] SCOPESArray = {"https://spreadsheets.google.com/freeds", "https://docs.google.com/feeds"};
            final List SCOPES = Arrays.asList(SCOPESArray);
            GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory).setServiceAccountId(GOOGLE_ACCOUNT_USERNAME).setServiceAccountScopes(SCOPES).setServiceAccountPrivateKeyFromP12File(FileUtil.SERVICE_ACCOUNT_PKCS12_FILE).build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private SpreadsheetEntry getSpreadsheet(String name) {
        SpreadsheetEntry sheetEntry;
        try {
            URL spreadSheetFeedUrl = new URL(SPREADSHEET_SERVICE_URL);
            SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(spreadSheetFeedUrl);
            spreadsheetQuery.setTitleQuery(name);
            spreadsheetQuery.setTitleExact(true);
            SpreadsheetFeed spreadsheet = service.getFeed(spreadsheetQuery, SpreadsheetFeed.class);
            if (spreadsheet.getEntries() != null && spreadsheet.getEntries().size() == 1) {
                return spreadsheet.getEntries().get(0);
            }
        } catch (IOException | ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WorksheetEntry getWorksheet(String sheetName, String worksheetName) {
        try {
            SpreadsheetEntry spreadsheet = getSpreadsheet(sheetName);
            if (spreadsheet != null) {
                WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
                List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
                for (WorksheetEntry worksheetEntry : worksheets) {
                    String title = worksheetEntry.getTitle().getPlainText();
                    if (title.equals(worksheetName)) {
                        return worksheetEntry;
                    }
                }
            }
        } catch (IOException | ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> getRawData(ListEntry row) {
        Map<String, Object> rawValues = new HashMap<>();
        for (String tag : row.getCustomElements().getTags()) {
            Object value = row.getCustomElements().getValue(tag);
            rawValues.put(tag, value);
        }
        return rawValues;
    }

    public List<Map<String, Object>> getTimes() {
        //TODO: Populate times list.
        WorksheetEntry worksheet = getWorksheet("Show Schedule", "Show Schedule");
        return null;
    }
}