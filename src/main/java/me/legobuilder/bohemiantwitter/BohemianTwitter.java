package me.legobuilder.bohemiantwitter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import twitter4j.*;
import twitter4j.auth.AccessToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BohemianTwitter {
    private static final List<Long> lineBreaks = Arrays.asList(1031957545731219456L, 1031376085034905600L, 1029894173074370560L, 1027457359168380928L, 1025985626800377856L,
            1024831808352006144L, 1022675170551627777L, 1021223014380294145L, 1020158643885477893L, 1018384424411508737L, 1016163838247538689L,
            1011086260243296256L, 1006359047342260225L, 1004582100689924096L, 1003022938176704512L, 1001653858593050625L, 1000456284699807744L,
            998331106263994368L, 997175892777594886L, 996231482833326080L, 994837772400758789L, 994348976014061569L, 993633693888544768L,
            993553004967100416L);

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        File auth = new File("auth.txt");
        if (!auth.exists()) {
            auth.createNewFile();
            JSONObject jsonAuth = new JSONObject();
            jsonAuth.put("consumer_key", "key");
            jsonAuth.put("consumer_secret", "secret");
            jsonAuth.put("access_token", "token");
            jsonAuth.put("access_token_secret", "secret");

            try (FileWriter file = new FileWriter(auth)) {
                file.write(jsonAuth.toString());
            }
            System.out.println("Fill auth.txt with the appropriate authentication information!");
            return;
        }
        JSONObject jsonAuth = (JSONObject) new JSONParser().parse(new FileReader(auth));
        Twitter twitter = TwitterFactory.getSingleton();

        AccessToken accessToken = null;
        if (jsonAuth != null && !jsonAuth.isEmpty()) {
            accessToken = new AccessToken((String) jsonAuth.get("access_token"), (String) jsonAuth.get("access_token_secret"));
        }
        if (accessToken == null || !jsonAuth.containsKey("consumer_key") || !jsonAuth.containsKey("consumer_secret")) {
            System.out.println("Fill auth.txt with the appropriate authentication information!");
            return;
        }

        twitter.setOAuthConsumer((String) jsonAuth.get("consumer_key"), (String) jsonAuth.get("consumer_secret"));
        twitter.setOAuthAccessToken(accessToken);

        LinkedList<Status> responses = new LinkedList<>();

        for (int i = 1; i < 4; i++) {
            ResponseList<Status> localResponses = twitter.getUserTimeline("CostcoRiceBag", new Paging(i, 200, 993357085931921408L, 1032699857117605888L));
            localResponses.forEach(s -> {
                String text = s.getText().replaceAll("\n", "");

                if (text.startsWith("RT") || text.startsWith("@")) return;

                Date created = s.getCreatedAt();

                int hours = created.getHours();
                int minutes = created.getMinutes();

                String hourString = hours < 10 ? "0" + hours : hours + "";
                String minString = minutes < 10 ? "0" + minutes : minutes + "";

                System.out.println("https://twitter.com/user/status/" + s.getId() + ": " + (created.getMonth() + 1) + "/" + created.getDate() + " @ " + hourString + ":" + minString + " \u0009 " + text.substring(0, text.length() < 40 ? text.length() : 40));

                if (lineBreaks.contains(s.getId())) {
                    System.out.println();
                }

                responses.add(s);
            });
        }

        System.out.println(responses.size());
    }
}
