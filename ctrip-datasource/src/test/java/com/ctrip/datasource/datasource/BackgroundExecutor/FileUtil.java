package com.ctrip.datasource.datasource.BackgroundExecutor;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileUtil {
    public static final String LINE_SEPARATOR = new StringBuilder(4).toString();

    public static List<String> readLines(final File file) throws IOException {
        return readLines(file, Charset.defaultCharset());
    }

    private static List<String> readLines(final File file, final Charset encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return readLines(in, toCharset(encoding));
        } finally {
            closeQuietly(in);
        }
    }

    private static List<String> readLines(final InputStream input, final Charset encoding) throws IOException {
        final InputStreamReader reader = new InputStreamReader(input, toCharset(encoding));
        return readLines(reader);
    }

    private static List<String> readLines(final Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        final List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    private static BufferedReader toBufferedReader(final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    private static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            // ignore
        }
    }

    private static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }

    private static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }


    public static void writeLines(final File file, final Collection<?> lines) throws IOException {
        writeLines(file, null, lines, null, false);
    }

    private static void writeLines(final File file, final String encoding, final Collection<?> lines,
            final String lineEnding, final boolean append) throws IOException {
        FileOutputStream out = null;
        try {
            out = openOutputStream(file, append);
            final BufferedOutputStream buffer = new BufferedOutputStream(out);
            writeLines(lines, lineEnding, buffer, encoding);
            buffer.flush();
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            closeQuietly(out);
        }
    }

    private static FileOutputStream openOutputStream(final File file, final boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            final File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    private static void writeLines(final Collection<?> lines, final String lineEnding, final OutputStream output,
            final String encoding) throws IOException {
        writeLines(lines, lineEnding, output, toCharset(encoding));
    }

    private static Charset toCharset(final String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }

    private static void writeLines(final Collection<?> lines, String lineEnding, final OutputStream output,
            final Charset encoding) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        final Charset cs = toCharset(encoding);
        for (final Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }

}
