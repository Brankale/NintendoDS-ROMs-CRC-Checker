package com.github.brankale.models;

import com.github.brankale.models.dat.DatEntry;
import com.github.brankale.models.dat.Rom;
import com.github.brankale.models.dat.RomStatus;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class DatFile {

    private final TreeMap<Long, DatEntry> entries;
    private final TreeSet<String> extensions;

    public DatFile(File dat) throws ParserConfigurationException, IOException, SAXException {
        entries = new TreeMap<>();
        extensions = new TreeSet<>();

        Document doc = getDocument(dat);
        NodeList datEntries = doc.getElementsByTagName("game");
        for (int i = 0; i < datEntries.getLength(); ++i) {
            DatEntry datEntry = parseDatEntry(datEntries.item(i));
            entries.put(datEntry.getRom().crc(), datEntry);
            extensions.add(datEntry.getRom().getExtension());
        }
    }

    private Document getDocument(File dat) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // skip xml validation
        dbf.setValidating(false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(dat);
    }

    private DatEntry parseDatEntry(Node datEntry) {
        String name = ((Element) datEntry).getAttribute("name");
        Rom rom = parseRom(((Element) datEntry).getElementsByTagName("rom").item(0));
        return new DatEntry(name, rom);
    }

    private Rom parseRom(Node rom) {
        String name = ((Element) rom).getAttribute("name");
        String crc = ((Element) rom).getAttribute("crc");
        String sha1 = ((Element) rom).getAttribute("sha1");
        String sha256 = ((Element) rom).getAttribute("sha256");
        String size = ((Element) rom).getAttribute("size");
        String serial = ((Element) rom).getAttribute("serial");
        String status = ((Element) rom).getAttribute("status");

        Rom.Builder builder = new Rom.Builder();

        builder.setName(StringEscapeUtils.unescapeXml(name));
        builder.setSize(Long.parseLong(size));
        builder.setCrc(Long.decode("0x" + crc));
        if (!sha1.isEmpty()) builder.setSha1(sha1);
        if (!sha256.isEmpty()) builder.setSha256(sha256);
        if (!serial.isEmpty()) builder.setSerial(serial);
        builder.setStatus(RomStatus.parse(status));

        return builder.build();
    }

    public boolean validateCrc(long crc) {
        return entries.containsKey(crc);
    }

    public DatEntry getDatEntryByCrc(long crc) {
        return entries.get(crc);
    }

    public List<String> getAcceptedExtensions() {
        return new ArrayList<>(extensions);
    }

}
