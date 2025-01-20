package com.unific.quote_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.unific.quote_management.model.Quote;
import com.unific.quote_management.model.QuoteState;

@SpringBootTest
class QuoteManagementApplicationTests {

	@Test
	void contextLoads() {
	}

	private Quote quote;

	@BeforeEach
	public void setUp() {
		quote = new Quote();
	}

	@Test
	public void testInitialState() {
		assertEquals(QuoteState.DRAFT, quote.getState());
	}

	@Test
	public void testPublishValidQuote() {
		quote.addLineItem("Item 1");
		quote.publish();
		assertEquals(QuoteState.PUBLISHED, quote.getState());
	}

	@Test
	public void testPublishWithoutLineItems() {
		Exception exception = assertThrows(IllegalStateException.class, quote::publish);
		assertEquals("Quote must have at least one line item to be published.", exception.getMessage());
	}

	@Test
	public void testCompleteFromPublishedState() {
		quote.addLineItem("Item 1");
		quote.publish();
		quote.complete();
		assertEquals(QuoteState.COMPLETED, quote.getState());
	}

	@Test
	public void testCompleteFromInvalidState() {
		Exception exception = assertThrows(IllegalStateException.class, quote::complete);
		assertEquals("Quote can only be completed from the PUBLISHED state.", exception.getMessage());
	}

	@Test
	public void testQuoteReviewWithLinesAdded() {
		quote.addLineItem("Item 1");
		assertEquals(true, quote.reviewQuote());
	}

	@Test
	public void testQuoteReviewWithoutLinesAdded() {
		assertEquals(false, quote.reviewQuote());
	}

	@Test
	public void testExpireFromPublishedState() {
		quote.addLineItem("Item 1");
		quote.setValidityDate(LocalDate.now().minusDays(1));
		quote.publish();
		quote.expire();
		assertEquals(QuoteState.EXPIRED, quote.getState());
	}

	@Test
	public void testExpireInvalidState() {
		quote.addLineItem("Item 1");
		Exception exception = assertThrows(IllegalStateException.class, quote::expire);
		assertEquals("Quote can only be expired if it is PUBLISHED and past its validity period.",
				exception.getMessage());
	}

	@Test
	public void testExpireBeforeExpiryDate() {
		quote.addLineItem("Item 1");
		quote.setValidityDate(LocalDate.now().plusDays(1));
		quote.publish();
		Exception exception = assertThrows(IllegalStateException.class, quote::expire);
		assertEquals("Quote can only be expired if it is PUBLISHED and past its validity period.",
				exception.getMessage());
	}

	@Test
	public void testArchiveFromDraft() {
		quote.archive();
		assertEquals(QuoteState.ARCHIVED, quote.getState());
	}

	@Test
	public void testArchiveFromPublishedState() {
		quote.addLineItem("Item 1");
		quote.publish();
		quote.archive();
		assertEquals(QuoteState.ARCHIVED, quote.getState());
	}

	@Test
	public void testArchiveFromExpiredState() {
		quote.addLineItem("Item 1");
		quote.setValidityDate(LocalDate.now().minusDays(1));
		quote.publish();
		quote.expire();
		Exception exception = assertThrows(IllegalStateException.class, quote::archive);
		assertEquals("COMPLETED or EXPIRED quotes can not be archived.", exception.getMessage());
	}

	@Test
	public void testArchiveFromCompletedState() {
		quote.addLineItem("Item 1");
		quote.publish();
		quote.complete();
		Exception exception = assertThrows(IllegalStateException.class, quote::archive);
		assertEquals("COMPLETED or EXPIRED quotes can not be archived.", exception.getMessage());
	}

	@Test
	public void testDeleteFromArchivedState() {
		quote.addLineItem("Item 1");
		quote.publish();
		quote.archive();
		quote.delete();
		assertEquals(QuoteState.DELETED, quote.getState());
	}

	@Test
	public void testDeleteInvalidState() {
		Exception exception = assertThrows(IllegalStateException.class, quote::delete);
		assertEquals("Only ARCHIVED quotes can be deleted.", exception.getMessage());
	}

	@Test
	public void testGenerateHtml() {
		quote.addLineItem("Item 1");
		quote.publish();
		String html = quote.generateHtml();
		assertTrue(html.contains("Item 1"));
		assertTrue(html.contains("PUBLISHED"));
	}

	@Test
	public void testGeneratePdf() {
		quote.addLineItem("Item 1");
		quote.publish();
		byte[] pdf = quote.generatePdf();
		assertNotNull(pdf);
		assertTrue(pdf.length > 0);
	}

}
