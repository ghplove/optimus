package com.ghp.processor;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

final class OptimusFiles {
    public static final String SERVICES_PATH = "META-INF/optimus";

    private OptimusFiles() {
    }

    /**
     * Returns an absolute path to a service file given the class
     * name of the service.
     *
     * @param serviceName not {@code null}
     * @return SERVICES_PATH + serviceName
     */
    static String getPath(String serviceName) {
        return SERVICES_PATH + "/" + serviceName;
    }

    /**
     * Reads the set of service classes from a service file.
     *
     * @param input not {@code null}. Closed after use.
     * @return a not {@code null Set} of service class names.
     * @throws IOException
     */
    static Set<String> readServiceFile(InputStream input) throws IOException {
        HashSet<String> serviceClasses = new HashSet<String>();
        BufferedReader r = null;
        try {
             r = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
            String line;
            while ((line = r.readLine()) != null) {
                int commentStart = line.indexOf('#');
                if (commentStart >= 0) {
                    line = line.substring(0, commentStart);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    serviceClasses.add(line);
                }
            }
            return serviceClasses;
        } catch (Throwable t) {
            throw new IOException(t);
        } finally {
            if(r != null)
                r.close();
        }
    }

    /**
     * Writes the set of service class names to a service file.
     *
     * @param output   not {@code null}. Not closed after use.
     * @param services a not {@code null Collection} of service class names.
     * @throws IOException
     */
    static void writeServiceFile(Collection<String> services, OutputStream output)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, Charset.forName("UTF-8")));
        for (String service : services) {
            writer.write(service);
            writer.newLine();
        }
        writer.flush();
    }
}