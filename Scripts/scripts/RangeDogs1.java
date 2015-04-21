package scripts;

import org.rscdaemon.client.Mob;
import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class RangeDogs1 extends Script {

	public RangeDogs1(Extension e) {
		super(e);

	}

	
	@Override
	public long run() {
		if(this.isSleeping() || getFatigue() >= 100) {
			return 1000;
		}
		else if(this.isRanging()) {
			return random(750, 2500);
		} 
		else {
			int[] dog = this.getNpcByIdNoCombatTimer(262);
			if(dog[0] != -1) {
				this.attackNpc(dog[0]);
				return random(2500, 3000);
			}
		}
		return random(150, 350);
	}


	public int[] getNpcByIdNoCombatTimer(int... ids) {
		int[] arrayOfInt = { -1, -1, -1, -1 };

		int minDistance = Integer.MAX_VALUE;
		for (int index = 0; index < getNpcCount(); index++) {
			if ((!inArray(ids, getNpcId(index))) || (npcHasCombatTimer(index))) {
				continue;
			}
			int npcX = getNpcX(index); // (getNpcX(index) - 64) / getDivisor() +
										// getAreaX();
			int npcY = getNpcY(index); // (getNpcY(index) - 64) / getDivisor() +
										// getAreaY();
			int curDistance = distanceTo(npcX, npcY, getX(), getY());
			if (curDistance >= minDistance)
				continue;
			arrayOfInt[0] = index;
			arrayOfInt[1] = npcX;
			arrayOfInt[2] = npcY;
			arrayOfInt[3] = getNpcId(index);
			minDistance = curDistance;
		}

		return arrayOfInt;
	}


	public boolean npcHasCombatTimer(int index) {
		try {
			Mob m = getNpcArray()[index];
			return m.combatTimer > 0;
		} catch (java.lang.NullPointerException e) {
			return false;	
		}
	}
	
	public boolean isRanging() {
		Mob npc = getNpcByServerIndex(getOurPlayer().attackingNpcIndex);
		return npc != null && !inCombat() && npc.hitPointsCurrent > 0 && npc.combatTimer > 0;
	}
	
	public Mob getNpcByServerIndex(int serverIndex) {
		try {
			Mob ret = this.getNpcRecordArray()[serverIndex];
			return ret;
		}
		catch(java.lang.NullPointerException e) {
			return null;
		}
	}
}
