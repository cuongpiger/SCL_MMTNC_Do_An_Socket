import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileInfo implements Serializable {
    private String iFileDetails;
    private int iNoPartitions;
    private int iLastByte;
    private String iHashCode;

    public FileInfo(String pFileDetails, int pNoPartitions, int pLastByte, String pHashCode) {
        iFileDetails = pFileDetails;
        iNoPartitions = pNoPartitions;
        iLastByte = pLastByte;
        iHashCode = pHashCode;
    }

    public static String genSha256(File pFile) {
        try {
            MessageDigest sha_digest = MessageDigest.getInstance("SHA-256");
            String hashcode = getFileChecksum(sha_digest, pFile);

            return hashcode;
        } catch (NoSuchAlgorithmException | IOException err) {
            return null;
        }
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    public static void main(String[] args) {
        File file = new File("./test.pdf");
        System.out.println(FileInfo.genSha256(file));
    }
}
