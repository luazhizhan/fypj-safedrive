package com.fypj.icreative.controller;

import android.graphics.Bitmap;

import com.fypj.icreative.model.PrivateSettlementModel;
import com.fypj.icreative.utils.Utils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class PrivateSettlementController {
    public PrivateSettlementModel generatePrivateSettlementForm(PrivateSettlementModel privateSettlementModel,
                                                                String endAddress, long dateNow, Bitmap ppSignature, Bitmap rpSignature) {
        Utils utils = new Utils();
        privateSettlementModel.setDateSubmitted(dateNow);
        String fDateNow = utils.fromMiliSecToDateString(privateSettlementModel.getDateSubmitted(), "dd-MMM-yyyy hh:mm a");
        String fTimeOfAccident = utils.fromMiliSecToDateString(privateSettlementModel.getDateTimeOfAccident(), "dd-MMM-yyyy hh:mm a");
        String privateSettlementFormName = privateSettlementModel.getTripUID() + "_" + fDateNow + ".pdf";
        privateSettlementModel.setFileName(privateSettlementFormName);
        try {
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph headerPara = new Paragraph("Private Settlement Form", headerFont);
            headerPara.add(Chunk.NEWLINE);
            headerPara.add(Chunk.NEWLINE);
            headerPara.setAlignment(Element.ALIGN_CENTER);
            document.add(headerPara);

            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font bodyFontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            Paragraph firstPara = new Paragraph();
            firstPara.setIndentationLeft(50f);
            firstPara.setIndentationRight(50f);
            firstPara.setFont(bodyFont);
            firstPara.add(new Chunk("The parties involved agree to the following in respect of the accident that occurred on "));
            firstPara.add(new Chunk(fTimeOfAccident, bodyFontBold));
            firstPara.add(new Chunk(" at "));
            firstPara.add(new Chunk(endAddress, bodyFontBold));
            firstPara.add(Chunk.NEWLINE);
            firstPara.add(Chunk.NEWLINE);
            document.add(firstPara);


            Paragraph secondPara = new Paragraph();
            secondPara.setFont(bodyFont);
            secondPara.setIndentationLeft(50f);
            secondPara.setIndentationRight(50f);
            secondPara.add(new Chunk("Motor vehicle A, registration no. "));
            secondPara.add(new Chunk(privateSettlementModel.getPpMotorVehicleRegNo(), bodyFontBold));
            secondPara.add(new Chunk(" , is owned by "));
            secondPara.add(new Chunk(privateSettlementModel.getPpFullName(), bodyFontBold));
            secondPara.add(new Chunk(" ,NRIC/Passport No. "));
            secondPara.add(new Chunk(" " + privateSettlementModel.getPpNRIC(), bodyFontBold));
            secondPara.add(new Chunk(" and driven by "));
            secondPara.add(new Chunk(privateSettlementModel.getPpFullName(), bodyFontBold));
            secondPara.add(new Chunk(" NRIC/Passport No."));
            secondPara.add(new Chunk(privateSettlementModel.getPpNRIC(), bodyFontBold));
            secondPara.add(new Chunk(" at the time of the accident."));
            secondPara.add(Chunk.NEWLINE);
            secondPara.add(Chunk.NEWLINE);
            document.add(secondPara);

            Paragraph thirdPara = new Paragraph();
            thirdPara.setFont(bodyFont);
            thirdPara.setIndentationLeft(50f);
            thirdPara.setIndentationRight(50f);
            thirdPara.add(new Chunk("Motor vehicle A, registration no. "));
            thirdPara.add(new Chunk(privateSettlementModel.getRpMotorVehicleRegNo(), bodyFontBold));
            thirdPara.add(new Chunk(" is owned by "));
            thirdPara.add(new Chunk(privateSettlementModel.getRpFullName(), bodyFontBold));
            thirdPara.add(new Chunk(" ,NRIC/Passport No. "));
            thirdPara.add(new Chunk(" " + privateSettlementModel.getRpNRIC(), bodyFontBold));
            thirdPara.add(new Chunk(" and driven by "));
            thirdPara.add(new Chunk(privateSettlementModel.getRpFullName(), bodyFontBold));
            thirdPara.add(new Chunk(" NRIC/Passport No."));
            thirdPara.add(new Chunk(privateSettlementModel.getRpNRIC(), bodyFontBold));
            thirdPara.add(new Chunk(" at the time of the accident."));
            thirdPara.add(Chunk.NEWLINE);
            thirdPara.add(Chunk.NEWLINE);
            document.add(thirdPara);


            Paragraph fourPara = new Paragraph();
            fourPara.setFont(bodyFont);
            fourPara.setIndentationLeft(50f);
            fourPara.setIndentationRight(50f);
            fourPara.add(new Chunk("The parties have agreed to settle this matter amicably as follows:"));
            fourPara.add(Chunk.NEWLINE);
            document.add(fourPara);

            Paragraph fifthPara = new Paragraph();
            fifthPara.setFont(bodyFont);
            fifthPara.setIndentationLeft(70f);
            fifthPara.setIndentationRight(70f);
            fifthPara.add(new Chunk("1. Neither party shall be liable to compensate the other party for any loss or damages (direct or indirect)"));
            fifthPara.add(new Chunk(" incurred or to be incurred as a result of the accident."));
            fifthPara.add(Chunk.NEWLINE);
            fifthPara.add(new Chunk("2. without any admission of liability, "));
            fifthPara.add(new Chunk(privateSettlementModel.getPpFullName(), bodyFontBold));
            fifthPara.add(new Chunk(" (Party paying"));
            fifthPara.add(new Chunk(" compensation) has paid a sum of "));
            fifthPara.add(new Chunk("$" + privateSettlementModel.getPpCompensationAmt() + "0", bodyFontBold));
            fifthPara.add(new Chunk(" which "));
            fifthPara.add(new Chunk(privateSettlementModel.getRpFullName(), bodyFontBold));
            fifthPara.add(new Chunk(" (Owner receiving compensation) hereby acknowledges receipt"));
            fifthPara.add(new Chunk(" there of in full and"));
            fifthPara.add(new Chunk(" there of in full and final settlement of all damages and cost incurred and/or"));
            fifthPara.add(new Chunk(" to be incurred as a result of the accident."));
            fifthPara.add(Chunk.NEWLINE);
            fifthPara.add(new Chunk("3. That "));
            fifthPara.add(new Chunk(privateSettlementModel.getRpFullName() + "/" + privateSettlementModel.getRpNRIC(), bodyFontBold));
            fifthPara.add(new Chunk(" have received the"));
            fifthPara.add(new Chunk(" aforesaid vehicle in good running order"));
            fifthPara.add(new Chunk(" and damages that were caused as a"));
            fifthPara.add(new Chunk(" result of the above-mentioned accident were repaired to satisfaction."));
            fifthPara.add(Chunk.NEWLINE);
            fifthPara.add(Chunk.NEWLINE);
            document.add(fifthPara);

            Paragraph sixthPara = new Paragraph();
            sixthPara.setFont(bodyFont);
            sixthPara.setIndentationLeft(50f);
            sixthPara.setIndentationRight(50f);
            sixthPara.add(new Chunk("Both parties have not and will not make a police report of this accident."));
            sixthPara.add(Chunk.NEWLINE);
            sixthPara.add(Chunk.NEWLINE);
            sixthPara.add(new Chunk("Both parties will not file any accident claims for this accident."));
            sixthPara.add(Chunk.NEWLINE);
            sixthPara.add(Chunk.NEWLINE);
            document.add(sixthPara);

            Paragraph seventhPara = new Paragraph();
            seventhPara.setFont(bodyFont);
            seventhPara.setIndentationLeft(50f);
            seventhPara.setIndentationRight(50f);
            ByteArrayOutputStream ppSignaturestream = new ByteArrayOutputStream();
            ppSignature = utils.getResizedBitmap(ppSignature, 150, 100, 0);
            ppSignature.compress(Bitmap.CompressFormat.PNG, 100, ppSignaturestream);
            Image ppSignatureImage = Image.getInstance(ppSignaturestream.toByteArray());

            ByteArrayOutputStream rpSignaturestream = new ByteArrayOutputStream();
            rpSignature = utils.getResizedBitmap(rpSignature, 150, 100, 0);
            rpSignature = utils.getResizedBitmap(rpSignature, 150, 100, 0);
            rpSignature.compress(Bitmap.CompressFormat.PNG, 100, rpSignaturestream);
            Image rpSignatureImage = Image.getInstance(rpSignaturestream.toByteArray());

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            PdfPCell cell1 = new PdfPCell(ppSignatureImage, true);
            cell1.setBorder(Rectangle.NO_BORDER);
            PdfPCell cell2 = new PdfPCell(rpSignatureImage, true);
            cell2.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell1);
            table.addCell(cell2);
            seventhPara.add(table);

            seventhPara.add(Chunk.NEWLINE);
            seventhPara.add(new Chunk("Signed, *owner/driver of motor vehicle A"));
            seventhPara.add(new Chunk("      "));
            seventhPara.add(new Chunk("Signed, *owner/driver of motor vehicle B"));
            seventhPara.add(Chunk.NEWLINE);
            seventhPara.add(Chunk.NEWLINE);
            seventhPara.add(new Chunk("Submitted on: " + fDateNow));
            document.add(seventhPara);
            document.close();
            privateSettlementModel.setPrivateSettlementForm(byteArrayOutputStream.toByteArray());

        } catch (DocumentException | FileNotFoundException | MalformedURLException e) {
            privateSettlementModel.setPrivateSettlementForm(null);
        } catch (IOException e) {
            privateSettlementModel.setPrivateSettlementForm(null);
        }
        return privateSettlementModel;
    }
}
