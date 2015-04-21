package scripts;

import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.Script;

public class DarkWizardsa extends Script {

	public DarkWizardsa(Extension e) {
		super(e);
	}

	int fightMode = 1;
	int sleepAt = 93;
	boolean buryBones = false;
	boolean debug = false;
	int boneId = -1;
	final int[] FOOD = { 350, 352, 362, 364, 367, 370, 373, 546, 555, 551, 553, 355, 359, 357, 326, 327, 328, 329, 330,
			332, 334, 335, 336, 337, 750, 346, 249, 257, 258, 259, 261, 262, 263, 210 };

	public boolean init() {
	
//		if (theParam.length > 1 && theParam[1].startsWith("y") || theParam[1].startsWith("Y")) {
//			buryBones = true;
//			boneId = 20;
//		}
//		if (theParam.length > 2)
//			debug = true;
		return true;
	}

	boolean direction = true;
	int lastY = 0;

	public long run() {
		int[] npc = getNpcByIdNotInCombat(new int[] { 57, 60 });
		int[] item = getGroundItemById(381, boneId, 31, 32, 33,
				34, 35, 36, 37, 38, 40, 41, 42, 46, 619, 825);
		
		// int[] door = getWallObjectByID()
		if (getCurrentStatLevel(3) < 30) {
			if (getInventoryCount(FOOD) > 0) {
				if (inCombat()) {
					walkTo(getX(), getY());
					return random(250, 350);
				} else {
					useItem(getInventoryIndex(FOOD));
					return random(850, 1250);
				}
			}
			else if (getCurrentStatLevel(3) < 15) {
				//e.autoLogin = false;
				// if(canLogout()) {
				//logout();
				// System.out.println("You're about to die...");
				stopScript();
				// }
			}
			return random(850, 1250);
		}
		// else if(getFightMode() != fightMode) {
		// setFightMode(fightMode);
		// return random(150, 250);
		// }
//		else
		if (inCombat() || isSleeping()) {
			return random(450, 750);
		} else if (getFatigue() >= sleepAt) { // sleep
			sleepAt = random(80, 100);
			useSleepingBag();
			return random(650, 1000);
		} else if (getInventoryCount(20) > 0 && buryBones) {
			// if(debug) System.out.println("opening doors");
			useItem(getInventoryIndex(20));
			return random(350, 750);
		} else if (!inTower() && allDoorsClosed() && (inTower(npc[1], npc[2]) || inTower(item[1], item[2]))) {
			if (debug)
				System.out.println("opening doors");
			openDoor();
			return random(1250, 1500);
		} else if (!inTower() && (inTower(npc[1], npc[2]) || inTower(item[1], item[2]))) {
			if (debug)
				System.out.println("walking in");
			walkTo(362, 572);
			return random(1250, 1500);
		} else if (item[0] != -1 && distanceTo(getX(), getY(), item[1], item[2]) < 20) {
			if (debug)
				System.out.println("picking item " + distanceTo(getX(), getY(), item[1], item[2]));
			if ((inTower(item[1], item[2]) && inTower()) || (!inTower(item[1], item[2]) && !inTower())) {
				takeItem(item);
			} else if (allDoorsClosed()) {
				if (debug)
					System.out.println("door");
				openDoor();
				return random(1250, 1500);
			} else {
				takeItem(item);
			}
			return random(550, 750);
		} else if (npc[0] != -1 && distanceTo(getX(), getY(), npc[1], npc[2]) < 20) {
			if (debug)
				System.out.println("attacking npc");
			if ((inTower(npc[1], npc[2]) && inTower()) || (!inTower(npc[1], npc[2]) && !inTower())) {
				attackNpc(npc[0]);
				return random(750, 1200);
			} else if (allDoorsClosed()) {
				if (debug)
					System.out.println("door");
				openDoor();
				return random(1250, 1500);
			} else {
				attackNpc(npc[0]);
			}
			return random(350, 550);
		} else {
			if (debug)
				System.out.println("Climbing ladder...");
			if (getY() < 1000) {
				if (allDoorsClosed() && !inTower()) {
					openDoor();
					return random(1250, 1500);
				} else {
					atObject(360, 570);
					return random(2000, 2500);
				}
			} else if (getY() < 2000) {
				// if(lastY < 1000 || lastY > 2000)
				if (direction) {
					lastY = getY();
					atObject(363, 1514);
				} else {
					lastY = getY();
					atObject(360, 1514);
				}
				direction = !direction;
				return random(2000, 2500);
			} else if (getY() > 2000) {
				atObject(363, 2458);
				return random(2000, 2500);
			}
		}
		return random(150, 300);
	}

