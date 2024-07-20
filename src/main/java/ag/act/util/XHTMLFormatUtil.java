package ag.act.util;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class XHTMLFormatUtil {

    private static final String CONTENTS_TAG_ID = "contents";

    public static String convertHtmlToXhtml(String htmlString) {
        final Document document = getDocument(htmlString);
        return document.html();
    }

    public static String getHtmlById(String htmlString) {
        return getHtmlContentByTagId(htmlString, CONTENTS_TAG_ID);
    }

    public static String getHtmlContentByTagId(String htmlString, String tagId) {
        Document doc = getDocument(htmlString);
        return doc.getElementById(tagId).html();
    }

    @NotNull
    private static Document getDocument(String htmlString) {
        final Document document = Jsoup.parse(htmlString);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
        document.outputSettings().charset("UTF-8");
        return document;
    }
}