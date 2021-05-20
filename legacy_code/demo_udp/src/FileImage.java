import java.io.Serializable;

public class FileImage implements Serializable {
    private static final long iSerial = 1L;

    private String iDestination;
    private String iSource;
    private String iFilename;
    private long iFilesize;
    private int iPieces;
    private int iPreviousByteLength;
    private String status;

    public String getiFilename() {
        return iFilename;
    }

    public long getiFilesize() {
        return iFilesize;
    }

    public int getiPieces() {
        return iPieces;
    }

    public int getiPreviousByteLength() {
        return iPreviousByteLength;
    }

    public String getiDestination() {
        return iDestination;
    }

    public void setiFilename(String pFilename) {
        iFilename = pFilename;
    }

    public void setiFilesize(long pFilesize) {
        iFilesize = pFilesize;
    }

    public void setiPieces(int pPieces) {
        iPieces = pPieces;
    }

    public void setiPreviousByteLength(int pPreviousByteLength) {
        iPreviousByteLength = pPreviousByteLength;
    }

    public void setiDestination(String pDestination) {
        iDestination = pDestination;
    }
}


