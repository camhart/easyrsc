package scripts;


import org.rscdaemon.client.bot.Extension;
import org.rscdaemon.client.bot.MessageListener;
import org.rscdaemon.client.bot.Script;

public class PowerShrimp extends Script implements MessageListener {

	int actionCounter = Integer.MAX_VALUE;
	int oldX = -1;
	int oldY = -1;
	private long lastFish;
	private long lastActionThreshold;
	private boolean needToMove = false;
	
	public PowerShrimp(Extension e) {
		super(e);
	}
	
	public long run() {
		if(isSleeping()) {
			return 1000;
		} else if(getFatigue() > 95) {
			stopScript();
			return 1000;
		} else {
			if(((System.currentTimeMillis() - lastFish > random(30000, 90000)) || (actionCounter >= getTotalFishActions() && System.currentTimeMillis() - lastAction > lastActionThreshold))) {
				
				if(needToMove) {
					if(getX() != oldX || getY() !=  oldY) {
						needToMove = false;
					} else {
						int[] moveCoords = this.getMoveCoords();
						walkTo(moveCoords[0], moveCoords[1]);
						return random(1250, 5000);
					}
				}
				
				int[] rock = getObjectById(193);
				System.out.println("time: " + (lastActionThreshold - (System.currentTimeMillis() - lastAction)));
				// 
				if(rock[0] != -1) {
					actionCounter = 0;
					atObject(rock[1], rock[2]);
					lastFish = System.currentTimeMillis();
					
				}
			}
		}
		return random(450, 750);
	}

	private int getTotalFishActions() {
		int stat = getCurrentStatLevel(10) / 10;
		if(stat == 0)
			stat = 1;
		return stat;
	}

	String[] fishingResults = {"You attempt to catch some fish", "You fail to catch anything", "You catch a "};
	private long lastAction;
	
	@Override
	public void onMessage(String message, int type, int status) {
		if(type == 3 && status == 0) {
			for(String s : fishingResults) {
				if(message.startsWith(s)) {
					actionCounter++;
					lastAction = System.currentTimeMillis();
					lastActionThreshold = random(500, 1250);
					return;
				}
			}
			if(message.equals("@cya@You have been standing here for 5 mins! Please move to a new area")) {
				this.needToMove = true;
				oldX = getX();
				oldY = getY();
			}
		}
		
	}

}
