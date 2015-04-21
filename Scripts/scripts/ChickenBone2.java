package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class ChickenBone2 extends Script {

	public ChickenBone2(Extension e) {
		super(e);

	}

	@Override
	public long run() {
		if(isSleeping()) {
			System.out.println("sleeping...");
			return 1000;
		}
		else if(inCombat())
			return random(500, 750);
		else if(getFatigue() > 95) {
//			this.useSleepingBag();
			stopScript();
			return random(4000, 5000);
		} else {
			int[] items = this.getGroundItemById(381, 20);
			int[] npc = this.getNpcByIdNotInCombat(3); //3 = chicken
			if(getInventoryCount(20) > 0) {
				useItem(getInventoryIndex(20));
				System.out.println("burying bone?");
				return random(300, 500);
			}
			else if(items[0] != -1 && distanceTo(items[1], items[2]) < 5) {
				System.out.println("Picking up item");
				this.takeItem(items);
				return random(750, 1250);
			}
			else if(npc[0] != -1) {
				System.out.println("Attacking chicken");
				this.attackNpc(npc[0]);
			}
			return random(700, 1500);
		}
	}

}
