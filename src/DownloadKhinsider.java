import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class DownloadKhinsider {
    private static void scrapeAlbum(String url, String targetDirectory, boolean override) {
        Document d;
        try {
            d = Jsoup.connect(url).get();
        } catch(IOException e) {
            System.out.println("Failed to get page!");
            return;
        }

        String title = "";
        for(String s : (url.substring(url.lastIndexOf("/")+1)).split("-")) {
            title += ((Character.toUpperCase(s.charAt(0))) + s.substring(1)) + " ";
        }
        if((new File(targetDirectory + File.separator + title)).mkdir() || override) {
            Elements tracks = (d.getElementById("songlist").child(0).select("> tr"));

            for (Element track : tracks.subList(1, tracks.size() - 1)) {
                Element t = track.child(2).child(0);
                try {
                    Document trackPage = Jsoup.connect("https://downloads.khinsider.com/" + t.attr("href")).get();
                    ReadableByteChannel rbc = Channels.newChannel(new URL(trackPage.getElementsByTag("audio").get(0).attr("src")).openStream());
                    FileOutputStream fout = new FileOutputStream(targetDirectory + File.separator + title + File.separator + t.text() + ".mp3");
                    fout.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    System.out.println("Downloaded track " + t.text() + "!");
                } catch (IOException e) {
                    System.out.println("Failed to download track; reason...unknown to us all");
                    return;
                }
              }
        } else {
            System.out.println("Failed to write files due to directory error");
        }
    }

    public static void main(String[] args) {
        if(args.length > 0) scrapeAlbum(args[args.length - 1], "/Users/zackamiton/Documents/Torrents/Khinsider Downloads", true);
        else scrapeAlbum("https://downloads.khinsider.com/game-soundtracks/album/bomberman-nes", "/Users/zackamiton/Documents/Torrents/Khinsider Downloads", true);
    }
}
