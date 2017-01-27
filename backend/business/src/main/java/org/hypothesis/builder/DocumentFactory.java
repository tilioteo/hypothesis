package org.hypothesis.builder;

import org.hypothesis.interfaces.Document;

public interface DocumentFactory {

	Document mergeSlideDocument(Document templateDocument, Document contentDocument);

	Document createEventDataDocument();

}