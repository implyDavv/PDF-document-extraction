package pdfHandle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;

public class ExtractText {
	final static File RESULT_FOLDER = new File("f:\\java\\", "table");


    public static void setUpBeforeClass() throws Exception
    {
        RESULT_FOLDER.mkdirs();
    }
    
    public void testExtract( PDDocument document ) throws COSVisitorException, IOException
    {
        //try (   InputStream documentStream = getClass().getResourceAsStream( "sampleFile.pdf" );
                //PDDocument document = PDDocument.load(documentStream))
        {
            String normal = extractNormal(document);

            System.out.println("extract normally:");
            System.out.println(normal);
            System.out.println("***********************************");
        }
    }

    // extract WITHOUT SortByPosition
    String extractNormal(PDDocument document) throws IOException
    {
        PDFTextStripper stripper = new PDFTextStripper();
        //stripper.setSortByPosition(true);
        return stripper.getText(document);
    }

    /**
     * <a href="http://stackoverflow.com/questions/32978179/using-pdfbox-to-get-location-of-line-of-text">
     * Using PDFBox to get location of line of text
     * </a>
     * <p>
     * This example shows how to extract text with the additional information of
     * the x coordinate at the start of line.
     * </p>
     */
    
    public void testExtractLineStart( PDDocument document ) throws COSVisitorException, IOException
    {
       // try (   InputStream documentStream = getClass().getResourceAsStream("sampleFile.pdf");
       //         PDDocument document = PDDocument.load(documentStream))
        {
            String normal = extractLineStart(document);

            System.out.println("extract with line starts:");
            System.out.println(normal);
            System.out.println("***********************************");
        }
    }
    
    String extractLineStart(PDDocument document) throws IOException
    {
        PDFTextStripper stripper = new PDFTextStripper()
        {
            @Override
            protected void startPage(PDPage page) throws IOException
            {
                startOfLine = true;
                super.startPage(page);
            }

            @Override
            protected void writeLineSeparator() throws IOException
            {
                startOfLine = true;
                super.writeLineSeparator();
            }

            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException
            {
                if (startOfLine)
                {
                    TextPosition firstProsition = textPositions.get(0);
                    writeString(String.format("[%s]", firstProsition.getXDirAdj()));
                    startOfLine = false;
                }
                super.writeString(text, textPositions);
            }
            boolean startOfLine = true;
        };
        //stripper.setSortByPosition(true);
        return stripper.getText(document);
    }

    /**
     * <a href="http://stackoverflow.com/questions/37566288/pdfbox-is-not-giving-right-output">
     * PDFBOX is not giving right output
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/bsm4zgv5v0mvj7v/Airtel.pdf?dl=0">
     * Airtel.pdf
     * </a>
     * <p>
     * Indeed, PDFBox text extraction hardly returns anything. But inspecting the PDF in question
     * makes clear why it is so: Virtually all "text" in the document is not drawn using text
     * drawing instructions but instead by defining the character outlines as paths and filling
     * them. Thus, hardly anything short of OCR will help here.
     * </p>
     */
    
    public void testExtractAsMudit() throws COSVisitorException, IOException
    {
        try (   InputStream documentStream = getClass().getResourceAsStream("Airtel.pdf");
                PDDocument document = PDDocument.load(documentStream))
        {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);
            String parsedText = pdfStripper.getText(document);

            System.out.println("extract as Mudit:");
            System.out.println(parsedText);
            System.out.println("***********************************");
        }
    }

    /**
     * <a href="http://stackoverflow.com/questions/39962563/detect-bold-italic-and-strike-through-text-using-pdfbox-with-vb-net">
     * Detect Bold, Italic and Strike Through text using PDFBox with VB.NET
     * </a>
     * <br/>
     * <a href="http://www.filedropper.com/exampledocument">
     * Example Document.pdf
     * </a>
     * <p>
     * This test shows how to extract text with styles using the {@link PDFStyledTextStripper}.
     * </p>
     */
    
    public void testExtractStyled(PDDocument document ) throws COSVisitorException, IOException
    {
        //try (   InputStream documentStream = getClass().getResourceAsStream("Example Document.pdf" );
        //        PDDocument document = PDDocument.load(documentStream))
        {
            String styled = extractStyled(document);

            System.out.println("\n'Example Document.pdf', extract with style:");
            System.out.println(styled);
            System.out.println("***********************************");
        }
    }

    /**
     * <a href="https://github.com/mkl-public/testarea-pdfbox1/issues/1">
     * PDFStyledTextStripper StrikeThrough Bug #1
     * </a>
     * <br/>
     * <a href="https://www.dropbox.com/s/5chty5f4yotkb7s/style-test.pdf?dl=1">
     * style-test.pdf
     * </a>
     * <p>
     * Indeed, the 's' of 'stroked' was not recognized as style strikethrough.
     * This appears due to a border case where float / double arithmetics give
     * you a headache. To make this work, the tolerance of the {@link PDFStyledTextStripper}
     * helper class <code>TransformedRectangle</code> method <code>strikesThrough</code>
     * (and also <code>underlines</code>) has been changed.
     * </p>
     */
    
    public void testExtractStyledFromStyleTest() throws COSVisitorException, IOException
    {
        try (   InputStream documentStream = getClass().getResourceAsStream("style-test.pdf" );
                PDDocument document = PDDocument.load(documentStream))
        {
            String styled = extractStyled(document);

            System.out.println("\n'style-test.pdf', extract with style:");
            System.out.println(styled);
            System.out.println("***********************************");
        }
    }

    String extractStyled(PDDocument document) throws IOException
    {
        PDFTextStripper stripper = new PDFStyledTextStripper();
        stripper.setSortByPosition(true);
        return stripper.getText(document);
    }

    /**
     * <a href="http://stackoverflow.com/questions/40296660/extract-pdf-text-location-using-pdfboxnet">
     * extract pdf text location using pdfboxnet
     * </a>
     * <br/>
     * <a href="https://drive.google.com/open?id=0B45rDxvaXzsmcFo1QXhNdDBXT28">
     * mathml88.pdf
     * </a>
     * <p>
     * This test shows how to extract text plus word positions.
     * </p>
     */
    
    public void txtExtractWordLocations(PDDocument document ) throws COSVisitorException, IOException
    {
   
        {
            String wordLocations = extractWordLocations(document);

            System.out.println("extract with word locations:");
            System.out.println(wordLocations);
            System.out.println("***********************************");
        }
    }

    String extractWordLocations(PDDocument document) throws IOException
    {
        PDFTextStripper stripper = new PDFTextStripper()
        {
            @Override
            protected void writeString(String text, List<TextPosition> textPositions) throws IOException
            {
                super.writeString(text, textPositions);

                TextPosition firstProsition = textPositions.get(0);
                TextPosition lastPosition = textPositions.get(textPositions.size() - 1);
                writeString(String.format("[%s - %s / %s]", firstProsition.getXDirAdj(), lastPosition.getXDirAdj() + lastPosition.getWidthDirAdj(), firstProsition.getYDirAdj()));
            }
        };
        stripper.setSortByPosition( false );        
        return stripper.getText(document);
    }
    
    
}
