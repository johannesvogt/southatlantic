/*
 *  Tiled Map Editor, (c) 2004-2006
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 */

package cb_painter;

import java.lang.String;
import java.io.Writer;
import java.io.IOException;
import java.util.Stack;

/**
 * A simple helper class to write an XML file, based on
 * http://www.xmlsoft.org/html/libxml-xmlwriter.html
 *
 * @version $Id: XMLWriter.java 683 2006-06-25 14:17:37Z bjorn $
 */
public class XMLWriter
{
    private boolean bIndent = true;
    private String indentString = " ";
    private String newLine = "\n";
    private final Writer w;

    private final Stack openElements;
    private boolean bStartTagOpen;
    private boolean bDocumentOpen;


    public XMLWriter(Writer writer) {
        openElements = new Stack();
        w = writer;
    }


    public void setIndent(boolean bIndent) {
        this.bIndent = bIndent;
        newLine = bIndent ? "\n" : "";
    }

    public void setIndentString(String indentString) {
        this.indentString = indentString;
    }


    public void startDocument() throws IOException {
        startDocument("1.0");
    }

    public void startDocument(String version) throws IOException {
        w.write("<?xml version=\"" + version + "\"?>" + newLine);
        bDocumentOpen = true;
    }

    public void startElement(String name)
        throws IOException {
        if (!bDocumentOpen) {
            throw new IOException(
                    "Can't start new element, no open document.");
        }

        if (bStartTagOpen) {
            w.write(">" + newLine);
        }

        writeIndent();
        w.write("<" + name);

        openElements.push(name);
        bStartTagOpen = true;
    }


    public void endDocument() throws IOException {
        // End all open elements.
        while (!openElements.isEmpty()) {
            endElement();
        }
    }

    public void endElement() throws IOException {
        String name = (String)openElements.pop();

        // If start tag still open, end with />, else with </name>.
        if (bStartTagOpen) {
            w.write("/>" + newLine);
            bStartTagOpen = false;
        } else {
            writeIndent();
            w.write("</" + name + ">" + newLine);
        }

        // Set document closed when last element is closed
        if (openElements.isEmpty()) {
            bDocumentOpen = false;
        }
    }


    public void writeAttribute(String name, String content)
        throws IOException {
        if (bStartTagOpen) {
            w.write(" " + name + "=\"" + (content != null ? content : "")
                    + "\"");
        } else {
            throw new IOException(
                    "Can't write attribute without open start tag.");
        }
    }

    public void writeAttribute(String name, int content)
        throws IOException {
        writeAttribute(name, String.valueOf(content));
    }

    public void writeAttribute(String name, float content)
        throws IOException {
        writeAttribute(name, String.valueOf(content));
    }

    public void writeCDATA(String content) throws IOException {
        if (bStartTagOpen) {
            w.write(">" + newLine);
            bStartTagOpen = false;
        }

        writeIndent();
        w.write(content + newLine);
    }

    public void writeComment(String content) throws IOException {
        if (bStartTagOpen) {
            w.write(">" + newLine);
            bStartTagOpen = false;
        }

        writeIndent();
        w.write("<!-- " + content + " -->" + newLine);
    }

    public void writeElement(String name, String content)
        throws IOException {
        startElement(name);
        writeCDATA(content);
        endElement();
    }


    private void writeIndent() throws IOException {
        if (bIndent) {
            for (int i = 0; i < openElements.size(); i++) {
                w.write(indentString);
            }
        }
    }
}
