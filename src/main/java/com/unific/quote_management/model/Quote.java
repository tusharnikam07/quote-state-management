package com.unific.quote_management.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.unific.quote_management.util.PdfGenerator;

@Service
public class Quote {

	private String id;
	private QuoteState state;
	private List<String> lineItems;
	private LocalDate validityDate;
	private TemplateEngine templateEngine;

	public Quote() {
		this.id = UUID.randomUUID().toString();
		this.state = QuoteState.DRAFT;
		this.lineItems = new ArrayList<>();

		TemplateEngine templateEngine = new TemplateEngine();
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode("HTML");
		resolver.setSuffix(".html");
		templateEngine.setTemplateResolver(resolver);

		this.templateEngine = templateEngine;
	}

	public String getId() {
		return id;
	}

	public QuoteState getState() {
		return state;
	}

	public void addLineItem(String item) {
		lineItems.add(item);
	}

	public void setValidityDate(LocalDate validityDate) {
		this.validityDate = validityDate;
	}

	public boolean reviewQuote() {
		if (lineItems.isEmpty()) {
			return false;
		}
		return true;
	}

	public void publish() {
		if (!reviewQuote()) {
			throw new IllegalStateException("Quote must have at least one line item to be published.");
		}
		if (state != QuoteState.DRAFT) {
			throw new IllegalStateException("Quote can only be published from the DRAFT state.");
		}
		this.state = QuoteState.PUBLISHED;
	}

	public void complete() {
		if (state != QuoteState.PUBLISHED) {
			throw new IllegalStateException("Quote can only be completed from the PUBLISHED state.");
		}
		this.state = QuoteState.COMPLETED;
	}

	public void expire() {
		if (state != QuoteState.PUBLISHED || validityDate == null || validityDate.isAfter(LocalDate.now())) {
			throw new IllegalStateException(
					"Quote can only be expired if it is PUBLISHED and past its validity period.");
		}
		this.state = QuoteState.EXPIRED;
	}

	public void archive() {
		if (state == QuoteState.COMPLETED || state == QuoteState.EXPIRED) {
			throw new IllegalStateException("COMPLETED or EXPIRED quotes can not be archived.");
		}
		this.state = QuoteState.ARCHIVED;
	}

	public void delete() {
		if (state != QuoteState.ARCHIVED) {
			throw new IllegalStateException("Only ARCHIVED quotes can be deleted.");
		}
		this.state = QuoteState.DELETED;
	}

	public String generateHtml() {
		Context context = new Context();
		context.setVariable("id", id);
		context.setVariable("state", state);
		context.setVariable("lineItems", lineItems);
		context.setVariable("validityDate", validityDate);
		return templateEngine.process("quoteTemplate", context);

	}

	public byte[] generatePdf() {
		try {
			String htmlContent = generateHtml();
			return PdfGenerator.generatePdfFromHtml(htmlContent);
		} catch (Exception e) {
			throw new RuntimeException("Error generating PDF", e);
		}
	}
}
