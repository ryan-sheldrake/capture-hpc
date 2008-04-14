package capture;

import java.io.*;

/**
 * PROJECT: Capture-HPC
 * DATE: Apr 13, 2008
 * FILE: Preprocessor
 * COPYRIGHT HOLDER: Victoria University of Wellington, NZ
 * AUTHORS: Christian Seifert (christian.seifert@gmail.com)
 * <p/>
 * This file is part of Capture-HPC.
 * <p/>
 * Capture-HPC is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * Capture-HPC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with Capture-HPC; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
public abstract class Preprocessor {
    private boolean hasMoreInputUrls;

    public void readInputUrls(final String inputUrlsFile) {
        Thread fileTail = new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputUrlsFile), "UTF-8"));

                    while (true) {
                        try {
                            String line = in.readLine();
                            if (line != null) {
                                if ((line.length() > 0)) {
                                    line = line.trim();

                                    if(line.equalsIgnoreCase("end")) {
                                        hasMoreInputUrls = false;
                                        return;
                                    }

                                    if (!line.startsWith("#")) {
                                        preprocess(line);
                                    }
                                }
                            } else {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace(System.out);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace(System.out);
                }
            }
        });
        fileTail.start();
    }

    /** method that indicates whether the reading of the file completed.
     * if the preprocess function blocks this method would only indicate true once the preprocess function has completed.
     *
     * @return true if its done reading the input urls
     */
    public boolean hasMoreInputUrls() {
        return hasMoreInputUrls;
    }

    /* The meat of the preprocessor. Urls are passed from the input file to the preprocessor.
    * If URLs shall be processed by Capture, the preprocessor must call the addUrlToCaptureQueue function.
    *
    * @param url - url as per capture format (including prg and delay)
    */
    abstract public void preprocess(String url);

    /* Sets the configuration of the preprocessor. Allows the preprocessor to be configured via the
     * existing config.xml configuration file.
     *
     * @param configuration - from the CDATA element of the preprocessor xml tag of config.xml
     */
    abstract public void setConfiguration(String configuration);

    /*
     * @param url - url as per capture format (including prg and delay)
     */
    public void addUrlToCaptureQueue(String url) {

        Element e = new Element();
        e.name = "url";
        e.attributes.put("add", "");
        e.attributes.put("url", url);
        EventsController.getInstance().notifyEventObservers(e);
    }

    public int getCaptureQueueSize() {
        return UrlsController.getInstance().getQueueSize();
    }
}
