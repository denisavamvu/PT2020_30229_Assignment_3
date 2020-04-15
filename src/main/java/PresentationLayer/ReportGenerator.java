package PresentationLayer;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

public class ReportGenerator <T>{

    public ReportGenerator(){
    }
    //create table header containing column names (field names) for each table
    private void addTableHeader(PdfPTable table,T obj) {
        String [] columnName= new String[obj.getClass().getDeclaredFields().length];
        int i=0;
        for(Field field: obj.getClass().getDeclaredFields()){
            field.setAccessible(true);
            columnName[i++]=field.getName();
        }
        Stream.of(columnName)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.YELLOW );
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    /**
     * create a report in pdf format
     * @param result objects returned by an SQL Select Statement, to be written in a tabular form
     * @param number order number of the file
     * @throws FileNotFoundException if the file does not exists
     */
    public void createReport(List<T> result,int number) throws FileNotFoundException {
        if(result.size()==0)
            return;
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Report "+result.get(0).getClass().getSimpleName()+number+".pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(result.get(0).getClass().getDeclaredFields().length); //nr de coloane
        addTableHeader(table, result.get(0));
        for(T object: result) {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object fieldValue;
                field.setAccessible(true);
                try {
                    fieldValue = field.get(object);
                    PdfPCell cell = new PdfPCell(new Paragraph(fieldValue.toString()));
                    table.addCell(cell);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
            try {
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        document.close();
    }

    /**
     * create a pdf file containing a Bill for an order
     * @param filename of the file to be created
     * @param toWrite information to be written in the file
     */
    public void generateBill(String filename, String toWrite){
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename+".pdf"));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk(toWrite, font);

        try {
            document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
    }

}
