package scripts;

import java.awt.Toolkit;



import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

public class EggHunter3 extends Script implements MessageListener {

	public EggHunter3(Extension e) {
		super(e);

	}
	
	@Override
	public long run() {

		int[] item = this.getGroundItemById(971,1749, 1156, 1557, 677, 81, 1357);
		if(item[0] != -1) {
			if(inCombat() || distanceTo(item[1], item[2]) > 7)
				walkTo(item[1], item[2]);
			else
				this.takeItem(item);
			Toolkit.getDefaultToolkit().beep();
			return this.random(450, 550);
		}
		return 1;		
	}

	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0 && 
				(message.contains("shooting") || message.contains("attacking") || message.contains("@cya@You have been standing here for "))) {
			Toolkit.getDefaultToolkit().beep();
			System.out.println("move!");
		}
	}
	
	

}
