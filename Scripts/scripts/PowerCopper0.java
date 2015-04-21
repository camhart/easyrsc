package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class PowerCopper0 extends Script {

	public PowerCopper0(Extension e) {
		super(e);

	}
	
	public long run() {
		if(isSleeping()) {
			return 1000;
		} else if(getFatigue() > 95) {
			stopScript();
			return 1000;
		} else {
			int[] rock = getObjectById(100);
			if(rock[0] != -1) {
				System.out.println("true");
				atObject(rock[1], rock[2]);
			} else {
				System.out.println("false");
			}
		}
		return random(750, 3500);
	}

}
