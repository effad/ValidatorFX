package net.synedra.validatorfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
class DefaultDecorationTest extends TestBase {

	@Test
	void testFactory() {
		Function<ValidationMessage, Decoration> defaultDefaultFactory = DefaultDecoration.getFactory(); 
		assertNotNull(defaultDefaultFactory);		
		Function<ValidationMessage, Decoration> testFactory = m -> null;
		try {
			DefaultDecoration.setFactory(testFactory);
			assertEquals(DefaultDecoration.getFactory(), testFactory);
		} finally {
			DefaultDecoration.setFactory(defaultDefaultFactory);
		}		
	}
	
	@Test
	void testStyleClassDecoration() {
		ValidationMessage message = new ValidationMessage(Severity.WARNING, "warning");
		Decoration decoration = DefaultDecoration.createStyleClassDecoration(message);
		assertNotNull(decoration);
		assertTrue(decoration instanceof StyleClassDecoration);
	}
	
	@Test
	void testGraphicDecoration() {
		ValidationMessage message = new ValidationMessage(Severity.WARNING, "warning");
		Decoration decoration = DefaultDecoration.createGraphicDecoration(message);
		assertNotNull(decoration);
		assertTrue(decoration instanceof GraphicDecoration);
	}
}
