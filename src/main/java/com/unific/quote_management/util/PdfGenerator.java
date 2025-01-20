package com.unific.quote_management.util;

import java.io.ByteArrayOutputStream;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class PdfGenerator {

	public static byte[] generatePdfFromHtml(String htmlContent) throws Exception {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(htmlContent, "");
			builder.toStream(outputStream);
			builder.run();
			return outputStream.toByteArray();
		}
	}
}
