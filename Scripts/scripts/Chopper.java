package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class Chopper extends Script {

	public Chopper(Extension e) {
		super(e);

	}

	@Override
	public long run() {
		if(getInventoryCount() < 30) {
			int[] tree = this.getObjectById(new int[]{1});
			if(distanceTo(tree[1], tree[2]) < 25) {
				atObject(tree[1], tree[2]);
			}
		}
		return random(1000, 2000);
	}
}
