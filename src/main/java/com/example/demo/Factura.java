package com.example.demo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Factura {
    PDDocument invc;
    int n;
    Integer total = 0;
    Integer price;
    String CustName;
    String CustID;
    String ProductName = new String("Alojamiento");
    Integer ProductPrice;
    Integer ProductQty = 1;
    String InvoiceTitle = new String("AlexHotels");
    String SubTitle;

    public PDDocument getInvc() {
        return invc;
    }

    public void setInvc(PDDocument invc) {
        this.invc = invc;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public Integer getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(Integer productPrice) {
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

    public Factura(String subTitle,String CustName, String CustID) {
        this.SubTitle = subTitle;
        this.CustName = CustName;
        this.CustID = CustID;
        //Create Document
        invc = new PDDocument();
        //Create Blank Page
        PDPage newpage = new PDPage();
        //Add the blank page
        invc.addPage(newpage);
    }


    public void WriteInvoice() {
        //get the page
        PDPage mypage = invc.getPage(0);
        try {
            //Prepare Content Stream
            PDPageContentStream cs = new PDPageContentStream(invc, mypage);

            //Writing Single Line text
            //Writing the Invoice title
            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 20);
            cs.newLineAtOffset(140, 750);
            cs.showText(InvoiceTitle);
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 18);
            cs.newLineAtOffset(270, 690);
            cs.showText(SubTitle);
            cs.endText();

            //Writing Multiple Lines
            //writing the customer details
            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.setLeading(20f);
            cs.newLineAtOffset(60, 610);
            cs.showText("Nombre: ");
            cs.newLine();
            cs.showText("Nº identificación: ");
            cs.newLineAtOffset(300,15);
            SimpleDateFormat formateador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es_ES"));
            Date fechaDate = new Date();
            String fecha = formateador.format(fechaDate);
            cs.showText("Fecha: " + fecha );
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.setLeading(20f);
            cs.newLineAtOffset(170, 610);
            cs.showText(CustName);
            cs.newLine();
            cs.showText(CustID);
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(80, 540);
            cs.showText("Product Name");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(200, 540);
            cs.showText("Unit Price");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(310, 540);
            cs.showText("Quantity");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(410, 540);
            cs.showText("Price");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(80, 520);
            for(int i =0; i<n; i++) {
                cs.showText(ProductName);
                cs.newLine();
            }
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(200, 520);
            for(int i =0; i<n; i++) {
                cs.showText(String.valueOf(ProductPrice));
                cs.newLine();
            }
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(310, 520);
            for(int i =0; i<n; i++) {
                cs.showText(String.valueOf(ProductQty));
                cs.newLine();
            }
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 12);
            cs.setLeading(20f);
            cs.newLineAtOffset(410, 520);
            for(int i =0; i<n; i++) {
                price = ProductPrice;
                cs.showText(price.toString());
                cs.newLine();
            }
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            cs.newLineAtOffset(310, (500-(20*n)));
            cs.showText("Total: ");
            cs.endText();

            cs.beginText();
            cs.setFont(PDType1Font.TIMES_ROMAN, 14);
            //Calculating where total is to be written using number of products
            cs.newLineAtOffset(410, (500-(20*n)));
            cs.showText(total.toString());
            cs.endText();

            //Close the content stream
            cs.close();
            //Save the PDF
            invc.save("facturas/factura.pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}