/**
 *
 *  @author Dyrda Stanisław S31552
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {

    private ClientTask(Callable<String> task) {
        super(task);
    }

    public static ClientTask create(Client client, List<String> reqList, boolean showRes) {
        return new ClientTask(() -> {
            client.connect();
            client.send("login " + client.getId());
            for (String req : reqList) {
                String res = client.send(req);
                if (showRes) System.out.println(res);
            }
            return client.send("bye and log transfer");
        });
    }
}
