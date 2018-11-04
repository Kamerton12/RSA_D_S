import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException
    {
        int bitLength = 32;
        Random r = new SecureRandom(new byte[]{2});
        BigInteger p = BigInteger.probablePrime(bitLength, r);
        BigInteger q = BigInteger.probablePrime(bitLength, r);
        BigInteger n = p.multiply(q);
        BigInteger m = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger publicKey;
        do
        {
            publicKey = BigInteger.probablePrime(bitLength * 2, r);
        }while(publicKey.compareTo(m) != -1 && publicKey.gcd(m).compareTo(BigInteger.ONE) != 0);
        BigInteger privateKey = getE(publicKey, m);

        String fileName = "CP.docx";
        String digitalSignatureFile = "DigitalSignature.dgs";

        DigitalSignature ds = new DigitalSignature(encodeDigitalSignature(fileName, privateKey, n), publicKey);
        ds.writeDigitalSignatureToFile(digitalSignatureFile);

        DigitalSignature readDS = new DigitalSignature(digitalSignatureFile);

        if(isMatchWithFile(readDS, fileName, n))
        {
            System.out.println("Подпись верна");
        }
        else
        {
            System.out.println("Подпись не верна");
        }
    }




    public static BigInteger[] encodeDigitalSignature(String fileName, BigInteger publicKey, BigInteger n) throws NoSuchAlgorithmException, IOException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md.digest();
        String checkSum = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return encode(checkSum, publicKey, n);
    }

    public static boolean isMatchWithFile(DigitalSignature digitalSignature, String fileName, BigInteger n) throws NoSuchAlgorithmException, IOException
    {
        String decode = decode(digitalSignature.getDigitalSignature(), digitalSignature.getPublicKey(), n);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md.digest();
        String checkSum = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return decode.equals(checkSum);
    }


    public static BigInteger[] encode(String message, BigInteger publicKey, BigInteger n)
    {
        BigInteger[] mes = divideMessage(message);
        for(int i = 0; i < mes.length; i++)
        {
            mes[i] = encodeBigInteger(mes[i], publicKey, n);
        }
        return mes;
    }
    public static String decode(BigInteger[] code, BigInteger privateKey, BigInteger n)
    {
        for(int i = 0; i < code.length; i++)
        {
            code[i] = decodeBigInteger(code[i], privateKey, n);
        }
        return combineMessage(code);
    }


    public static BigInteger[] divideMessage(String message)
    {
        BigInteger[] ans = new BigInteger[message.length()];
        char[] msg = message.toCharArray();
        for(int i = 0; i < message.length(); i++)
        {
            ans[i] = BigInteger.valueOf(msg[i]);
        }
        return ans;
    }

    public static String combineMessage(BigInteger[] message)
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < message.length; i++)
            sb.append((char)Integer.parseInt(message[i].toString()));
        return sb.toString();
    }

    public static BigInteger encodeBigInteger(BigInteger message, BigInteger publicKey, BigInteger n)
    {
        return message.modPow(publicKey, n);
    }

    public static BigInteger decodeBigInteger(BigInteger encodedMessage, BigInteger privateKey, BigInteger n)
    {
        return encodedMessage.modPow(privateKey, n);
    }


    public static BigInteger getE(BigInteger d, BigInteger m)
    {
        BigInteger[][] E = {{BigInteger.ONE,BigInteger.ZERO},{BigInteger.ZERO,BigInteger.ONE}};
        while(true)
        {
            BigInteger r = m.mod(d);
            if(r.compareTo(BigInteger.ZERO) == 0)
            {
                return E[1][1];
            }
            BigInteger q = m.divide(d);
            BigInteger[][] mult = {{BigInteger.ZERO,BigInteger.ONE},{BigInteger.ONE, q.negate()}};
            E = matrMult(E, mult);
            m = d;
            d = r;
        }
    }

    public static BigInteger[][] matrMult(BigInteger[][] a, BigInteger[][] b)
    {
        BigInteger leftUp = a[0][0].multiply(b[0][0]).add(a[0][1].multiply(b[1][0]));
        BigInteger rightUp = a[0][0].multiply(b[0][1]).add(a[0][1].multiply(b[1][1]));
        BigInteger leftDown = a[1][0].multiply(b[0][0]).add(a[1][1].multiply(b[1][0]));
        BigInteger rightDown = a[1][0].multiply(b[0][1]).add(a[1][1].multiply(b[1][1]));
        return new BigInteger[][]{{leftUp, rightUp},{leftDown, rightDown}};
    }

}
