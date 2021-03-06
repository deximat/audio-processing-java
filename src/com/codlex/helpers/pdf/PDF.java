package com.codlex.helpers.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class PDF {

	public static void exportImageToPdf(String path, BufferedImage bufferedImage) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(path));

			document.open();
			Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
			Chunk chunk = new Chunk("This pdf is created by RN13/11's fancy application", font);

			document.add(chunk);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			Image iTextImage = Image.getInstance(baos.toByteArray());
			document.add(iTextImage);

			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String fileChooser(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Wav File");
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("pdf file.", Lists.newArrayList("pdf")));
		return fileChooser.showSaveDialog(stage).getAbsolutePath();
	}

}
