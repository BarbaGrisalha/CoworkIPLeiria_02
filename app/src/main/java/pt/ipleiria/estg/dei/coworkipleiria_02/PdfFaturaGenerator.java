package pt.ipleiria.estg.dei.coworkipleiria_02;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfFaturaGenerator {

    public static void gerarFatura(Context context, Reserva reserva) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Fatura_Cowork_" + reserva.getSala().getNome().replace(" ", "_") + "_" + timeStamp + ".pdf";

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        int y = 40;
        int leftMargin = 40;

        // Cabeçalho - Padrão Portugal
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        canvas.drawText("FATURA SIMPLIFICADA", leftMargin, y, paint);
        y += 35;

        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        canvas.drawText("Cowork Ipleiria", leftMargin, y, paint);
        y += 20;
        canvas.drawText("NIF: 509 123 456", leftMargin, y, paint);
        y += 20;
        canvas.drawText("Rua da Cooperação, 123 - 2410-116 Leiria", leftMargin, y, paint);
        y += 30;

        canvas.drawText("Nº Fatura: FC" + timeStamp.substring(8), leftMargin, y, paint);
        y += 20;
        canvas.drawText("Data Emissão: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()), leftMargin, y, paint);
        y += 40;

        // Linha separadora
        paint.setStrokeWidth(1);
        canvas.drawLine(leftMargin, y, 555, y, paint);
        y += 30;

        // Dados cliente
        canvas.drawText("Cliente: Utilizador da App", leftMargin, y, paint);
        y += 20;
        canvas.drawText("NIF: 999 999 999", leftMargin, y, paint);
        y += 40;

        // Detalhes reserva
        canvas.drawText("Descrição:", leftMargin, y, paint);
        y += 20;
        canvas.drawText("Sala: " + reserva.getSala().getNome() + " (" + reserva.getSala().getTipo().name().replace("_", " ") + ")", leftMargin, y, paint);
        y += 20;
        canvas.drawText("Data: " + reserva.getData(), leftMargin, y, paint);
        y += 20;
        canvas.drawText("Horário: " + reserva.getHoraInicio() + " às " + reserva.getHoraFim(), leftMargin, y, paint);
        y += 20;
        canvas.drawText("Duração: " + reserva.getDuracaoHoras() + " hora(s)", leftMargin, y, paint);
        y += 30;

        // Total
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("TOTAL A PAGAR: " + String.format("%.2f €", reserva.getPrecoTotal()), leftMargin, y, paint);
        y += 60;

        // QR Code fictício (fixo)
        Bitmap qrBitmap = generateQRCode("ATCUD:123456789/2026-ABCDEF-HASH123", 150, 150);
        if (qrBitmap != null) {
            canvas.drawBitmap(qrBitmap, leftMargin, y, null);
            canvas.drawText("QR Code ATCUD (Validação AT)", leftMargin + 170, y + 80, paint);
        }

        document.finishPage(page);

        Uri uri = null;
        File file = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API 29+
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/FaturasCowork");

                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            } else { // API < 29
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File subDir = new File(downloadsDir, "FaturasCowork");
                if (!subDir.exists()) subDir.mkdirs();
                file = new File(subDir, fileName);

                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                fos.close();
                uri = Uri.fromFile(file);
            }

            if (uri != null) {
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    document.writeTo(outputStream);
                    outputStream.close();
                }

                Toast.makeText(context, "Fatura salva em Downloads/FaturasCowork: " + fileName, Toast.LENGTH_LONG).show();

                // Abrir o PDF
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "PDF salvo! Verifique em Downloads.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
    }

    private static Bitmap generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}