package userinterface.helperconfiguration;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {


    public  String Encrypt(String sPlainText, String sSecretKey) {
        try {

            // Initiaize the key byte array
            byte[] keyArray;
            byte[] toEncryptArray = sPlainText.getBytes(StandardCharsets.UTF_8);

            // Convert the secret key in to MD5 hashing
            MessageDigest hashMD5 = MessageDigest.getInstance(ConfigurationConstant.MD_5);
            keyArray = hashMD5.digest(sSecretKey.getBytes(StandardCharsets.UTF_8));

            // Check if the key is less than 16 byte
            if (keyArray.length == 16) {
                byte[] tmpKey = new byte[24];
                System.arraycopy(keyArray, 0, tmpKey, 0, 16);
                System.arraycopy(keyArray, 0, tmpKey, 16, 8);
                keyArray = tmpKey;
            }

            // Make the Cipher here
            Cipher cipher = Cipher.getInstance(ConfigurationConstant.DESEDE_ECB_PKCS_5_PADDING);
            SecretKeySpec myKey = new SecretKeySpec(keyArray, ConfigurationConstant.DE_SEDE);

            // Initialize the Encryption mode
            cipher.init(Cipher.ENCRYPT_MODE, myKey);

            byte[] cipherText = cipher.doFinal(toEncryptArray);
            String encodedCipherText;

            // Convert the text into base 64 string
            encodedCipherText = Base64.encodeToString(cipherText, Base64.DEFAULT);

            // Return the base 64 encrypted string
            return encodedCipherText;

        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException oException) {

            return oException.toString();

        }
    }

    /**
     * Algorithm is used for the decryption of the text
     *
     * @param sCipherText -> Cipher Text which is the result of the encrypted
     *                    Text
     * @param sSecretKey  -> Same secret key is used in both encryption and
     *                    decryption
     * @return -> Decrypted Text
     */
    public  String Decrypt(String sCipherText, String sSecretKey) {

        try {

            // Initiaize the key byte array
            byte[] keyArray;

            //Convert the byte array into base 64
            byte[] toEncryptArray = Base64.decode(sCipherText, Base64.DEFAULT);
            // MD5 hashing
            byte[] bytesOfMessage = sSecretKey.getBytes(StandardCharsets.UTF_8);

            MessageDigest hashMD5 = MessageDigest.getInstance(ConfigurationConstant.MD_5);
            keyArray = hashMD5.digest(bytesOfMessage);

            keyArray = hashMD5.digest(sSecretKey.getBytes(StandardCharsets.UTF_8));

            // Check if the key is less than 16 byte
            if (keyArray.length == 16) {
                byte[] tmpKey = new byte[24];
                System.arraycopy(keyArray, 0, tmpKey, 0, 16);
                System.arraycopy(keyArray, 0, tmpKey, 16, 8);
                keyArray = tmpKey;
            }

            // Reset the hash
            hashMD5.reset();

            // Create the DES instance here
            Cipher cipher = Cipher.getInstance(ConfigurationConstant.DESEDE_ECB_PKCS_5_PADDING);
            SecretKeySpec myKey = new SecretKeySpec(keyArray, ConfigurationConstant.DE_SEDE);

            cipher.init(Cipher.DECRYPT_MODE, myKey);

            //Decrption perform here
            byte[] cipherText = cipher.doFinal(toEncryptArray);
            String encodedCipherText;

            // Change the final byte array to string
            encodedCipherText = new String(cipherText, StandardCharsets.UTF_8);

            // Return the decrypted string
            return encodedCipherText;

            // Exception are there
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException oException) {

            return oException.toString();
        }
    }

}
