package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.bluetoothlibrary.Printer.Print;
import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.far.farpdf.Entities.AmountsResume;
import com.far.farpdf.Entities.Client;
import com.far.farpdf.Entities.Header;
import com.far.farpdf.Entities.Invoice;
import com.far.farpdf.Entities.Order;
import com.far.farpdf.Entities.OrderDetail;
import com.far.farpdf.Entities.Payment;
import com.far.farpdf.Objects.Image;
import com.far.farpdf.Objects.Line;
import com.far.farpdf.Objects.LineItem;
import com.far.farpdf.Objects.Table;
import com.far.farpdf.Objects.TableCell;
import com.far.farpdf.Objects.TableColumn;
import com.far.farpdf.PDF.Writer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ReceiptController {
    public static final String TABLE_NAME ="RECEIPTS";
    //public static final String TABLE_NAME_HISTORY ="RECEIPTS_HISTORY";
    public static  String CODE = "code",CODEUSER = "codeuser", CODESALE = "codesale",CODECLIENT="codeclient", STATUS = "status",  NCF = "ncf" ,SUBTOTAL="subtotal",TAXES = "taxes", DISCOUNT="discount", TOTAL = "total",PAIDAMOUNT="paidamount",
            DATE = "date", MDATE = "mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+CODEUSER+" TEXT,"+CODESALE+" TEXT, "+CODECLIENT+" TEXT,  "+STATUS+" TEXT, "+NCF+" TEXT,"+SUBTOTAL+" NUMERIC,"+TAXES+" NUMERIC,"+DISCOUNT+" NUMERIC, "+TOTAL+", "+PAIDAMOUNT+" NUMERIC, " +
            ""+DATE+" TEXT, "+MDATE+" TEXT)";
    public static String[] columns = new String[]{CODE,CODEUSER,CODESALE, CODECLIENT, STATUS, NCF,SUBTOTAL,TAXES,DISCOUNT,TOTAL,PAIDAMOUNT,  DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static ReceiptController instance;
    private ReceiptController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static ReceiptController getInstance(Context c){
        if(instance == null){
            instance = new ReceiptController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersReceipts);
        return reference;
    }


    public long insert(Receipts r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(CODESALE, r.getCodesale());
        cv.put(CODECLIENT, r.getCodeclient());
        cv.put(STATUS, r.getStatus());
        cv.put(NCF,r.getNcf());
        cv.put(SUBTOTAL, r.getSubtotal());
        cv.put(TAXES, r.getTaxes());
        cv.put(DISCOUNT, r.getDiscount());
        cv.put(TOTAL, r.getTotal());
        cv.put(PAIDAMOUNT, r.getPaidamount());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Receipts r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(CODESALE, r.getCodesale());
        cv.put(CODECLIENT, r.getCodeclient());
        cv.put(STATUS, r.getStatus());
        cv.put(NCF,r.getNcf());
        cv.put(SUBTOTAL, r.getSubtotal());
        cv.put(TAXES, r.getTaxes());
        cv.put(DISCOUNT, r.getDiscount());
        cv.put(TOTAL, r.getTotal());
        cv.put(PAIDAMOUNT, r.getPaidamount());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,CODE+"=?", new String[]{r.getCode()});
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Receipts> getReceipts(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Receipts> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DATE;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Receipts(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Receipts getReceiptByCode(String code){
       ArrayList<Receipts> array = getReceipts(new String[]{CODE}, new String[]{code}, null);
       return array.size()>0?array.get(0):null;
    }


    public void getDataFromFireBase(OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> receipts = getReferenceFireStore().get();
            receipts.addOnSuccessListener(onSuccessListener);
            receipts.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Receipts object = dc.getDocument().toObject(Receipts.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCode()};
                            delete(where, args);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendToFireBase(Receipts receipt){
        try {
            WriteBatch lote = db.batch();
                if (receipt.getMdate() == null) {
                    lote.set(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
                } else {
                    lote.update(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
                }

            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    /*public ArrayList<ReceiptSavedModel> getReceiptsSM(String codeAreaDetail){
        ArrayList<ReceiptSavedModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + CODE + " as CODE,r."+STATUS+" as STATUS, r." + CODEUSER + " as CODEUSER, u." + UsersController.USERNAME + " as USERNAME, r." + NCF + " as NCF, " +
                    "ad." + AreasDetailController.CODEAREA + " as CODEAREA, a." + AreasController.DESCRIPTION + " as AREADESCRIPTION, ad." + AreasDetailController.CODE + " as CODEAREADETAIL, ad." + AreasDetailController.DESCRIPTION + " as AREADETAILDESCRIPTION, " +
                    "r." + SUBTOTAL + " as SUBTOTAL, r." + TAXES + " as TAXES, r." + DISCOUNT + " as DISCOUNT, r." + TOTAL + " as TOTAL, r." + DATE + " as DATE, r." + MDATE + " as MDATE " +
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + UsersController.TABLE_NAME + " u ON r." + CODEUSER + " = u." + UsersController.CODE + " " +
                    "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on r." + CODEAREADETAIL + " = ad." + AreasDetailController.CODE + " " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on ad." + AreasDetailController.CODEAREA + " = a." + AreasController.CODE + " " +
                    "ORDER BY r." + DATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                result.add(new ReceiptSavedModel(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }*/


    public ArrayList<ReceiptRowModel> getReceiptsRM(String where, String[]args){
        ArrayList<ReceiptRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + CODE + " as CODE,r."+STATUS+" as STATUS, r." + DATE + " as DATE, c." + ClientsController.CODE + " as CODECLIENT, c." + ClientsController.NAME + " as CLIENTNAME, " +
                    "c." + ClientsController.DOCUMENT + " as DOCUMENT, c." + ClientsController.PHONE + " as PHONE,"+
                    "r."+SUBTOTAL+" as SUBTOTAL, r."+TAXES+" as TAXES,  r."+DISCOUNT+" as DISCOUNT,  r." + TOTAL + " as TOTAL, r."+PAIDAMOUNT+" as PAID " +
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + ClientsController.TABLE_NAME + " c ON r." + CODECLIENT + " = c." + ClientsController.CODE + " " +
                    "WHERE "+where+" "+
                    "ORDER BY r." + DATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while (c.moveToNext()) {
                String code = c.getString(c.getColumnIndex("CODE"));
                String status = c.getString(c.getColumnIndex("STATUS"));
                String date = c.getString(c.getColumnIndex("DATE"));
                String codeClient = c.getString(c.getColumnIndex("CODECLIENT"));
                String clientName = c.getString(c.getColumnIndex("CLIENTNAME"));
                String document = c.getString(c.getColumnIndex("DOCUMENT"));
                String phone = c.getString(c.getColumnIndex("PHONE"));
                double subTotal = c.getDouble(c.getColumnIndex("SUBTOTAL"));
                double taxes = c.getDouble(c.getColumnIndex("TAXES"));
                double discount = c.getDouble(c.getColumnIndex("DISCOUNT"));
                double total = c.getDouble(c.getColumnIndex("TOTAL"));
                double paid = c.getDouble(c.getColumnIndex("PAID"));

               //String code, String status, String codeClient, String clientName, String clientDocument, String clientPhone, String date, double total
                result.add(new ReceiptRowModel(code,status,codeClient,clientName,document,phone,date,subTotal, discount, taxes, total, paid));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }


    /**
     * obtiene la fecha mas alta guardada en la base de datos local en la tabla.
     * @param status
     * @return
     */
    public Date getLastDateSaved(String status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" DESC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }

    /**
     * obtiene la fecha mas baja guardada en la base de datos local en la tabla .
     * @param status
     * @return
     */
    public Date getLastInitialDateSaved(String status){
        Date date = null;
        String sql = "SELECT "+DATE+" as DATE " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+STATUS+" = '"+status+"' "+
                "ORDER BY "+DATE+" ASC " +
                "LIMIT 1 ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("DATE")));
        }c.close();
        return date;
    }



    public void searchChanges(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }

    }


    public void consumeQuerySnapshot(QuerySnapshot querySnapshot){
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Receipts obj = doc.toObject(Receipts.class);
                if(update(obj) <=0){
                    insert(obj);
                }
            }
        }

    }



    public void searchReceiptByCodeFromFireBase(String code, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){
            getReferenceFireStore().
                    whereEqualTo(CODE, code).
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);

    }


    public void searchReceiptFilteredFromFireBase(String status,String codeClient,Date ini, Date end,  OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){
        Query query =getReferenceFireStore().
                whereEqualTo(STATUS, status);
        if(codeClient != null){
            query= query.whereEqualTo(CODECLIENT, codeClient);
        }

        if(ini != null){
            query= query.whereGreaterThanOrEqualTo(DATE, ini);
        }

        if(end != null){
            query= query.whereLessThanOrEqualTo(DATE, end);
        }

       query.get().
                addOnSuccessListener(success).addOnCompleteListener(complete).
                addOnFailureListener(failure);

    }

    public  String printReceipt(String codeReceipt)throws Exception{

        Print p = new Print(context,Print.PULGADAS.PULGADAS_2);
        CompanyController.getInstance(context).addCompanyToPrint(p);

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        p.drawLine();
        p.drawText("Detalle");
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);

        double total = 0.0;
        for(SalesDetailModel sdm :SalesController.getInstance(context).getSaleDetailModels(codeReceipt)){
            total+=Double.parseDouble(sdm.getTotal());
            p.drawText(sdm.getProductDescription());
            p.drawText(Funciones.reservarCaracteres("Cant:"+sdm.getQuantity(),9)+Funciones.reservarCaracteres(sdm.getMeasureDescription(),10)+Funciones.reservarCaracteresAlinearDerecha(" $"+Funciones.formatDecimal(sdm.getTotal()),13));
        }
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_RIGHT);
        p.drawText("Total:"+Funciones.formatDecimal(total), Print.TEXT_ALIGN.RIGHT);


        p.printText("02:3D:D3:DB:D5:06");
        return null;
    }

    public String createPDF(String codeReceipt, int format) throws Exception{
        Writer writer = new Writer(context);
       if(format == 1){
           return writer.createPDF("BaseSales", "R_"+codeReceipt,format1(codeReceipt));
       }else if(format == 2){
           return writer.createPDF("BaseSales", "R_"+codeReceipt,format2(codeReceipt));
       }
        return null;
    }

    public Invoice format2(String codeReceipt) throws Exception{

        Receipts receipts = getReceiptByCode(codeReceipt);
        Company company = CompanyController.getInstance(context).getCompany();
        Header header=null;
        if(company!= null){
            Bitmap logo =Picasso.with(context).load(company.getLOGO()).get();
            Image i = new Image(/*BitmapFactory.decodeResource(context.getResources(),R.drawable.optica)*/logo);
            header = new Header(company.getNAME(), company.getADDRESS(), Funciones.formatPhone(company.getPHONE()), company.getADDRESS2(), i);
        }else{
            Image i = new Image(BitmapFactory.decodeResource(context.getResources(),R.drawable.optica));
            header = new Header("NONE", "NONE", "NONE", "NONE", i);
        }

        Clients clients = ClientsController.getInstance(context).getClientByCode(receipts.getCodeclient());
        Client c = new Client(clients.getNAME(), clients.getDOCUMENT(), clients.getPHONE(), "address");

        ArrayList<OrderDetail> detail = new ArrayList<>();
        for(SalesDetailModel sd : SalesController.getInstance(context).getSaleDetailModels(receipts.getCode())){
            detail.add(new OrderDetail(sd.getQuantity(), sd.getProductDescription(),sd.getTotal(), sd.getTotal()));
        }
        ArrayList<AmountsResume> amountsResumes = new ArrayList<>();
        amountsResumes.add(new AmountsResume("Total:", "???"));
        Order o = new Order(detail,amountsResumes);

        ArrayList<Payment> payments = new ArrayList<>();
        for(com.far.basesales.CloudFireStoreObjects.Payment payment: PaymentController.getInstance(context).getPayments(PaymentController.CODERECEIPT+"=?", new String[]{receipts.getCode()}, null)){
            String name = payment.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)?"CREDITO":"EFECTIVO";
            payments.add(new Payment(name, payment.getTOTAL()+""));
        }

        Invoice invoice = new Invoice(header,"Recibo", "footer", receipts.getCode(),Funciones.getFormatedDateRepDom(new Date()),c, o, payments);
        return invoice;
    }

    public ArrayList<Object> format1(String codeReceipt) throws Exception{
        ArrayList<Object> obj = new ArrayList<>();

        Receipts receipts = getReceiptByCode(codeReceipt);
        Company company = CompanyController.getInstance(context).getCompany();
        Header header=null;
        if(company!= null){
            Bitmap logo =Picasso.with(context).load(company.getLOGO()).get();
            Image i = new Image(/*BitmapFactory.decodeResource(context.getResources(),R.drawable.optica)*/logo);
            header = new Header(company.getNAME(), company.getADDRESS(), Funciones.formatPhone(company.getPHONE()), company.getADDRESS2(), i);
        }else{
            Image i = new Image(BitmapFactory.decodeResource(context.getResources(),R.drawable.optica));
            header = new Header("NONE", "NONE", "NONE", "NONE", i);
        }

        Clients clients = ClientsController.getInstance(context).getClientByCode(receipts.getCodeclient());
        Client c = new Client(clients.getNAME(), clients.getDOCUMENT(), clients.getPHONE(), "address");

        Users u = UsersController.getInstance(context).getUserByCode(receipts.getCodeuser());


        obj.add(header.getLogo().center());
        //obj.add(new LineItem(header.getName()).bold().center());
        obj.add(new LineItem(header.getAddress()).bold().center());
        obj.add(new LineItem(header.getPhone()).bold().center());
        obj.add(new LineItem(" "));
        obj.add(new LineItem("Fecha: "+new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a").format(receipts.getDate())));
        obj.add(new LineItem("No: "+receipts.getCode()));
        obj.add(new LineItem(" "));
        obj.add(new LineItem("Vendedor: "+u.getUSERNAME()));
        obj.add(new LineItem("Cliente:  "+c.getName()));
        obj.add(new LineItem(" "));
        obj.add(new LineItem("FACTURA COMERCIAL").size(25).bold().center());
        obj.add(new LineItem(" "));
        obj.add(new Line());
        obj.add(new LineItem(" "));

        ArrayList<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("Cantidad",20));
        columns.add(new TableColumn("Descripcion",50));
        columns.add(new TableColumn("Precio",30));

        ArrayList<TableCell> cells = new ArrayList<>();

        for(SalesDetailModel sd : SalesController.getInstance(context).getSaleDetailModels(receipts.getCode())){
            cells.add(new TableCell(sd.getQuantity()).center());
            cells.add(new TableCell(sd.getMeasureDescription()+" "+sd.getProductDescription()).left());
            cells.add(new TableCell("$"+Funciones.formatMoney(Double.parseDouble(sd.getTotal()))).right());
        }

        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").noBorder());


        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell("Total:").bold().right().noBorder());
        cells.add(new TableCell("$"+Funciones.formatMoney(receipts.getTotal())).bold().right().noBorder());

        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").right().noBorder());
        cells.add(new TableCell(" ").right().noBorder());

        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell("Pagos").bold().size(20).center().noBorder());
        cells.add(new TableCell(" ").noBorder());

        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").noBorder());

        double totalPayment = 0.0;
        ArrayList<com.far.basesales.CloudFireStoreObjects.Payment> payments =PaymentController.getInstance(context).getPayments(PaymentController.CODERECEIPT+" = ?", new String[]{receipts.getCode()}, null);
        for(com.far.basesales.CloudFireStoreObjects.Payment p : payments){
            totalPayment+=p.getTOTAL();

            String label ="";
            if(p.getTYPE().equals(CODES.PAYMENTTYPE_CASH)){
                label="Efectivo: ";
            }else if(p.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)){
                label="Credito:  ";
            }

            cells.add(new TableCell(" "));
            cells.add(new TableCell(label).right());
            cells.add(new TableCell("$"+Funciones.formatMoney(p.getTOTAL())).right());
        }

        cells.add(new TableCell(" "));
        cells.add(new TableCell("Total Pagado").right());
        cells.add(new TableCell("$"+Funciones.formatMoney(totalPayment)).right());

        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").noBorder());
        cells.add(new TableCell(" ").right().noBorder());

        obj.add(new Table(columns, cells));


        obj.add(new LineItem(" "));
        obj.add(new Line().thickness(2));
        obj.add(new LineItem(" "));
        //obj.add(new LineItem("30 dias de garantia. Favor enviar esta factura al Email:opticaellocario@wepa.com con un dia de antelacion.").size(10).center());

        return obj;

    }

}
