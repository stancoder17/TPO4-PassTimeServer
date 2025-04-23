/**
 *
 *  @author Dyrda Stanisław S31552
 *
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Tools {
    static Options createOptionsFromYaml(String fileName) throws Exception {
        // Initialize yaml
        Yaml yaml = new Yaml();
        String yamlString = String.join("\n", Files.readAllLines(Paths.get(fileName)));
        Map<String, Object> yamlContents = yaml.load(yamlString);

        // Get yaml contents
        String host = yamlContents.get("host").toString();
        int port = Integer.parseInt(yamlContents.get("port").toString());
        boolean concurMode = (boolean) yamlContents.get("concurMode");
        boolean showSendRes = (boolean) yamlContents.get("showSendRes");
        Map<String, List<String>> clientsMap = (Map<String, List<String>>) yamlContents.get("clientsMap");

        return new Options(host, port, concurMode, showSendRes, clientsMap);
    }
}
