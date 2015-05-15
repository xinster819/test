package lx.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

public class BasicSelectorDemo {

    public static void main(String[] args) throws IOException, URISyntaxException {

        URL url = BasicChannelDemo.class.getResource("/nio/words.txt");
        File f = new File(url.toURI());
        RandomAccessFile aFile = new RandomAccessFile(f, "rw");
        FileChannel channel = aFile.getChannel();
        Selector selector = Selector.open();
        // channel.configureBlocking(false);
        //
        // SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

        while (true) {

            int readyChannels = selector.select();

            if (readyChannels == 0)
                continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {

                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // a connection was accepted by a ServerSocketChannel.

                } else if (key.isConnectable()) {
                    // a connection was established with a remote server.

                } else if (key.isReadable()) {
                    // a channel is ready for reading

                } else if (key.isWritable()) {
                    // a channel is ready for writing
                }

                keyIterator.remove();
            }
        }
    }

}