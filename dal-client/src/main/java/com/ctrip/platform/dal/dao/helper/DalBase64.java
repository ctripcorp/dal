package com.ctrip.platform.dal.dao.helper;

import org.apache.commons.codec.binary.Base64;

public class DalBase64 extends Base64 {
    static final byte[] CHUNK_SEPARATOR = {'\r', '\n'};

    public DalBase64(boolean urlSafe) {
        super(urlSafe);
    }

    public DalBase64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        super(lineLength, lineSeparator, urlSafe);
    }

    public DalBase64() {
        super();
    }

    public static byte[] encodeBase64(final byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked, final boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }

    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked, final boolean urlSafe,
            final int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }

        // Create this so can use the super-class method
        // Also ensures that the same roundings are performed by the ctor and the code
        final Base64 b64 = isChunked ? new DalBase64(urlSafe) : new DalBase64(0, CHUNK_SEPARATOR, urlSafe);
        final long len = b64.getEncodedLength(binaryData);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len
                    + ") than the specified maximum size of " + maxResultSize);
        }

        return b64.encode(binaryData);
    }

    public static byte[] decodeBase64(final byte[] base64Data) {
        return new DalBase64().decode(base64Data);
    }

    @Override
    protected int getDefaultBufferSize() {
        return 256;
    }
}
