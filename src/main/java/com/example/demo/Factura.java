package com.example.demo;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Factura {
    PDDocument invc;
    Integer total = 0;
    String CustName;
    String CustID;
    String ProductName = new String("Alojamiento");
    Float ProductPrice;
    Integer ProductQty = 1;
    String InvoiceTitle = new String("AlexHotels");
    String SubTitle;

    public PDDocument getInvc() {
        return invc;
    }

    public void setInvc(PDDocument invc) {
        this.invc = invc;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getCustID() {
        return CustID;
    }

    public void setCustID(String CustID) {
        CustID = CustID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Float getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(Float productPrice) {
        ProductPrice = productPrice;
    }

    public Integer getProductQty() {
        return ProductQty;
    }

    public void setProductQty(Integer productQty) {
        ProductQty = productQty;
    }

    public String getInvoiceTitle() {
        return InvoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        InvoiceTitle = invoiceTitle;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public Factura(String subTitle,String CustName, String CustID,Float ProductPrice) {
        this.SubTitle = subTitle;
        this.CustName = CustName;
        this.CustID = CustID;
        this.ProductPrice = ProductPrice;
        //Create Document
        invc = new PDDocument();
        //Create Blank Page
        PDPage newpage = new PDPage((new PDRectangle(PDRectangle.A5.getHeight(), PDRectangle.A5.getWidth())));
        //Add the blank page
        invc.addPage(newpage);
    }


    public void WriteInvoice() {
        //get the page
        PDPage mypage = invc.getPage(0);
        try {
            //Prepare Content Stream
            PDPageContentStream cs = new PDPageContentStream(invc, mypage);
            PDImageXObject pdImage = PDImageXObject.createFromFile("img/logo.png", invc);
            cs.drawImage(pdImage, 30, 290,100,100);
            PDImageXObject pdImage2 = PDImageXObject.createFromFile("img/divisor2.png", invc);
            cs.drawImage(pdImage2, 20, 143,550,40);

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 18);
            cs.newLineAtOffset(270, 325 );
            cs.showText(SubTitle);
            cs.endText();

            //Writing Multiple Lines
            //writing the customer details
            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.setLeading(20f);
            cs.newLineAtOffset(40, 245);
            cs.showText("Nombre: ");
            cs.newLine();
            cs.showText("Nº identificación: ");
            cs.newLineAtOffset(350,0);
            SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es_ES"));
            Date fechaDate = new Date();
            String fecha = formateador.format(fechaDate);
            cs.showText("Fecha: " + fecha );
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.setLeading(20f);
            cs.newLineAtOffset(150, 245);
            cs.showText(CustName);
            cs.newLine();
            cs.showText(CustID);
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(80, 180);
            cs.showText("Descripción");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(200, 180);
            cs.showText("Precio unitario");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(310, 180);
            cs.showText("Cantidad");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(410, 180);
            cs.showText("Total");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(80, 135);
            cs.showText(ProductName);
            cs.newLine();
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(225, 135);
            cs.showText(String.valueOf(ProductPrice));
            cs.newLine();
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(335, 135);
            cs.showText(String.valueOf(ProductQty));
            cs.newLine();
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(415, 135);
            cs.showText(String.valueOf(ProductPrice));
            cs.newLine();
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(380, 50);
            cs.showText("Total: ");
            cs.endText();

            cs.addRect(375,45,120,1);
            cs.setNonStrokingColor(Color.red);
            cs.fill();
            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(450, 50);
            cs.showText(String.valueOf(ProductPrice + "€"));
            cs.endText();

            //Close the content stream
            cs.close();
            //Save the PDF
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            invc.save("facturas/factura"+ CustID + formatter.format(date) + ".pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}