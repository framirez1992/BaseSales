package com.far.farpdf.PDF;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.far.farpdf.Entities.Invoice;
import com.far.farpdf.Entities.OrderDetail;
import com.far.farpdf.Entities.AmountsResume;
import com.far.farpdf.Objects.Line;
import com.far.farpdf.Objects.LineItem;
import com.far.farpdf.Objects.Table;
import com.far.farpdf.Objects.TableCell;
import com.far.farpdf.Objects.TableColumn;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
public class Writer {

    Context context;

    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL);


    public Writer(Context c){
        this.context = c;
    }


    public Paragraph addParagraph(String text, Font f){
        Paragraph p = new Paragraph(text, f);
        p.setAlignment(Paragraph.ALIGN_LEFT);
        return p;
    }

    public Paragraph addParagraph(String text, Font f, int aligment){
        Paragraph p = new Paragraph(text, f);
        p.setAlignment(aligment);
        return p;
    }

    public Paragraph newLine(){
        return  addParagraph(" ",fontNormal());
    }

    public Font getFont(int size, boolean bold){
        int fontType = bold?Font.BOLD:Font.NORMAL;
        return new Font(Font.FontFamily.TIMES_ROMAN, size,fontType);
    }

    public Font getFont(int size){
        int fontType = Font.NORMAL;
        return new Font(Font.FontFamily.TIMES_ROMAN, size,fontType);
    }

    public Font getFont(int size, boolean bold, BaseColor color){
        int fontType = bold?Font.BOLD:Font.NORMAL;
        return new Font(Font.FontFamily.TIMES_ROMAN, size,fontType,color);
    }


    public Font fontTitle(){
        return getFont(18,true);
    }
    public Font fontNormal(boolean bold){
        return getFont(16,bold);
    }
    public Font fontNormal(){
        return getFont(16,false);
    }
    public void showPDF(String fileRoute){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(fileRoute)), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public void emailNote(String fileRoute)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Message body");
        Uri uri = Uri.fromFile(new File(fileRoute));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(intent);
    }



    public void promptForNextAction(final String filePath)
    {
        final String[] options = { "Email", "Preview",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Email")){
                    emailNote(filePath);
                }else if (options[which].equals("Preview")){
                    showPDF(filePath);
                }else if (options[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    public String createPDF(String folderName, String name, ArrayList<Object> data)  {

        try {
            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folderName);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
            }


            File myFile = new File(pdfFolder, name + ".pdf");

            OutputStream output = new FileOutputStream(myFile);

            //los documentos pdf tienen un tamano especifico, no es como un listView. por esta raron se deben especificar:
            /**
             * Rectangle pagesize = new Rectangle(216f, 720f);
             * Document document = new Document(pagesize, 36f, 72f, 108f, 180f);
             */
            //pero itex nos provee tamanos standar para las paginas.
            Document document = new Document(PageSize.LETTER);
            /**
             * The PdfWriter class is responsible for writing the PDF file. And in this step,
             * you associate the blank document we created above with the PdfWriter class and point it to
             * the output stream where the PDF will be written to like so:
             */
            PdfWriter.getInstance(document, output);

            document.open();

            for (Object o : data) {
                if (o instanceof LineItem) {
                    LineItem li = (LineItem) o;
                    Paragraph p = new Paragraph(li.getText(), li.getFont());
                    p.setAlignment(li.getAlign());
                    document.add(p);
                } else if (o instanceof Line) {
                    Line l = (Line) o;
                    if (l.isDotted()) {
                        DottedLineSeparator dottedSeparator = new DottedLineSeparator();
                        dottedSeparator.setLineColor(l.color);
                        dottedSeparator.setLineWidth(l.thickness);
                        dottedSeparator.setPercentage(l.width);
                        dottedSeparator.setAlignment(l.alignment);
                        document.add(dottedSeparator);
                    } else {
                        LineSeparator lineSeparator = new LineSeparator();
                        lineSeparator.setLineColor(l.color);
                        lineSeparator.setLineWidth(l.thickness);
                        lineSeparator.setPercentage(l.width);
                        lineSeparator.setAlignment(l.alignment);
                        document.add(lineSeparator);
                    }

                } else if (o instanceof com.far.farpdf.Objects.Image) {
                    try {
                        com.far.farpdf.Objects.Image img = (com.far.farpdf.Objects.Image) o;
                        Image i = Image.getInstance(img.getBytes());
                        i.scaleAbsolute(img.width, img.height);
                        i.setAlignment(img.alignment);
                        document.add(i);
                    } catch (Exception e) {
                        document.add(addParagraph("NO_IMAGE", fontNormal(true)));
                    }
                } else if (o instanceof Table) {
                    Table t = (Table) o;
                    PdfPTable table = new PdfPTable(t.getColumnPercent());

                    // t.setBorderColor(BaseColor.GRAY);
                    // t.setPadding(4);
                    // t.setSpacing(4);
                    // t.setBorderWidth(1);

                    for (TableColumn tc : t.getColumns()) {
                        PdfPCell c1 = new PdfPCell(new Phrase(tc.text, tc.getFont()));
                        c1.setBorderColor(tc.borderColor);
                        c1.setBackgroundColor(tc.backgroundColor);
                        c1.setHorizontalAlignment(tc.align);
                        table.addCell(c1);
                    }

                    for (TableCell tc : t.getCells()) {
                        PdfPCell cell = new PdfPCell(new Phrase(tc.text, tc.getFont()));
                        cell.setBorderColor(tc.borderColor);
                        cell.setBackgroundColor(tc.backgroundColor);
                        cell.setHorizontalAlignment(tc.align);
                        table.addCell(cell);
                    }

                    document.add(table);

                }

            }


            document.close();
            return myFile.getAbsolutePath();
        }catch (Exception e){
            Toast.makeText(context,"", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String createPDF(String folderName, String name, Invoice invoice)  {

        try {
            File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), folderName);
            if (!pdfFolder.exists()) {
                pdfFolder.mkdir();
            }

            File myFile = new File(pdfFolder, name + ".pdf");

            OutputStream output = new FileOutputStream(myFile);

            //los documentos pdf tienen un tamano especifico, no es como un listView. por esta raron se deben especificar:
            /**
             * Rectangle pagesize = new Rectangle(216f, 720f);
             * Document document = new Document(pagesize, 36f, 72f, 108f, 180f);
             */
            //pero itex nos provee tamanos standar para las paginas.
            Document document = new Document(PageSize.LETTER);
            /**
             * The PdfWriter class is responsible for writing the PDF file. And in this step,
             * you associate the blank document we created above with the PdfWriter class and point it to
             * the output stream where the PDF will be written to like so:
             */
            PdfWriter.getInstance(document, output);

            document.open();

            PdfPTable table = new PdfPTable(new float[]{50f, 50f});

            PdfPCell cell = new PdfPCell();
            Image img = Image.getInstance(invoice.getHeader().getLogo().getBytes());
            img.scaleAbsolute(invoice.getHeader().getLogo().width, invoice.getHeader().getLogo().height);
            cell.setHorizontalAlignment(invoice.getHeader().getLogo().alignment);
            cell.addElement(img);
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.LEFT);
            cell.setBorderWidth(3f);
            cell.setBorderColor(new BaseColor(22, 94, 171));
            cell.setPaddingRight(50f);
            table.addCell(cell);

            cell = new PdfPCell();
            PdfPTable t = new PdfPTable(1);
            PdfPCell subCell = new PdfPCell();
            subCell.addElement(addParagraph(invoice.getHeader().getName(), getFont(25, true), Paragraph.ALIGN_CENTER));
            subCell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            t.addCell(subCell);

            subCell = new PdfPCell();
            subCell.addElement(addParagraph(invoice.getHeader().getAddress(), getFont(12, true), Paragraph.ALIGN_CENTER));
            subCell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            subCell.setPaddingLeft(5f);
            t.addCell(subCell);

            subCell = new PdfPCell();
            subCell.addElement(addParagraph(invoice.getHeader().getPhone(), getFont(12, true), Paragraph.ALIGN_CENTER));
            subCell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            t.addCell(subCell);

            subCell = new PdfPCell();
            subCell.addElement(addParagraph(invoice.getHeader().getEmail(), getFont(12, true), Paragraph.ALIGN_CENTER));
            subCell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            t.addCell(subCell);


            cell.addElement(t);
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            cell.setPaddingLeft(5f);
            cell.setPaddingBottom(10f);
            table.addCell(cell);


            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(addParagraph(invoice.getLabel(), getFont(50, true), Paragraph.ALIGN_CENTER));
            cell.setColspan(2);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(new Chunk(" "));
            cell.setColspan(2);

            table.addCell(cell);


            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            Paragraph p = new Paragraph();
            p.add(new Chunk(invoice.getInvoiceNo(), getFont(16, true)));
            p.add(new Chunk("  |  ", getFont(20, true)));
            p.add(new Chunk(invoice.getDate()/*"Fecha: "+new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date())*/, getFont(16, true)));


            cell.addElement(p);
            cell.setColspan(2);
            table.addCell(cell);

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(new Chunk(" "));
            cell.setColspan(2);
            table.addCell(cell);
            document.add(table);


            table = new PdfPTable(new float[]{45f, 55f});
            cell = new PdfPCell();
            cell.addElement(addParagraph(invoice.getClient().getName(), getFont(16, true)));
            cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
            cell.setColspan(2);
            table.addCell(cell);

            if (invoice.getClient().getIdentification() != null) {
                cell = new PdfPCell();
                cell.addElement(addParagraph(invoice.getClient().getIdentification(), getFont(16, true)));
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                cell.setColspan(2);
                table.addCell(cell);
            }

            if (invoice.getClient().getPhone() != null) {
                cell = new PdfPCell();
                cell.addElement(addParagraph(invoice.getClient().getPhone(), getFont(16, true)));
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                cell.setColspan(2);
                table.addCell(cell);
            }

            if (invoice.getClient().getAddress() != null) {
                cell = new PdfPCell();
                cell.addElement(addParagraph(invoice.getClient().getAddress(), getFont(16, true)));
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.LEFT);
                cell.setColspan(2);
                table.addCell(cell);
            }
            document.add(table);

            document.add(addParagraph(" ", getFont(16, false)));


            table = new PdfPTable(new float[]{40f, 10f, 25f, 25f});

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(new Chunk("Descripcion", getFont(16, true)));
            cell.setBorderWidth(5f);
            cell.setPaddingBottom(5f);
            cell.setBorderColor(new BaseColor(22, 94, 171));
            table.addCell(cell);

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(new Chunk("Cant.", getFont(16, true)));
            cell.setBorderWidth(5f);
            cell.setPaddingBottom(5f);
            cell.setBorderColor(new BaseColor(22, 94, 171));
            table.addCell(cell);

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(addParagraph("Precio", getFont(16, true), Paragraph.ALIGN_CENTER));
            cell.setBorderWidth(5f);
            cell.setPaddingBottom(5f);
            cell.setBorderColor(new BaseColor(22, 94, 171));
            table.addCell(cell);

            cell = new PdfPCell();
            cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
            cell.addElement(addParagraph("Total", getFont(16, true), Paragraph.ALIGN_CENTER));
            cell.setBorderWidth(5f);
            cell.setPaddingBottom(5f);
            cell.setBorderColor(new BaseColor(22, 94, 171));
            table.addCell(cell);


            for (OrderDetail od : invoice.getOrder().getDetail()) {
                cell = new PdfPCell();
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(new Chunk(od.getDescription(), getFont(16, false)));
                cell.setPaddingBottom(5f);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(addParagraph(od.getQuantity(), getFont(16, false), Paragraph.ALIGN_CENTER));
                cell.setPaddingBottom(5f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(addParagraph(od.getPrice(), getFont(16, false), Paragraph.ALIGN_RIGHT));
                cell.setPaddingBottom(5f);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(addParagraph(od.getTotal(), getFont(16, false), Paragraph.ALIGN_RIGHT));
                cell.setPaddingBottom(5f);
                table.addCell(cell);
            }

            document.add(table);

            document.add(addParagraph(" ", getFont(16, false)));


            table = new PdfPTable(new float[]{30f, 30f, 40f});
            for (int i = 0; i < invoice.getOrder().getAmountsResume().size(); i++) {

                AmountsResume amountsResume = invoice.getOrder().getAmountsResume().get(i);
                boolean last = i == invoice.getOrder().getAmountsResume().size() - 1;

                cell = new PdfPCell();
                if (last) {
                    cell.setBackgroundColor(new BaseColor(22, 94, 171));
                }
                cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(new Chunk(amountsResume.getName(), last ? getFont(18, true, BaseColor.WHITE) : getFont(16, true)));
                table.addCell(cell);

                cell = new PdfPCell();
                if (last) {
                    cell.setBackgroundColor(new BaseColor(22, 94, 171));
                }
                cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(addParagraph(amountsResume.getAmount(), last ? getFont(20, false, BaseColor.WHITE) : getFont(16, false), Paragraph.ALIGN_RIGHT));
                table.addCell(cell);

                cell = new PdfPCell();
                if (last) {
                    cell.setBackgroundColor(new BaseColor(22, 94, 171));
                }
                cell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
                cell.addElement(new Chunk(" ", getFont(16, true)));
                table.addCell(cell);


            }
            document.add(table);

            document.add(addParagraph(" ", getFont(16, false)));


            if (invoice.getFooter() != null) {
                document.add(addParagraph(invoice.getFooter(), getFont(12, true), Paragraph.ALIGN_CENTER));
            }


            document.close();
            return myFile.getAbsolutePath();
        }catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