	public void openDoor() {
		int[] nearestDoor = getObjectById(2);
		if (nearestDoor[0] != -1)
			atObject(nearestDoor[1], nearestDoor[2]);
	}

	/*
	 * outside coords: 323, 447 364,569 363,573 359,572 360,568
	 */
	public boolean allDoorsClosed() {
		if (getY() < 1000 && getObjectIdFromCoords(360, 568) == 2 && getObjectIdFromCoords(359, 572) == 2
				&& getObjectIdFromCoords(363, 573) == 2 && getObjectIdFromCoords(364, 569) == 2)
			return true;
		return false;
	}

	public boolean inTower() {
		return inTower(getX(), getY());
	}

	public boolean inTower(int x, int y) {
		if (y > 1000 && distanceTo(getX(), getY(), x, y) < 20)
			return true;
		else if (inArea(x, y, 364, 571, 359, 569) || inArea(x, y, 360, 572, 363, 572)
				|| inArea(x, y, 361, 573, 362, 573) || inArea(x, y, 363, 569, 360, 569)
				|| inArea(x, y, 361, 568, 362, 568)) {
			return true;
		} else
			return false;
	}

//	public int[] getNpc(int id) {
//		return getNpc(new int[] { id });
//	}

//	public int[] getItem(int id) {
//		return getItem(new int[] { id });
//	}

//	public int[] getNpc(int[] paramArrayOfInt) {
//		int[] arrayOfInt = { -1, -1, -1 };
//
//		int i = Integer.MAX_VALUE;
//		for (int j = 0; j < this.getNpcCount(); j++) {
//			if ((!inArray(paramArrayOfInt, this.e.npcArray[j].type)))
//				continue;
//			int k = (this.e.npcArray[j].currentX - 64) / 128 + getAreaX();
//			int m = (this.e.npcArray[j].currentY - 64) / 128 + getAreaY();
//			int n = distanceTo(k, m, getX(), getY());
//			if (n >= i || !isReachable(k, m))
//				continue;
//			arrayOfInt[0] = j;
//			arrayOfInt[1] = k;
//			arrayOfInt[2] = m;
//			i = n;
//		}
//		// if(debug)
//		// System.out.println("NPC: " + arrayOfInt[0] + ", " + arrayOfInt[1] +
//		// ", " + arrayOfInt[2]);
//		return arrayOfInt;
//	}

//	public int[] getItem(int[] paramArrayOfInt) {
//		int[] arrayOfInt = { -1, -1, -1 };
//
//		int i = Integer.MAX_VALUE;
//		for (int j = 0; j < this.e.get(); j++) {
//			if (!inArray(paramArrayOfInt, this.e.itemIdArray()[j]))
//				continue;
//			int k = this.e.itemXArray()[j] + this.e.getTileX();
//			int m = this.e.itemYArray()[j] + this.e.getTileY();
//			int n = distanceTo(k, m, getX(), getY());
//			if (n >= i || !isReachable(k, m))
//				continue;
//			arrayOfInt[0] = this.e.itemIdArray()[j];
//			arrayOfInt[1] = k;
//			arrayOfInt[2] = m;
//			i = n;
//		}
//		// if(debug)
//		// System.out.println("Item: " + arrayOfInt[0] + ", " + arrayOfInt[1] +
//		// ", " + arrayOfInt[2]);
//		return arrayOfInt;
//	}

//	public int getCurrentLevel(int stat) {
//		return this.e.aR[stat];
//		//Smithing
//	}

	public boolean inArea(int x1, int y1, int x2, int y2) {
		return inArea(getX(), getY(), x1, y1, x2, y2);
	}

	public boolean inArea(int myx, int myy, int x1, int y1, int x2, int y2) {
		if (myx <= x1 && myx >= x2 && myy >= y1 && myy <= y2) {
			return true;
		} else if (myx <= x2 && myx >= x1 && myy >= y2 && myy <= y1) {
			return true;
		} else if (myx <= x1 && myx >= x2 && myy >= y2 && myy <= y1) {
			return true;
		} else if (myx <= x2 && myx >= x1 && myy >= y1 && myy <= y2) {
			return true;
		} else
			return false;
	}
}