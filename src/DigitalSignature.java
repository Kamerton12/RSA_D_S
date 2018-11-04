import java.io.*;
import java.math.BigInteger;

public class DigitalSignature implements Serializable
{
    private BigInteger[] digitalSignature;
    private BigInteger publicKey;

    public DigitalSignature(){}

    public DigitalSignature(String fileName) throws IOException, ClassNotFoundException
    {
        readDigitalSignatureFromFile(fileName);
    }

    public DigitalSignature(BigInteger[] digitalSignature, BigInteger publicKey)
    {
        this.digitalSignature = digitalSignature;
        this.publicKey = publicKey;
    }

    public BigInteger getPublicKey()
    {
        return publicKey;
    }
    public void setPublicKey(BigInteger publicKey)
    {
        this.publicKey = publicKey;
    }

    public BigInteger[] getDigitalSignature()
    {
        return digitalSignature;
    }
    public void setDigitalSignature(BigInteger[] digitalSignature)
    {
        this.digitalSignature = digitalSignature;
    }

    public void readDigitalSignatureFromFile(String fileName) throws IOException, ClassNotFoundException
    {
        InputStream is = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(is);
        DigitalSignature bi = (DigitalSignature) ois.readObject();
        this.digitalSignature = bi.getDigitalSignature();
        this.publicKey = bi.getPublicKey();
        ois.close();
        is.close();
    }

    public void writeDigitalSignatureToFile(String fileName) throws IOException
    {
        OutputStream os = new FileOutputStream(fileName);
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(this);
        oos.flush();
        oos.close();
        os.close();
    }
}
