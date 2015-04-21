package scripts;

import java.lang.reflect.Field;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class Chicken9 extends Script {

	public Chicken9(Extension e) {
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
			int[] items = this.getGroundItemById(381);
			int[] npc = this.getNpcByIdNotInCombat(3); //3 = chicken
			if(items[0] != -1) {
				System.out.println("Picking up item");
				this.takeItem(items);
			}
			else if(npc[0] != -1) {
				System.out.println("Attacking chicken");
				this.attackNpc(npc[0]);
			}
			return random(750, 1250);
		}
	}

}
