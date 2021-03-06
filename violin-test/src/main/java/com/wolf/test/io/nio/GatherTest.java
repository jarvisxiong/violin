package com.wolf.test.io.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Description:
 * <br/> Created on 2017/6/3 17:35
 *
 * @author 李超
 * @since 1.0.0
 */
public class GatherTest {

    private static final String DEMOGRAPHIC = "d:\\blahblah.txt";

    // "Leverage frictionless methodologies"
    public static void main(String[] argv) throws Exception {
        FileOutputStream fos = new FileOutputStream(DEMOGRAPHIC);
        GatheringByteChannel gatherChannel = fos.getChannel();
// Generate some brilliant marcom, er, repurposed content
        ByteBuffer[] bs = utterBS();
// Deliver the message to the waiting market
        while(gatherChannel.write(bs) > 0) {
// Empty body
// Loop until write( ) returns zero
        }
        System.out.println("Mindshare paradigms synergized to " + DEMOGRAPHIC);
        fos.close();
    }

    // ------------------------------------------------
// These are just representative; add your own
    private static String[] col1 = {"Aggregate", "Enable", "Leverage", "Facilitate", "Synergize", "Repurpose", "Strategize", "Reinvent", "Harness"};
    private static String[] col2 = {"cross-platform", "best-of-breed", "frictionless", "ubiquitous", "extensible", "compelling", "mission-critical", "collaborative", "integrated"};
    private static String[] col3 = {"methodologies", "infomediaries", "platforms", "schemas", "mindshare", "paradigms", "functionalities", "web services", "infrastructures"};
    private static String newline = System.getProperty("line.separator");

    // The Marcom-atic 9000
    private static ByteBuffer[] utterBS() throws Exception {
        List<ByteBuffer> list = new LinkedList<ByteBuffer>();
        for(int i = 0; i < 10; i++) {
            list.add(pickRandom(col1, " "));
            list.add(pickRandom(col2, " "));
            list.add(pickRandom(col3, newline));
        }
        ByteBuffer[] bufs = new ByteBuffer[list.size()];
        list.toArray(bufs);
        return (bufs);
    }

    // The communications director
    private static Random rand = new Random();
// Pick one, make a buffer to hold it and the suffix, load it with
// the byte equivalent of the strings (will not work properly for

    // non-Latin characters), then flip the loaded buffer so it's ready
// to be drained
    private static ByteBuffer pickRandom(String[] strings, String suffix) throws Exception {
        String string = strings[rand.nextInt(strings.length)];
        int total = string.length() + suffix.length();
        ByteBuffer buf = ByteBuffer.allocate(total);
        buf.put(string.getBytes("US-ASCII"));
        buf.put(suffix.getBytes("US-ASCII"));
        buf.flip();
        return (buf);
    }
}
