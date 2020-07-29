package com.example.PrintingService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.Sides;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class PrintPage {

    @GetMapping(value = "/printpage")
    public ResponseEntity<String> printFile(
            @RequestParam(value = "printername", required = true, defaultValue = "") String printerName,
            @RequestParam(value = "israngeset", required = true, defaultValue = "false") boolean isRangeSet,
            @RequestParam(value = "from", required = false, defaultValue = "1") int from,
            @RequestParam(value = "to", required = false, defaultValue = "1") String to,
            @RequestParam(value = "copies", required = false, defaultValue = "1") int copies,
            @RequestParam(value = "layout", required = false, defaultValue = "Portrait") String layout,
    ) {

        File file = new File("/Users/thienle/Documents/PrintingService/sample.txt");

        boolean printSuccess = canPrint(file.getAbsolutePath(), printerName, isRangeSet, from, to, copies);

        if (printSuccess) {
            return new ResponseEntity<>("Printing sucess", HttpStatus.OK);
        }
        return new ResponseEntity<>("fail to print", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/printers")
    String printerList() {
        PrintService printServices = PrintServiceLookup.lookupDefaultPrintService();
        System.out.println(printServices.getAttributes());
        return printServices.getName();

    }


    static boolean canPrint(String fileName, String printerName, boolean isRangeSet, int from, int to, int copies) {
        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
        patts.add(Sides.ONE_SIDED);
        patts.add(MediaSizeName.ISO_A4);
        patts.add(new Copies(copies));

        if (isRangeSet) {
            PageRanges pageRanges = new PageRanges(from, to);
            patts.add(pageRanges);
        }

        PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
        if (ps.length == 0) {
            return false;
        }

        PrintService myService = null;
        for (PrintService printService : ps) {
            if (printService.getName().equalsIgnoreCase(printerName)) {
                myService = printService;
                break;
            }
        }

        if (myService == null) {
            return false;
        }

        try {
            FileInputStream fis = new FileInputStream(fileName);
            Doc pdfDoc = new SimpleDoc(fis, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            DocPrintJob printJob = myService.createPrintJob();
            printJob.print(pdfDoc, patts);
            fis.close();
            return true;
        } catch (IOException io) {
            System.out.println("file not found");
        } catch (PrintException pe) {
            System.out.println("unable to print");
        }
        return false;
    }



//    @GetMapping("/createfile")
//    public ResponseEntity<String> createFile() {
//
//        File file = new File("")
//    }
}
