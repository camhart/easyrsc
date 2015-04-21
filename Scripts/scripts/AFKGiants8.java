package scripts;

import java.awt.Toolkit;





import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;
import org.rscdaemon.client.bot.Extension;
//import org.rscdaemon.client.bot.Script;

public class AFKGiants8 extends Script implements MessageListener {

	public AFKGiants8(Extension e) {
		super(e);

	}
	
	@Override
	public long run() {
		if(inCombat() || isSleeping()) {
			return random(350, 750);
		} else {
			if(getInventoryCount(413) > 0) {
				if(getFatigue() > 95) {
					stopScript();
					System.out.println("Stopping script... need to sleep.");
				}
				useItem(getInventoryIndex(413));
				return random(550, 1500);
			}
			int[] items = this.getGroundItemById(413, 42, 40, 41, 38, 31, 32, 33, 34, 619, 526, 527);
			if(items[0] != -1 && distanceTo(items[1], items[2]) == 0) {
				this.takeItem(items);
				return random(750, 1500);
			}			
		}
		return random(550, 1250);
	}



	@Override
	public void onMessage(String message, int type, int status) {
		message = message.toLowerCase();
		if((type == 2 && status == 0) || message.contains("mod") || message.contains("n0m") || message.contains("fate")) {
			Toolkit.getDefaultToolkit().beep();
			System.out.println("beep");
		}
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;
	}

}
