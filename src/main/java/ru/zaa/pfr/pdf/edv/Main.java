package ru.zaa.pfr.pdf.edv;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static PdfReader inFile1;
    private static PdfCopy writer1;

    public static void main(String[] args) throws IOException, DocumentException {

        String inputFile1;
        String inputFile2;
        String outputFile;

        if (args.length == 3) {
            inputFile1 = args[0];
            inputFile2 = args[1];
            outputFile = args[2];
        } else if (args.length == 2) {
            inputFile1 = args[0];
            inputFile2 = args[1];
            outputFile = "c:\\output.pdf";
        } else return;

        inFile1 = new PdfReader(inputFile1);

        PdfReader inFile2 = new PdfReader(inputFile2);

        Map<KeyMap, PdfImportedPage> edv = getMapNpd(inFile1);
        Map<KeyMap, PdfImportedPage> nsu = getMapNpd(inFile2);


        //Формирование отсортированного списка
        List<KeyMap> list = edv.keySet().stream().sorted().collect(Collectors.toList());


        /*
         * Формирование выходного файла из двух Map<?,?>
         */
//-------------------------------------------------
        Document document1 = new Document(inFile1.getPageSizeWithRotation(1));

        writer1 = new PdfCopy(document1, new FileOutputStream(outputFile));
        document1.open();

        boolean flag = true;
        for (KeyMap key : list) {
            flag = isFlag(edv, flag, key);
            flag = isFlag(nsu, flag, key);
        }

        document1.close();
        writer1.close();
//-------------------------------------------------


        inFile1.close();
        inFile2.close();

    }

    private static boolean isFlag(Map<KeyMap, PdfImportedPage> edv, boolean flag, KeyMap key)
            throws DocumentException, IOException {
        if (edv.containsKey(key)) {
            if (!flag) writer1.addPage(inFile1.getPageSize(1), 0);
            writer1.addPage(edv.get(key));
            flag = true;
        } else flag = false;
        return flag;
    }


    private static Map<KeyMap, PdfImportedPage> getMapNpd(PdfReader pdfReader) throws IOException, DocumentException {

        Map<KeyMap, PdfImportedPage> tmp = new HashMap<>();

        int indexOfSnils;
        String snils;

        int indexOfNpd;
        String npd;

        int amountPage = pdfReader.getNumberOfPages();

        for (int i = 1; i <= amountPage; i++) {

            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(pdfReader, i, strategy);

            indexOfSnils = text.indexOf("СНИЛС");
            snils = text.substring(indexOfSnils + 6, indexOfSnils + 20).trim();

            indexOfNpd = text.indexOf("Дело №");
            npd = text.substring(indexOfNpd + 6, indexOfNpd + 15).trim();

            Document document = new Document(pdfReader.getPageSizeWithRotation(1));
            PdfCopy writer = new PdfCopy(document, new ByteArrayOutputStream());
            PdfImportedPage page = writer.getImportedPage(pdfReader, i);

            KeyMap keyMap = new KeyMap(npd, snils);

            if (snils.length() == 14)
                tmp.put(keyMap, page);

        }

        return tmp;
    }

}