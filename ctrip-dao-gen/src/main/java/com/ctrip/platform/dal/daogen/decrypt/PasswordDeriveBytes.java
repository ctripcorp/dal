package com.ctrip.platform.dal.daogen.decrypt;

import java.io.UnsupportedEncodingException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordDeriveBytes {

    private String HashNameValue;
    private byte[] SaltValue;
    private int IterationsValue;

    private MessageDigest hash;
    private int state;
    private byte[] password;
    private byte[] initial;
    private byte[] output;
    private byte[] firstBaseOutput;
    private int position;
    private int hashnumber;
    private int skip;

    public PasswordDeriveBytes(String strPassword, byte[] rgbSalt) {
        Prepare(strPassword, rgbSalt, "SHA-1", 100);
    }

    public PasswordDeriveBytes(String strPassword, byte[] rgbSalt, String strHashName, int iterations) {
        Prepare(strPassword, rgbSalt, strHashName, iterations);
    }

    public PasswordDeriveBytes(byte[] password, byte[] salt) {
        Prepare(password, salt, "SHA-1", 100);
    }


    public PasswordDeriveBytes(byte[] password, byte[] salt, String hashName, int iterations) {
        Prepare(password, salt, hashName, iterations);
    }

    private void Prepare(String strPassword, byte[] rgbSalt, String strHashName, int iterations) {
        if (strPassword == null)
            throw new NullPointerException("strPassword");

        byte[] pwd = null;
        try {
            pwd = strPassword.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Prepare(pwd, rgbSalt, strHashName, iterations);
    }

    private void Prepare(byte[] password, byte[] rgbSalt, String strHashName, int iterations) {
        if (password == null)
            throw new NullPointerException("password");

        this.password = password;

        state = 0;
        setSalt(rgbSalt);
        setHashName(strHashName);
        setIterationCount(iterations);

        initial = new byte[hash.getDigestLength()];
    }

    public byte[] getSalt() {
        if (SaltValue == null)
            return null;
        return SaltValue;
    }

    public void setSalt(byte[] salt) {
        if (state != 0) {
            throw new SecurityException("Can't change this property at this stage");
        }
        if (salt != null)
            SaltValue = salt;
        else
            SaltValue = null;
    }

    public String getHashName() {
        return HashNameValue;
    }

    public void setHashName(String hashName) {
        if (hashName == null)
            throw new NullPointerException("HashName");
        if (state != 0) {
            throw new SecurityException("Can't change this property at this stage");
        }
        HashNameValue = hashName;

        try {
            hash = MessageDigest.getInstance(hashName);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public int getIterationCount() {
        return IterationsValue;
    }

    public void setIterationCount(int iterationCount) {
        if (iterationCount < 1)
            throw new NullPointerException("HashName");
        if (state != 0) {
            throw new SecurityException("Can't change this property at this stage");
        }
        IterationsValue = iterationCount;
    }

    public byte[] GetBytes(int cb) throws DigestException {
        if (cb < 1) {
            throw new IndexOutOfBoundsException("cb");
        }

        if (state == 0) {
            Reset();
            state = 1;
        }

        byte[] result = new byte[cb];
        int cpos = 0;
        // the initial hash (in reset) + at least one iteration
        int iter = Math.max(1, IterationsValue - 1);

        // start with the PKCS5 key
        if (output == null) {
            // calculate the PKCS5 key
            output = initial;

            // generate new key material
            for (int i = 0; i < iter - 1; i++) {
                output = hash.digest(output);
            }
        }

        while (cpos < cb) {
            byte[] output2 = null;
            if (hashnumber == 0) {
                // last iteration on output
                output2 = hash.digest(output);
            } else if (hashnumber < 1000) {
                byte[] n = Integer.toString(hashnumber).getBytes();
                output2 = new byte[output.length + n.length];
                for (int j = 0; j < n.length; j++) {
                    output2[j] = n[j];
                }
                System.arraycopy(output, 0, output2, n.length, output.length);
                // don't update output
                output2 = hash.digest(output2);
            } else {
                throw new SecurityException("too long");
            }

            int rem = output2.length - position;
            int l = Math.min(cb - cpos, rem);
            System.arraycopy(output2, position, result, cpos, l);

            cpos += l;
            position += l;
            while (position >= output2.length) {
                position -= output2.length;
                hashnumber++;
            }
        }

        // saving first output length
        if (state == 1) {
            if (cb > 20) {
                skip = 40 - result.length;
            } else {
                skip = 20 - result.length;
            }
            firstBaseOutput = new byte[result.length];
            System.arraycopy(result, 0, firstBaseOutput, 0, result.length);
            state = 2;
        }
        // processing second output
        else if (skip > 0) {
            byte[] secondBaseOutput = new byte[(firstBaseOutput.length + result.length)];
            System.arraycopy(firstBaseOutput, 0, secondBaseOutput, 0, firstBaseOutput.length);
            System.arraycopy(result, 0, secondBaseOutput, firstBaseOutput.length, result.length);
            System.arraycopy(secondBaseOutput, skip, result, 0, skip);

            skip = 0;
        }

        return result;
    }

    public void Reset() throws DigestException {
        state = 0;
        position = 0;
        hashnumber = 0;
        skip = 0;

        if (SaltValue != null) {
            hash.update(password, 0, password.length);
            hash.update(SaltValue, 0, SaltValue.length);
            hash.digest(initial, 0, initial.length);
        } else {
            initial = hash.digest(password);
        }
    }
}
