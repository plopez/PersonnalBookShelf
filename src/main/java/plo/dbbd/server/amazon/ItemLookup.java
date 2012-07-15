package plo.dbbd.server.amazon;

import com.fasterxml.jackson.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import plo.dbbd.server.model.Book;
import plo.dbbd.server.model.ItemLookupResponse;
import plo.dbbd.server.model.Serie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ItemLookup {

    // French endpoint
    private static final String ENDPOINT = "ecs.amazonaws.fr";

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemLookup.class);

    private final static ItemLookup singleton = new ItemLookup();

    private ItemLookup() {
        super();
    }

    public static ItemLookup getInstance() {
        return singleton;
    }

    public Book lookupItemByIsbn(String isbn) {
        SignedRequestsHelper helper = createAuthenticatedHelper();

        Map<String, String> params = fillSearchParameterIsbn(isbn);

        String requestUrl = helper.sign(params);

        String xmlBackFromAmazon = readXmlFromUrl(requestUrl);

        LOGGER.debug(xmlBackFromAmazon);

        ItemLookupResponse itemLookUpResponse = mapXml(xmlBackFromAmazon);

        if (itemLookUpResponse != null) {
            return itemLookUpResponse.Items.books.get(0);
        }

        return null;
    }

    public Collection<Book> lookupCollectionByTitleAndPublisher(String title, String publisher) {
        SignedRequestsHelper helper = createAuthenticatedHelper();

        Map<String, String> params = fillPowerSearchParameters(title, publisher);

        String requestUrl = helper.sign(params);

        String xmlBackFromAmazon = readXmlFromUrl(requestUrl);

        LOGGER.debug(xmlBackFromAmazon);

        ItemLookupResponse itemLookUpResponse = mapXml(xmlBackFromAmazon);

        if (itemLookUpResponse != null) {
            return itemLookUpResponse.Items.books;
        }

        return null;
    }

    private static Map<String, String> fillSearchParameterIsbn(String isbn) {
        Map<String, String> params = fillCommonSearchParameters();
        params.put("Operation", "ItemLookup");

        params.put("ItemId", isbn);
        // UPN pour les disques
        params.put("IdType", "ISBN");

        return params;
    }

    // TODO refactor
    private static Map<String, String> fillPowerSearchParameters(String title, String publisher) {
        Map<String, String> params = fillCommonSearchParameters();
        StringBuffer powerCriteria = new StringBuffer();
//        powerCriteria.append("author:");
//        powerCriteria.append(author);
//        powerCriteria.append(" and ");
        powerCriteria.append("title-begins:");
        powerCriteria.append(title.substring(0, title.indexOf(",") - 1));
        powerCriteria.append(" and ");
        powerCriteria.append("publisher:");
        powerCriteria.append(publisher);


        params.put("Power", powerCriteria.toString());
        params.put("Operation", "ItemSearch");

        return params;
    }

    // TODO refactor
    private static Map<String, String> fillTitleParameters(String title) {
        Map<String, String> params = fillCommonSearchParameters();
        StringBuffer powerCriteria = new StringBuffer();
        powerCriteria.append("title:");
        powerCriteria.append(title+"*");

        params.put("Power", powerCriteria.toString());
        params.put("Operation", "ItemSearch");

        return params;
    }

    private static Map<String, String> fillCommonSearchParameters() {
        // Fill search parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        // params.put("Version", "2009-03-31");
        params.put("ResponseGroup", "Medium");
        params.put("SearchIndex", "Books");
        params.put("AssociateTag", "");
        return params;
    }

    private static SignedRequestsHelper createAuthenticatedHelper() {
        Properties credentials = new Properties();
        SignedRequestsHelper helper = null;
        try {
            credentials.load(ItemLookup.class.getResourceAsStream("/AwsCredentials.properties"));

            String AWS_ACCESS_KEY_ID = credentials.getProperty("accessKey");
            String AWS_SECRET_KEY = credentials.getProperty("secretKey");

            // Set up the signed requests helper
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return helper;
    }

    // Generate object from Xml using Jackson
    private ItemLookupResponse mapXml(String xml) {
        XmlMapper mapper = new XmlMapper();
        try {
            ItemLookupResponse itemLookUpResponse = mapper.readValue(xml, ItemLookupResponse.class);
            return itemLookUpResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Use an url to read from and return a String
    private String readXmlFromUrl(String urlAsString) {
        StringBuffer xmlAsString = new StringBuffer();
        URL url = null;
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            url = new URL(urlAsString);
            inputStreamReader = new InputStreamReader(url.openStream());
            bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                xmlAsString.append(inputLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return xmlAsString.toString();
    }

    public Serie lookupSerieByTitle(String title) {
        SignedRequestsHelper helper = createAuthenticatedHelper();

        Map<String, String> params = fillTitleParameters(title);

        String requestUrl = helper.sign(params);

        LOGGER.debug(requestUrl);

        String xmlBackFromAmazon = readXmlFromUrl(requestUrl);

        LOGGER.debug(xmlBackFromAmazon);

        ItemLookupResponse itemLookUpResponse = mapXml(xmlBackFromAmazon);

        if (itemLookUpResponse != null) {
            int i = 0;
            Book book;
            do {
                book = itemLookUpResponse.Items.books.get(i);
                i++;
            } while (book.ItemAttributes.Title.indexOf(",") == 0);
            return new Serie(book);
        }

        return null;
    }

    public Collection<Book> lookupAllBooksInSerieByTitle(String title) {
        SignedRequestsHelper helper = createAuthenticatedHelper();

        Map<String, String> params = fillTitleParameters(title);

        String requestUrl = helper.sign(params);

        LOGGER.debug(requestUrl);

        String xmlBackFromAmazon = readXmlFromUrl(requestUrl);

        LOGGER.debug(xmlBackFromAmazon);

        ItemLookupResponse itemLookUpResponse = mapXml(xmlBackFromAmazon);

        if (itemLookUpResponse != null) {
            return itemLookUpResponse.Items.books;
        }

        return null;
    }
}
