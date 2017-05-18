package com.fypj.icreative.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.fypj.icreative.activity.CarAccidentClaimActivity;
import com.fypj.icreative.model.CarAccidentClaimModel;
import com.fypj.icreative.model.CarAccidentClaimPhotosModel;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CarAccidentClaimController {
    private Context context;
    private static final String IMAGE_DIRECTORY_NAME = "iCreative";
    private static final File MEDIA_STORAGE_DIR = new File(Environment.
            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME);

    public CarAccidentClaimController(Context context) {
        this.context = context;
    }

    public boolean isDeviceSupportCamera() {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
    * returning image / video
    */
    public File getOutputMediaFile(int type) {

        // Create the storage directory if it does not exist
        if (!MEDIA_STORAGE_DIR.exists()) {
            if (!MEDIA_STORAGE_DIR.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == CarAccidentClaimActivity.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(MEDIA_STORAGE_DIR.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void deleteFolder() {
        if (MEDIA_STORAGE_DIR.isDirectory()) {
            String[] children = MEDIA_STORAGE_DIR.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    new File(MEDIA_STORAGE_DIR, children[i]).delete();
                }
            }
        }
    }

    public String getWeatherForcastURL(LatLng dest, String apikey) {
        String baseURL = "http://api.openweathermap.org/data/2.5/weather?lat=";
        String extraString = dest.latitude + "&lon=" + dest.longitude + "&apikey=" + apikey;
        return baseURL + extraString;
    }


    public HashMap<String, String> parseWeather(JSONObject jsonObject) {
        HashMap<String, String> weatherHM = new HashMap<>();
        JSONArray weatherArray;
        JSONObject windObject;
        JSONObject cloudsObject;
        try {
            weatherArray = jsonObject.getJSONArray("weather");
            windObject = jsonObject.getJSONObject("wind");
            cloudsObject = jsonObject.getJSONObject("clouds");
            String weather = (String) ((JSONObject) weatherArray.get(0)).get("main");
            String weatherDesc = (String) ((JSONObject) weatherArray.get(0)).get("description");
            String windSpeed = windObject.get("speed").toString();
            String cloudiness = cloudsObject.get("all").toString();
            weatherHM.put("Weather", weather);
            weatherHM.put("WeatherDesc", weatherDesc);
            weatherHM.put("WindSpeed", windSpeed);
            weatherHM.put("Cloudiness", cloudiness);

        } catch (JSONException ignored) {

        }
        return weatherHM;
    }

    public CarAccidentClaimModel generateCarAccidentForm(CarAccidentClaimModel carAccidentClaimModel, TripModel tripModel,
                                                         List<CarAccidentClaimPhotosModel> carAccidentClaimPhotosModelList, long dateNow, String startAddress,
                                                         String endAddress, int safetyIndex, byte[] googleMapSnapShot, HashMap<String, String> weatherHM) {
        carAccidentClaimPhotosModelList = getAllImagesOfCarAccident(carAccidentClaimPhotosModelList);
        Utils utils = new Utils();
        carAccidentClaimModel.setDateSubmitted(dateNow);
        String fDateNow = utils.fromMiliSecToDateString(carAccidentClaimModel.getDateSubmitted(), "dd-MMM-yyyy hh:mm a");
        String fTimeOfAccident = utils.fromMiliSecToDateString(carAccidentClaimModel.getDateTimeOfAccident(), "dd-MMM-yyyy hh:mm a");
        String carAccidentClaimFormName = carAccidentClaimModel.getTripUID() + "_" + fDateNow + ".pdf";
        carAccidentClaimModel.setFileName(carAccidentClaimFormName);
        try {
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
//            File pdfFolder = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOCUMENTS)));
//            File myFile = new File(pdfFolder + "/" + carAccidentClaimFormName);
//            OutputStream output = new FileOutputStream(myFile);
//            PdfWriter.getInstance(document, output);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Font dateTimeFont = new Font(Font.FontFamily.HELVETICA, 11);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font bodyHeaderFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12);
            Font bodyFontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            String accidentTime = "Accident Time: " + fTimeOfAccident;
            String submissionDate = "Date Submitted: " + fDateNow;
            String tripSafetyIndex = "Safety Index (This trip): " + tripModel.getTripSafetyIndex();
            Paragraph dateTimePara = new Paragraph(accidentTime + "\n" + submissionDate + "\n" + tripSafetyIndex, dateTimeFont);
            dateTimePara.setAlignment(Element.ALIGN_RIGHT);
            dateTimePara.add(Chunk.NEWLINE);
            document.add(dateTimePara);

            Paragraph headerPara = new Paragraph("Car Accident Claim Form", headerFont);
            headerPara.setAlignment(Element.ALIGN_CENTER);
            headerPara.add(Chunk.NEWLINE);
            document.add(headerPara);

            Paragraph firstHeaderPara = new Paragraph("Particulars (Self)", bodyHeaderFont);
            firstHeaderPara.setIndentationLeft(50f);
            firstHeaderPara.setIndentationRight(50f);
            firstHeaderPara.setAlignment(Element.ALIGN_LEFT);
            firstHeaderPara.add(Chunk.NEWLINE);
            firstHeaderPara.add(Chunk.NEWLINE);
            document.add(firstHeaderPara);

            Paragraph firstPara = new Paragraph();
            firstPara.setFont(bodyFont);
            firstPara.setIndentationLeft(50f);
            firstPara.setIndentationRight(50f);
            PdfPTable selfParticularTable = new PdfPTable(2);
            selfParticularTable.setWidthPercentage(100);
            PdfPCell pdfFullNameCell = new PdfPCell();
            pdfFullNameCell.addElement(new Phrase("Full Name: ", bodyFont));
            pdfFullNameCell.addElement(new Phrase(carAccidentClaimModel.getSelfFullName(), bodyFontBold));
            selfParticularTable.addCell(pdfFullNameCell);
            PdfPCell pdfNricCell = new PdfPCell();
            pdfNricCell.addElement(new Phrase("NRIC Number: ", bodyFont));
            pdfNricCell.addElement(new Phrase(carAccidentClaimModel.getSelfNRIC(), bodyFontBold));
            selfParticularTable.addCell(pdfNricCell);
            PdfPCell pdfContactNumCell = new PdfPCell();
            pdfContactNumCell.addElement(new Phrase("Contact Number: ", bodyFont));
            pdfContactNumCell.addElement(new Phrase(String.valueOf(carAccidentClaimModel.getSelfContactNum()), bodyFontBold));
            selfParticularTable.addCell(pdfContactNumCell);
            PdfPCell pdfCarRegNoCell = new PdfPCell();
            pdfCarRegNoCell.addElement(new Phrase("Car Registration No.: ", bodyFont));
            pdfCarRegNoCell.addElement(new Phrase(carAccidentClaimModel.getSelfMotorVehicleRegNo(), bodyFontBold));
            selfParticularTable.addCell(pdfCarRegNoCell);
            PdfPCell pdfInsuranceCompanyCell = new PdfPCell();
            pdfInsuranceCompanyCell.addElement(new Phrase("Car Insurance Company: ", bodyFont));
            pdfInsuranceCompanyCell.addElement(new Phrase(carAccidentClaimModel.getSelfInsuranceCompany(), bodyFontBold));
            selfParticularTable.addCell(pdfInsuranceCompanyCell);
            PdfPCell pdfSafetyIndexCell = new PdfPCell();
            pdfSafetyIndexCell.addElement(new Phrase("Safety Index: ", bodyFont));
            if (safetyIndex >= 80) {
                pdfSafetyIndexCell.addElement(new Phrase(String.valueOf(safetyIndex) + "(Band 1)", bodyFontBold));
            } else if (safetyIndex >= 60) {
                pdfSafetyIndexCell.addElement(new Phrase(String.valueOf(safetyIndex) + "(Band 2)", bodyFontBold));
            } else if (safetyIndex >= 40) {
                pdfSafetyIndexCell.addElement(new Phrase(String.valueOf(safetyIndex) + "(Band 3)", bodyFontBold));
            } else {
                pdfSafetyIndexCell.addElement(new Phrase(String.valueOf(safetyIndex) + "(Band 4)", bodyFontBold));
            }
            selfParticularTable.addCell(pdfSafetyIndexCell);
            firstPara.add(selfParticularTable);
            firstPara.add(Chunk.NEWLINE);
            document.add(firstPara);

            Paragraph secondHeaderPara = new Paragraph("Particulars (Other Party)", bodyHeaderFont);
            secondHeaderPara.setIndentationLeft(50f);
            secondHeaderPara.setIndentationRight(50f);
            secondHeaderPara.setAlignment(Element.ALIGN_LEFT);
            secondHeaderPara.add(Chunk.NEWLINE);
            secondHeaderPara.add(Chunk.NEWLINE);
            document.add(secondHeaderPara);

            Paragraph secondPara = new Paragraph();
            secondPara.setFont(bodyFont);
            secondPara.setIndentationLeft(50f);
            secondPara.setIndentationRight(50f);
            PdfPTable otherPartyParticularTable = new PdfPTable(2);
            otherPartyParticularTable.setWidthPercentage(100);
            PdfPCell pdfOPFullNameCell = new PdfPCell();
            pdfOPFullNameCell.addElement(new Phrase("Full Name: ", bodyFont));
            pdfOPFullNameCell.addElement(new Phrase(carAccidentClaimModel.getOpFullName(), bodyFontBold));
            otherPartyParticularTable.addCell(pdfOPFullNameCell);
            PdfPCell pdfOPNricCell = new PdfPCell();
            pdfOPNricCell.addElement(new Phrase("NRIC Number: ", bodyFont));
            pdfOPNricCell.addElement(new Phrase(carAccidentClaimModel.getOpNRIC(), bodyFontBold));
            otherPartyParticularTable.addCell(pdfOPNricCell);
            PdfPCell pdfOPContactNumCell = new PdfPCell();
            pdfOPContactNumCell.addElement(new Phrase("Contact Number: ", bodyFont));
            pdfOPContactNumCell.addElement(new Phrase(String.valueOf(carAccidentClaimModel.getOpContactNum()), bodyFontBold));
            otherPartyParticularTable.addCell(pdfOPContactNumCell);
            PdfPCell pdfOPCarRegNoCell = new PdfPCell();
            pdfOPCarRegNoCell.addElement(new Phrase("Car Registration No.: ", bodyFont));
            pdfOPCarRegNoCell.addElement(new Phrase(carAccidentClaimModel.getOpMotorVehicleRegNo(), bodyFontBold));
            otherPartyParticularTable.addCell(pdfOPCarRegNoCell);
            PdfPCell pdfOPInsuranceCompanyCell = new PdfPCell();
            pdfOPInsuranceCompanyCell.setColspan(2);
            pdfOPInsuranceCompanyCell.addElement(new Phrase("Car Insurance Company: ", bodyFont));
            pdfOPInsuranceCompanyCell.addElement(new Phrase(carAccidentClaimModel.getOpInsuranceCompany(), bodyFontBold));
            otherPartyParticularTable.addCell(pdfOPInsuranceCompanyCell);
            secondPara.add(otherPartyParticularTable);
            secondPara.add(Chunk.NEWLINE);
            document.add(secondPara);

            Paragraph thirdHeaderPara = new Paragraph("Other Information (From this trip)", bodyHeaderFont);
            thirdHeaderPara.setIndentationLeft(50f);
            thirdHeaderPara.setIndentationRight(50f);
            thirdHeaderPara.setAlignment(Element.ALIGN_LEFT);
            thirdHeaderPara.add(Chunk.NEWLINE);
            thirdHeaderPara.add(Chunk.NEWLINE);
            document.add(thirdHeaderPara);

            Paragraph thirdPara = new Paragraph();
            thirdPara.setFont(bodyFont);
            thirdPara.setIndentationLeft(50f);
            thirdPara.setIndentationRight(50f);
            PdfPTable otherInfoTable = new PdfPTable(2);
            otherInfoTable.setWidthPercentage(100);
            PdfPCell pdfOtherInfoStartPointCell = new PdfPCell();
            pdfOtherInfoStartPointCell.addElement(new Phrase("Starting Point: ", bodyFont));
            pdfOtherInfoStartPointCell.addElement(new Phrase(startAddress, bodyFontBold));
            otherInfoTable.addCell(pdfOtherInfoStartPointCell);
            PdfPCell pdfOtherInfoEndPointCell = new PdfPCell();
            pdfOtherInfoEndPointCell.addElement(new Phrase("Ending Point: ", bodyFont));
            pdfOtherInfoEndPointCell.addElement(new Phrase(endAddress, bodyFontBold));
            otherInfoTable.addCell(pdfOtherInfoEndPointCell);
            PdfPCell pdfOtherInfoAvgSpeedCell = new PdfPCell();
            pdfOtherInfoAvgSpeedCell.addElement(new Phrase("Average speed: ", bodyFont));
            pdfOtherInfoAvgSpeedCell.addElement(new Phrase(String.valueOf(tripModel.getAvgSpeed() + "km/h"), bodyFontBold));
            otherInfoTable.addCell(pdfOtherInfoAvgSpeedCell);
            PdfPCell pdfOtherInfoDistanceCell = new PdfPCell();
            pdfOtherInfoDistanceCell.addElement(new Phrase("Distance Travelled: ", bodyFont));
            pdfOtherInfoDistanceCell.addElement(new Phrase(String.valueOf(tripModel.getDistanceTravelled() + "m"), bodyFontBold));
            otherInfoTable.addCell(pdfOtherInfoDistanceCell);
            PdfPCell pacOtherInfoPenaltyCell = new PdfPCell();
            pacOtherInfoPenaltyCell.addElement(new Phrase("Speeding Count ", bodyFont));
            pacOtherInfoPenaltyCell.addElement(new Phrase(String.valueOf(tripModel.getSpeedingCount() + " Count"), bodyFontBold));
            otherInfoTable.addCell(pacOtherInfoPenaltyCell);
            PdfPCell pacOtherInfoIncentiveCell = new PdfPCell();
            pacOtherInfoIncentiveCell.addElement(new Phrase("Sharp Turns Count: ", bodyFont));
            pacOtherInfoIncentiveCell.addElement(new Phrase(String.valueOf(tripModel.getVigorousTurnCount() + " Count"), bodyFontBold));
            otherInfoTable.addCell(pacOtherInfoIncentiveCell);
            PdfPCell pacOtherInfoRemarksCell = new PdfPCell();
            pacOtherInfoRemarksCell.setColspan(2);
            pacOtherInfoRemarksCell.addElement(new Phrase("Remarks: ", bodyFont));
            pacOtherInfoRemarksCell.addElement(new Phrase(carAccidentClaimModel.getRemarks(), bodyFontBold));
            otherInfoTable.addCell(pacOtherInfoRemarksCell);
            thirdPara.add(otherInfoTable);
            thirdPara.add(Chunk.NEWLINE);
            document.add(thirdPara);

            document.newPage();

            Paragraph fourthHeaderPara = new Paragraph("Current Location Map Snapshot", bodyHeaderFont);
            fourthHeaderPara.setIndentationLeft(50f);
            fourthHeaderPara.setIndentationRight(50f);
            fourthHeaderPara.setAlignment(Element.ALIGN_LEFT);
            fourthHeaderPara.add(Chunk.NEWLINE);
            fourthHeaderPara.add(Chunk.NEWLINE);
            document.add(fourthHeaderPara);

            Paragraph fourthPara = new Paragraph();
            fourthPara.setFont(bodyFont);
            fourthPara.setIndentationLeft(50f);
            fourthPara.setIndentationRight(50f);
            PdfPTable googleMapTable = new PdfPTable(1);
            googleMapTable.setWidthPercentage(100);
            Image googleMapPhoto = Image.getInstance(googleMapSnapShot);
            PdfPCell googleMapCell = new PdfPCell();
            googleMapCell.addElement(googleMapPhoto);
            googleMapCell.addElement(new Chunk("Accident Location (Purple Marker): " + endAddress));
            googleMapCell.addElement(new Chunk("Color Code (Fast...Slow): Green, Orange, Red, DarkRed"));
            googleMapTable.addCell(googleMapCell);
            fourthPara.add(googleMapTable);
            fourthPara.add(Chunk.NEWLINE);
            document.add(fourthPara);

            Paragraph fifthHeaderPara = new Paragraph("Weather Forecast in Current Location", bodyHeaderFont);
            fifthHeaderPara.setIndentationLeft(50f);
            fifthHeaderPara.setIndentationRight(50f);
            fifthHeaderPara.setAlignment(Element.ALIGN_LEFT);
            fifthHeaderPara.add(Chunk.NEWLINE);
            fifthHeaderPara.add(Chunk.NEWLINE);
            document.add(fifthHeaderPara);

            Paragraph fifthPara = new Paragraph();
            fifthPara.setFont(bodyFont);
            fifthPara.setIndentationLeft(50f);
            fifthPara.setIndentationRight(50f);
            PdfPTable weatherTable = new PdfPTable(2);
            weatherTable.setWidthPercentage(100);
            PdfPCell weatherCell = new PdfPCell();
            weatherCell.addElement(new Phrase("Weather: ", bodyFont));
            weatherCell.addElement(new Phrase(weatherHM.get("Weather"), bodyFontBold));
            weatherTable.addCell(weatherCell);

            PdfPCell weatherDescCell = new PdfPCell();
            weatherDescCell.addElement(new Phrase("Description: ", bodyFont));
            weatherDescCell.addElement(new Phrase(weatherHM.get("WeatherDesc"), bodyFontBold));
            weatherTable.addCell(weatherDescCell);

            PdfPCell windSpeedCell = new PdfPCell();
            windSpeedCell.addElement(new Phrase("Wind Speed: ", bodyFont));
            windSpeedCell.addElement(new Phrase(weatherHM.get("WindSpeed") + "m/s   ", bodyFontBold));
            weatherTable.addCell(windSpeedCell);

            PdfPCell cloudinessCell = new PdfPCell();
            cloudinessCell.addElement(new Phrase("Cloudiness: ", bodyFont));
            cloudinessCell.addElement(new Phrase(weatherHM.get("Cloudiness") + "%", bodyFontBold));
            weatherTable.addCell(cloudinessCell);

            fifthPara.add(weatherTable);
            fifthPara.add(Chunk.NEWLINE);
            document.add(fifthPara);

            document.newPage();

            Paragraph sixthHeaderPara = new Paragraph("Photographs of Accident", bodyHeaderFont);
            sixthHeaderPara.setIndentationLeft(50f);
            sixthHeaderPara.setIndentationRight(50f);
            sixthHeaderPara.setAlignment(Element.ALIGN_LEFT);
            sixthHeaderPara.add(Chunk.NEWLINE);
            sixthHeaderPara.add(Chunk.NEWLINE);
            document.add(sixthHeaderPara);

            Paragraph sixthPara = new Paragraph();
            sixthPara.setFont(bodyFont);
            sixthPara.setIndentationLeft(50f);
            sixthPara.setIndentationRight(50f);
            PdfPTable photographTable = new PdfPTable(1);
            photographTable.setWidthPercentage(100);
            for (int i = 0; i < carAccidentClaimPhotosModelList.size(); i++) {
                PdfPCell pdfPhotographCell = new PdfPCell();
                pdfPhotographCell.addElement(new Phrase("Photo " + (i + 1), bodyFont));
                ByteArrayOutputStream photoByteArrayOutputStream = new ByteArrayOutputStream();
                Bitmap resizedBitmap = utils.getResizedBitmap(carAccidentClaimPhotosModelList.get(i).getPhotoBitmap(), 500, 350, 0);
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, photoByteArrayOutputStream);
                Image iPhoto = Image.getInstance(photoByteArrayOutputStream.toByteArray());
                pdfPhotographCell.addElement(iPhoto);
                pdfPhotographCell.addElement(new Chunk("Taken on: " + carAccidentClaimPhotosModelList.get(i).getDateTaken() + "\nAddress: " + carAccidentClaimPhotosModelList.get(i).getCurrentLocation()));
                photographTable.addCell(pdfPhotographCell);
            }
            sixthPara.add(photographTable);
            document.add(sixthPara);

            document.close();
            carAccidentClaimModel.setCarAccidentClaimForm(byteArrayOutputStream.toByteArray());
        } catch (DocumentException | FileNotFoundException | MalformedURLException e) {
            carAccidentClaimModel.setCarAccidentClaimForm(null);
        } catch (IOException e) {
            carAccidentClaimModel.setCarAccidentClaimForm(null);
        }
        return carAccidentClaimModel;
    }

    private List<CarAccidentClaimPhotosModel> getAllImagesOfCarAccident(List<CarAccidentClaimPhotosModel> carAccidentClaimPhotosModelList) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        for (int i = 0; i < carAccidentClaimPhotosModelList.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(carAccidentClaimPhotosModelList.get(i).getFileUri().getPath(), options);
            carAccidentClaimPhotosModelList.get(i).setPhotoBitmap(bitmap);
        }
        return carAccidentClaimPhotosModelList;
    }
}
