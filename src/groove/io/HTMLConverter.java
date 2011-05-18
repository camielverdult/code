// GROOVE: GRaphs for Object Oriented VErification
// Copyright 2003--2007 University of Twente

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
/*
 * $Id: Converter.java,v 1.10 2008-01-30 09:32:02 iovka Exp $
 */
package groove.io;

import static groove.view.aspect.AspectKind.REMARK;
import groove.gui.jgraph.AspectJGraph;
import groove.util.Colors;
import groove.util.Groove;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jgraph.graph.GraphConstants;

/**
 * Performs conversions to and from HTML code.
 * @author Arend Rensink
 * @version $Revision: 3122 $
 */
public class HTMLConverter {

    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public String toHtml(Object text) {
        return toHtml(new StringBuilder(text.toString())).toString();
    }

    /**
     * Converts a piece of text to HTML by replacing special characters to their
     * HTML encodings.
     */
    static public StringBuilder toHtml(StringBuilder text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
            case '/':
                text.replace(i, i + 1, "&#47;");
                i += 4;
                break;
            case '<':
                text.replace(i, i + 1, "&lt;");
                i += 3;
                break;
            case '>':
                text.replace(i, i + 1, "&gt;");
                i += 3;
                break;
            case '\n':
                text.replace(i, i + 1, HTML_LINEBREAK);
                i += HTML_LINEBREAK.length() - 1;
                break;
            case Groove.LC_PI:
                text.replace(i, i + 1, HTML_PI);
            }
        }
        return text;
    }

    /**
     * Returns an HTML tag embedder.
     */
    static public HTMLTag createHtmlTag(String tag) {
        return new HTMLTag(tag);
    }

    /**
     * Returns an HTML tag embedded with an argument string.
     */
    static public HTMLTag createHtmlTag(String tag, String attribute,
            String arguments) {
        return new HTMLTag(tag, attribute, arguments);
    }

    /**
     * Returns a span tag with a style argument.
     */
    static public HTMLTag createSpanTag(String arguments) {
        return new HTMLTag(SPAN_TAG_NAME, STYLE_ATTR_NAME, arguments);
    }

    /**
     * Returns a span tag with a style argument.
     */
    static public HTMLTag createDivTag(String arguments) {
        return new HTMLTag(DIV_TAG_NAME, STYLE_ATTR_NAME, arguments);
    }

    /**
     * Returns a HTML span tag that imposes a given color on a text.
     */
    static public HTMLTag createColorTag(Color color) {
        HTMLTag result = colorTagMap.get(color);
        if (result == null) {
            StringBuffer arg = new StringBuffer();
            int red = color.getRed();
            int blue = color.getBlue();
            int green = color.getGreen();
            int alpha = color.getAlpha();
            arg.append("color: rgb(");
            arg.append(red);
            arg.append(",");
            arg.append(green);
            arg.append(",");
            arg.append(blue);
            arg.append(");");
            if (alpha != MAX_ALPHA) {
                // the following is taken from the internet; it is to make
                // sure that all html interpretations set the opacity correctly.
                double alphaFraction = ((double) alpha) / MAX_ALPHA;
                arg.append("float:left;filter:alpha(opacity=");
                arg.append((int) (100 * alphaFraction));
                arg.append(");opacity:");
                arg.append(alphaFraction);
                arg.append(";");
            }
            result = HTMLConverter.createSpanTag(arg.toString());
            colorTagMap.put(color, result);
        }
        return result;
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public String toUppercase(String text, boolean upper) {
        return toUppercase(new StringBuilder(text), upper).toString();
    }

    /** Converts the first letter of a given string to upper- or lowercase. */
    static public StringBuilder toUppercase(StringBuilder text, boolean upper) {
        Character firstChar = text.charAt(0);
        if (upper) {
            firstChar = Character.toUpperCase(firstChar);
        } else {
            firstChar = Character.toLowerCase(firstChar);
        }
        text.replace(0, 1, firstChar.toString());
        return text;
    }

    /**
     * Strips the color tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was blue, 2 if green, 3 if red and 0 otherwise.
     */
    public static int removeColorTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int result = 0;
        if (!blue.off(htmlLine).equals(originalLine)) {
            result = 1;
        } else if (!green.off(htmlLine).equals(originalLine)) {
            result = 2;
        } else if (!red.off(htmlLine).equals(originalLine)) {
            result = 3;
        } else if (!remark.off(htmlLine).equals(originalLine)) {
            result = 4;
        }
        return result;
    }

    /**
     * Strips the font tags from the HTML line.
     * @param htmlLine the line to be striped
     * @return 1 if the line was bold, 2 if the line was italic, 3 if the line
     *         was both bold and italic, and 0 otherwise.
     */
    public static int removeFontTags(StringBuilder htmlLine) {
        String originalLine = htmlLine.toString();
        int bold = 0;
        int italic = 0;
        if (!STRONG_TAG.off(htmlLine).equals(originalLine)) {
            bold = 1;
            originalLine = htmlLine.toString();
        }
        if (!ITALIC_TAG.off(htmlLine).equals(originalLine)) {
            italic = 2;
        }
        return bold + italic;
    }

    // The readable codes do not work on the Mac in some situations. Replaced
    // them with the numeric codes - this fixes it. -- Maarten
    /** HTML greater than symbol. */
    static public final String HTML_GT = "&#62;"; // &gt;
    /** HTML forall symbol. */
    static public final String HTML_FORALL = "&#8704;"; // &forall;
    /** HTML exists symbol. */
    static public final String HTML_EXISTS = "&#8707;"; // &exist;
    /** HTML or symbol. */
    static public final String HTML_OR = "&#8744;"; // &or;
    /** HTML negation symbol. */
    static public final String HTML_NOT = "&#172;"; // &not;
    /** HTML lambda symbol. */
    static public final String HTML_LAMBDA = "&#955;"; // &lambda;
    /** HTML left angular bracket symbol. */
    static public final String HTML_LANGLE = "&lt;"; // &#9001; / &lang;
    /** HTML right angular bracket symbol. */
    static public final String HTML_RANGLE = "&gt;"; // &#9002; / &rang;
    /** HTML tau symbol. */
    static public final String HTML_TAU = "&#932;"; // &tau;
    /** HTML pi symbol. */
    static public final String HTML_PI = "&#960;"; // &pi;
    /** HTML epsilon symbol. */
    static public final String HTML_EPSILON = "&#949;"; // &epsilon;
    /** HTML times symbol. */
    static public final String HTML_TIMES = "&#215;"; // &times;
    /** Name of the HTML tag (<code>html</code>). */
    static public final String HTML_TAG_NAME = "html";
    /** HTML tag. */
    static public final HTMLTag HTML_TAG = new HTMLTag(HTML_TAG_NAME);
    /** Name of the span tag (<code>span</code>). */
    static public final String SPAN_TAG_NAME = "span";
    /** Name of the span tag (<code>div</code>). */
    static public final String DIV_TAG_NAME = "div";
    /** Name of the span style attribute. */
    static public final String STYLE_ATTR_NAME = "style";
    /** Name of the linebreak tag (<code>br</code>). */
    static public final String LINEBREAK_TAG_NAME = "br";
    /** Name of the horizontal rule tag (<code>hr</code>). */
    static public final String HORIZONTAL_LINE_TAG_NAME = "hr";
    /** Name of the font underline tag (<code>u</code>). */
    static public final String UNDERLINE_TAG_NAME = "u";
    /** Font underline tag. */
    static public final HTMLTag UNDERLINE_TAG = new HTMLTag(UNDERLINE_TAG_NAME);
    /** Name of the font strikethrough tag (<code>s</code>). */
    static public final String STRIKETHROUGH_TAG_NAME = "s";
    /** Font strikethrough tag. */
    static public final HTMLTag STRIKETHROUGH_TAG = new HTMLTag(
        STRIKETHROUGH_TAG_NAME);
    /** Name of the italic font tag (<code>i</code>). */
    static public final String ITALIC_TAG_NAME = "i";
    /** Italic font tag. */
    static public final HTMLTag ITALIC_TAG = new HTMLTag(ITALIC_TAG_NAME);
    /** Name of the strong font tag (<code>strong</code>). */
    static public final String STRONG_TAG_NAME = "strong";
    /** Strong font tag. */
    static public final HTMLTag STRONG_TAG = new HTMLTag(STRONG_TAG_NAME);
    /** Name of the superscript font tag. */
    static public final String SUPER_TAG_NAME = "sup";
    /** Superscript font tag. */
    static public final HTMLTag SUPER_TAG = new HTMLTag(SUPER_TAG_NAME);
    /** Name of the subscript font tag. */
    static public final String SUB_TAG_NAME = "sub";
    /** Subscript font tag. */
    static public final HTMLTag SUB_TAG = new HTMLTag(SUB_TAG_NAME);
    /** Name of the table tag. */
    static public final String TABLE_TAG_NAME = "table";
    /** Table tag. */
    static public final HTMLTag TABLE_TAG = new HTMLTag(TABLE_TAG_NAME);

    /** The <code>html</code> tag to insert a line break. */
    static public final String HTML_LINEBREAK =
        createHtmlTag(LINEBREAK_TAG_NAME).tagBegin;
    /** The <code>html</code> tag to insert a horizontal line. */
    static public final String HTML_HORIZONTAL_LINE =
        createHtmlTag(HORIZONTAL_LINE_TAG_NAME).tagBegin;

    /** Map from colours to HTML tags imposing the colour on a text. */
    private static final Map<Color,HTMLTag> colorTagMap =
        new HashMap<Color,HTMLTag>();
    /** The maximum alpha value according to {@link Color#getAlpha()}. */
    private static final int MAX_ALPHA = 255;

    /** Blue color tag. */
    public static final HTMLTag blue = createColorTag(Colors.findColor("blue"));
    /** Green color tag. */
    public static final HTMLTag green =
        createColorTag(Colors.findColor("green.darker"));
    /** Red color tag. */
    public static final HTMLTag red = createColorTag(Colors.findColor("red"));
    /** Remark color tag. */
    public static final HTMLTag remark =
        createColorTag(GraphConstants.getForeground(AspectJGraph.ASPECT_NODE_ATTR.get(REMARK)));

    /**
     * Class that allows some handling of HTML text.
     */
    static public class HTMLTag {
        HTMLTag(String tag) {
            this.tagBegin = String.format("<%s>", tag);
            this.tagEnd = String.format("</%s>", tag);
        }

        HTMLTag(String tag, String attrName, String attrValue) {
            this.tagBegin =
                String.format("<%s %s=\"%s\">", tag, attrName,
                    toHtml(attrValue));
            this.tagEnd = String.format("</%s>", tag);
        }

        /**
         * Puts the tag around a given object description, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the object from which the description is to be abstracted
         */
        public String on(Object text) {
            return on(new StringBuilder(text.toString())).toString();
        }

        /**
         * Puts the tag around a given string builder, and returns the result.
         * The changes are implemented in the string builder itself, i.e., the
         * parameter is modified. The description is assumed to be in HTML
         * format.
         * @param text the string builder that is to be augmented with this tag
         */
        public StringBuilder on(StringBuilder text) {
            text.insert(0, this.tagBegin);
            text.append(this.tagEnd);
            return text;
        }

        /**
         * Puts the tag around a given string, first converting special HTML
         * characters if required, and returns the result.
         * @param text the object from which the description is to be abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String on(Object text, boolean convert) {
            if (convert) {
                return on(toHtml(new StringBuilder(text.toString()))).toString();
            } else {
                return on(text);
            }
        }

        /**
         * Puts the tag around the strings in a given array, and returns the
         * result. The description is assumed to be in HTML format.
         * @param text the array of objects from which the description is to be
         *        abstracted
         */
        public String[] on(Object[] text) {
            return on(text, false);
        }

        /**
         * Puts the tag around the strings in a given array, first converting
         * special HTML characters if required, and returns the result.
         * @param text the array of objects from which the description is to be
         *        abstracted
         * @param convert if true, text is converted to HTML first.
         */
        public String[] on(Object[] text, boolean convert) {
            String[] result = new String[text.length];
            for (int labelIndex = 0; labelIndex < text.length; labelIndex++) {
                result[labelIndex] = on(text[labelIndex], convert);
            }
            return result;
        }

        /**
         * Strips the HTML tags from the string given.
         * @param text the string to be analyzed.
         * @return the input string unmodified if it did not contain the the
         *         HTML tags or the string striped from the tags.
         */
        public String off(StringBuilder text) {
            int tagEndStart = text.indexOf(this.tagEnd);
            int tagBeginStart = text.indexOf(this.tagBegin);
            if (tagEndStart > -1 && tagBeginStart > -1) {
                int end = tagEndStart + this.tagEnd.length();
                text.replace(tagEndStart, end, "");
                end = tagBeginStart + this.tagBegin.length();
                text.replace(tagBeginStart, end, "");
            }
            return text.toString();
        }

        /**
         * Strips the HTML tags from the string given.
         * @param text the string to be analyzed.
         * @return the input string unmodified if it did not contain the the
         *         HTML tags or the string striped from the tags.
         */
        public String off(String text) {
            int tagEndStart = text.indexOf(this.tagEnd);
            int tagBeginStart = text.indexOf(this.tagBegin);
            if (tagEndStart > -1 && tagBeginStart > -1) {
                int end = tagEndStart + this.tagEnd.length();
                text = text.substring(0, tagEndStart) + text.substring(end);
                end = tagBeginStart + this.tagBegin.length();
                text = text.substring(0, tagBeginStart) + text.substring(end);
            }
            return text;
        }

        /** Start text of this tag. */
        public final String tagBegin;
        /** End text of this tag. */
        public String tagEnd;
    }

}