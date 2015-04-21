package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class ThieveMen1 extends Script {

	public ThieveMen1(Extension e) {
		super(e);

	}

	int startX = -1;
	int startY = -1;
	
	@Override
	public long run() {
		if(startX == -1 || startY == -1) {
			startX = getX();
			startY = getY();
		}
		
		if(isSleeping()) {
			System.out.println("sleeping...");
			return 1000;
		}
		else if(inCombat()) {
			walkTo(getX(), getY());
			return random(750, 1250);
		}
		else if(getFatigue() > 95) {
//			this.useSleepingBag();
			stopScript();
			return random(4000, 5000);
		} else {
//			int[] items = this.getGroundItemById(20, 165, 435, 436, 437, 438, 439, 440, 441, 442, 443, 815, 817, 819, 821, 823, 933, 934);
			int[] npc = this.getNpcByIdNotInCombat(11); //3 = chicken
			if(getInventoryCount(getInventoryIndex(20)) > 0) {
				useItem(getInventoryIndex(20));
				return random(300, 500);
			}
//			else if(items[0] != -1 && getInventoryCount() < 30 && distanceTo(startX, startY, items[1], items[2]) < 25) {
//				this.takeItem(items);
//				return random(750, 1250);
//			}
			else if(npc[0] != -1 && distanceTo(startX, startY, npc[1], npc[2]) < 25) {
				this.pickPocketNpc(npc[0]);
				return random(750, 1250);
			}
			return random(2500, 6500);
		}
	}

}
